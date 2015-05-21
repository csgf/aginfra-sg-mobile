package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.pojos.DummyTabContent;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;


public class MenuFragment extends Fragment implements OnTabChangeListener {

	private static final String TAG = MenuFragment.class.getSimpleName();
	private AppPreferences _appPrefs;
	protected static boolean isMenuShown;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_appPrefs = new AppPreferences(this.getActivity().getBaseContext());

		View viewRoot = inflater.inflate(R.layout.tab_menu_layout, container,
				false);
		
		TabHost tHost = (TabHost) viewRoot.findViewById(android.R.id.tabhost);
		tHost.setup();

		tHost.setOnTabChangedListener(this);

		TabHost.TabSpec tSpecTypes = tHost.newTabSpec("types");
		tSpecTypes.setIndicator("Types");
		tSpecTypes.setContent(new DummyTabContent(getActivity()
				.getBaseContext()));
		tHost.addTab(tSpecTypes);
		
		TabHost.TabSpec tSpecRepositories = tHost.newTabSpec("repositories");
		tSpecRepositories.setIndicator("Repositories");
		tSpecRepositories.setContent(new DummyTabContent(getActivity()
				.getBaseContext()));
		tHost.addTab(tSpecRepositories);
		
		return viewRoot;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);

	}

	@Override
	public void onTabChanged(String tabId) {
		FragmentManager fm = getFragmentManager();
		TypesFragment typesFragment = (TypesFragment) fm
				.findFragmentByTag("types");
		RepositoriesFragment repositoriesFragment = (RepositoriesFragment) fm
				.findFragmentByTag("repositories");
		
		AppleFragment appleFragment = (AppleFragment) fm
				.findFragmentByTag("apple");
		FragmentTransaction ft = fm.beginTransaction();

		/** Detaches the repositoriesfragment if exists */
		if (repositoriesFragment != null)
			ft.detach(repositoriesFragment);

		/** Detaches the typesfragment if exists */
		if (typesFragment != null)
			ft.detach(typesFragment);

		/** Detaches the applefragment if exists */
		if (appleFragment != null)
			ft.detach(appleFragment);

		/** If current tab is types */
		if (tabId.equalsIgnoreCase("types")) {

			if (typesFragment == null) {
				/**
				 * Create RepositoriesFragment and adding to fragmenttransaction
				 */
				ft.add(R.id.tab_1, new TypesFragment(), "types");
			} else {
				/**
				 * Bring to the front, if already exists in the
				 * fragmenttransaction
				 */
				ft.attach(typesFragment);
			}

		} else if (tabId.equalsIgnoreCase("repositories")) {
			/** If current tab is apple */
			if (repositoriesFragment == null) {
				/** Create AppleFragment and adding to fragmenttransaction */
				ft.add(R.id.tab_1, new RepositoriesFragment(), "repositories");
			} else {
				/**
				 * Bring to the front, if already exists in the
				 * fragmenttransaction
				 */
				ft.attach(repositoriesFragment);
			}
		} else {
			/** If current tab is apple */
			if (appleFragment == null) {
				/** Create AppleFragment and adding to fragmenttransaction */
				ft.add(R.id.tab_1, new AppleFragment(), "apple");
			} else {
				/**
				 * Bring to the front, if already exists in the
				 * fragmenttransaction
				 */
				ft.attach(appleFragment);
			}
		}
		ft.commit();

	}

}
