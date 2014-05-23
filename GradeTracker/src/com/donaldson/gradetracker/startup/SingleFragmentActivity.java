
package com.donaldson.gradetracker.startup;

import java.lang.reflect.Field;

import com.donaldson.gradetracker.R;
import com.donaldson.gradetracker.R.id;
import com.donaldson.gradetracker.R.layout;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.ViewConfiguration;

public abstract class SingleFragmentActivity extends FragmentActivity {
	protected abstract Fragment createFragment();
/*
	protected int getLayoutResId() {
		return R.layout.activity_project_fragment;
	}*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_fragment);
		
		// This is used on devices that have a dedicated menu button.
		// This will allow the for 4 or more menu items to exist and to 
		// allow the user to see them in the list format
		// Source: Vogella and StackOverflow
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		FragmentManager fm = getSupportFragmentManager();
		Fragment fragment = fm.findFragmentById(R.id.fragmentClassContainer);
		if (fragment == null) {
			fragment = createFragment();
			fm.beginTransaction().add(R.id.fragmentClassContainer, fragment)
					.commit();
		}
	}
}
