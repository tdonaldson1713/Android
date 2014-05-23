package com.donaldson.gradetracker.startup;

import com.donaldson.gradetracker.R;
import com.donaldson.gradetracker.R.drawable;
import com.donaldson.gradetracker.R.id;
import com.donaldson.gradetracker.R.layout;
import com.donaldson.gradetracker.R.string;
import com.donaldson.gradetracker.database.user.UserLab;
import com.donaldson.gradetracker.login.DialogLoginFragment;
import com.donaldson.gradetracker.signup.SignUpActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

public class StartUpFragment extends Fragment {
	private static final String DIALOG_LOGIN = "login";
	private static final int REQUEST_LOGIN = 0;
	ImageButton btnCreateNew;
	ImageButton btnLogin;
	ImageButton btnAbout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_start_up, parent, false);
		setBackground(v);

		btnLogin = (ImageButton) v.findViewById(R.id.imgBtnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				int userCount = UserLab.get(getActivity()).selectUsers();
				if (userCount > 0) {
					DialogLoginFragment loginDialog = DialogLoginFragment.newInstance();
					loginDialog.setTargetFragment(StartUpFragment.this, REQUEST_LOGIN);
					loginDialog.show(getActivity().getSupportFragmentManager(), DIALOG_LOGIN);
				} else {
					Toast.makeText(getActivity(), R.string.empty_user, Toast.LENGTH_LONG).show();
					userSignUp();
				}
			}
		});

		btnCreateNew = (ImageButton) v.findViewById(R.id.imgBtnNew);
		btnCreateNew.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				userSignUp();
			}
		});
/*
		btnAbout = (ImageButton) v.findViewById(R.id.imgBtnAbout);
		btnAbout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "About", Toast.LENGTH_SHORT).show();
			}
		});*/

		return v;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		if (requestCode == REQUEST_LOGIN) {
			// do nothing
		}	
	}

	// We have to change the background depending on what the orientation of the device
	// is at the current moment.
	private void setBackground(View v) {
		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

		if (metrics.heightPixels > metrics.widthPixels) {
			v.setBackgroundResource(R.drawable.launch_background_port);
		} else {
			v.setBackgroundResource(R.drawable.launch_background_land);
		}
	}
	
	public void userSignUp() {
		Intent i = new Intent(getActivity(), SignUpActivity.class);
		startActivity(i);
	}
}
