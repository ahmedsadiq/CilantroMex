/**
 * 
 */
package com.dg.android.lcp.activities;

import com.dg.android.lcp.utils.AndroidUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;


/**
 * @author MehrozKarim
 * 
 */
public class FaceBookTabActivity extends Activity {

    Context context;
    WebView webview;
    Button back;
    ProgressDialog progressdilog;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.wall_tab);
        url =AndroidUtil.FB_PAGE_URL;
//        url =getString(R.string.facebook_url);
         back = (Button) findViewById(R.id.back);

        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {


            }
        });

        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (webview.canGoBack()) {
                    webview.goBack();
                }
            }
        });

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Handle the error
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
                    startActivity(intent);
                } else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                progressdilog.dismiss();
            }
        });

        webview.loadUrl(url);
        progressdilog = ProgressDialog.show(FaceBookTabActivity.this, "", getString(R.string.loading), true);

        if (progressdilog.getProgress() == 100) progressdilog.dismiss();
    }

}
