package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.utils.ConnectionDetector;
import it.infn.ct.aginfrasgmobile.utils.DialogFactory;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity implements
		DialogInterface.OnClickListener {

	private static final String TAG = WebViewActivity.class.getSimpleName();
	private static final String SERVER_URL = "https://gridp.garr.it/ds/WAYF?entityID=";
	private static final String SUFFIX = "/shibboleth&action=selection&origin=";
	private static final int RESULT_SETTINGS = 1;
	private AppPreferences _appPrefs;
	CookieManager cookieManager;
	private boolean canShowDialog = false;
	private WebView myWebView;
	private ConnectionDetector cd;
	private ProgressDialog pDialog;
	private String url;
	private boolean load = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		cd = new ConnectionDetector(getApplicationContext());
		setContentView(R.layout.web_view);
		cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
		_appPrefs = new AppPreferences(getApplicationContext());

		myWebView = (WebView) findViewById(R.id.webview);
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		Log.d("CIAO", SERVER_URL + _appPrefs.getGateway() + SUFFIX + url);
		// myWebView.loadUrl(SERVER_URL + "https://www.chain-project.eu" +
		// SUFFIX + url);

		myWebView.setWebViewClient(new MyWebViewClient(this));
		WebSettings webSettings = myWebView.getSettings();

		webSettings.setJavaScriptEnabled(true);
		myWebView.getSettings().setLoadWithOverviewMode(true);
		myWebView.getSettings().setUseWideViewPort(true);
		// if (cd.isConnectingToInternet())
		// myWebView.loadUrl(SERVER_URL + _appPrefs.getGateway() + SUFFIX
		// + url);
		// else {
		// Dialog d = DialogFactory.getDialog(this, "No internet connection",
		// "You don't have internet connection.", false,
		// "Wireless settings", "Exit", this, this);
		// d.show();
		// }
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			i.putExtra("default_idp", true);
			startActivityForResult(i, RESULT_SETTINGS);
			break;
		}
		return true;
	}

	//
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "OnActivityResult");
		if (requestCode == RESULT_SETTINGS && resultCode == RESULT_OK)
			finish();
		else
			load = false;
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "OnStart");

		super.onStart();
		canShowDialog = true;
	}

	@Override
	protected void onResume() {
		Log.d(TAG, "OnResume");
		if (cd.isConnectingToInternet()) {
			if (load) {
				myWebView.loadUrl(SERVER_URL + _appPrefs.getGateway() + SUFFIX
						+ url);
			}
		} else {
			Dialog d = DialogFactory.getDialog(this, "No internet connection",
					"You don't have internet connection.", false,
					"Connect", "Exit", this, this);
			d.show();
		}
		super.onResume();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "OnPause");
		super.onPause();
		canShowDialog = false;
	}

	@Override
	public void onBackPressed() {

		// Dialog d = DialogFactory.getDialog(this, "Closing Activity",
		// "Are you sure you want to close this activity?", false, "Yes",
		// "No", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// _appPrefs.saveCookie("");
		// finish();
		// }
		// }, null);
		// d.show();
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	private class MyWebViewClient extends WebViewClient {
		// private String args = null;
		private Activity baseActivity;

		public MyWebViewClient(WebViewActivity webViewActivity) {
			this.baseActivity = webViewActivity;
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// if
			// (!Uri.parse(url).getHost().equals("earthserver-sg.consorzio-cometa.it"))
			// {
			// // This is my web site, so do not override; let my WebView load
			// // the page
			// return false;
			// } else

			// Otherwise, the link is not for a page on my site, so launch
			// another Activity that handles URLs
			return false;
		}

		//
		@Override
		public void onLoadResource(WebView view, String url) {
			if (pDialog == null) {

				pDialog = new ProgressDialog(baseActivity);
				pDialog.setTitle("Loading...");
				pDialog.setMessage("Loading data...");
				pDialog.setIndeterminate(false);
				pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pDialog.setCancelable(true);
				pDialog.setCanceledOnTouchOutside(false);
				if (canShowDialog)
					pDialog.show();

			} else if (!pDialog.isShowing() && !url.contains("UserPassword")) {
				pDialog.setTitle("Loading...");
				pDialog.setMessage("Signing in...");
				pDialog.setCanceledOnTouchOutside(false);
				if (canShowDialog)
					pDialog.show();
				if (url.contains(_appPrefs.getGateway()))
					setContentView(R.layout.web_view);
			}

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			Log.i("URL onPageFineshed", url);
			if (pDialog.isShowing())
				pDialog.dismiss();
			if (!url.contains("Redirect") && cookieManager.hasCookies()) {
				String rawCookieString = cookieManager.getCookie(url)
						.toLowerCase();
				Log.d("cookeis", rawCookieString);
				String args = null;
				if (rawCookieString.length() > 0) {
					String[] rawCookies = rawCookieString.split(";");
					for (int i = 0; i < rawCookies.length; i++)
						if (rawCookies[i].contains("_shibsession_")) {
							args = rawCookies[i];
						}

					if (args != null) {
						_appPrefs.saveCookie(args);
						if (pDialog != null)
							pDialog.dismiss();
						//
						Intent intent = new Intent(
								baseActivity.getBaseContext(),
								MainActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(intent);
						finish();
					}
				}

			}
		}

	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case -1:
			Log.d(TAG, "Positve Pressed");
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
			break;

		case -2:
			_appPrefs.saveCookie("");
			finish();
			break;
		default:
			Log.d(TAG, "Unrecognized button pressed.");
			break;
		}

	}

}
