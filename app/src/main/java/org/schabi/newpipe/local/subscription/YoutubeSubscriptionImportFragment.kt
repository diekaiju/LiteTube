package org.schabi.newpipe.local.subscription

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.json.JSONArray
import org.schabi.newpipe.NewPipeDatabase
import org.schabi.newpipe.R
import org.schabi.newpipe.database.subscription.SubscriptionEntity

class YoutubeSubscriptionImportFragment : DialogFragment() {

    private lateinit var statusText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var webView: WebView
    private lateinit var cancelButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Make the dialog fill most of the screen when shown
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_youtube_import, container, false)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusText = view.findViewById(R.id.import_status_text)
        progressBar = view.findViewById(R.id.import_progress_bar)
        webView = view.findViewById(R.id.import_webview)
        cancelButton = view.findViewById(R.id.import_cancel_button)

        cancelButton.setOnClickListener {
            dismiss()
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.addJavascriptInterface(ScraperInterface(), "NewPipeApp")
        webView.webChromeClient = WebChromeClient()

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                Log.d("SubscriptionImport", "Finished loading: $url")

                if (url.contains("accounts.google.com") || url.contains("signin")) {
                    // Requires login, show the WebView
                    statusText.text = "Please sign in to your YouTube/Google account."
                    progressBar.visibility = View.GONE
                    webView.visibility = View.VISIBLE
                } else if (url.contains("youtube.com/feed/channels")) {
                    // Logged in and on the subscription list page! Hide WebView and run scraper.
                    webView.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    statusText.text = "Logged in. Auto-scrolling and scraping subscriptions..."
                    injectScraperScript()
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                Log.d("SubscriptionImport", "Loading URL: $url")
                return false
            }
        }

        // Start by loading the mobile YouTube subscriptions page
        webView.loadUrl("https://m.youtube.com/feed/channels?ra=m")
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun injectScraperScript() {
        val js = """
            javascript:(function() {
                var lastHeight = document.body.scrollHeight;
                var scrollAttempts = 0;
                var maxAttempts = 30;
                
                function scrollAndScrape() {
                    window.scrollTo(0, document.body.scrollHeight);
                    
                    setTimeout(function() {
                        var newHeight = document.body.scrollHeight;
                        if (newHeight > lastHeight && scrollAttempts < maxAttempts) {
                            lastHeight = newHeight;
                            scrollAttempts++;
                            scrollAndScrape();
                        } else {
                            var channels = [];
                            var anchors = document.querySelectorAll('a');
                            anchors.forEach(function(a) {
                                var href = a.getAttribute('href');
                                if (!href) return;
                                var isChannel = href.indexOf('/channel/') !== -1 
                                             || href.indexOf('/c/') !== -1 
                                             || href.indexOf('/user/') !== -1
                                             || href.startsWith('/@');
                                if (isChannel) {
                                    var name = a.innerText ? a.innerText.trim() : '';
                                    if (!name) {
                                        var img = a.querySelector('img');
                                        if (img && img.alt) {
                                            name = img.alt.trim();
                                        }
                                    }
                                    if (name) {
                                        name = name.split('\n')[0].trim();
                                    }
                                    var avatar = '';
                                    var img = a.querySelector('img');
                                    if (img && img.src) {
                                        avatar = img.src;
                                    }
                                    var absoluteUrl = a.href;
                                    if (absoluteUrl && name && !channels.some(function(c) { return c.url === absoluteUrl; })) {
                                        if (name !== 'Home' && name !== 'Subscriptions' && name !== 'Library' && name !== 'Trending') {
                                            channels.push({
                                                url: absoluteUrl,
                                                name: name,
                                                avatar: avatar
                                            });
                                        }
                                    }
                                }
                            });
                            window.NewPipeApp.onSubscriptionsScraped(JSON.stringify(channels));
                        }
                    }, 1000);
                }
                
                scrollAndScrape();
            })();
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    private inner class ScraperInterface {
        @JavascriptInterface
        fun onSubscriptionsScraped(json: String) {
            val activity = activity ?: return
            activity.runOnUiThread {
                try {
                    val channelsArray = JSONArray(json)
                    val entities = ArrayList<SubscriptionEntity>()

                    for (i in 0 until channelsArray.length()) {
                        val obj = channelsArray.getJSONObject(i)
                        val url = obj.getString("url")
                        val name = obj.getString("name")
                        val avatar = obj.optString("avatar", "")

                        entities.add(
                            SubscriptionEntity(
                                serviceId = 0, // YouTube
                                url = url,
                                name = name,
                                avatarUrl = avatar
                            )
                        )
                    }

                    Log.d("SubscriptionImport", "Scraped ${entities.size} subscriptions.")
                    if (entities.isNotEmpty()) {
                        saveSubscriptionsToDb(entities)
                    } else {
                        Toast.makeText(activity, "No subscriptions found.", Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                } catch (e: Exception) {
                    Log.e("SubscriptionImport", "Failed to parse scraped channels", e)
                    Toast.makeText(activity, "Failed to parse subscriptions.", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    private fun saveSubscriptionsToDb(entities: List<SubscriptionEntity>) {
        val context = context ?: return
        Completable.fromAction {
            val database = NewPipeDatabase.getInstance(context)
            database.subscriptionDAO().upsertAll(entities)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                Toast.makeText(
                    context,
                    "Imported ${entities.size} subscriptions successfully!",
                    Toast.LENGTH_LONG
                ).show()
                dismiss()
            }, { throwable ->
                Log.e("SubscriptionImport", "Error saving subscriptions", throwable)
                Toast.makeText(context, "Error saving subscriptions to database.", Toast.LENGTH_SHORT).show()
                dismiss()
            })
    }
}
