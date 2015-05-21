package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.net.RESTLoader;
import it.infn.ct.aginfrasgmobile.net.RESTLoader.RESTResponse;
import it.infn.ct.aginfrasgmobile.pojos.Column;
import it.infn.ct.aginfrasgmobile.pojos.Field;
import it.infn.ct.aginfrasgmobile.pojos.Filter;
import it.infn.ct.aginfrasgmobile.pojos.Metadata;
import it.infn.ct.aginfrasgmobile.pojos.NewProduct;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class DetailsFragment extends ListFragment implements
		LoaderCallbacks<RESTLoader.RESTResponse>, OnScrollListener {

	private static final String TAG = DetailsFragment.class.getSimpleName();
	// private static final String PRODUCT_URL =
	// "https://earthserver-sg.consorzio-cometa.it/glibrary/glib";
	private static final String PRODUCT_URL = "/glibrary/glib";
	// private static final String METADATA_URL =
	// "https://earthserver-sg.consorzio-cometa.it/glibrary/metadata";
	private static final String METADATA_URL = "/glibrary/metadata";
	private String SUFFIX = "";
	private String shibCookie;
	private AppPreferences _appPrefs;

	private int pageNumber = 0;
	private int start = 0;
	private boolean loadingMore = false;

	NewMyAdapter newAdapter;
	View footerView;
	// ArrayList<Product> products = new ArrayList<Product>();
	ArrayList<NewProduct> products = new ArrayList<NewProduct>();
	ArrayList<Column> columns = new ArrayList<Column>();
	ArrayList<Column> visibleColumns = new ArrayList<Column>();
	Metadata metadata;
	private Menu menu;
	private boolean showRemoveFilters;
	private int total;
	private static HashMap<String, Filter> chosenFilter = new HashMap<String, Filter>();
	private static ArrayList<Filter> filters = new ArrayList<Filter>();
	public static final String ARG_POSITION = "position";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_appPrefs = new AppPreferences(this.getActivity().getBaseContext());

		setHasOptionsMenu(true);

		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		// setListShownNoAnimation(true);
		// Bundle args = getArguments();
		// if (args != null) {
		// //
		// String path = args.getString(ARG_POSITION);
		// SUFFIX = path;
		// Log.d(TAG, path);
		//
		// shibCookie = _appPrefs.getCookie();
		// updateFragment(args);
		// // if (!shibCookie.equals("")) {
		// // Uri uri = Uri.parse(METADATA_URL + path);
		// // Log.i(TAG, uri.toString());
		// //
		// // Bundle args1 = new Bundle();
		// // args1.putParcelable("ARGS_URI", uri);
		// //
		// // Bundle params = new Bundle();
		// // params.putString("Cookie", shibCookie);
		// // args1.putParcelable("ARGS_PARAMS", params);
		// //
		// // getLoaderManager().initLoader(1, args1, this);
		// // }
		//
		// }
		// // else if (mCurrentPosition != -1) {
		// // // Set article based on saved instance state defined during
		// // // onCreateView
		// // // updateIdpsList(mCurrentPosition);
		// // }
		getListView().setOnScrollListener(this);
	}

	public void updateFragment(Bundle args) {
		if (args != null) {
			setListShownNoAnimation(false);
			String path = args.getString(ARG_POSITION);
			SUFFIX = path;
			Log.d(TAG, path);
			newAdapter.clear();
			products.clear();
			shibCookie = _appPrefs.getCookie();
			if (!shibCookie.equals("")) {

				String parsedFilter = "";
				if (args.getSerializable("chosenFilter") != null) {
					filters = args.getParcelableArrayList("filters");
					parsedFilter = parseChosenFilter(args
							.getSerializable("chosenFilter"));
					SUFFIX += "/?" + parsedFilter;

					showRemoveFilters = true;
				}
				Uri uri = Uri.parse(_appPrefs.getGateway() + METADATA_URL
						+ path + "/?" + parsedFilter);
				Log.i(TAG, uri.toString());

				Bundle args1 = new Bundle();
				args1.putParcelable("ARGS_URI", uri);

				Bundle params = new Bundle();
				params.putString("Cookie", shibCookie);
				args1.putParcelable("ARGS_PARAMS", params);
				getLoaderManager().restartLoader(1, args1, this);
			}
		} else {
			Log.d(TAG, "**ELSE**");
			if (footerView.getVisibility() == View.VISIBLE)
				footerView.setVisibility(View.GONE);
			filters = new ArrayList<Filter>();
			chosenFilter = new HashMap<String, Filter>();
			newAdapter.clear();
		}
		start = 0;
	}

	// @Override
	// public void onAttach(Activity activity) {
	// Log.d(TAG, "onAttach("+activity.getLocalClassName()+")");
	// super.onAttach(activity);
	// }

	private String parseChosenFilter(Serializable serializable) {
		chosenFilter = (HashMap<String, Filter>) serializable;
		String result = "";
		Iterator<String> iterator = chosenFilter.keySet().iterator();
		try {
			int i = 0;
			while (iterator.hasNext()) {
				if (!result.equals(""))
					result += "&";
				String tmp = URLEncoder.encode("filter[" + i + "]", "utf-8");
				String key = (String) iterator.next();
				Filter f = chosenFilter.get(key);
				result += tmp + URLEncoder.encode("[field]", "utf-8") + "="
						+ key + "&" + tmp
						+ URLEncoder.encode("[data][type]", "utf-8") + "="
						+ f.getType() + "&";
				for (int j = 0; j < f.getFilterList().length; j++) {
					if (j != 0)
						result += "&";
					result += tmp + URLEncoder.encode("[data][value]", "utf-8")
							+ "="
							+ URLEncoder.encode(f.getFilterList()[j], "utf-8");
				}
				i++;
			}

			Log.d(TAG, "Parsed chosenFilter: " + result);

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result = "";
		}
		return result;

	}

	// private void loadMoreData(int start) {
	//
	// loadMoreData(start, 50);
	//
	// }
	private void loadMoreData() {

		loadMoreData(start, 50);

	}

	private void loadMoreData(int start, int limit) {
		// Toast.makeText(getActivity(), "Load More Data", Toast.LENGTH_SHORT)
		// .show();
		Uri uri;
		if (SUFFIX.contains("?filter")) {
			uri = Uri.parse(_appPrefs.getGateway()
					+ PRODUCT_URL
					+ SUFFIX.replace("?", "?start=" + start + "&limit=" + limit
							+ "&"));
		} else {
			uri = Uri.parse(_appPrefs.getGateway() + PRODUCT_URL + SUFFIX
					+ "?start=" + start + "&limit=" + limit);
		}
		Log.i(TAG, uri.toString());

		Bundle args1 = new Bundle();
		args1.putParcelable("ARGS_URI", uri);

		Bundle params = new Bundle();
		params.putString("Cookie", _appPrefs.getCookie());
		args1.putParcelable("ARGS_PARAMS", params);
		getLoaderManager().restartLoader(2, args1, this);
		// getLoaderManager().initLoader(2, args1, this);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		newAdapter = new NewMyAdapter(getActivity().getBaseContext(),
				R.layout.type_lv_item);

		footerView = ((LayoutInflater) getActivity().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.loading_footer, null, false);

		getListView().addFooterView(footerView);
		getListView().setAdapter(newAdapter);

		if (savedInstanceState == null) {
			setListShownNoAnimation(true);
			Bundle args = getArguments();
			if (args != null) {
				//
				String path = args.getString(ARG_POSITION);
				SUFFIX = path;
				Log.d(TAG, path);

				shibCookie = _appPrefs.getCookie();
				updateFragment(args);

			}
			getListView().removeFooterView(footerView);
		} else {
			getListView().invalidateViews();
			setListShownNoAnimation(true);
			products = (ArrayList<NewProduct>) savedInstanceState
					.getSerializable("products");
			metadata = (Metadata) savedInstanceState
					.getSerializable("metadata");
			columns = savedInstanceState.getParcelableArrayList("columns");
			SUFFIX = savedInstanceState.getString("suffix");
			pageNumber = savedInstanceState.getInt("pagNumber", 0);
			visibleColumns = getVisibleColumn();
			newAdapter.clear();
			newAdapter.setData(products);
			total = savedInstanceState.getInt("total");
			start = savedInstanceState.getInt("start");
			loadingMore = savedInstanceState.getBoolean("loadingMore");
			if ((total - newAdapter.getCount()) <= 0) {
				getListView().removeFooterView(footerView);
			}

		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		Log.d(TAG, "CIAOOOOOOO: " + resultCode);

		super.onActivityResult(requestCode, resultCode, data);
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
	public void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		Log.d(TAG, "" + position + id);
		Log.d(TAG, "" + l.toString() + v.toString());

		// Log.d(TAG,""+ products.get(position).getVarLongName());

		Intent intent = new Intent(getActivity().getBaseContext(),
				ProductsDetails.class);
		intent.putExtra("product", products.get(position));
		intent.putExtra("metadata", metadata);
		intent.putParcelableArrayListExtra("columns", columns);
		startActivity(intent);

	}

	@Override
	public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse data) {
		int code = data.getCode();
		String json = data.getData();

		if (code == 200 && !json.equals("")) {

			if (loader.getId() == 1) {
				// Log.d(TAG, json);
				metadata = getMetadataFromJson(json);
				columns = getColumnsFromJson(json);
				visibleColumns = getVisibleColumn();
				if (filters.size() == 0)
					filters = getFiltersFromJson(json);
				Uri uri = Uri.parse(_appPrefs.getGateway() + PRODUCT_URL
						+ SUFFIX);
				Log.i(TAG, uri.toString());

				Bundle args1 = new Bundle();
				args1.putParcelable("ARGS_URI", uri);

				Bundle params = new Bundle();
				params.putString("Cookie", shibCookie);
				args1.putParcelable("ARGS_PARAMS", params);
				getLoaderManager().restartLoader(2, args1, this);
			} else if (loader.getId() == 2) {
				// Log.d(TAG, json);
				// products = getNewProductsFromJson(json);
				Log.d(TAG, "Adding new value... pageNumber: " + pageNumber);

				Log.d(TAG, "products.size(): " + products.size());
				ArrayList<NewProduct> tmp = getNewProductsFromJson(json);
				if (tmp.size() > 0) {
					products.addAll(tmp);
					// products = getNewProductsFromJson(json);
					pageNumber++;
					Log.d(TAG,
							"Adding completed products.size(): "
									+ products.size());
					newAdapter.setData(products);

					// if ((getActivity().findViewById(R.id.lodingMorePb) !=
					// null)
					// && (getActivity().findViewById(R.id.lodingMorePb)
					// .getVisibility() == View.VISIBLE)) {
					// getActivity().findViewById(R.id.lodingMorePb)
					// .setVisibility(View.INVISIBLE);
					// getActivity().findViewById(R.id.loadMore_btn)
					// .setVisibility(View.VISIBLE);
					// }
					if (isResumed()) {
						setListShown(true);
					} else {
						setListShownNoAnimation(true);
					}
					total = getTotal(json);
					// if (tmp.size() <= 50 || getTotal(json) > 0) {
					// if ((getTotal(json) - newAdapter.getCount()) > 0) {
					if ((total - newAdapter.getCount()) > 0) {
						loadingMore = false;
						getListView().addFooterView(footerView);
						// if (footerView.getVisibility() == View.GONE)
						// footerView.setVisibility(View.VISIBLE);
						// getListView().addFooterView(footerView);
						Log.d(TAG, "THEN tmp.size(): " + tmp.size());
					} else {
						// if (footerView.getVisibility() == View.VISIBLE)
						// footerView.setVisibility(View.GONE);
						// getListView().removeFooterView(footerView);
						getListView().removeFooterView(footerView);
						Log.d(TAG, "ELSE tmp.size(): " + tmp.size());
					}
				}
				onPrepareOptionsMenu(menu);
			}

		}
	}

	private int getTotal(String json) {
		int total = 0;
		try {
			JSONObject productsWrapper = (JSONObject) new JSONTokener(json)
					.nextValue();
			total = productsWrapper.getJSONArray("total").getInt(0);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return total;
	}

	@Override
	public void onLoaderReset(Loader<RESTResponse> arg0) {
		// TODO Auto-generated method stub

	}

	private ArrayList<Filter> getFiltersFromJson(String json) {
		ArrayList<Filter> filters = new ArrayList<Filter>();

		try {
			JSONObject objectWrapper = (JSONObject) new JSONTokener(json)
					.nextValue();

			JSONArray filtersJSONArray = (JSONArray) objectWrapper
					.getJSONArray("filters");
			for (int i = 0; i < filtersJSONArray.length(); i++) {
				JSONObject filterJSON = filtersJSONArray.getJSONObject(i);
				Filter f = new Filter();
				if (filterJSON.has("filterList")) {
					JSONArray tmp = filterJSON.getJSONArray("filterList");
					ArrayList<String> tmpFilterList = new ArrayList<String>();
					for (int j = 0; j < tmp.length(); j++) {

						tmpFilterList.add(tmp.getString(j).replace("[\"", "")
								.replace("\"]", ""));
					}
					f.setFilterList(tmpFilterList
							.toArray(new String[tmpFilterList.size()]));
					f.setLabelField(filterJSON.getString("labelField"));
					f.setType(filterJSON.getString("type"));
					f.setDataindex(filterJSON.getString("dataIndex"));
				} else {
					f.setType(filterJSON.getString("type"));
					f.setDataindex(filterJSON.getString("dataIndex"));
				}
				filters.add(f);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e(TAG, "Failed to parse JSON.", e);
		}

		return filters;
	}

	protected static ArrayList<Filter> getFilters() {
		return filters;
	}

	protected static HashMap<String, Filter> getChosenFilters() {
		return chosenFilter;
	}

	protected static void resetFilters() {
		filters = new ArrayList<Filter>();
	}

	protected static void resetChosenFilters() {
		chosenFilter = new HashMap<String, Filter>();
	}

	private ArrayList<Column> getColumnsFromJson(String json) {
		ArrayList<Column> columns = new ArrayList<Column>();
		try {
			JSONObject productsWrapper = (JSONObject) new JSONTokener(json)
					.nextValue();

			JSONArray columnsJSONArray = (JSONArray) productsWrapper
					.getJSONArray("columns");
			for (int i = 0; i < columnsJSONArray.length(); i++) {
				JSONObject columnJSON = columnsJSONArray.getJSONObject(i);
				Column c;
				if (columnJSON.has("id"))
					c = new Column(columnJSON.getString("header"),
							columnJSON.getBoolean("sortable"),
							columnJSON.getString("dataIndex"),
							columnJSON.getInt("width"),
							columnJSON.getString("colType"),
							columnJSON.getString("align"),
							columnJSON.getBoolean("hidden"),
							columnJSON.getString("css"),
							columnJSON.getString("id"));
				else
					c = new Column(columnJSON.getString("header"),
							columnJSON.getBoolean("sortable"),
							columnJSON.getString("dataIndex"),
							columnJSON.getInt("width"),
							columnJSON.getString("colType"),
							columnJSON.getString("align"),
							columnJSON.getBoolean("hidden"),
							columnJSON.getString("css"));

				columns.add(c);
			}

		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.", e);
		}

		return columns;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "onSaveInstance");
		outState.putSerializable("products", products);
		outState.putSerializable("metadata", metadata);
		outState.putParcelableArrayList("columns", columns);
		outState.putString("suffix", SUFFIX);
		outState.putInt("pagNumber", pageNumber);
		outState.putInt("total", total);
		outState.putInt("start", start);
		outState.putBoolean("loadingMore", loadingMore);
		super.onSaveInstanceState(outState);
	}

	private Metadata getMetadataFromJson(String json) {

		Metadata metadata = new Metadata();
		try {
			JSONObject jsonWrapper = (JSONObject) new JSONTokener(json)
					.nextValue();
			JSONObject metadataWrapper = jsonWrapper.getJSONObject("metadata");

			metadata.setTotalProperty(metadataWrapper
					.getString("totalProperty"));
			metadata.setRoot(metadataWrapper.getString("root"));
			metadata.setCustom(metadataWrapper.getString("custom"));

			JSONArray fieldsWrapper = (JSONArray) metadataWrapper
					.getJSONArray("fields");
			ArrayList<Field> fieldList = new ArrayList<Field>();
			for (int i = 0; i < fieldsWrapper.length(); i++) {

				JSONObject fieldJSON = fieldsWrapper.getJSONObject(i);
				Iterator<?> jsonKeys = fieldJSON.keys();
				HashMap<String, String> entries = new HashMap<String, String>();
				Field f = new Field();
				while (jsonKeys.hasNext()) {
					String key = (String) jsonKeys.next();
					String value = fieldJSON.getString(key);

					entries.put(key, value);

				}
				f.setField(entries);
				fieldList.add(f);
			}
			metadata.setFields(fieldList);

		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.", e);
		}
		return metadata;

	}

	private ArrayList<NewProduct> getNewProductsFromJson(String json) {
		ArrayList<NewProduct> newProducts = new ArrayList<NewProduct>();
		try {
			JSONObject productsWrapper = (JSONObject) new JSONTokener(json)
					.nextValue();

			JSONArray productsArray = (JSONArray) productsWrapper
					.getJSONArray(metadata.getRoot());
			Log.d(TAG, "JSONArray.size(): " + productsArray.length());
			start += productsArray.length();
			for (int i = 0; i < productsArray.length(); i++) {
				JSONObject productJSON = productsArray.getJSONObject(i);
				NewProduct p = new NewProduct();
				for (Field f : metadata.getFields()) {
					HashMap<String, String> entries = f.getField();
					Object[] obj = new Object[2];
					if (entries.containsKey("type")) {
						obj[0] = entries.get("type");
					} else {
						obj[0] = "String";
					}
					obj[1] = productJSON.get(entries.get("mapping"));
					p.setEntry(entries.get("name"), obj);
				}
				newProducts.add(p);
			}

			Log.d(TAG, "#@#@total: "
					+ productsWrapper.getJSONArray("total").get(0));

		} catch (JSONException e) {
			Log.e(TAG, "Failed to parse JSON.", e);
		}

		return newProducts;
	}

	private ArrayList<Column> getVisibleColumn() {
		ArrayList<Column> visibleColumns = new ArrayList<Column>();
		int i = 0;
		for (Column column : columns) {

			if (column.getId().contains("thumb")
					|| column.getDataIndex().contains("FileName")
					|| column.getDataIndex().contains("FileType"))
				// || column.getDataIndex().contains("Size"))
				visibleColumns.add(column);
			else if (i < 5 && !column.isHidden()) {
				visibleColumns.add(column);
				i++;
			}

		}
		return visibleColumns;
	}

	private class NewMyAdapter extends ArrayAdapter<NewProduct> {

		Context context;

		public NewMyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.context = context;
		}

		public void setData(List<NewProduct> data) {
			clear();
			if (data != null) {
				for (NewProduct p : data) {
					add(p);
				}
			}
		}

		/* private view holder class */
		private class ViewHolder {
			ImageView imageView;
			TextView fileName, fileType;
			TextView lbl1, lbl6;
			TextView label;
			// TextView[] labels = new TextView[5];
			TextView value;
			// TextView[] values = new TextView[5];
			// final List<Column> visibleColumns = columns.subList(0, 4);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			NewProduct p = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.product_lv_item, null);
				holder = new ViewHolder();
				holder.fileName = (TextView) convertView
						.findViewById(R.id.fileName);
				holder.value = (TextView) convertView
						.findViewById(R.id.var_long_name);
				// holder.values[0] = (TextView) convertView
				// .findViewById(R.id.var_long_name);
				// holder.values[1] = (TextView) convertView
				// .findViewById(R.id.period);
				// holder.values[2] = (TextView) convertView
				// .findViewById(R.id.day);
				// holder.values[3] = (TextView) convertView
				// .findViewById(R.id.month);
				// holder.values[4] = (TextView) convertView
				// .findViewById(R.id.year);
				//
				holder.fileType = (TextView) convertView
						.findViewById(R.id.fileType);
				holder.label = (TextView) convertView.findViewById(R.id.lbl1);
				// holder.labels[0] = (TextView) convertView
				// .findViewById(R.id.lbl1);
				// holder.labels[1] = (TextView) convertView
				// .findViewById(R.id.lbl2);
				// holder.labels[2] = (TextView) convertView
				// .findViewById(R.id.lbl3);
				// holder.labels[3] = (TextView) convertView
				// .findViewById(R.id.lbl4);
				// holder.labels[4] = (TextView) convertView
				// .findViewById(R.id.lbl5);
				holder.lbl6 = (TextView) convertView.findViewById(R.id.lbl6);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.folderImage);
				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();
			Iterator<Column> iterator = visibleColumns.iterator();
			// for (Column c : holder.visibleColumns) {
			int j = 0;
			while (iterator.hasNext()) {
				Column c = iterator.next();
				Object[] obj = p.getEntry(c.getDataIndex());
				if (c.getId() != null && c.getId().contains("thumb")) {
					byte[] tmp = Base64.decode((String) obj[1], Base64.DEFAULT);
					holder.imageView.setImageBitmap(BitmapFactory
							.decodeByteArray(tmp, 0, tmp.length));
				} else if (c.getDataIndex().toLowerCase().contains("title")
						|| c.getDataIndex().toLowerCase().contains("titolo")) {
					holder.fileName.setText((String) obj[1]);
				} else if (c.getDataIndex().contains("rilev")
						|| c.getDataIndex().contains("anno_pubbl")) {
					if (!obj[1].toString().equals("")) {
						holder.lbl6.setText(c.getDataIndex() + ":");
						holder.fileType.setText((String) obj[1]);
					}
				} else if (c.getDataIndex().toLowerCase().contains("cod_reg")) {
					holder.label.setText(c.getDataIndex() + ":");
					holder.value.setText((String) obj[1]);
					// holder.labels[j].setText(c.getDataIndex());
					// holder.values[j].setText((String) obj[1]);
					// j++;
				} else if (c.getDataIndex().contains("FileType")
						&& !obj[1].toString().equals("")) {
					holder.lbl6.setText(c.getDataIndex() + ":");
					holder.fileType.setText((String) obj[1]);
				} else if (c.getDataIndex().contains("Description")
						&& !obj[1].toString().equals("")) {
					holder.label.setText(c.getDataIndex() + ":");
					holder.value.setText((String) obj[1]);
				}
			}

			return convertView;
		}

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// super.onCreateOptionsMenu(menu, inflater);
		this.menu = menu;
		inflater.inflate(R.menu.activity_itemdetails, menu);
		Log.d(TAG, "onCreateOptionsMenu");

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		if (!products.isEmpty())
			menu.findItem(R.id.filters).setVisible(true);

		if (!chosenFilter.isEmpty())
			menu.findItem(R.id.remove_filters).setVisible(true);
		else
			menu.findItem(R.id.remove_filters).setVisible(false);
		// Log.d(TAG, "onPrepareOptionsMenu");
		// this.menu=menu;
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		int lastInScreen = firstVisibleItem + visibleItemCount;

		if ((lastInScreen == totalItemCount) && !loadingMore && start > 0) {
			Log.d(TAG, "Last Visible!!!");
			Log.d(TAG, "calling for start: " + start);
			loadingMore = true;
			loadMoreData();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

}
