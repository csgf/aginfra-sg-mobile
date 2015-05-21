package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.net.RESTLoader;
import it.infn.ct.aginfrasgmobile.net.RESTLoader.RESTResponse;
import it.infn.ct.aginfrasgmobile.pojos.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;


public class RepositoriesFragment extends ListFragment implements LoaderCallbacks<RESTLoader.RESTResponse> {

	private static final String TAG = RepositoriesFragment.class.getSimpleName();
	private static final String SERVER_URL = "http://glibrary.ct.infn.it/glibrary_new/repository.json";
	private AppPreferences _appPrefs;
	
	private ArrayList<Repository> repositories=new ArrayList<Repository>();
	private ArrayAdapter<Repository> adapter;
	private int selectedRepo = -1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_appPrefs = new AppPreferences(this.getActivity().getBaseContext());

		if(_appPrefs.getRepos().equals("")){
			Uri uri = Uri.parse(SERVER_URL);
			Log.i(TAG, uri.toString());

			Bundle args = new Bundle();
			args.putParcelable("ARGS_URI", uri);

			getLoaderManager().initLoader(1, args, this);
		} else
			repositories = getRepositoriesFromJson(_appPrefs.getRepos());
		
		adapter = new ArrayAdapter<Repository>(this.getActivity().getBaseContext(),
				android.R.layout.simple_list_item_single_choice, android.R.id.text1, repositories);

		setListAdapter(adapter);
		
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
       
  		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
  		updateListView();
  		
		selectedRepo = getListView().getCheckedItemPosition();
		Log.i(TAG, "Selected Repo: "+getListView().getCheckedItemPosition());
				
	}

	private void updateListView() {
		
		if (repositories.size() != 0 ) {
			if(adapter.isEmpty())	
				adapter.addAll(repositories);

			getListView().setAdapter(adapter);
			String defaultRepo = _appPrefs.getDefaultRepository();
			for (int i = 0; i < repositories.size(); i++)
				if (!defaultRepo.equals("")
						&& repositories.get(i).getRepository()
								.equals(defaultRepo)) {
					selectedRepo = i;
					break;
				}

			if (selectedRepo != -1)
				getListView().setItemChecked(selectedRepo, true);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.d(TAG, ""+position+id);
		Log.d(TAG, ""+l.toString()+v.toString());
		
		Log.d(TAG,""+ l.getAdapter().getItem(position));

		_appPrefs.saveDefaultRepo(repositories.get(position).getRepository());
		TabHost tHost = (TabHost) getActivity().findViewById(android.R.id.tabhost);
		tHost.setCurrentTab(0);
		DetailsFragment detailsFragment = (DetailsFragment) getFragmentManager()
				.findFragmentById(R.id.details_fragment);
		if(detailsFragment!=null){
			detailsFragment.updateFragment(null);		
		} else {
			DetailsFragment.resetFilters();
			DetailsFragment.resetChosenFilters();
		}
			
	}

	@Override
	public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
		if (args != null && args.containsKey("ARGS_URI")) {
			Uri action = args.getParcelable("ARGS_URI");
			if (args.containsKey("ARGS_PARAMS")) {

				Bundle params = args.getParcelable("ARGS_PARAMS");

				return new RESTLoader(this.getActivity().getBaseContext(),
						RESTLoader.HTTPVerb.GET, action, params);
			} else {
				return new RESTLoader(this.getActivity().getBaseContext(),
						RESTLoader.HTTPVerb.GET, action);
			}
		}

		return null;
	}

	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
		
		Log.d(TAG,"CODE: "+data.getCode()+"\nDATA: " + data.getData());
		
		int code = data.getCode();
		String json = data.getData();
				
		if (code == 200 && !json.equals("")) {

			repositories = getRepositoriesFromJson(json);
			adapter.clear();
	
			updateListView();
            
		} else {
			Toast.makeText(
					this.getActivity().getBaseContext(),
					"Failed to load Federation data. Check your internet settings.",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		// TODO Auto-generated method stub
		
	}

	private ArrayList<Repository> getRepositoriesFromJson(String json) {
		ArrayList<Repository> repositories = new ArrayList<Repository>();
		Resources res = getResources();
		List<String> defRepositories = Arrays.asList(res.getStringArray(R.array.agINFRA_repos));
		try {
			JSONArray repositoriesWrapper = (JSONArray) new JSONTokener(json)
					.nextValue();
			_appPrefs.saveRepos(repositoriesWrapper.toString());
			for (int i = 0; i < repositoriesWrapper.length(); i++) {
				
				JSONObject repositoryJSON = repositoriesWrapper.getJSONObject(i);
				Repository repo = new Repository(repositoryJSON.getString("repository"), repositoryJSON.getString("rep_name"), repositoryJSON.getString("thumb"));
				
				if(defRepositories.contains(repo.getRepositoryName())){
					repositories.add(repo);
					_appPrefs.saveDefaultRepo(repo.getRepository());
				}
			}
			
		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.", e);
		}
		return repositories;
	}

}
