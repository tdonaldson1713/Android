package com.diligencedojo.boxready;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ListSample extends Activity {

	public final static String ITEM_TITLE = "title";
	public final static String ITEM_CAPTION = "caption";

	// Section Headers
	private final static String[] weekNums = new String[] { "Week 1", "Week 2",
			"Week 3", "Week 4", "Week 5", "Week 6", "Week 7", "Week 8" };

	// Section Contents
	private final static String[] workoutNums = new String[] { "Workout 1",
			"Workout 2", "Workout 3" };

	// Adapter for ListView Contents
	private SeperatedListAdapter adapter;

	public Map<String, ?> createItem(String title, String caption) {
		Map<String, String> item = new HashMap<String, String>();
		item.put(ITEM_TITLE, title);
		item.put(ITEM_CAPTION, caption);
		return item;
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		// Get the user's work out time
		Intent intent = getIntent();
		final String wodTime = intent.getExtras().getString("wodTime");
		byte[] byteArray = intent.getByteArrayExtra("image");
		final Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

		List<Map<String, ?>> security = new LinkedList<Map<String, ?>>();
		for (int a = 0; a < workoutNums.length; a++) {
			security.add(createItem(workoutNums[a], "Score:  " + wodTime));
		}

		// Create the ListView Adapter
		adapter = new SeperatedListAdapter(this);
		
		// Add Sections (Weeks 1 - 8)
		for (int i = 0; i < weekNums.length; i++) {
			adapter.addSection(weekNums[i], new SimpleAdapter(this, security,
					R.layout.list_complex, new String[] { ITEM_TITLE,
							ITEM_CAPTION },
					new int[] { R.id.list_complex_title,
							R.id.list_complex_wodTime }));
		}

		ListView list = new ListView(this);
		list.setAdapter(adapter);
		this.setContentView(list);
		
		// Listen for Click events on list items
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long duration) {
				ImageView iv = (ImageView) view.findViewById(R.id.screenshots);
				iv.setImageBitmap(bmp);
				
				// displays week number for item selected
				String section = (String) adapter.getSection(position);
				Toast.makeText(getApplicationContext(), section,
						Toast.LENGTH_SHORT).show();
				
				// displays work out number for item selected
				String item = (String) adapter.getItem(position).toString();
				Toast.makeText(getApplicationContext(), item,
						Toast.LENGTH_LONG).show();
			}
		});
	}
}

// // Sets the View Layer
// setContentView(R.layout.main);
//
// // Get the user's work out time
// Intent intent = getIntent();
// final String wodTime = intent.getExtras().getString("wodTime");
//
// workoutNums[0] = workoutNums[0] + "\t" + wodTime;
//
// // Create the ListView Adapter
// adapter = new SeperatedListAdapter(this);
// ArrayAdapter<String> listadapter = new ArrayAdapter<String>(this,
// R.layout.list_item, workoutNums);
//
// // Add Sections (Weeks 1 - 8)
// for (int i = 0; i < weekNums.length; i++) {
// adapter.addSection(weekNums[i], listadapter);
// }
//
// // Get a reference to the ListView holder
// logListView = (ListView) this.findViewById(R.id.list_journal);
//
// // Set the adapter on the ListView holder
// logListView.setAdapter(adapter);
//
// // Listen for Click events on list items
// logListView.setOnItemClickListener(new OnItemClickListener() {
// @Override
// public void onItemClick(AdapterView<?> parent, View view,
// int position, long duration) {
// // displays week number for item selected
// String section = (String) adapter.getSection(position);
// Toast.makeText(getApplicationContext(), section,
// Toast.LENGTH_SHORT).show();
// // displays work out number for item selected
// String item = (String) adapter.getItem(position);
// Toast.makeText(getApplicationContext(), item,
// Toast.LENGTH_SHORT).show();
// }
// });
// }

