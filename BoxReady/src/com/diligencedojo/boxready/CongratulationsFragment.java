package com.diligencedojo.boxready;

import java.io.InputStream;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CongratulationsFragment extends Fragment {

	boolean hasUpgrade = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		// inflate explanation layout
		View v = inflater.inflate(R.layout.congratulations, parent, false);

		// makes action bar appear with no text (title)
		getActivity().getActionBar().setDisplayShowTitleEnabled(false);

		// get the intent passed in from Registration and set repsAdj array
		// as a duplicate to pass to MovementSelection
		Intent i = getActivity().getIntent();
		final String wodTime = i.getExtras().getString("wodTime");
		final byte[] byteArray = i.getByteArrayExtra("image");
		final Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
		
		final Button fb = (Button)v.findViewById(R.id.facebook_button);
		final ImageView iv = (ImageView)v.findViewById(R.id.screenshots);

		// TextView yourTimeIs = (TextView) v.findViewById(R.id.your_score_is);
		final TextView finalTime = (TextView) v.findViewById(R.id.timerValue);
		final Button logWod = (Button) v.findViewById(R.id.log_workout);
		final TextView congrats = (TextView)v.findViewById(R.id.congrats);
		final TextView done = (TextView)v.findViewById(R.id.donefortoday);

		finalTime.setText(wodTime);

		// WE'LL SAVE AN INCREMENTED VALUE TO USE IN THE APP_EXPLANATION CLASS
		// TO KEEP UP WITH WHICH WEEK AND WORK OUT NUMBER THE USER IS ON

		final String upgradeToUse = "You must upgrade to the full version of "
				+ "Box Ready to keep a log of you workouts.";
		// initiates log work out process (expanding list view)
		logWod.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				
				hasUpgrade = true;

				if (hasUpgrade == false) {
					Toast t = Toast.makeText(v.getContext(), upgradeToUse,
							Toast.LENGTH_LONG);
					t.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
					t.show();
				} else {
					// if upgraded, the intent leading to the log view will go
					// here
					Intent toLog = new Intent(v.getContext(), ListSample.class);
					toLog.putExtra("wodTime", wodTime);
					toLog.putExtra("image", byteArray);
					startActivity(toLog);
				}

				// Intent toMovSel = new Intent(v.getContext(),
				// MovementSelectionActivity.class);
				// toMovSel.putExtra("repsAdj", repsAdj);
				// startActivity(toMovSel);

			}
		});

		setHasOptionsMenu(true);

		return v;
	}

	// the next two functions control the Upgrade button on the action bar
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.upgrade_action_bar, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.upgrade_action_bar:
			// Log.d("upgradeApp", "pressed!");
			upgradeApp();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}// End onOptionsItemSelected

	// this will transfer the user to the upgrade screen
	private void upgradeApp() {
		Log.d("upgradeApp", "pressed!");

		// Intent upgradeIntent = new Intent();
	}

}
