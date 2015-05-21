package it.infn.ct.aginfrasgmobile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	public static final String KEY_PREFS_SHIBBOLETH_COOKIE = "shib_cookie";
	public static final String KEY_PREFS_DEFAULT_REPOSITORY = "default_repo";
	public static final String KEY_PREFS_REPOSITORIES = "repositories";
	private static final int APP_SHARED_PREFS = R.string.preference_file_key; // Name
																				// of
																				// the
																				// file
																				// -.xml
	private static final String KEY_PREFS_GATEWAY = "pref_gateway";
	private static final String KEY_PREFS_WELCOME_PAGE = "show_welcome_page";
	private static final String KEY_PREFS_DEFAULT_IDP = "default_idp";
	private SharedPreferences _sharedPrefs;
	private Editor _prefsEditor;

	public AppPreferences(Context context) {
		this._sharedPrefs = context.getSharedPreferences(
				context.getString(APP_SHARED_PREFS), Activity.MODE_PRIVATE);
		this._prefsEditor = _sharedPrefs.edit();
	}

	public String getCookie() {
		return _sharedPrefs.getString(KEY_PREFS_SHIBBOLETH_COOKIE, "");
	}

	public void saveCookie(String args) {
		_prefsEditor.putString(KEY_PREFS_SHIBBOLETH_COOKIE, args);
		_prefsEditor.commit();
	}

	public String getDefaultRepository() {
		return _sharedPrefs.getString(KEY_PREFS_DEFAULT_REPOSITORY, "aginfra");
	}

	public void saveDefaultRepo(String item) {
		_prefsEditor.putString(KEY_PREFS_DEFAULT_REPOSITORY, item);
		_prefsEditor.commit();

	}

	public void saveRepos(String json) {
		_prefsEditor.putString(KEY_PREFS_REPOSITORIES, json);
		_prefsEditor.commit();
	}

	public String getRepos() {
		return _sharedPrefs.getString(KEY_PREFS_REPOSITORIES, "");
	}

	// public void saveGateway(String json) {
	// _prefsEditor.putString(KEY_PREFS_REPOSITORIES, json);
	// _prefsEditor.commit();
	// }

	public String getDefaultIDP() {
		return _sharedPrefs.getString(KEY_PREFS_DEFAULT_IDP, "");
	}

	public void saveDefaultIDP(String value) {
		_prefsEditor.putString(KEY_PREFS_DEFAULT_IDP, value);
		_prefsEditor.commit();
	}

	public String getGateway() {
		return _sharedPrefs.getString(KEY_PREFS_GATEWAY,
				"https://aginfra-sg.ct.infn.it");
	}

	public void saveShowWelcomePage(boolean value) {
		_prefsEditor.putBoolean(KEY_PREFS_WELCOME_PAGE, value);
		_prefsEditor.commit();
	}

	public boolean getShowWelcomePage() {

		return _sharedPrefs.getBoolean(KEY_PREFS_WELCOME_PAGE, true);
	}

	public String getRepoName() {
		String result = "";
		String defRep = getDefaultRepository();
		JSONArray repos;
		try {
			String s = getRepos();
			if(!s.equals("")){
				repos = (JSONArray) new JSONTokener(getRepos()).nextValue();
				for (int i = 0; i < repos.length(); i++) {
					JSONObject repositoryJSON = repos.getJSONObject(i);
					if(repositoryJSON.getString("repository").equals(defRep)){
						result = repositoryJSON.getString("rep_name");
						break;
					}
				}
			} else {
				result = "agINFRA Repo";
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}