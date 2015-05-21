package it.infn.ct.aginfrasgmobile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.ViewSwitcher;

public class HowToActivity extends Activity {
	// Set the display time, in milliseconds (or extract it out as a
	// configurable parameter)
	private AppPreferences _appPrefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		_appPrefs = new AppPreferences(this.getBaseContext());

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_how_to);

		CheckBox checkBox = (CheckBox) findViewById(R.id.howToChkBox);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				_appPrefs.saveShowWelcomePage(false);

			}
		});
		
		final ScrollView sv = (ScrollView) findViewById(R.id.scrl);
		final ViewSwitcher switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher1);
		final Button prevBtn = (Button) findViewById(R.id.button1);
		final Button nextBtn = (Button) findViewById(R.id.button2);

		prevBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AnimationUtils();
				sv.scrollTo(0, sv.getTop());
				nextBtn.setVisibility(View.VISIBLE);
				prevBtn.setVisibility(View.INVISIBLE);
				switcher.setAnimation(AnimationUtils.makeInAnimation(
						getApplicationContext(), true));
				switcher.showPrevious();
			}
		});

		nextBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				new AnimationUtils();
				sv.scrollTo(0, sv.getTop());
				nextBtn.setVisibility(View.INVISIBLE);
				prevBtn.setVisibility(View.VISIBLE);
				switcher.setAnimation(AnimationUtils.makeInAnimation(
						getApplicationContext(), true));
				switcher.showNext();
			}
		});

		ImageView im = (ImageView) findViewById(R.id.imageView2);
		im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "https://idpopen.garr.it/register";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		ImageView imearthPrj = (ImageView) findViewById(R.id.imEarthPrj);
		imearthPrj.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "http://aginfra.eu/";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		ImageView imearthSG = (ImageView) findViewById(R.id.imEarthSG);
		imearthSG.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String url = "https://aginfra-sg.ct.infn.it/";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
			}
		});

		ImageView imLogin = (ImageView) findViewById(R.id.imLogin);
		imLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				HowToActivity.this.finish();
				if (!_appPrefs.getDefaultIDP().equals("")) {
					Intent intent = new Intent(getBaseContext(),
							WebViewActivity.class);
					intent.putExtra("URL", _appPrefs.getDefaultIDP());
					intent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
					startActivity(intent);
				} else {
					Intent mainIntent = new Intent(HowToActivity.this,
							MainActivity.class);
					// mainIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
					HowToActivity.this.startActivity(mainIntent);
				}
			}
		});

	}

}
