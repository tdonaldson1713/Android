package com.diligencedojo.boxready;

import android.support.v4.app.Fragment;

public class AppExplanationActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {

		return new AppExplanationFragment();
	}

}