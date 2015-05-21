package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.pojos.Column;
import it.infn.ct.aginfrasgmobile.pojos.Field;
import it.infn.ct.aginfrasgmobile.pojos.Metadata;
import it.infn.ct.aginfrasgmobile.pojos.NewProduct;
import it.infn.ct.aginfrasgmobile.pojos.Replica;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProductsDetails extends FragmentActivity implements
		OnClickListener {

	private static final String TAG = ProductsDetails.class.getSimpleName();
	private String shibCookie;
	private AppPreferences _appPrefs;

	// private static final String SERVER_URL = "/glibrary/links2/";

	Metadata metadata;
	NewProduct product = new NewProduct();
	ArrayList<Column> columns;
	MyAdapter adapter;
	private Replica replica;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products_details);

		_appPrefs = new AppPreferences(this.getBaseContext());
		Intent intent = getIntent();
		product = (NewProduct) intent.getSerializableExtra("product");
		columns = intent.getParcelableArrayListExtra("columns");
		metadata = (Metadata) intent.getSerializableExtra("metadata");

		ListView lv = (ListView) findViewById(R.id.listView1);
		adapter = new MyAdapter(this, R.layout.activity_product_detail_lv_item);
		ImageView imgView = (ImageView) findViewById(R.id.imageView1);
		ArrayList<String[]> attributes = new ArrayList<String[]>();
		for (Column c : columns) {
			Object[] obj = product.getEntry(c.getDataIndex());
			if (c.getId() != null && c.getId().contains("thumb")) {
				byte[] tmp = Base64.decode((String) obj[1], Base64.DEFAULT);
				imgView.setImageBitmap(BitmapFactory.decodeByteArray(tmp, 0,
						tmp.length));
			} else {
				attributes
						.add(new String[] { c.getDataIndex(), (String) obj[1] });
			}

		}
		adapter.setData(attributes);
		lv.setAdapter(adapter);

		shibCookie = _appPrefs.getCookie();
		if (!shibCookie.equals("")) {
			String productId = "";
			ArrayList<Field> fields = metadata.getFields();
			for (Field field : fields) {
				String k = field.getField().get("name");
				if (k.contains("FILE"))
					productId = product.getEntry(k)[1].toString();
			}
			if (!productId.equals("")) {
				// Uri uri = Uri.parse(_appPrefs.getGateway()+SERVER_URL
				// + _appPrefs.getDefaultRepository() + "/" + productId);
				// Log.i(TAG, uri.toString());
				//
				// Bundle args1 = new Bundle();
				// args1.putParcelable("ARGS_URI", uri);
				//
				// Bundle params = new Bundle();
				// params.putString("Cookie", shibCookie);
				// args1.putParcelable("ARGS_PARAMS", params);
				//
				// getSupportLoaderManager().initLoader(1, args1, this);
				Object[] tmp = product.getEntry("source");
				if (tmp != null) {
					for (Object object : tmp) {
						Log.d(TAG, object.toString());
					}
					if (!tmp[1].toString().equals("")) {
						replica = new Replica(0, 0, 1, tmp[1].toString(), "");
						// replicas = new ArrayList<Replica>()
						// tmp[1].toString();
						Button btnReplicas = (Button) findViewById(R.id.btnReplicas);
						btnReplicas.setEnabled(true);
						btnReplicas.setOnClickListener(this);
						// btnReplicas.setOnClickListener(new OnClickListener()
						// {
						//
						// @Override
						// public void onClick(View v) {
						//
						// Intent intent = new Intent(getBaseContext(),
						// ReplicasActivity.class);
						//
						// intent.putParcelableArrayListExtra("replicas",
						// replicas);
						// intent.putExtra("product", product);
						// startActivity(intent);
						// }
						// });
					}
				}
			}

		}
	}

	// @Override
	// public Loader<RESTResponse> onCreateLoader(int id, Bundle args) {
	// if (args != null && args.containsKey("ARGS_URI")) {
	// Uri action = args.getParcelable("ARGS_URI");
	// if (args.containsKey("ARGS_PARAMS")) {
	//
	// Bundle params = args.getParcelable("ARGS_PARAMS");
	//
	// return new RESTLoader(this.getBaseContext(),
	// RESTLoader.HTTPVerb.GET, action, params);
	// } else {
	// return new RESTLoader(this.getBaseContext(),
	// RESTLoader.HTTPVerb.GET, action);
	// }
	// }
	// return null;
	// }
	//
	// @Override
	// public void onLoadFinished(Loader<RESTResponse> loader, RESTResponse
	// data) {
	//
	// int code = data.getCode();
	// String json = data.getData();
	// Log.d(TAG, "" + code);
	// if (code == 200 && !json.equals("")) {
	// Log.d(TAG, json);
	// replicas = getLinksFromJson(json);
	// }
	// }
	//
	// @Override
	// public void onLoaderReset(Loader<RESTResponse> arg0) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.products_details, menu);
		return true;
	}

	private class MyAdapter extends ArrayAdapter<String[]> {

		Context context;

		public MyAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
			this.context = context;
		}

		public void setData(ArrayList<String[]> data) {
			clear();
			if (data != null) {
				for (String[] s : data) {
					add(s);
				}

			}
		}

		private class ViewHolder {
			TextView field, value;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			String[] element = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.activity_product_detail_lv_item, null);
				holder = new ViewHolder();
				holder.field = (TextView) convertView
						.findViewById(R.id.fieldName);
				holder.value = (TextView) convertView
						.findViewById(R.id.fieldValue);

				convertView.setTag(holder);
			} else
				holder = (ViewHolder) convertView.getTag();

			holder.field.setText(element[0]);
			holder.value.setText(element[1]);

			return convertView;
		}

	}

	// private ArrayList<Replica> getLinksFromJson(String json) {
	// ArrayList<Replica> replicas = new ArrayList<Replica>();
	// try {
	// JSONArray replicasArray = (JSONArray) new JSONTokener(json)
	// .nextValue();
	//
	// for (int i = 0; i < replicasArray.length(); i++) {
	// JSONObject replicaJSON = replicasArray.getJSONObject(i);
	// Replica r = new Replica(replicaJSON.getDouble("lat"),
	// replicaJSON.getDouble("lng"),
	// replicaJSON.get("enabled"),
	// replicaJSON.getString("link"),
	// replicaJSON.getString("name"));
	// replicas.add(r);
	// }
	//
	// } catch (JSONException e) {
	// Log.e(TAG, "Failed to parse JSON.", e);
	// }
	// findViewById(R.id.btnReplicas).setEnabled(true);
	// return replicas;
	// }

	@Override
	public void onClick(View v) {

		Intent intent = new Intent(getBaseContext(),
				DownloadWebViewActivity.class);
		// if(isDownloadManagerAvailable(this));

		String url = replica.getLink();
		if (replica.getEnabled() == 1) {
			if (!url.contains("http")) {
				// url = "https://earthserver-sg.consorzio-cometa.it/" + url;
				url = _appPrefs.getGateway() + "/" + url;
			}
			try {

				URL u1 = new URL(url);
				if (!u1.getFile().toLowerCase().contains(".kmz")) {
					if (url.contains("https://maps.google.com")) {

						intent = new Intent(android.content.Intent.ACTION_VIEW,
								Uri.parse(url));
						intent.setClassName("com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity");
						startActivity(intent);
					} else if (u1.getFile().toLowerCase().contains(".pdf")) {
						File file = new File(Environment
								.getExternalStorageDirectory().getPath()
								+ "/Download/"
								+ url.substring(url.lastIndexOf('/') + 1,
										url.length()));

						if (!file.exists()) {
							new DownloadFileFromURL(this).execute(url);
						} else
							openFile(file);
					} else {
						intent.putExtra("URL", replica.getLink());
						startActivity(intent);
					}
				} else {

					File file = new File(Environment
							.getExternalStorageDirectory().getPath()
							+ "/Download/"
							+ url.substring(url.lastIndexOf('/') + 1,
									url.length()));

					if (!file.exists()) {
						new DownloadFileFromURL(this).execute(url);
					} else
						openFile(file);
				}
				//
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else
			Toast.makeText(
					this,
					"You can't download this file from this replica, this replica is not enabled.",
					Toast.LENGTH_SHORT).show();
	}

	private void openFile(File file) {
		Uri path = Uri.fromFile(file);

		if (path.getLastPathSegment().contains(".kmz")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/vnd.google-earth.kml+xml");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this,
						"No application found to open the dowloaded file.",
						Toast.LENGTH_SHORT).show();
				Log.e(TAG, e.getMessage());
			}
		} else if (path.getLastPathSegment().toLowerCase().contains(".pdf")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(path, "application/pdf");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this,
						"No application found to open the dowloaded file.",
						Toast.LENGTH_SHORT).show();
				Log.e(TAG, e.getMessage());
			}
		} else
			Toast.makeText(this,
					"No application found to open the dowloaded file.",
					Toast.LENGTH_SHORT).show();
	}

	/**
	 * Background Async Task to download file
	 * */
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

		Activity baseActivity;
		private final HttpClient Client = new DefaultHttpClient();
		private String Content;

		protected DownloadFileFromURL(Activity baseActivity) {
			this.baseActivity = baseActivity;
		}

		/**
		 * Before starting background thread Show Progress Bar Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			pDialog = new ProgressDialog(baseActivity);
			pDialog.setMessage("Downloading file. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setMax(100);
			pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pDialog.setCancelable(true);
			pDialog.show();
			// showDialog(ProgressDialog.STYLE_HORIZONTAL);
		}

		/**
		 * Downloading file in background thread
		 * */
		@Override
		protected String doInBackground(String... f_url) {
			int count;
			try {
				URL url = new URL(f_url[0]);

				HttpGet httpget = new HttpGet(f_url[0]);
				httpget.setHeader("Cookie", _appPrefs.getCookie());
				HttpContext localContext = new BasicHttpContext();

				HttpResponse response = Client.execute(httpget, localContext);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					long lenghtOfFile = entity.getContentLength();
					InputStream input = entity.getContent();

					FileOutputStream output = new FileOutputStream(new File(
							Environment.getExternalStorageDirectory().getPath()
									+ "/Download/", f_url[0].substring(
									f_url[0].lastIndexOf('/') + 1,
									f_url[0].length())));

					byte data[] = new byte[1024];

					long total = 0;

					while ((count = input.read(data)) != -1) {
						total += count;
						// publishing the progress....
						// After this onProgressUpdate will be called
						publishProgress(""
								+ (int) ((total * 100) / lenghtOfFile));

						// writing data to file
						output.write(data, 0, count);
					}

					// flushing output
					output.flush();

					// closing streams
					output.close();
					input.close();
				}
				// ResponseHandler<String> responseHandler = new
				// BasicResponseHandler();
				// Content = Client.ex.execute(httpget, responseHandler);

				// HttpURLConnection conection =
				// (HttpURLConnection)url.openConnection();
				// conection.setRequestMethod("GET");
				// conection.setDoOutput(true);
				// String tmp = _appPrefs.getCookie();
				// conection.setRequestProperty("Cookie",
				// _appPrefs.getCookie());
				//
				// conection.connect();
				// // this will be useful so that you can show a tipical 0-100%
				// // progress bar
				// int lenghtOfFile = conection.getContentLength();
				//
				// // download the file
				// // InputStream input = conection.getInputStream();
				// InputStream input = new BufferedInputStream(url.openStream(),
				// 8192);
				//
				// FileOutputStream output = new FileOutputStream(new File(
				// Environment.getExternalStorageDirectory().getPath()
				// + "/Download/",
				// product.getEntry("FileName")[1].toString()));
				// // Output stream
				// // OutputStream output = new FileOutputStream(Environment
				// // .getExternalStorageDirectory().getPath()
				// // + "/Download/"
				// // + product.getEntry("FileName")[1].toString());
				//
				// byte data[] = new byte[1024];
				//
				// long total = 0;
				//
				// while ((count = input.read(data)) != -1) {
				// total += count;
				// // publishing the progress....
				// // After this onProgressUpdate will be called
				// publishProgress("" + (int) ((total * 100) / lenghtOfFile));
				//
				// // writing data to file
				// output.write(data, 0, count);
				// }
				//
				// // flushing output
				// output.flush();
				//
				// // closing streams
				// output.close();
				// input.close();

			} catch (Exception e) {
				Log.e("Error: ", e.getMessage());
			}

			return null;
		}

		/**
		 * Updating progress bar
		 * */
		protected void onProgressUpdate(String... progress) {
			// setting progress percentage
			pDialog.setProgress(Integer.parseInt(progress[0]));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		@Override
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after the file was downloaded
			pDialog.dismiss();
			// // Displaying downloaded image into image view
			// // Reading image path from sdcard
			// String imagePath = Environment.getExternalStorageDirectory()
			// .getPath() + "/Download/downloadedfile.jpg";
			// // setting downloaded into image view
			// ImageView my_Image = (ImageView) findViewById(R.id.folderImage);
			// my_Image.setImageDrawable(Drawable.createFromPath(imagePath));

			File file = new File(Environment.getExternalStorageDirectory()
					.getPath()
					+ "/Download/"
					+ product.getEntry("source")[1].toString().substring(
							product.getEntry("source")[1].toString()
									.lastIndexOf('/') + 1,
							product.getEntry("source")[1].toString().length()));

			if (file.exists()) {
				openFile(file);
			}
			// File f = new File(Environment
			// .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
			// +);
			// Intent intent = new Intent(Intent.ACTION_VIEW,
			// Uri.fromFile(f));
			// intent.setType("application/pdf");
			// PackageManager pm = getPackageManager();
			// List<ResolveInfo> activities = pm.queryIntentActivities(intent,
			// 0);
			// if (activities.size() > 0) {
			// startActivity(intent);
			// } else {
			// // Do something else here. Maybe pop up a Dialog or Toast
			// }

		}

	}

}
