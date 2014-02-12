package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ListViewCategoryAdapter extends ArrayAdapter<Grade> {
	private ArrayList<Grade> grades;
	private Context mContext;
	private boolean isPercentage;

	public ListViewCategoryAdapter(Context context, int resource,
			ArrayList<Grade> objects, boolean percentage) {
		super(context, resource, objects);

		grades = new ArrayList<Grade>();
		grades.addAll(objects);
		mContext = context;
		isPercentage = percentage;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.list_item_grade_activity, null);

		TextView txtCategoryTitle = (TextView) v.findViewById(R.id.textCategory);
		TextView txtIndividualGrade = (TextView) v.findViewById(R.id.textEarnedByPossible);
		TextView txtCategoryGrade = (TextView) v.findViewById(R.id.textCategoryPercentage);
		TextView txtOverallGrade = (TextView) v.findViewById(R.id.textOverallPercentage);

		txtCategoryTitle.setText(grades.get(position).getGradeCategory());
		txtIndividualGrade.setText(grades.get(position).getEarnedGrade() + " / " + grades.get(position).getPossibleGrade());
		txtCategoryGrade.setText("Category : " + (grades.get(position).getPercentage() * 100) + "%");

		double overall = 0;
		double overallPercentage = 0;

		if (isPercentage) {
			double earned = 0, possible = 0, percentage = 0;

			boolean first = true;

			for (int b = 0; b < (position+1); b++) {
				possible += grades.get(b).getPossibleGrade();
				earned += grades.get(b).getEarnedGrade();

				if (isPercentage && first) {
					percentage = grades.get(b).getPercentage();
					overallPercentage += percentage;
					first = false;
				}
			}

			overall += CalculateGrades.calculate_running_percentage(earned, possible, percentage);
			first = true;

		} else {
			double earned = 0, possible = 0;
			for (int a = 0; a < (position+1); a++) {
				earned += grades.get(a).getEarnedGrade();
				possible += grades.get(a).getPossibleGrade();
			}

			overall += CalculateGrades.calculate_running_points(earned, possible);

			txtCategoryGrade.setText(earned + " / " + possible);
		}

		DecimalFormat df = new DecimalFormat("0.00");

		if (isPercentage) {
			overall = Double.valueOf(df.format((overall / overallPercentage) * 100));
		} else {
			overall = Double.valueOf(df.format(overall * 100));
		}

		txtOverallGrade.setText("Overall: " + overall + "%");
		overall = 0;

		return v;
	}
}
