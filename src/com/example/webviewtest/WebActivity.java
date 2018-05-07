package com.example.webviewtest;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import com.example.webviewtest3.R;

public class WebActivity extends Activity{
	WebView mWebview;
	WebSettings mWebSettings;
	TextView beginLoading,endLoading,loading,mtitle;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web);


		mWebview = (WebView) findViewById(R.id.webView1);
		beginLoading = (TextView) findViewById(R.id.text_beginLoading);
		endLoading = (TextView) findViewById(R.id.text_endLoading);
		loading = (TextView) findViewById(R.id.text_Loading);
		mtitle = (TextView) findViewById(R.id.title);

		mWebSettings = mWebview.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setDefaultTextEncodingName("UTF-8");
		mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		mWebview.setWebChromeClient(new WebChromeClient());

		//设置不用系统浏览器打开,直接显示在当前Webview
		WebViewClient webViewClient = new WebViewClient() {
			@Override
	        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
	            // 如下方案可在非微信内部WebView的H5页面中调出微信支付
	            if (url.startsWith("weixin://wap/pay?")) {
	                Intent intent = new Intent();
	                intent.setAction(Intent.ACTION_VIEW);
	                intent.setData(Uri.parse(url));
	                startActivity(intent);
	                return true;
	            }
	            return super.shouldOverrideUrlLoading(view, url);
	        }
	 
			//设置结束加载函数
	        @Override
	        public void onPageFinished(WebView view, String url) {
	            super.onPageFinished(view, url);
				endLoading.setText("结束加载了");
	        }
	 
	        @Override
	        public void onReceivedError(WebView view, int errorCode,
	                String description, String failingUrl) {
	            super.onReceivedError(view, errorCode, description, failingUrl);
	        }  

			//设置加载前的函数
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				System.out.println("开始加载了");
				beginLoading.setText("开始加载了");

			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, android.net.http.SslError error) { // 重写此方法可以让webview处理https请求
				handler.proceed();
			}
		};
		mWebview.setWebViewClient(webViewClient);
		mWebview.loadUrl("http://paydev.sanduspace.cn:8089/TestH5Pay/payTest.html");


		//设置WebChromeClient类
		mWebview.setWebChromeClient(new WebChromeClient() {
			//获取网站标题
			@Override
			public void onReceivedTitle(WebView view, String title) {
				System.out.println("标题在这里");
				mtitle.setText(title);
			}

			//获取加载进度
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress < 100) {
					String progress = newProgress + "%";
					loading.setText(progress);
				} else if (newProgress == 100) {
					String progress = newProgress + "%";
					loading.setText(progress);
				}
			}
		});
	}

	//点击返回上一页面而不是退出浏览器
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
			mWebview.goBack();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	//销毁Webview
	@Override
	protected void onDestroy() {
		if (mWebview != null) {
			mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
			mWebview.clearHistory();

			((ViewGroup) mWebview.getParent()).removeView(mWebview);
			mWebview.destroy();
			mWebview = null;
		}
		super.onDestroy();
	}
}
