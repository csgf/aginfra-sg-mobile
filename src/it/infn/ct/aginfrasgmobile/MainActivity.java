package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.FederationsFragment.OnFederationSelectedListener;
import it.infn.ct.aginfrasgmobile.TypesFragment.OnTypesSelectedListener;
import it.infn.ct.aginfrasgmobile.pojos.Federation;
import it.infn.ct.aginfrasgmobile.pojos.Filter;
import it.infn.ct.aginfrasgmobile.pojos.Type;
import it.infn.ct.aginfrasgmobile.utils.DialogFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements
		OnFederationSelectedListener, OnTypesSelectedListener, OnClickListener {

	private static final int RESULT_SETTINGS = 1;
	private static final String TAG = MainActivity.class.getSimpleName();
	private AppPreferences _appPrefs;
	private boolean canClose;
	private boolean flag = true;
	private Type typeSelected = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_appPrefs = new AppPreferences(getApplicationContext());
		Intent intent = getIntent();
		if (intent.hasExtra("flag"))
			flag = intent.getBooleanExtra("flag", true);
		String cookie = _appPrefs.getCookie();
		String defIDP = _appPrefs.getDefaultIDP();
		Log.d(TAG, cookie);
		if (cookie.equals("")) {
			setContentView(R.layout.activity_main);

			// Verifico se sto usando la versione del layout con il fragment
			// container. Se si aggiungo il primo fragment
			if (findViewById(R.id.fragment_container) != null) {

				// Se sto recuperando uno stato precedente non devo fare niente
				if (savedInstanceState != null) {
					return;
				}

				// Create an instance of ExampleFragment
				FederationsFragment firstFragment = new FederationsFragment();

				// In case this activity was started with special instructions
				// from
				// an Intent,
				// pass the Intent's extras to the fragment as arguments
				firstFragment.setArguments(getIntent().getExtras());

				// Add the fragment to the 'fragment_container' FrameLayout
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.fragment_container, firstFragment,
								"federations").commit();
			}
			ImageView im = (ImageView) findViewById(R.id.notSureImageVIew);
			im.setOnClickListener(this);
		} else {

			setContentView(R.layout.new_layout);

			if (findViewById(R.id.new_fragment_container) != null) {

				if (savedInstanceState != null) {
					typeSelected = (Type) savedInstanceState
							.getSerializable("typeSelected");
					return;
				}

				// // Create an instance of ExampleFragment
				MenuFragment firstFragment = new MenuFragment();
				//
				// In case this activity was started with special instructions
				// from
				// an Intent,
				// pass the Intent's extras to the fragment as arguments
				firstFragment.setArguments(getIntent().getExtras());

				// Add the fragment to the 'fragment_container' FrameLayout
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.new_fragment_container, firstFragment,
								"MENU_FRAG")
						// .add(R.id.new_fragment_container, firstFragment)
						.commit();

			} else if (savedInstanceState != null)
				typeSelected = (Type) savedInstanceState
						.getSerializable("typeSelected");
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		canClose = true;
		Log.d(TAG, "OnResume");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		canClose = false;
		Log.d(TAG, "OnPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(TAG, "onStop");
		TypesFragment myFragment = (TypesFragment) getSupportFragmentManager()
				.findFragmentByTag("types");
		if (canClose && myFragment != null && myFragment.isVisible()) {
			_appPrefs.saveCookie("");
			finish();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("typeSelected", typeSelected);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		// super.onBackPressed();
		Log.d(TAG, "OnBack");
		getActionBar().setDisplayHomeAsUpEnabled(false);

		TypesFragment typesFragment = (TypesFragment) getSupportFragmentManager()
				.findFragmentByTag("types");
		RepositoriesFragment repositoriesFragment = (RepositoriesFragment) getSupportFragmentManager()
				.findFragmentByTag("repositories");
		FederationsFragment federationsFragment = (FederationsFragment) getSupportFragmentManager()
				.findFragmentByTag("federations");

		if (flag
				&& ((typesFragment != null && typesFragment.isVisible())
						|| (repositoriesFragment != null && repositoriesFragment
								.isVisible())
						|| (federationsFragment != null && federationsFragment
								.isVisible()) || (findViewById(R.id.main_fragment_container)) != null)) {

			new AlertDialog.Builder(this)
					// .setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Closing Activity")
					.setMessage("Are you sure you want to close this activity?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									_appPrefs.saveCookie("");
									finish();
								}

							}).setNegativeButton("No", null).show();

			//
		} else if (findViewById(R.id.new_fragment_container) != null) {

			// if (savedInstanceState != null) {
			// typeSelected = (Type)
			// savedInstanceState.getSerializable("typeSelected");
			// return;
			// }

			// // Create an instance of ExampleFragment
			MenuFragment firstFragment = new MenuFragment();
			//
			// In case this activity was started with special instructions
			// from
			// an Intent,
			// pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments(getIntent().getExtras());

			// Add the fragment to the 'fragment_container' FrameLayout
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.new_fragment_container, firstFragment,
							"MENU_FRAG")
					// .add(R.id.new_fragment_container, firstFragment)
					.commit();

		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onFederationSelected(Federation federation, int position) {
		// The user selected the federation from the FederationsFragment

		// Capture the idps fragment from the activity layout
		IDPsFragment idpsFragment = (IDPsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.idps_fragment);

		if (idpsFragment != null) {
			// If idps frag is available, we're in two-pane layout...

			// Call the update method in the IDPsFragment to update its content
			idpsFragment.updateIdpsList(federation.getIdps(), position);

		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it the selected idp
			IDPsFragment newFragment = new IDPsFragment();
			Bundle args = new Bundle();
			args.putSerializable(IDPsFragment.ARG_POSITION,
					federation.getIdps());
			newFragment.setArguments(args);
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.fragment_container, newFragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!_appPrefs.getCookie().equals(""))
			menu.findItem(R.id.not_sure).setVisible(false);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onTypeSelected(Type type, int position) {

		typeSelected = type;
		DetailsFragment.resetFilters();
		DetailsFragment.resetChosenFilters();

		DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_fragment);

		if (detailsFragment != null) {
			// If idps frag is available, we're in two-pane layout...

			// Call the update method in the IDPsFragment to update its content
			// idpsFragment.updateIdpsList(federation.getIdps(), position);
			Bundle args = new Bundle();
			args.putString(DetailsFragment.ARG_POSITION, type.getPath());
			detailsFragment.updateFragment(args);
		} else {
			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it the selected idp
			DetailsFragment newFragment = new DetailsFragment();

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			Bundle args = new Bundle();
			args.putString(DetailsFragment.ARG_POSITION, type.getPath());
			newFragment.setArguments(args);

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.new_fragment_container, newFragment);
			// transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case R.id.action_settings:
			Intent i = new Intent(this, UserSettingActivity.class);
			startActivityForResult(i, RESULT_SETTINGS);
			break;
		case android.R.id.home:
			getActionBar().setDisplayHomeAsUpEnabled(false);
			TypesFragment typesFragment = (TypesFragment) getSupportFragmentManager()
					.findFragmentByTag("types");
			RepositoriesFragment repositoriesFragment = (RepositoriesFragment) getSupportFragmentManager()
					.findFragmentByTag("repositories");
			if ((typesFragment != null && !typesFragment.isVisible())
					|| (repositoriesFragment != null && !repositoriesFragment
							.isVisible()))
				onBackPressed();
			break;
		case R.id.filters:
			ArrayList<Filter> filters = DetailsFragment.getFilters();
			HashMap<String, Filter> chosenFilters = DetailsFragment
					.getChosenFilters();
			if (filters != null) {
				Intent intent = new Intent(this, FiltersActivity.class);
				intent.putParcelableArrayListExtra("filters", filters);
				intent.putExtra("chosenFilters", chosenFilters);
				intent.putExtra(FiltersActivity.ARG_POSITION,
						typeSelected.getPath());
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				// startActivity(intent);
				startActivityForResult(intent, 100);
			} else
				Toast.makeText(this, "No filters!", Toast.LENGTH_SHORT).show();
			break;
		case R.id.remove_filters:
			Log.d(TAG, "REMOVING ALL APPLIED FILTERS");
			Dialog d = DialogFactory
					.getDialog(
							this,
							"Confirm",
							"Are you sure that you want to remove all selected filters?",
							true, android.R.string.yes, android.R.string.no,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Log.d(TAG, "CLICK POSITIVE");
									DetailsFragment.resetChosenFilters();
									DetailsFragment.resetFilters();
									callUpdateDetailFragment(null, null);

								}
							}, null);
			d.show();
			break;
		}

		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SETTINGS:
			showUserSettings();
			break;

		case 100:
			if (resultCode == RESULT_OK)
				if (data.hasExtra("chosenFilter") && data.hasExtra("filters")) {
					callUpdateDetailFragment(
							data.getSerializableExtra("chosenFilter"),
							data.getParcelableArrayListExtra("filters"));
				}

			// break;
			// Log.d(TAG, "CIAOOOOOOO: " + resultCode);
			// if (resultCode == RESULT_OK && requestCode == 100) {
			// if (data.hasExtra("returnKey1")) {
			// Toast.makeText(this,
			// data.getExtras().getString("returnKey1"),
			// Toast.LENGTH_SHORT).show();
			// Toast.makeText(this,
			// data.getExtras().getString("returnKey2"),
			// Toast.LENGTH_SHORT).show();
			// }
			// }
		}

	}

	private void callUpdateDetailFragment(Serializable serializable,
			ArrayList<Parcelable> filters) {

		DetailsFragment detailsFragment = (DetailsFragment) getSupportFragmentManager()
				.findFragmentById(R.id.details_fragment);

		if (detailsFragment != null) {
			// If idps frag is available, we're in two-pane layout...

			// Call the update method in the IDPsFragment to update its content
			// idpsFragment.updateIdpsList(federation.getIdps(), position);
			Bundle args = new Bundle();
			args.putString(DetailsFragment.ARG_POSITION, typeSelected.getPath());
			args.putSerializable("chosenFilter", serializable);
			args.putParcelableArrayList("filters", filters);
			detailsFragment.updateFragment(args);
		} else {

			// If the frag is not available, we're in the one-pane layout and
			// must swap frags...

			// Create fragment and give it the selected idp
			DetailsFragment newFragment = new DetailsFragment();

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();

			Bundle args = new Bundle();
			args.putString(DetailsFragment.ARG_POSITION, typeSelected.getPath());
			if (serializable != null && filters != null) {
				args.putSerializable("chosenFilter", serializable);
				args.putParcelableArrayList("filters", filters);
			}
			newFragment.setArguments(args);

			// Replace whatever is in the fragment_container view with this
			// fragment,
			// and add the transaction to the back stack so the user can
			// navigate back
			transaction.replace(R.id.new_fragment_container, newFragment);
			// transaction.remove(getSupportFragmentManager()
			// .findFragmentById(R.id.new_fragment_container));
			// transaction.add(R.id.new_fragment_container, newFragment, null);
			// transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
	}

	private void showUserSettings() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);

		StringBuilder builder = new StringBuilder();

		builder.append("\n Username: "
				+ sharedPrefs.getString("prefUsername", "NULL"));

		builder.append("\n Send report:"
				+ sharedPrefs.getBoolean("prefSendReport", false));

		builder.append("\n Sync Frequency: "
				+ sharedPrefs.getString("prefSyncFrequency", "NULL"));

		// TextView settingsTextView = (TextView)
		// findViewById(R.id.textUserSettings);

		// settingsTextView.setText(builder.toString());
	}

	@Override
	public void onClick(View v) {
		Log.d(TAG, "Not sure tapped.");
		Dialog d = DialogFactory
				.getDialog(
						this,
						"Sugestion",
						"In case you do not belong to any of the shown Identity Federations, "
								+ "or you are not sure you do, you are welcome to register to our Identity Provider Open."
								+ " When registration procedure will be completed, you can sign in again and select GrIDP "
								+ "as your Identity Federation and then IDPOPEN GARR as your Identity Provider.",
						false, "Register", "Cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								String url = "https://idpopen.garr.it/register";
								Intent i = new Intent(Intent.ACTION_VIEW);
								i.setData(Uri.parse(url));
								startActivity(i);
							}
						}, null);
		d.show();

	}
}
