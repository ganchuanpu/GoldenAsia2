package com.goldenasia.lottery.fragment;


import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.goldenasia.lottery.BuildConfig;
import com.goldenasia.lottery.R;
import com.goldenasia.lottery.app.BaseFragment;
import com.goldenasia.lottery.app.GoldenAsiaApp;

import butterknife.BindView;

/**
 * Created By Sakura
 */
public class WebViewFragment extends BaseFragment {
    protected String url;

    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle
            savedInstanceState) {
        // Inflate the layout for this fragment
        return inflateView(inflater, container, true, "", R.layout.fragment_web_view);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    protected void init() {
        Bundle bundle;
        bundle = getActivity().getIntent().getExtras();
        if (TextUtils.isEmpty(url))
            url = (String) bundle.get("url");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);
        }
        webView.setOverScrollMode(WebView.OVER_SCROLL_ALWAYS);
        WebSettings webSettings = webView.getSettings();

        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new JsInterface(), "androidJs");
        webView.setWebChromeClient(new WebChromeClient());
//        webView.setWebViewClient(new WebViewClient());

        //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        //CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, GoldenAsiaApp.getUserCentre().getSession());
        //CookieSyncManager.getInstance().sync();

        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                loadingLayout.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {//处理https请
                handler.proceed();    //表示等待证书响应
                // handler.cancel();      //表示挂起连接，为默认方式
                // handler.handleMessage(null);    //可做其他处理
            }

        });
        webView.setWebContentsDebuggingEnabled(true);
    }

    @Override
    public void onDestroyView() {
        webView.destroy();
        super.onDestroyView();
    }

    private class JsInterface {
        /**
         * 给网页调用，网页点“离开”游戏时使用 --> androidJs.finishGame()
         */
        @JavascriptInterface
        public void finishGame() {
            getActivity().finish();
        }
    }
}
