package com.diligencedojo.boxready;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

public class WODListFragment extends ListFragment {
	public static final String NEW_MOVE_ADDED = "new_move_in_list";
	public static final String MOVE_REMOVED = "move_removed";
	public static final String REPS_ADJ = "repsAdj";

	private long startTime = 0L;
	private Handler customHandler = new Handler();
	long timeInMilliseconds = 0L;
	long timeSwapBuff = 0L;
	long updatedTime = 0L;
	private TextView timerValue;
	Integer stylePos;
	Boolean timerRunning = false;
	String[] repsAdj;
	//int countdownStart = 11000; // 11 seconds in milliseconds
	int countdownStart = 4000;
	int decrementBy = 1000; // 1 second in milliseconds

	private ArrayList<Integer> mMovements;
	
	// ****************************************************************************************************//

	/**
	 * Create a new instance of the fragment. This will allow us to use the
	 * moves passed in from MovementSelectionFragment within the
	 * WODListFragment.
	 */
	public static WODListFragment newInstance(ArrayList<Integer> moves) {
		Bundle args = new Bundle();
		args.putSerializable(NEW_MOVE_ADDED, moves);
		// Log.d("fmovements", "size: " + moves.size());
		WODListFragment fragment = new WODListFragment();
		fragment.setArguments(args);

		return fragment;
	}
	
	// ****************************************************************************************************//

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle(R.string.movement_title);

		// Get the arguments that were serialized in WODListActivity.
		Bundle args = getArguments();
		mMovements = (ArrayList<Integer>) args.getSerializable(NEW_MOVE_ADDED);

