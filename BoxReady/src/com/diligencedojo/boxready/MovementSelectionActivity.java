package com.diligencedojo.boxready;

import android.support.v4.app.Fragment;
 
public class MovementSelectionActivity extends SingleFragmentActivity {
	
	@Override
	protected Fragment createFragment() {

		return new MovementSelectionFragment();
	}

}
