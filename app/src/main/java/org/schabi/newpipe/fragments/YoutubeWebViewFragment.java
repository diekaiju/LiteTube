package org.schabi.newpipe.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import org.schabi.newpipe.BaseFragment;
import org.schabi.newpipe.MainActivity;
import org.schabi.newpipe.R;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.util.NavigationHelper;

public class YoutubeWebViewFragment extends BaseFragment implements BackPressable {

    private WebView webView;

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
        }

        return view;
    }

    private void injectClickInterceptor() {
        final String js = "javascript:(function() { "
                + "document.addEventListener('click', function(e) { "
                + "  var link = e.target.closest('a'); "
                + "  if (link && link.href) { "
                + "    var isVid = link.href.indexOf('/watch?') !== -1 "
                + "      || link.href.indexOf('/shorts/') !== -1 "
                + "      || link.href.indexOf('youtu.be/') !== -1; "
                + "    if (isVid) { "
                + "      e.preventDefault(); "
                + "      e.stopPropagation(); "
                + "      window.NewPipeApp.onVideoClicked(link.href); "
                + "    } "
                + "  } "
                + "}, true); "
                + "})();";

        webView.evaluateJavascript(js, null);
    }

    private final class WebAppInterface {
        @JavascriptInterface
        public void onVideoClicked(final String url) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    try {
                        Log.d("YoutubeWebView", "Intercepted via JS: " + url);

                        // Pause any background playback
                        webView.evaluateJavascript(
                                "document.getElementsByTagName('video')[0].pause();",
                                null);

                        final StreamingService service = NewPipe.getServiceByUrl(url);

                        NavigationHelper.openVideoDetailFragment(requireContext(),
                                requireActivity().getSupportFragmentManager(),
                                service.getServiceId(), url, "", null, false);

                    } catch (final Exception e) {
                        Log.e("YoutubeWebView", "Failed to open intercepted link: " + url, e);
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
