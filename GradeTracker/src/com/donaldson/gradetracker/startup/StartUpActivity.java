package com.donaldson.gradetracker.startup;

import android.support.v4.app.Fragment;

public class StartUpActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {
		return new StartUpFragment();
	}

}
