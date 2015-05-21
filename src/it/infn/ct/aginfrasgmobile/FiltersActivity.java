package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.net.RESTLoader;
import it.infn.ct.aginfrasgmobile.net.RESTLoader.RESTResponse;
import it.infn.ct.aginfrasgmobile.pojos.Filter;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class FiltersActivity extends FragmentActivity implements
		DialogInterface.OnClickListener, OnItemSelectedListener,
		OnItemLongClickListener, LoaderCallbacks<RESTLoader.RESTResponse> {

	private ProgressDialog pDialog;
	// private MenuItem mSpinnerItem;
	private static final String SERVER_URL = "/glibrary/test";
	private static final String STARTQUERYSTRING = "?filterData=";
	private String SUFFIX;
	private AppPreferences _appPrefs;
	private static final String TAG = FiltersActivity.class.getSimpleName();
	private ArrayList<Filter> filters = new ArrayList<Filter>();
	// private HashMap<String, ArrayList<String>> chosenFilters = new
	// HashMap<String, ArrayList<String>>();
	private HashMap<String, Filter> chosenFilters = new HashMap<String, Filter>();
	private String[] dataIndex;
	private String[] filterValues;
	// private ArrayAdapter<String> adapter;
	private FilterAdapter filterAdapter;
	// private Filter tmpFilter;

	/** Items entered by the user is stored in this ArrayList variable */
	ArrayList<String> list = new ArrayList<String>();
	ListView lv;
	private Spinner spinnerName;
	private Spinner spinnerValues;
	private Menu menu;
	private boolean forceReload = false;
	public static final String ARG_POSITION = "position";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_appPrefs = new AppPreferences(getBaseContext());
		setContentView(R.layout.activity_filters);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		// filters = (ArrayList<Filter>) intent.getSerializableExtra("filters");
		lv = (ListView) findViewById(R.id.listView1);
		if (savedInstanceState != null) {
			filters = savedInstanceState.getParcelableArrayList("filters");
			SUFFIX = savedInstanceState.getString(ARG_POSITION);
			filterAdapter = (FilterAdapter) savedInstanceState
					.getSerializable("filterAdapter");
			lv.setAdapter(filterAdapter);
			chosenFilters = (HashMap<String, Filter>) savedInstanceState
					.getSerializable("chosenFilter");
			if (!chosenFilters.isEmpty())
				findViewById(R.id.filters_empty).setVisibility(View.INVISIBLE);
		} else {
			Intent intent = getIntent();
			filters = intent.getParcelableArrayListExtra("filters");
			if (intent.hasExtra("chosenFilters"))
				chosenFilters = (HashMap<String, Filter>) intent
						.getSerializableExtra("chosenFilters");
			SUFFIX = intent.getStringExtra(ARG_POSITION);
			filterAdapter = new FilterAdapter(this, R.layout.filter_row);
			if (!chosenFilters.isEmpty()) {
				for (String key : chosenFilters.keySet()) {
					Filter tmp = chosenFilters.get(key);
					for (String s : tmp.getFilterList()) {
						filterAdapter.setData(new Filter(key, s));
					}
					// filterAdapter.setData(chosenFilters.get(key));
				}
				findViewById(R.id.filters_empty).setVisibility(View.INVISIBLE);
				lv.setAdapter(filterAdapter);
			} else {

			}

		}
		dataIndex = new String[filters.size()];
		for (int i = 0; i < filters.size(); i++) {
			dataIndex[i] = filters.get(i).getDataindex();
			filterValues = filters.get(i).getFilterList();
		}
		// lv = (ListView) findViewById(R.id.listView1);

		// filterAdapter = new FilterAdapter(this, R.layout.filter_row);

		lv.setOnItemLongClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		this.menu = menu;
		getMenuInflater().inflate(R.menu.filters, menu);

		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (!chosenFilters.isEmpty())
			showOption(R.id.clear_filters);
//			menu.findItem(R.id.clear_filters).setVisible(true);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			// super.onBackPressed();
			break;

		case R.id.action_settings:

			break;

		case R.id.add_new_filter:
			// tmpFilter = new Filter();
			if (chosenFilters.size() != 0 || forceReload) {
				_appPrefs = new AppPreferences(getBaseContext());
				// if (savedInstanceState != null) {
				String encodeChosenFilters = "";
				try {
					encodeChosenFilters = encodeChosenFilters();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Uri uri = Uri
						.parse(_appPrefs.getGateway() + SERVER_URL + SUFFIX
								+ "/" + STARTQUERYSTRING + encodeChosenFilters);
				Log.i(TAG, uri.toString());

				Bundle args = new Bundle();
				args.putParcelable("ARGS_URI", uri);
				Bundle params = new Bundle();
				params.putString("Cookie", _appPrefs.getCookie());
				args.putParcelable("ARGS_PARAMS", params);
				// getSupportLoaderManager().initLoader(1, args, this);
				getSupportLoaderManager().restartLoader(1, args, this);

				// createDialog("Loading", "Retriving filters...");
			} else
				createDialog("Select filter", dataIndex, filterValues);
			// addFilter();
			break;
		case R.id.clear_filters:
			createDialog(
					"Confirm",
					"Are you sure that you want to remove all selected filters?",
					null);
			break;

		}

		return true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos,
			long id) {
		Vibrator vibe = (Vibrator) getApplicationContext().getSystemService(
				Context.VIBRATOR_SERVICE);
		vibe.vibrate(50);
		Log.d(TAG, "Long clicked on position: " + pos);
		TextView tv1 = (TextView) view.findViewById(R.id.tv1);
		TextView tv2 = (TextView) view.findViewById(R.id.tv2);

		createDialog(
				"Confirm",
				"Are you sure that you want to remove filter: \n"
						+ tv1.getText() + "\n" + tv2.getText() + "?",
				(Filter) arg0.getItemAtPosition(pos));
		return false;
	}

	@Override
	public void finish() {
		// Prepare data intent
		Intent data = new Intent();
		// data.putExtra("returnKey1", "Swinging on a star. ");
		// data.putExtra("returnKey2", "You could be better then you are. ");
		data.putExtra("chosenFilter", chosenFilters);
		data.putParcelableArrayListExtra("filters", filters);
		// Activity finished ok, return the data
		setResult(RESULT_OK, data);
		super.finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		Log.d(TAG, "onSaveInstance");
		outState.putParcelableArrayList("filters", filters);
		outState.putString(ARG_POSITION, SUFFIX);
		outState.putSerializable("filterAdapter", filterAdapter);
		outState.putSerializable("chosenFilter", chosenFilters);
		super.onSaveInstanceState(outState);
	}

	private void createDialog(String title, String message, final Filter f) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setPositiveButton(android.R.string.yes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								if (f != null) {
									filterAdapter.remove(f);
									filterAdapter.notifyDataSetChanged();

									Filter removingFilter = chosenFilters.get(f
											.getDataindex());
									String[] tmpValues = removingFilter.removefilterValue(f
											.getFilterList()[f.getFilterList().length - 1]);
									if (tmpValues.length == 0)
										chosenFilters.remove(removingFilter
												.getDataindex());

									for (String key : chosenFilters.keySet()) {
										Log.d(TAG, chosenFilters.get(key)
												.toString());
									}
									if (chosenFilters.isEmpty()) {
										findViewById(R.id.filters_empty)
												.setVisibility(View.VISIBLE);
										hideOption(R.id.clear_filters);
										forceReload = true;
									}
								} else {
									chosenFilters = new HashMap<String, Filter>();
									filterAdapter.clear();
									findViewById(R.id.filters_empty)
											.setVisibility(View.VISIBLE);
									hideOption(R.id.clear_filters);
									forceReload = true;
								}
							}
						})
				.setNegativeButton(android.R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// User cancelled the dialog
							}
						});

		builder.setTitle(title);
		builder.show();
	}

	private void createDialog(String title, String[] data, String[] filterValues) {

		if (data != null && filterValues != null) {
			LayoutInflater inflater = getLayoutInflater();
			View dialoglayout = inflater.inflate(R.layout.filters_dialog, null);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// View view = (View) findViewById(R.layout.filters_dialog);
			builder.setView(dialoglayout);
			builder.setTitle(title);

			spinnerName = (Spinner) dialoglayout
					.findViewById(R.id.spinner_name);
			spinnerName.setAdapter(new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, data));
			spinnerName.setOnItemSelectedListener(this);
			spinnerValues = (Spinner) dialoglayout
					.findViewById(R.id.spinner2_values);
			spinnerValues
					.setAdapter(new ArrayAdapter<String>(this,
							android.R.layout.simple_spinner_dropdown_item,
							filterValues));
			spinnerValues.setOnItemSelectedListener(this);
			builder.setPositiveButton("OK", this);
			builder.setNegativeButton("Cancel", this);
			builder.show();
		}
		// if (data != null) {
		// // custom dialog
		// Dialog dialog = new Dialog(this);
		// dialog.setContentView(R.layout.filters_dialog);
		// dialog.setTitle(title);
		//
		// Spinner spinnerName = (Spinner)
		// dialog.findViewById(R.id.spinner_name);
		// spinnerName.setAdapter(new ArrayAdapter<String>(this,
		// android.R.layout.simple_spinner_dropdown_item, data));
		//
		//
		// dialog.show();
		// }
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if (findViewById(R.id.filters_empty).getVisibility() == View.VISIBLE) {
				findViewById(R.id.filters_empty).setVisibility(View.INVISIBLE);
				showOption(R.id.clear_filters);
			}
			int selectedFilterNameIndex = spinnerName.getSelectedItemPosition();
			int selectedFilterValueIndex = spinnerValues
					.getSelectedItemPosition();

			Filter f;
			if (!chosenFilters.containsKey(filters.get(selectedFilterNameIndex)
					.getDataindex())) {
				f = new Filter();
				f.setDataindex(filters.get(selectedFilterNameIndex)
						.getDataindex());
				f.setType(filters.get(selectedFilterNameIndex).getType());
				f.addFilterValue(filters.get(selectedFilterNameIndex)
						.getFilterList()[selectedFilterValueIndex]);
				//
				chosenFilters.put(filters.get(selectedFilterNameIndex)
						.getDataindex(), f);

			} else {
				f = chosenFilters.get(filters.get(selectedFilterNameIndex)
						.getDataindex());
				f.addFilterValue(filters.get(selectedFilterNameIndex)
						.getFilterList()[selectedFilterValueIndex]);
			}

			Filter filter2 = new Filter();
			filter2.setDataindex(filters.get(selectedFilterNameIndex)
					.getDataindex());
			filter2.addFilterValue(filters.get(selectedFilterNameIndex)
					.getFilterList()[selectedFilterValueIndex]);
			filterAdapter.setData(filter2);
			for (String s : chosenFilters.keySet()) {
				Log.d(TAG, "chosenFilter[" + s + "] = "
						+ chosenFilters.get(s).toString());
			}
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			break;

		default:

			break;
		}
		lv.setAdapter(filterAdapter);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {

		switch (parent.getId()) {
		case R.id.spinner_name:
			Log.d(TAG, "Selected Filter: " + dataIndex[pos]);
			filterValues = filters.get(pos).getFilterList();
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item, filterValues);
			spinnerValues.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			break;

		case R.id.spinner2_values:
			Log.d(TAG, "Selected Value: " + filterValues[pos]);
			break;
		default:
			break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	private class FilterAdapter extends ArrayAdapter<Filter> implements
			Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		Context context;

		public FilterAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.context = context;
		}

		public void setData(Filter f) {
			// clear();
			if (f != null) {
				// for (Filter p : data) {
				add(f);
			}
			// }
		}

		/* private view holder class */
		private class ViewHolder {
			TextView filterName;
			TextView filterValues;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			Filter f = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			// if (convertView == null) {
			convertView = mInflater.inflate(R.layout.filter_row, null);
			holder = new ViewHolder();
			holder.filterName = (TextView) convertView.findViewById(R.id.tv1);
			holder.filterValues = (TextView) convertView.findViewById(R.id.tv2);
			// holder.filterValues.setOnItemSelectedListener(this);

			convertView.setTag(holder);

			holder.filterName.setText(f.getDataindex());
			holder.filterValues
					.setText(f.getFilterList()[f.getFilterList().length - 1]);

			return convertView;
		}

	}

	private String encodeChosenFilters() throws UnsupportedEncodingException {

		String result = "[";
		for (String key : chosenFilters.keySet()) {
			if (!result.equals("["))
				result += ",";
			result += "{\"field\":\"" + key + "\",\"data\":{\"type\":\""
					+ chosenFilters.get(key).getType() + "\",\"value\":[\"";
			for (int i = 0; i < chosenFilters.get(key).getFilterList().length; i++) {
				if (i == 0)
					result += chosenFilters.get(key).getFilterList()[i] + "\"";
				else
					result += ",\"" + chosenFilters.get(key).getFilterList()[i]
							+ "\"";
			}
			result += "]}}";

		}
		result += "]";
		Log.d(TAG, "Encoded query filters: " + result);
		// Log.d(TAG, URLEncoder.encode(result, "utf-8"));
		return URLEncoder.encode(result, "utf-8");

	}

	@Override
	public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
		pDialog = new ProgressDialog(this);
		pDialog.setTitle("Loading...");
		pDialog.setMessage("Retrieving filters...");
		pDialog.setIndeterminate(false);
		pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pDialog.setCancelable(true);
		pDialog.setCanceledOnTouchOutside(false);
		pDialog.show();
		if (args != null && args.containsKey("ARGS_URI")) {
			Uri action = args.getParcelable("ARGS_URI");
			if (args.containsKey("ARGS_PARAMS")) {

				Bundle params = args.getParcelable("ARGS_PARAMS");

				return new RESTLoader(getBaseContext(),
						RESTLoader.HTTPVerb.GET, action, params);
			} else {
				return new RESTLoader(getBaseContext(),
						RESTLoader.HTTPVerb.GET, action);
			}
		}
		return null;
	}

	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
		int code = data.getCode();
		String json = data.getData();
		// Log.d(TAG, "onLoadFinished with code: " + code + " and json: " +
		// json);
		pDialog.dismiss();
		if (code == 200 && !json.equals("")) {
			updateFilfersValues(json);
		}

		createDialog("Select filter", dataIndex, filterValues);
	}

	private void updateFilfersValues(String json) {

		try {
			JSONArray filtersJsonArray = (JSONArray) new JSONTokener(json)
					.nextValue();
			Log.d(TAG, "filters.size(): " + filters.size());
			Log.d(TAG,
					"filtersJsonArray.length(): " + filtersJsonArray.length());
			for (int i = 0; i < filtersJsonArray.length(); i++) {
				// Log.d(TAG, fitersJsonArray.get(i).toString());
				JSONArray filterValuesJsonArray = filtersJsonArray
						.getJSONArray(i);
				ArrayList<String> tmpFilterList = new ArrayList<String>();
				for (int j = 0; j < filterValuesJsonArray.length(); j++) {
					Log.d(TAG,
							"Received Values: "
									+ filterValuesJsonArray.getString(j));
					tmpFilterList.add(filterValuesJsonArray.getString(j)
							.replace("[\"", "").replace("\"]", ""));
				}
				// Filter f = filters.get(i);
				Filter f = filters.remove(i);
				f.setFilterList(tmpFilterList.toArray(new String[tmpFilterList
						.size()]));

				// filters.set(i, f);
				filters.add(i, f);
				filterValues = filters.get(i).getFilterList();
				for (int j = 0; j < filters.get(i).getFilterList().length; j++) {
					Log.d(TAG, filters.get(i).getFilterList()[j]);
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		// TODO Auto-generated method stub

	}

	private void hideOption(int id) {
		MenuItem item = menu.findItem(id);
		item.setVisible(false);
	}

	private void showOption(int id) {
		MenuItem item = menu.findItem(id);
		item.setVisible(true);
	}

	private void setOptionTitle(int id, String title) {
		MenuItem item = menu.findItem(id);
		item.setTitle(title);
	}

	private void setOptionIcon(int id, int iconRes) {
		MenuItem item = menu.findItem(id);
		item.setIcon(iconRes);
	}
}
