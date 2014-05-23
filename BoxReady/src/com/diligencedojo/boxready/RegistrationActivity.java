package com.diligencedojo.boxready;

import android.support.v4.app.Fragment;

public class RegistrationActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {

		return new RegistrationFragment();
	}

}
