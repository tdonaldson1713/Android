package com.diligencedojo.boxready;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;

	// Keep all Images in array
	public Integer[] mThumbIds = { R.drawable.push_up_icon,
			R.drawable.pull_up_icon, R.drawable.burpees, R.drawable.air_squats,
			R.drawable.hspu_icon, R.drawable.mtn_climbers, R.drawable.run_icon,
			R.drawable.jump_rope, R.drawable.lunges, R.drawable.box_jumps, };

	// Constructor
	public ImageAdapter(Context c) {
		mContext = c;
	}

	@Override
	public int getCount() {
		// Log.d("mThumbIds.length = ", Integer.toString(mThumbIds.length));
		return mThumbIds.length;
	}

	@Override
	public Object getItem(int position) {
		return mThumbIds[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		 ImageView imageView = new ImageView(mContext);
		 imageView.setImageResource(mThumbIds[position]);
		 imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		 imageView.setTag(new Integer(position));

//		ImageView imageView;
//
//		if (convertView == null) {
//			imageView = new ImageView(mContext);
//			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//		} else {
//			imageView = (ImageView) convertView;
//		}
//
//		imageView.setImageResource(mThumbIds[position]);

		return imageView;
	}

}
