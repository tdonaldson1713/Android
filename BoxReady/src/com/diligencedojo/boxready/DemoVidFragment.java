package com.diligencedojo.boxready;

import android.content.Intent;
import android.net.Uri;
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
import android.widget.MediaController;
import android.widget.VideoView;

public class DemoVidFragment extends Fragment {

	private Button mPlayButton;
	// this is referencing a single video now, but when multiple videos exist, a
	// form of loop searching
	// will be more appropriate
	private Uri videoUri = Uri.parse("android.resource://"
			+ "com.diligencedojo.boxready/" + R.raw.push_ups);

	// ****************************************************************************************************//
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	// ****************************************************************************************************//

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.demo_vid, parent, false);
		final VideoView mVideo = (VideoView) v.findViewById(R.id.video_view);
		mVideo.setMediaController(new MediaController(getActivity()));

		// get intent data////////
		// Intent i = getActivity().getIntent();

		mPlayButton = (Button) v.findViewById(R.id.demo_playButton);
		mPlayButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mPlayButton.getText().equals("Play")
						|| (mPlayButton.getText().equals("Replay"))) {
					mVideo.start();
					mVideo.setVideoURI(videoUri);
					mPlayButton.setText("Stop");
					mPlayButton
							.setBackgroundResource(R.drawable.silver_button_background);
				}

				else if (mPlayButton.getText().equals("Stop")) {
					mVideo.pause();
					mPlayButton.setText("Replay");
					mPlayButton
							.setBackgroundResource(R.drawable.green_shaded_button_background);
				}
			}
		});

		setHasOptionsMenu(true);

		return v;
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
