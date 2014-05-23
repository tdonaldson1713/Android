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
import android.widget.TextView;

public class MovementDescriptionFragment extends Fragment {
	
	// ****************************************************************************************************//

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	// ****************************************************************************************************//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.movement_description, parent, false);
		
		// get intent data
		Intent i = getActivity().getIntent();
		
		// selected list item id
		int position = i.getExtras().getInt("id");

		// new instance of Movement (set name and reps based on position
		// passed in from WODListFragment)
		Movement m = new Movement();
		//m.setMovementName(m.movementNames[position]);
		//m.setReps(10); // ***this may need to be changed from 10 to arrayPos
		
		TextView movName = (TextView)v.findViewById(R.id.movement_name);
		movName.setText(m.movementNames[position]);
		//movName.setText(m.getMovementName());
		
		// set the text in the textView from the movDescriptions array in
		// the Movement class
		TextView description = (TextView)v.findViewById(R.id.movement_description);
		description.setText(m.getDescrList(position));
		
		Button demoButton = (Button)v.findViewById(R.id.demo_button);
		
		demoButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent demoMov = new Intent(v.getContext(), DemoVidActivity.class);
				startActivity(demoMov);
				
			}
		});
		
		setHasOptionsMenu(true);

		return v;
	}
	
	// ****************************************************************************************************//
	// the next two functions control the Upgrade button on the action bar
	// ****************************************************************************************************//
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
