package com.diligencedojo.boxready;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class AppExplanationFragment extends Fragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		// inflate explanation layout
		View v = inflater.inflate(R.layout.explanation, parent, false);
		
		// get the intent passed in from Registration and set repsAdj array
		// as a duplicate to pass to MovementSelection
		Intent i = getActivity().getIntent();
		final String[] repsAdj = i.getExtras().getStringArray("repsAdj");
		
		// WEEK & WORKOUT PROGRESS WILL BE A SAVED PREFERENCE VARIABLE
		// INCREMENTED EACH TIME A WOD IS COMPLETED
		// free version: Week 1, Work Out 1-3
		// full version: Week 1-8, Work Out 1-3
		
		// set up continue to work out builder button
		Button skipToWod = (Button) v.findViewById(R.id.skip_to_wod);
		
		// when work out builder button is clicked, continue to MovementSelection  
		// and pass in repsAdj array
		skipToWod.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toMovSel = new Intent(v.getContext(), MovementSelectionActivity.class);
				toMovSel.putExtra("repsAdj", repsAdj);
				startActivity(toMovSel);
				
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
			
			switch(item.getItemId()) {
			case R.id.upgrade_action_bar:
				//Log.d("upgradeApp", "pressed!");
				upgradeApp();
				return true;
			
			default:
				return super.onOptionsItemSelected(item);
			}
		}//End onOptionsItemSelected
		
		// this will transfer the user to the upgrade screen
		private void upgradeApp() {
			Log.d("upgradeApp", "pressed!");
			
			//Intent upgradeIntent = new Intent();
		}

}
