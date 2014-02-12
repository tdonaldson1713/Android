package com.donaldson.gradetracker;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

public class ClassListFGCArrayAdapter extends ArrayAdapter<Class> {
	private ArrayList<Class> classes = new ArrayList<Class>();
	public Context mContext;
	
	public ClassListFGCArrayAdapter(Context context, int resource, 
			ArrayList<Class> object) {
		super(context, resource, object);
		mContext = context;
		classes.addAll(object);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) { 
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.list_fgc_item, parent, false);
		
		RadioButton rbutton = (RadioButton) v.findViewById(R.id.radio_fgc);
		rbutton.setText(classes.get(position).getClassName());

		return v;
	}
}
