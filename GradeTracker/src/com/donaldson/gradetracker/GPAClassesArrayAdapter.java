package com.donaldson.gradetracker;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class GPAClassesArrayAdapter extends ArrayAdapter<Class> {
	private Context mContext;
	private ArrayList<Class> list = new ArrayList<Class>();

	GradePointCalculation gpa = new GradePointCalculation();

	public GPAClassesArrayAdapter(Context context, int resource,
			ArrayList<Class> objects) {
		super(context, resource, objects);
		list.addAll(objects);
		mContext = context;
	}

	@Override public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.list_gpa_item, parent, false);

		final TextView class_name = (TextView) v.findViewById(R.id.text_gpa_calculate_class_title);		
		final EditText editScale = (EditText) v.findViewById(R.id.scale_value);
		final EditText editGPA = (EditText) v.findViewById(R.id.gpa_points);

		double grade = list.get(position).getOverallGrade();

		if (grade >= 90) {
			class_name.setText(list.get(position).getClassName() + " (A)");
			editGPA.setText(gpa.getGPA("A").scale_value + "");
		} else if (grade < 90 && grade >= 85) {
			class_name.setText(list.get(position).getClassName() + " (B+)");
			editGPA.setText(gpa.getGPA("B+").scale_value + "");
		} else if (grade >= 80 && grade < 85) {
			class_name.setText(list.get(position).getClassName() + " (B)");
			editGPA.setText(gpa.getGPA("B").scale_value + "");
		} else if (grade < 80 && grade >= 75) {
			class_name.setText(list.get(position).getClassName() + " (C+)");
			editGPA.setText(gpa.getGPA("C+").scale_value + "");
		} else if (grade >= 70 && grade < 75) {
			class_name.setText(list.get(position).getClassName() + " (C)");
			editGPA.setText(gpa.getGPA("C").scale_value + "");
		} else if (grade < 70 && grade >= 65) {
			class_name.setText(list.get(position).getClassName() + " (D+)");
			editGPA.setText(gpa.getGPA("D+").scale_value + "");
		} else if (grade >= 60 && grade < 65) {
			class_name.setText(list.get(position).getClassName() + " (D)");
			editGPA.setText(gpa.getGPA("D").scale_value + "");
		} else {
			class_name.setText(list.get(position).getClassName() + " (F)");
			editGPA.setText(gpa.getGPA("F").scale_value + "");
		}

		if (list.get(position).getCredits() > 0) {
			editScale.setText(list.get(position).getCredits() + "");
		} else {
			editScale.setText("");
		}

		editScale.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus && !editScale.getText().toString().equals("")) {
					double value = Double.valueOf(editScale.getText().toString());
					list.get(position).setCredits(value);
				}
			}
		});

		list.get(position).setGPAScale(Double.valueOf(editGPA.getText().toString()));
		notifyDataSetChanged();

		return v;
	}
}