		MovementsAdapter adapter = new MovementsAdapter(mMovements);
		setListAdapter(adapter);
	}
	
	// ****************************************************************************************************//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		final View v = inflater.inflate(R.layout.wod_list, parent, false);

		// get the amount of total movements allowed in the wod
		Intent i = getActivity().getIntent();
		final int moveCount = i.getExtras().getInt("count");
		final int position = i.getExtras().getInt("pos");
		stylePos = position;
		repsAdj = i.getExtras().getStringArray("repsAdj");

		final Button finalize_wod = (Button) v.findViewById(R.id.finalize_wod);
		final Button delete_move = (Button) v.findViewById(R.id.delete_move);
		final TextView wod_type = (TextView) v.findViewById(R.id.wod_type);
		final Button start_wod = (Button) v.findViewById(R.id.start_wod);
		final Button startButton = (Button) v.findViewById(R.id.startButton);
		final Button pauseButton = (Button) v.findViewById(R.id.pauseButton);
		timerValue = (TextView) v.findViewById(R.id.timerValue);
		final TextView congrats = (TextView) v.findViewById(R.id.congrats);
		final Button exitApp = (Button) v.findViewById(R.id.exit_app);
		final TextView donefortoday = (TextView) v
				.findViewById(R.id.donefortoday);
		final TextView add_move = (TextView) v.findViewById(R.id.add_move);

		Movement m = new Movement();
		CharSequence[] s = getResources().getTextArray(R.array.wod_style_array);
		CharSequence wodString = s[position];
		wod_type.setText(TextUtils.concat(wodString, m.wodScoring[position]));

		// show finalize button when wod is full
		if (mMovements.size() == moveCount) {
			add_move.setVisibility(View.GONE);
			finalize_wod.setVisibility(View.VISIBLE);
		}

		// set finalize button
		finalize_wod.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v1) {

				// Movement m = new Movement();

				// change visible buttons on finalize click
				finalize_wod.setVisibility(View.GONE);
				delete_move.setVisibility(View.GONE);
				start_wod.setVisibility(View.VISIBLE);

				// set start wod button
				start_wod.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v2) {

						startButton.setVisibility(View.VISIBLE);
						start_wod.setVisibility(View.GONE);

						startButton
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View v) {
										startButton.setVisibility(View.GONE);

										// does count down then starts timer
										countdown();

										// this does a count down before
										// displaying the
										// pauseButton to keep the user from
										// pressing the
										// end work out button(pauseButton)
										// while the count
										// down timer is running
										new CountDownTimer(countdownStart,
												decrementBy) {
											public void onTick(
													long millisUntilFinished) {

											}

											public void onFinish() {
												pauseButton
														.setVisibility(View.VISIBLE);
											}
										}.start();
									}
								});

						// **labeled as End Work Out button**
						pauseButton
								.setOnClickListener(new View.OnClickListener() {

									@Override
									public void onClick(View view3) {
										startButton.setVisibility(View.GONE);
										pauseButton.setVisibility(View.GONE);
//										congrats.setVisibility(View.VISIBLE);
//										donefortoday
//												.setVisibility(View.VISIBLE);
										//exitApp.setVisibility(View.VISIBLE);
										final CharSequence wodTime;
										String wodTimeString;

										// stop clock
										timeSwapBuff += timeInMilliseconds;
										customHandler
												.removeCallbacks(updateTimerThread);
										
										// SCREEN SHOT TRIAL*******
										
										final Bitmap bmp = screenShot(v);
										
										//Convert to byte array
										ByteArrayOutputStream stream = new ByteArrayOutputStream();
										bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
										byte[] byteArray = stream.toByteArray();

										// SCREEN SHOT TRIAL*******
										
										// package to send to congratulations screen
										wodTime = timerValue.getText();
										wodTimeString = (String) wodTime;
										
										// *** I WILL EITHER NEED TO SAVE THE MOVEMENTS, REPS, AND 
										// WORK OUT TYPE TO A DATABASE ON THE DEVICE OR SAVE A 
										// SINGLE SCREENSHOT OF EACH WORKOUT TO BE DISPLAYED ON 
										// ITEM CLICK IN THE WORKOUT LOG VIEW ****
										
										// GOES TO CONGRATULATIONS
										Intent toCongrats = new Intent(v.getContext(), CongratulationsActivity.class);
										toCongrats.putExtra("wodTime", wodTimeString);
										toCongrats.putExtra("image",byteArray);
										startActivity(toCongrats);
										
										exitApp.setOnClickListener(new View.OnClickListener() {

											@Override
											public void onClick(View v) {
//												ImageView image;
//												image = (ImageView)v.findViewById(R.id.screenshots);
//												image.setImageBitmap(bm);
												
												// EXITS APPLICATION
//												Intent intent = new Intent(
//														Intent.ACTION_MAIN);
//												intent.addCategory(Intent.CATEGORY_HOME);
//												intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//												startActivity(intent);
											}
										}); // end exitApp setOnClickListener
									}
								}); // end pause setOnClickListener
					}
				}); // end of start_wod setOnClickListener
			}
		}); // end of finalize_wod setOnClickListener

		// set delete button
		delete_move.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// check to make sure mMovements isn't empty
				if (mMovements.size() > 0) {
					mMovements.remove(mMovements.size() - 1);

					// update the listView with the value deleted
					((MovementsAdapter) getListAdapter())
							.notifyDataSetChanged();

					Activity activity = getActivity();
					if (activity instanceof WODListActivity) {
						((WODListActivity) activity).setMovements(mMovements);
					}
					finalize_wod.setVisibility(View.GONE);
					add_move.setVisibility(View.VISIBLE);
				}
			}
		}); // end of delete_move setOnClickListener
		
		setHasOptionsMenu(true);

		return v;
	}
	
	public Bitmap screenShot(View view) {
	    Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
	            view.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(bitmap);
	    view.draw(canvas);
	    return bitmap;
	}

	// COUNTDOWN TIMER
	// function that does a count down before the timer starts
	public void countdown() {

		new CountDownTimer(countdownStart, decrementBy) {
			// caveat-the last tick won't display with this
			// built in function
			public void onTick(long m) {
				long sec = m / 1000 + 1;

				timerValue.setText(String.valueOf(sec - 1));

				if (sec < 5) // red from 3 down (1 is subtracted)
					timerValue.setTextColor(getResources()
							.getColor(R.color.red));
				else
					// green until 3
					timerValue.setTextColor(getResources().getColor(
							R.color.green));

				timerValue.setVisibility(View.VISIBLE);
				timerValue.setTextSize(272);
			}

			public void onFinish() {
				timerValue.setTextColor(getResources().getColor(R.color.black));

				// start timer
				startTime = SystemClock.uptimeMillis();
				customHandler.postDelayed(updateTimerThread, 0);
			}
		}.start();
	}

	// ****************************************************************************************************//
	
	// TIMER
	// function that runs the timer after the count down is complete
	public Runnable updateTimerThread = new Runnable() {
		public void run() {

			timerValue.setTextSize(82);
			timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
			updatedTime = timeSwapBuff + timeInMilliseconds;

			int secs = (int) (updatedTime / 1000);
			int mins = secs / 60;
			secs = secs % 60;

			int milliseconds = (int) (updatedTime % 100);

			if (mins > 9) {
				timerValue.setText("" + mins + ":"
						+ String.format("%02d", secs));
				timerValue.setTextSize(128);
			} else {
				timerValue.setText("" + mins + ":"
						+ String.format("%02d", secs) + ":"
						+ String.format("%02d", milliseconds));
			}

			timerValue.setVisibility(View.VISIBLE);

			customHandler.postDelayed(this, 0);
		}
	};
	
	// ****************************************************************************************************//

	public class MovementsAdapter extends ArrayAdapter<Integer> {
		public MovementsAdapter(ArrayList<Integer> movements) {
			super(getActivity(), 0, movements);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(
						R.layout.wod_list_item, null);
			}

			final int move = getItem(position);

			TextView movementName = (TextView) convertView
					.findViewById(R.id.movement_list_item_name);
			TextView movementReps = (TextView) convertView
					.findViewById(R.id.movement_list_item_reps);
			final Button aboutMove = (Button) convertView
					.findViewById(R.id.btnViewAbout);

			CharSequence[] s = getResources().getTextArray(
					R.array.wod_style_array);
			CharSequence wodString = s[position];
			// Toast.makeText(v.getContext(), wodString,
			// Toast.LENGTH_SHORT).show();
			// wod_type.setText(TextUtils.concat(wodString,
			// m.wodScoring[position]));
			Movement m = new Movement();
			CharSequence movement = getResources().getText(R.string.movement);
			movementName.setText(TextUtils.concat(movement,
					m.movementNames[move]));

			CharSequence reps = getResources().getText(R.string.reps);

			// set appropriate reps for work out
			if (stylePos == 0) { // 3 rounds for time
				movementReps.setText(TextUtils.concat(reps, repsAdj[move]));
			} else if (stylePos == 1) { // 21-15-9
				movementReps.setText(TextUtils.concat(reps, "21-15-9"));
			} else { // max reps
				movementReps.setText("Max Reps in 7 Minutes");
			}

			// set about button
			aboutMove.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					if (timerRunning == false) {
						// GOES TO DEMO VID
						Intent demoMov = new Intent(v.getContext(), DemoVidActivity.class);
						startActivity(demoMov);
						
						// GOES TO MOVEMENT DESCRIPTION
//						Intent describeMov = new Intent(v.getContext(),
//								MovementDescriptionActivity.class);
//
//						describeMov.putExtra("id", move);
//						startActivity(describeMov);
					}
				}
			});

			return convertView;
		}
	}
	
	// ****************************************************************************************************//

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
