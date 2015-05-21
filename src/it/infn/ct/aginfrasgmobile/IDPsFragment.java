package it.infn.ct.aginfrasgmobile;

import it.infn.ct.aginfrasgmobile.net.ImageDownloaderTask;
import it.infn.ct.aginfrasgmobile.pojos.IDP;
import it.infn.ct.aginfrasgmobile.utils.DialogFactory;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class IDPsFragment extends ListFragment implements OnClickListener {

	final static String ARG_POSITION = "position";
	private static final String TAG = IDPsFragment.class.getSimpleName();
	private IDP[] idps;
	private ArrayList<String> origins;
//	View footerView;
	int mCurrentPosition = -1;
	private AppPreferences _appPrefs;

	private ArrayAdapter<String> mAdapter;
	private IDPAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);

		_appPrefs = new AppPreferences(this.getActivity().getBaseContext());
		// layout diverso per terminali con versione inferiore alla 3
		int layout = Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB ? android.R.layout.simple_list_item_activated_1
				: android.R.layout.simple_list_item_1;

		// Creo un array adapter con le federations

		mAdapter = new ArrayAdapter<String>(
				this.getActivity().getBaseContext(), layout);

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

//		footerView = ((LayoutInflater) getActivity().getSystemService(
//				Context.LAYOUT_INFLATER_SERVICE)).inflate(
//				R.layout.not_sure_footer, null, false);
//		footerView.setVisibility(View.GONE);

//		getListView().addFooterView(footerView);
		setListAdapter(mAdapter);
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.i(TAG, "onStart");

		// Durante l'avvio, verifica se ci sono argomenti passati al fragment.
		// Il metodo onStart è un buon posto per fare questo perchè possiamo
		// essere sicuri che il layout è stato applicato al fragment e
		// possiamo tranquillamente chiamare il metodo sotto che setta il
		// testo dell'articolo
		Bundle args = getArguments();
		if (args != null) {
			// Set IDP based on argument passed in Bundle
			idps = (IDP[]) args.getSerializable(ARG_POSITION);// ,
																// mCurrentPosition;

			updateIdpsList(idps, mCurrentPosition);

		} else if (mCurrentPosition != -1) {
			// Set article based on saved instance state defined during
			// onCreateView
			getListView().setItemChecked(mCurrentPosition, true);
			updateIdpsList(idps, mCurrentPosition);
		}
		getListView().setAdapter(adapter);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i(TAG, "onActivityCreated");

		// adapter = new FederationAdapter(getActivity().getBaseContext(),
		// android.R.layout.simple_list_item_1);
		adapter = new IDPAdapter(getActivity().getBaseContext(),
				R.layout.federation_lv_item);
