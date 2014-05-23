package com.diligencedojo.boxready;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MovementSelectionFragment extends Fragment {

	private static final int REQUEST_NEW_MOVEMENT = 0;
	public static final String NEW_MOVE_ADDED = "new_move_in_list";
	public static final String MOVE_REMOVED = "move_removed";

	private int numSelLeft;
	private int origCount;
	private GridView mGridView;
	Boolean moveDel = false;
	
	// ****************************************************************************************************//
	/*
	 * This array allows the user to select multiple work outs at once. It
	 * doesn't need to be re-instantiated every time the user backs up to this
	 * view.
	 */
	private ArrayList<Integer> movements = new ArrayList<Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	// ****************************************************************************************************//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.movement_selection, parent, false);

		// pull intent originated in Registration with adjusted rep array
		Intent i = getActivity().getIntent();
		final String[] repsAdj = i.getExtras().getStringArray("repsAdj");

		// choose a random number between 0 and 3; used to select work out type
		// from the following String array in Movement class:
		// **diffWODstyles = {3 MOVEMENTS FOR 3 ROUNDS, 21-15-9, MAX REPS}**
//		 Random r = new Random();
//		 final Integer ranPos = r.nextInt(3 - 0) + 0;

		// FOR TESTING**************************************************
		// set work out type for testing (see above - line 59)
		final Integer ranPos = 2;
		//**************************************************************
		
		// display number of moves in wod
		if (movements.isEmpty()) {
			numSelLeft = displayBeforeSelect(v, ranPos);
			origCount = numSelLeft;
		}

		// locate gridView and assign ImageAdapter
		mGridView = (GridView) v.findViewById(R.id.gridView);
		mGridView.setAdapter(new ImageAdapter(v.getContext()));

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long id) {

				// Sending image id to WODListActivity
				Intent movementIntent = new Intent(v.getContext(),
						WODListActivity.class);

				movements.add(position); // Add position of movement to array

				v.setVisibility(View.GONE); // kill selected button

				movementIntent.putExtra(WODListFragment.NEW_MOVE_ADDED,
						movements);
				movementIntent.putExtra("count", origCount);
				movementIntent.putExtra("pos", ranPos);
				movementIntent.putExtra(WODListFragment.REPS_ADJ, repsAdj);
				startActivityForResult(movementIntent, REQUEST_NEW_MOVEMENT);
			}

		});

		mGridView.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			// occurs on new instance of gallery (when the view is scrolled all
			// the way to the very top or very bottom)
			@Override
			public void onScrollStateChanged(AbsListView arg0, int arg1) {

				// set all the image views in the grid to visible
				for (int a = 0; a < mGridView.getCount(); a++) {
					ImageView img = (ImageView) mGridView.getChildAt(a);
					if (img != null) {
						img.setVisibility(View.VISIBLE);
					}
				}

				// Now we're going to go through the movements array and set
				// the visibility of the number in the movements list to GONE
				final int numVisibleChildren = mGridView.getChildCount();
				final int firstVisiblePosition = mGridView
						.getFirstVisiblePosition();

				for (int movementIndex = 0; movementIndex < movements.size(); movementIndex++) {

					for (int i = 0; i < numVisibleChildren; i++) {
						int positionOfView = firstVisiblePosition + i;

						if (positionOfView == movements.get(movementIndex)) {
							View view = mGridView.getChildAt(i);
							view.setVisibility(View.GONE);
						}
					} // end inner for loop
				} // end outer for loop
			}
		});

		return v;
	}
	
	// ****************************************************************************************************//

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		// Save UI state changes to the savedInstanceState.
		// This bundle will be passed to onCreate if the process is
		// killed and restarted.
		savedInstanceState.putString("saved", "saved");
		savedInstanceState.putIntegerArrayList("msfArray", movements);
		savedInstanceState.putAll(savedInstanceState);

	}
	
	// ****************************************************************************************************//

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_NEW_MOVEMENT) {

			movements = data
					.getIntegerArrayListExtra(WODListFragment.NEW_MOVE_ADDED);

			// set all the image views in the grid to visible
			for (int a = 0; a < mGridView.getCount(); a++) {
				ImageView img = (ImageView) mGridView.getChildAt(a);
				if (img != null) {
					img.setVisibility(View.VISIBLE);
				}
			}

			// Now we're going to go through the movements array and set
			// the visibility of the number in the movements list to GONE
			final int numVisibleChildren = mGridView.getChildCount();
			final int firstVisiblePosition = mGridView
					.getFirstVisiblePosition();

			for (int movementIndex = 0; movementIndex < movements.size(); movementIndex++) {

				for (int i = 0; i < numVisibleChildren; i++) {
					int positionOfView = firstVisiblePosition + i;

					if (positionOfView == movements.get(movementIndex)) {
						View view = mGridView.getChildAt(i);
						view.setVisibility(View.GONE);
					}
				} // end inner for loop
			} // end outer for loop
		}

	}
	
	// ****************************************************************************************************//

	public Integer displayBeforeSelect(View v, Integer ranPos) {

		// declare Movement object to access arrays in class
		Movement m = new Movement();
		String ranWod = m.diffWODstyles[ranPos];
		String instrStr = m.chooseMovInstr[ranPos];

		// // tell user which wod has been randomly selected***
		// Toast wodToast = Toast.makeText(v.getContext(), ranWod,
		// Toast.LENGTH_LONG);
		// wodToast.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
		// wodToast.show();
		// // tell user how many movements to select for this wod***
		// Toast t = Toast.makeText(v.getContext(), instrStr,
		// Toast.LENGTH_LONG);
		// t.setGravity(Gravity.TOP | Gravity.CENTER, 0, 200);
		// t.show();

		return m.numMoves[ranPos];
	}

}
