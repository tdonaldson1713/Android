package com.diligencedojo.boxready;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


public class WODListActivity extends SingleFragmentActivity{
	
	ArrayList<Integer> movements = new ArrayList<Integer>();
	ArrayList<Integer> backArray = new ArrayList<Integer>();
	
	@Override
	protected Fragment createFragment() {
		movements.addAll((Collection<? extends Integer>) getIntent().getSerializableExtra(WODListFragment.NEW_MOVE_ADDED));
		
		return WODListFragment.newInstance(movements);
	}

	@Override 
	public void onPause() {
		super.onPause();
		setMovements(movements);
	}
	
	
	public void setMovements(ArrayList<Integer> moves) {
		
		//Log.d("amovements", "size: " + moves.size());
		Intent data = new Intent();
		data.putExtra(WODListFragment.NEW_MOVE_ADDED, moves);
		setResult(Activity.RESULT_OK, data);
	}

	@Override
	public void onBackPressed() {
		
		int moveCount = getIntent().getIntExtra("count", -1);
		Log.d("moveCount", "onBackPressed: " + moveCount);
		
		if (movements.size() == moveCount) {
//			Toast.makeText(getApplicationContext(), "The back button.", Toast.LENGTH_SHORT).show();
//			Toast.makeText(getApplicationContext(), "has been disabled.", Toast.LENGTH_SHORT).show();
//			Toast.makeText(getApplicationContext(), "Your workout is either full or", Toast.LENGTH_SHORT).show();
//			Toast.makeText(getApplicationContext(), "it has been finalized or completed.", Toast.LENGTH_SHORT).show();
		} 
		else {
			super.onBackPressed();
		}
	}

}
