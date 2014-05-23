//Unused file
package com.donaldson.gradetracker.fgc;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

import com.donaldson.gradetracker.R;
import com.donaldson.gradetracker.database.classes.Class;

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
		double grade = classes.get(position).getOverallGrade();
		
		Log.d("TEST", "Grade: " + grade);
		if (grade == -1.0) {
			grade = 0.0;
		}
		
		rbutton.setText(classes.get(position).getClassName() + " - " + grade + "%");

		return v;
	}
}