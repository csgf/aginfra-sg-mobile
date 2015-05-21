package it.infn.ct.aginfrasgmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class SplashScreenActivity extends Activity {
	// Set the display time, in milliseconds (or extract it out as a
	// configurable parameter)
	private final int SPLASH_DISPLAY_LENGTH = 3000;
	private AppPreferences _appPrefs;
	private boolean flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_appPrefs = new AppPreferences(this.getBaseContext());
		_appPrefs.saveCookie("");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash_screen);
		if (savedInstanceState != null)
			flag = savedInstanceState.getBoolean("flag");
		else
			flag = true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putBoolean("flag", false);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (flag) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// Finish the splash activity so it can't be returned to.
					SplashScreenActivity.this.finish();
					// Create an Intent that will start the main activity.
					if (!_appPrefs.getShowWelcomePage()) {
						if (!_appPrefs.getDefaultIDP().equals("")) {
							Intent intent = new Intent(getBaseContext(),
									WebViewActivity.class);
							intent.putExtra("URL", _appPrefs.getDefaultIDP());
							intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
							startActivity(intent);
						} else {
							Intent mainIntent = new Intent(
									SplashScreenActivity.this,
									MainActivity.class);
							SplashScreenActivity.this.startActivity(mainIntent);
						}
					} else {
						Intent welcomeIntent = new Intent(
								SplashScreenActivity.this, HowToActivity.class);
						SplashScreenActivity.this.startActivity(welcomeIntent);
					}

				}
			}, SPLASH_DISPLAY_LENGTH);

		}
	}
}
