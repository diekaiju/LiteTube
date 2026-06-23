package org.schabi.newpipe.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;

import org.schabi.newpipe.BaseFragment;
import org.schabi.newpipe.MainActivity;
import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.schabi.newpipe.info_list.dialog.InfoItemDialog;
import org.schabi.newpipe.util.NavigationHelper;

public class YoutubeWebViewFragment extends BaseFragment implements BackPressable {

    private WebView webView;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_youtube_webview, container, false);
        webView = view.findViewById(R.id.youtube_webview);

        final WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Register the JavaScript Interface
        webView.addJavascriptInterface(new WebAppInterface(), "NewPipeApp");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String url) {
                super.onPageFinished(view, url);
                injectClickInterceptor();
            }

            @Override
            public boolean shouldOverrideUrlLoading(final WebView view,
                                                    final WebResourceRequest request) {
                final String url = request.getUrl().toString();

                if (url.startsWith("intent://")) {
                    return true;
                }

                // If not YouTube and is a valid web link, open in system browser
                final boolean isYouTube = url.contains("youtube.com") || url.contains("youtu.be");
                if (!isYouTube && (url.startsWith("http://") || url.startsWith("https://"))) {
                    try {
                        final Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                request.getUrl());
                        startActivity(browserIntent);
                        return true;
                    } catch (final Exception e) {
                        Log.e("YoutubeWebView", "External browser failed", e);
                    }
                }

                // Allow YouTube navigations to load inside WebView initially
                return false;
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl("https://m.youtube.com/");
        } else {
            webView.restoreState(savedInstanceState);
        }

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull final Bundle outState) {
        super.onSaveInstanceState(outState);
        if (webView != null) {
            webView.saveState(outState);
        }
    }

    private void checkAndSyncHistoryToYouTube(final Context context, final String url) {
        final boolean isSyncEnabled = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.enable_webview_history_sync_key), false);

        if (!isSyncEnabled) {
            return;
        }

        Log.d("YoutubeWebView", "Syncing watch history to YouTube for URL: " + url);

        final WebView bgWebView = new WebView(context.getApplicationContext());
        bgWebView.getSettings().setJavaScriptEnabled(true);
        bgWebView.getSettings().setDomStorageEnabled(true);

        bgWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, final String pageUrl) {
                super.onPageFinished(view, pageUrl);
                Log.d("YoutubeWebView", "Sync WebView loaded: " + pageUrl
                        + ". Keeping alive for 5 seconds.");

                new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                    try {
                        bgWebView.stopLoading();
                        bgWebView.destroy();
                        Log.d("YoutubeWebView", "Sync WebView destroyed for: " + pageUrl);
                    } catch (final Exception e) {
                        Log.e("YoutubeWebView", "Error destroying sync WebView", e);
                    }
                }, 5000);
            }
        });

        bgWebView.loadUrl(url);
    }

    private void injectClickInterceptor() {
        final String js = "javascript:(function() { "
                + "if (window.clickInterceptorInjected) return; "
                + "window.clickInterceptorInjected = true; "
                + "document.addEventListener('click', function(e) { "
                + "  var link = e.target.closest('a'); "
                + "  if (link && link.href) { "
                + "    var isVid = link.href.indexOf('/watch?') !== -1 "
                + "      || link.href.indexOf('/shorts/') !== -1 "
                + "      || link.href.indexOf('youtu.be/') !== -1; "
                + "    var isPlaylist = link.href.indexOf('/playlist?') !== -1; "
                + "    if (isVid || isPlaylist) { "
                + "      e.preventDefault(); "
                + "      e.stopPropagation(); "
                + "      window.NewPipeApp.onClicked(link.href, isVid); "
                + "    } "
                + "  } "
                + "}, true); "
                + "document.addEventListener('contextmenu', function(e) { "
                + "  var link = e.target.closest('a'); "
                + "  if (link && link.href) { "
                + "    var isVid = link.href.indexOf('/watch?') !== -1 "
                + "      || link.href.indexOf('/shorts/') !== -1 "
                + "      || link.href.indexOf('youtu.be/') !== -1; "
                + "    if (isVid) { "
                + "      e.preventDefault(); "
                + "      e.stopPropagation(); "
                + "      window.NewPipeApp.onLongPressed(link.href, link.innerText || ''); "
                + "    } "
                + "  } "
                + "}, true); "
                + "})();";

        webView.evaluateJavascript(js, null);
    }

    private final class WebAppInterface {
        @JavascriptInterface
        public void onClicked(final String url, final boolean isVideo) {
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    if (!isAdded()) {
                        return;
                    }
                    try {
                        Log.d("YoutubeWebView", "Intercepted: " + url);

                        webView.evaluateJavascript(
                                "(function(){var v=document.querySelector('video');"
                                        + "if(v)v.pause();})()", null);

                        String finalUrl = url;
                        if (url.contains("youtube.com") || url.contains("youtu.be")) {
                            finalUrl = url.replace("m.youtube.com", "www.youtube.com")
                                    .replace("mobile.youtube.com", "www.youtube.com");
                        }

                        final StreamingService service = NewPipe.getServiceByUrl(finalUrl);

                        if (isVideo) {
                            checkAndSyncHistoryToYouTube(activity, url);

                            NavigationHelper.openVideoDetailFragment(activity,
                                    activity.getSupportFragmentManager(),
                                    service.getServiceId(), finalUrl, "", null, false);
                        } else {
                            NavigationHelper.openPlaylistFragment(
                                    activity.getSupportFragmentManager(),
                                    service.getServiceId(), finalUrl, "");
                        }

                    } catch (final Exception e) {
                        Log.e("YoutubeWebView", "Native open failed, falling back: " + url,
                                e);
                        webView.loadUrl(url);
                    }
                });
            }
        }

        @JavascriptInterface
        public void onLongPressed(final String url, final String title) {
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    if (!isAdded()) {
                        return;
                    }
                    try {
                        Log.d("YoutubeWebView", "Long pressed: " + url + ", title: " + title);

                        String finalUrl = url;
                        if (url.contains("youtube.com") || url.contains("youtu.be")) {
                            finalUrl = url.replace("m.youtube.com", "www.youtube.com")
                                    .replace("mobile.youtube.com", "www.youtube.com");
                        }

                        final StreamingService service = NewPipe.getServiceByUrl(finalUrl);

                        String cleanTitle = title;
                        if (cleanTitle != null) {
                            cleanTitle = cleanTitle.split("\n")[0].trim();
                        }
                        if (cleanTitle == null || cleanTitle.isEmpty()) {
                            cleanTitle = "YouTube Video";
                        }

                        final StreamInfoItem item = new StreamInfoItem(
                                service.getServiceId(),
                                finalUrl,
                                cleanTitle,
                                org.schabi.newpipe.extractor.stream.StreamType.VIDEO_STREAM
                        );

                        new InfoItemDialog.Builder(
                                activity,
                                getContext(),
                                YoutubeWebViewFragment.this,
                                item
                        ).create().show();

                    } catch (final Exception e) {
                        Log.e("YoutubeWebView", "Long press handling failed", e);
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle("YouTube Web");
        if (getActivity() != null && getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("YouTube Web");
        }
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu,
                                    @NonNull final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_youtube_webview, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        if (item.getItemId() == R.id.menu_item_refresh) {
            if (webView != null) {
                webView.reload();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return false;
    }
}
