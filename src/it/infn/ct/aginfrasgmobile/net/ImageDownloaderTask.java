package it.infn.ct.aginfrasgmobile.net;

import it.infn.ct.aginfrasgmobile.R;
import it.infn.ct.aginfrasgmobile.pojos.IDP;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
	private final WeakReference<ImageView> imageViewReference;
//	private final WeakReference<IDP> idpReference;
//	private final WeakReference<Federation> federationReference;
	private final WeakReference<Object> reference;
	private Context context;
	private String fileName;

	public ImageDownloaderTask(Context c, ImageView imageView, Object obj) {
		context = c;
		imageViewReference = new WeakReference<ImageView>(imageView);
//		idpReference = new WeakReference<IDP>(idp);
//		federationReference = new WeakReference<Federation>(federation);
		reference = new WeakReference<Object>(obj);
	}

	@Override
	// Actual download method, run in the task thread
	protected Bitmap doInBackground(String... params) {
		// params comes from the execute() call: params[0] is the url.
		fileName = params[0].substring(params[0].lastIndexOf("/") + 1);
		return downloadBitmap(params[0]);
	}

	@Override
	// Once the image is downloaded, associates it to the imageView
	// and store it in the internal storage
	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}

		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
//				Federation federation = federationReference.get();
//				IDP idp = idpReference.get();
				Object obj = reference.get();
				if (bitmap != null) {
					try {
						FileOutputStream fos = context.openFileOutput(fileName,
								Context.MODE_PRIVATE);
						bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);
						fos.close();
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					imageView.setImageBitmap(bitmap);
					if(obj != null && obj instanceof IDP){
						if (R.id.idpFlagImage == imageView.getId())
							((IDP)obj).setFlag(bitmap);
						else
							((IDP)obj).setLogo(bitmap);
						
					}
//					if (idp != null){
//						if (R.id.idpFlagImage == imageView.getId())
//							idp.setFlag(bitmap);
//						else
//							idp.setLogo(bitmap);
//					} else if (federation != null){
//						if (R.id.flagImage == imageView.getId())
//							federation.setFlag(bitmap);
//						else
//							federation.setLogo(bitmap);
//					}
				} else {
					imageView.setImageDrawable(imageView.getContext()
							.getResources().getDrawable(R.drawable.blank_flag));
				}
			}

		}
	}

	static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient
				.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url.replaceAll(" ", "%20"));
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory
							.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from "
					+ url);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}

}
