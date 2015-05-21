package it.infn.ct.aginfrasgmobile;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		Intent i = getActivity().getIntent();
        addPreferencesFromResource(R.xml.settings);
        Preference p = findPreference("default_idp");
        p.setEnabled(i.getBooleanExtra("default_idp", false));
        p.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {

				Intent mainIntent = new Intent(getActivity(),
						MainActivity.class);
//				mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				mainIntent.putExtra("flag", false);
				getActivity().startActivity(mainIntent);
				getActivity().finish();
				return false;
			}
		});
        
	}

}
