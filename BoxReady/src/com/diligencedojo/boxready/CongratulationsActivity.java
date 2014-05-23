package com.diligencedojo.boxready;

import android.support.v4.app.Fragment;

public class CongratulationsActivity extends SingleFragmentActivity {

	@Override
	protected Fragment createFragment() {

		return new CongratulationsFragment();
	}

}
