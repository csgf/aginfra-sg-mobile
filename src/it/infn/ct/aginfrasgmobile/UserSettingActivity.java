package it.infn.ct.aginfrasgmobile;

import android.app.Activity;
import android.os.Bundle;

public class UserSettingActivity extends Activity {

	public static int RESULT_OK_BUT_NOT_CLOSE = 0;
	private boolean setResult = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// addPreferencesFromResource(R.xml.settings);
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK_BUT_NOT_CLOSE);
		setResult = false;
		super.onBackPressed();
	}

	@Override
	public void finish() {
		if (setResult) {
			setResult(RESULT_OK);
		}
		// setResult(RESULT_OK_BUT_NOT_CLOSE);
		super.finish();
	}

}
