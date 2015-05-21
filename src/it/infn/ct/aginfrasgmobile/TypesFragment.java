package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.net.RESTLoader;
import it.infn.ct.aginfrasgmobile.net.RESTLoader.RESTResponse;
import it.infn.ct.aginfrasgmobile.pojos.TreeElement;
import it.infn.ct.aginfrasgmobile.pojos.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

public class TypesFragment extends ListFragment implements
		LoaderCallbacks<RESTLoader.RESTResponse> {

	private static final String TAG = TypesFragment.class.getSimpleName();
	// private static final String SERVER_URL =
	// "https://earthserver-sg.consorzio-cometa.it/glibrary/mountTree/";
	private static final String SERVER_URL = "/glibrary/mountTree/";
	private static final String SUFFIX = "?node=0";
	private AppPreferences _appPrefs;

	TreeAdapter adapter;
	ArrayList<Type> types = new ArrayList<Type>();

	OnTypesSelectedListener mCallback;

	// Interfaccia per l'invio di messagggi tra i fragments
	public interface OnTypesSelectedListener {
		/** Called by FederationsFragment when a list item is selected */
		public void onTypeSelected(Type type, int position);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_appPrefs = new AppPreferences(this.getActivity().getBaseContext());
		/** Creating array adapter to set data in listview */

		if (savedInstanceState == null) {
			String repoName = _appPrefs.getDefaultRepository();
			String shibCookie = _appPrefs.getCookie();
			if (!repoName.equals("")) {
				Uri uri = Uri.parse(_appPrefs.getGateway() + SERVER_URL
						+ repoName + SUFFIX);
				Log.i(TAG, uri.toString());

				Bundle args = new Bundle();
				args.putParcelable("ARGS_URI", uri);

				Bundle params = new Bundle();
				params.putString("Cookie", shibCookie);
				args.putParcelable("ARGS_PARAMS", params);

				getLoaderManager().restartLoader(1, args, this);
			} else {
				getListView().invalidateViews();
				setListShownNoAnimation(true);
				types = (ArrayList<Type>) savedInstanceState
						.getSerializable("types");
				for (Type t : types)
					if (!t.isLeaf()) {
						t.setLeafs(loadLeaf(t.getId()));
					}
				ArrayList<TreeElement> rederingElement = null;// = new
																// ArrayList<TreeElement>();

				int pad = 0;
				for (Type t : types) {
					rederingElement = rederTree(rederingElement, t, pad);
				}

				adapter.setData(rederingElement);
				types.clear();
				for (TreeElement treeElement : rederingElement) {
					types.add(treeElement.getType());
				}
			}
		}

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// adapter = new CustomListViewAdapter(getActivity().getBaseContext(),
		// R.layout.type_lv_item);
		adapter = new TreeAdapter(getActivity().getBaseContext(),
				R.layout.type_lv_item);

		getActivity().setTitle(_appPrefs.getRepoName());
		getListView().setAdapter(adapter);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstance");
		outState.putSerializable("types", types);

		super.onSaveInstanceState(outState);
	}

	@Override
	public void onStart() {
		super.onStart();

		if (getFragmentManager().findFragmentById(R.id.details_fragment) != null) {
			getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (OnTypesSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnTypesSelectedListener");
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

		int code = data.getCode();
		String json = data.getData();

		if (code == 200 && !json.equals("")) {
			Log.d(TAG, json);

			types = getTypesFromJson(json);

			for (Type t : types)
				if (!t.isLeaf()) {
					t.setLeafs(loadLeaf(t.getId()));
				}

			ArrayList<TreeElement> rederingElement = null;// = new
															// ArrayList<TreeElement>();

			int pad = 0;
			for (Type t : types) {
				rederingElement = rederTree(rederingElement, t, pad);
			}

			adapter.setData(rederingElement);
			types.clear();
			for (TreeElement treeElement : rederingElement) {
				types.add(treeElement.getType());
			}

			if (isResumed()) {
				setListShown(true);
			} else {
				setListShownNoAnimation(true);
			}

		} else
			Toast.makeText(
					this.getActivity().getBaseContext(),
					"Failed to load Federation data. Check your internet settings.",
					Toast.LENGTH_SHORT).show();
	}

	private ArrayList<TreeElement> rederTree(
			ArrayList<TreeElement> oldTreeElements, Type t, int pad) {
		if (oldTreeElements != null)
			oldTreeElements.add(new TreeElement(t, pad));
		else {
			oldTreeElements = new ArrayList<TreeElement>();
			oldTreeElements.add(new TreeElement(t, pad));
		}
		if (!t.isLeaf()) {
			pad += 20;
			ArrayList<Type> children = t.getLeafs();
			for (Type type : children)
				rederTree(oldTreeElements, type, pad);
		}
		return oldTreeElements;
	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.d(TAG, "" + position + id);
		Log.d(TAG, "" + l.toString() + v.toString());

		Log.d(TAG, "" + types.get(position).getPath());

		mCallback.onTypeSelected(types.get(position), position);
	}

	private ArrayList<Type> getTypesFromJson(String json) {
		ArrayList<Type> types = new ArrayList<Type>();
		try {
			JSONArray typesWrapper = (JSONArray) new JSONTokener(json)
					.nextValue();

			for (int i = 0; i < typesWrapper.length(); i++) {

				JSONObject typeJSON = typesWrapper.getJSONObject(i);
				if (!typeJSON.getString("text").equals("Demo")) {
					Type type = new Type(typeJSON.getString("text"),
							typeJSON.getBoolean("leaf"), typeJSON.getInt("id"),
							typeJSON.getString("path"));
					types.add(type);
				}
			}

		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.", e);
		}
		return types;
	}

	private ArrayList<Type> loadLeaf(int id) {
		HttpClient httpclient = new DefaultHttpClient();
		ArrayList<Type> leafs = new ArrayList<Type>();
		// Prepare a request object
		HttpGet httpget = new HttpGet(_appPrefs.getGateway() + SERVER_URL
				+ _appPrefs.getDefaultRepository() + "?node=" + id);

		httpget.setHeader("Cookie", _appPrefs.getCookie());
		// Execute the request
		HttpResponse response;
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);

			response = httpclient.execute(httpget);
			// Examine the response status
			Log.i(TAG, response.getStatusLine().toString());

			// Get hold of the response entity
			HttpEntity entity = response.getEntity();
			// If the response does not enclose an entity, there is no need
			// to worry about connection release

			if (entity != null) {

				// A Simple JSON Response Read
				InputStream instream = entity.getContent();
				String result = convertStreamToString(instream);
				Log.i(TAG, result);

				leafs = getTypesFromJson(result);

				// Closing the input stream will trigger connection release
				instream.close();

				for (Type t : leafs)
					if (!t.isLeaf()) {
						t.setLeafs(loadLeaf(t.getId()));
					}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return leafs;
	}

	private String convertStreamToString(InputStream instream) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				instream));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				instream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
