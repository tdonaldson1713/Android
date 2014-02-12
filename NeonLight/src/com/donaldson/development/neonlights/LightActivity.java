package com.donaldson.development.neonlights;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class LightActivity extends Activity {
	public static final String COLOR_ARGS = "color";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_color);

		int color = getIntent().getIntExtra(COLOR_ARGS, 1);
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_color);
		layout.setBackgroundResource(color);
		
		changeBrightness();
	}
	
	// Change the brightness of the current activity.
	private void changeBrightness() {
		Window w = getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.screenBrightness = 1.0F;
		w.setAttributes(lp);
	}
}
