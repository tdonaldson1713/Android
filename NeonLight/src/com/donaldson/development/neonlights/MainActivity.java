package com.donaldson.development.neonlights;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity {
	private ArrayList<Integer> colorList = new ArrayList<Integer>();
	private GridViewAdapter adapter;
	private long width;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Look up the AdView as a resource and load a request.
		AdView adView = (AdView)this.findViewById(R.id.adView2);
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
		
		initialLoad();
		
		GridView gridColors = (GridView) findViewById(R.id.gridView);
		gridColors.setAdapter(adapter);
	}

	public class GridViewAdapter extends BaseAdapter {
		private Context mContext;

		public GridViewAdapter(Context c) {
			mContext = c;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return colorList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return colorList.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;         

			if(convertView == null) {
				LayoutInflater li = getLayoutInflater();
				v = li.inflate(R.layout.gallery_item, null);
			} else {             
				v = convertView;         
			}

			final int pos = position;
			TextView tv = (TextView)v.findViewById(R.id.gallery_item_text);
			tv.setBackgroundResource(colorList.get(position));         
			tv.setWidth((int) (width/2));
			tv.setHeight((int) (width/2));
			
			tv.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent i = new Intent(mContext, LightActivity.class);
					i.putExtra(LightActivity.COLOR_ARGS, colorList.get(pos));
					startActivity(i);
				}
			});
			
			Log.d("TEST", "here");
			return v;   
		}

	}

	private void getWidth() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		width = metrics.widthPixels;
	}
	
	private ArrayList<Integer> getColors() {
		ArrayList<Integer> colors = new ArrayList<Integer>();
		colors.add(R.color.white);
		colors.add(R.color.green);
		colors.add(R.color.blue);
		colors.add(R.color.orange);
		colors.add(R.color.pink);
		colors.add(R.color.purple);
		colors.add(R.color.red);
		colors.add(R.color.yellow);
		return colors;
	}
	
	private void initialLoad() {
		getWidth();
		colorList.addAll(getColors());
		adapter = new GridViewAdapter(this);
	}
}
