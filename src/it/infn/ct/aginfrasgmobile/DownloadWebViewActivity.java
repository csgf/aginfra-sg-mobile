package it.infn.ct.aginfrasgmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownloadWebViewActivity extends Activity {

	private AppPreferences _appPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.web_view);
		// final ProgressBar Pbar;
		// final TextView txtview = (TextView)findViewById(R.id.tV1);
		// Pbar = (ProgressBar) findViewById(R.id.pB1);
		_appPrefs = new AppPreferences(getApplicationContext());

		WebView myWebView = (WebView) findViewById(R.id.webview);
		Intent intent = getIntent();
		String url = intent.getStringExtra("URL");
		Log.d("CIAO", url);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.setCookie("Cookie", _appPrefs.getCookie());

		myWebView.setWebViewClient(new MyWebViewClient(this));
		WebSettings webSettings = myWebView.getSettings();
		webSettings.setBuiltInZoomControls(true);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.getSettings().setUseWideViewPort(true);

		if (url.contains("http"))
			myWebView.loadUrl(url);
		else
			myWebView.loadUrl(_appPrefs.getGateway() + "/" + url);

		final ProgressBar Pbar;
		final TextView txtview = (TextView) findViewById(R.id.tv1);
		Pbar = (ProgressBar) findViewById(R.id.pb1);
		final Activity activity = this;
		myWebView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 100);
				if (progress < 100
						&& Pbar.getVisibility() == ProgressBar.INVISIBLE) {
					Pbar.setVisibility(ProgressBar.VISIBLE);
					txtview.setVisibility(View.VISIBLE);
				}
				Pbar.setProgress(progress);
				if (progress == 100) {
					Pbar.setVisibility(ProgressBar.INVISIBLE);
					txtview.setVisibility(View.INVISIBLE);
				}
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	private class MyWebViewClient extends WebViewClient {
		// private String args = null;

		private Activity baseActivity;

		public MyWebViewClient(DownloadWebViewActivity downloadWebViewActivity) {
			this.baseActivity = downloadWebViewActivity;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
//			if (url.contains("https://maps.google.com")) {
//
//				Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//						Uri.parse(url));
//				intent.setClassName("com.google.android.apps.maps",
//						"com.google.android.maps.MapsActivity");
//				startActivity(intent);
//			} else {
//				// ...
//			}
//
			return false;
		}

		@Override
		public void onLoadResource(WebView view, String url) {

		}

		@Override
		public void onPageFinished(WebView view, String url) {


		}

	}

}