//		View footerView = ((LayoutInflater) getActivity().getSystemService(
//				Context.LAYOUT_INFLATER_SERVICE)).inflate(
//				R.layout.not_sure_footer, null, false);
//
//		getListView().addFooterView(footerView);

		getActivity().setTitle("Select your Identity Federation");
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i(TAG, "onCreateVew");

		if (savedInstanceState != null) {
			// updateIdpsList((IDP[])
			// savedInstanceState.getSerializable("idps"),
			// mCurrentPosition);
			idps = (IDP[]) savedInstanceState.getSerializable("idps");
			mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
			Log.d("IDP", "CIAO");
		}
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void updateIdpsList(IDP[] idps, int position) {

		this.idps = idps;
		mCurrentPosition = position;
		origins = new ArrayList<String>();
		// mAdapter.clear();
		for (IDP idp : idps) {
			origins.add(idp.getOrigin());
		}
		// adapter.clear();
		adapter.setData(new ArrayList<IDP>(Arrays.asList(idps)));
//		if (footerView != null && footerView.getVisibility() == View.GONE)
//			footerView.setVisibility(View.VISIBLE);
//		adapter.notifyDataSetChanged();
		getActivity().setTitle("Select your Identity Provider");
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// Notifica all'Activity padre l'item selezionato

		if (position < idps.length) {
			mCurrentPosition = position;
			if (_appPrefs.getDefaultIDP().equals("")
					|| !_appPrefs.getDefaultIDP().equals(origins.get(position))) {
				Dialog d = DialogFactory.getDialog(getActivity(),
						"Set as defaul IDP", "Do you want to set:\n"
								+ idps[position].getDisplayName()
								+ "\nas your default Identity Provider?",
						false, "Yes", "No", this, this);

				d.show();

			} else {
				showWebView();
			}

			// Setta l'item come check per essere evidenziato quando ci sono i
			// due
			// panel visibili.
			// mCurrentPosition = position;
			getListView().setItemChecked(mCurrentPosition, true);

		} else {
			Dialog d = DialogFactory
					.getDialog(
							getActivity(),
							"Suggestion",
							"In case you do not belong to any of the shown Identity Providers, "
									+ "or you are not sure you do, you are welcome to register to our Identity Provider Open. "
									+ "When registration procedure will be completed, you can sign in again and select GrIDP as your Identity Federation and then IDPOPEN GARR as your Identity Provider.",
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
			d.setOwnerActivity(getActivity());
			d.show();
		}

	}

	private void showWebView() {

		Intent intent = new Intent(getActivity().getBaseContext(),
				WebViewActivity.class);

		intent.putExtra("URL", origins.get(mCurrentPosition));
		intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
		startActivity(intent);
		getActivity().finish();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// salvo la selezione attiva nel caso debba ricreare il fragment
		outState.putInt(ARG_POSITION, mCurrentPosition);
		outState.putSerializable("idps", idps);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {

		switch (which) {
		case -1:
			Log.d(TAG, "Positve Pressed");
			_appPrefs.saveDefaultIDP(origins.get(mCurrentPosition));
			break;

		case -2:
			Log.d(TAG, "Negative Pressed");
			break;
		default:
			Log.d(TAG, "Unrecognized button pressed.");
			break;
		}
		showWebView();
	}

	private class IDPAdapter extends ArrayAdapter<IDP> {

		Context context;

		public IDPAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);

			this.context = context;
		}

		public void setData(ArrayList<IDP> data) {
			clear();
			if (data != null) {
				for (IDP i : data) {
					add(i);
				}
			}

		}

		private class ViewHolder {
			TextView idpName;
			TextView idpCountry;
			ImageView idpFlag;
			ImageView idpLogo;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			IDP i = getItem(position);

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//			if (convertView == null) {

				int layout = R.layout.idp_lv_item;
				convertView = mInflater.inflate(layout, null);
				holder = new ViewHolder();

				holder.idpName = (TextView) convertView
						.findViewById(R.id.txtIDPName);
				holder.idpCountry = (TextView) convertView
						.findViewById(R.id.idpTxtCountry);
				holder.idpFlag = (ImageView) convertView
						.findViewById(R.id.idpFlagImage);
				holder.idpLogo = (ImageView) convertView
						.findViewById(R.id.idpLogoImage);
				convertView.setTag(holder);
//			} else
//				holder = (ViewHolder) convertView.getTag();

			holder.idpName.setText(i.getDisplayName());
			holder.idpCountry.setText(i.getCountry());
			if (i.getFlag() != null) {
				holder.idpFlag.setVisibility(View.VISIBLE);
				holder.idpFlag.setImageBitmap(i.getFlag());
			} else if (i.getFlagURL() != "") {
				holder.idpFlag.setVisibility(View.VISIBLE);
				new ImageDownloaderTask(context, holder.idpFlag, i).execute(i
						.getFlagURL());
			} else
				holder.idpFlag.setVisibility(View.INVISIBLE );
			if (i.getLogo() != null){
				holder.idpLogo.setVisibility(View.VISIBLE);
				holder.idpLogo.setImageBitmap(i.getLogo());
			}
			else if (i.getLogoURL() != ""){
				holder.idpLogo.setVisibility(View.VISIBLE);
				new ImageDownloaderTask(context, holder.idpLogo, i).execute(i
						.getLogoURL());
			}
			else
				holder.idpLogo.setVisibility(View.INVISIBLE);
			return convertView;
		}
	}

}
