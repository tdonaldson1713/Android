package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.donaldson.gradetracker.DatabaseHelper.GradeCursor;

public class ListViewCategoryAdapter extends ArrayAdapter<Grade> {
	private ArrayList<Grade> grades;
	private Context mContext;
	private DecimalFormat df = new DecimalFormat("0.00");
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.list_item_grade_activity, null);

		TextView txtCategoryTitle = (TextView) v.findViewById(R.id.textCategory);
		TextView txtIndividualGrade = (TextView) v.findViewById(R.id.textEarnedByPossible);
		TextView txtCategoryGrade = (TextView) v.findViewById(R.id.textCategoryPercentage);
		TextView txtOverallGrade = (TextView) v.findViewById(R.id.textOverallPercentage);
		ImageButton editGrade = (ImageButton) v.findViewById(R.id.editGrade);
		ImageButton deleteGrade = (ImageButton) v.findViewById(R.id.deleteGrade);

		txtCategoryTitle.setText(grades.get(position).getGradeCategory());
		txtIndividualGrade.setText(grades.get(position).getEarnedGrade() + " / " + grades.get(position).getPossibleGrade());
		txtCategoryGrade.setText((grades.get(position).getPercentage() * 100) + "%");

		editGrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				createEditGradeDialog(position);
			}
		});

		deleteGrade.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				actuallyDelete(position);
			}
		});

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

	private ArrayList<String> getCategories() {
		ArrayList<String> categories = new ArrayList<String>();
		for (int a = 0; a < grades.size(); a++) {
			if (!categories.contains(grades.get(a).getGradeCategory())) {
				categories.add(grades.get(a).getGradeCategory());
			}
		}

		return categories;
	}

	private void actuallyDelete(final int position) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("Delete");
		builder.setMessage("Are you sure you want to delete?");
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				GradeCursor cursor = GradeLab.get(mContext).deleteGrade(grades.get(position));
				cursor.moveToFirst();
				Grade mGrade = cursor.getGradeInfo();
				cursor.close();
				dialog.dismiss();
				
				grades.remove(position);
				ListViewCategoryAdapter.this.notifyDataSetChanged();
			}
		});
		
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
		
	}
	private void createEditGradeDialog(final int pos) {
		Resources r = mContext.getResources();
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View edit_grade = inflater.inflate(R.layout.dialog_new_grade, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		final AlertDialog edit_dialog = builder.create();
		edit_dialog.setView(edit_grade, 0, 0, 0, 0);

		ImageView banner = (ImageView) edit_grade.findViewById(R.id.imageView1);
		banner.setImageResource(R.drawable.edit_grade);

		final ImageButton insert_button = (ImageButton) edit_grade.findViewById(R.id.imgInsertGrade);
		insert_button.setImageResource(R.drawable.edit);

		final Spinner grade_type_spinner = (Spinner) edit_grade.findViewById(R.id.spinnerGradeType);
		final EditText grade_earned = (EditText) edit_grade.findViewById(R.id.editGradeEarned);
		final EditText grade_possible = (EditText) edit_grade.findViewById(R.id.editGradePossible);
		final EditText grade_percentage = (EditText) edit_grade.findViewById(R.id.editGradePercentage);		

		if (!isPercentage) {
			grade_percentage.setEnabled(false);
		}

		/*
		 * Have to manually change the colors since they inherit from the parent activity
		 */
		grade_earned.setTextColor(r.getColor(android.R.color.black));
		grade_possible.setTextColor(r.getColor(android.R.color.black));
		grade_percentage.setTextColor(r.getColor(android.R.color.black));

		/*
		 * Put the stored values into the edit texts and populate the spinner
		 * with only the categories that have grades entered
		 */
		grade_earned.setText(String.valueOf(grades.get(pos).getEarnedGrade()));
		grade_possible.setText(String.valueOf(grades.get(pos).getPossibleGrade()));
		grade_percentage.setText(String.valueOf(grades.get(pos).getPercentage() * 100));

		ArrayList<String> categories = new ArrayList<String>();
		categories.addAll(getCategories());

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, categories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		grade_type_spinner.setAdapter(adapter);

		for (int a = 0; a < categories.size(); a++) {
			if (grade_type_spinner.getItemAtPosition(a).toString().equals(grades.get(pos).getGradeCategory())) {
				grade_type_spinner.setSelection(a);
				break;
			}
		}
		grade_type_spinner.setEnabled(false);

		insert_button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				double percentage = 0.0;

				if (!grade_earned.getText().toString().equals("")) {
					grades.get(pos).setEarnedGrade(Double.valueOf(grade_earned.getText().toString()));
				}

				if (!grade_possible.getText().toString().equals("")) {
					grades.get(pos).setPossibleGrade(Double.valueOf(grade_possible.getText().toString()));
				}

				if (isPercentage && !grade_percentage.getText().toString().equals("")) {
					percentage = Double.valueOf(grade_percentage.getText().toString());
					if (percentage > 1) {
						percentage /= 100.0;
					}
				} else {
					percentage = 0.01;
				}


				// This is so we can change all the categories later on.
				boolean percentage_change = false;
				String category_title = "";
				if (percentage != grades.get(pos).getPercentage()) {
					percentage_change = true;
					category_title = grades.get(pos).getGradeCategory();
				}

				grades.get(pos).setPercentage(Double.valueOf(df.format(percentage)));

				/*
				 * Finish updating. If the grade of the percentage has changed, we need to
				 * update all the grades with that category also.
				 */
				GradeCursor cursor = GradeLab.get(mContext).updateGrade(grades.get(pos));
				cursor.moveToFirst();
				Grade mGrade = cursor.getGradeInfo();
				cursor.close();

				/*
				 * Here we will update all the grades of a category...only if
				 * the user has change the percentage.
				 */

				if (percentage_change) {
					for (int a = 0; a < grades.size(); a++) {
						if (grades.get(a).getGradeCategory().equals(category_title)) {
							grades.get(a).setPercentage(percentage);
							GradeCursor temp = GradeLab.get(mContext).updateGrade(grades.get(a));
							temp.moveToFirst();
							Grade tempG = temp.getGradeInfo();
							cursor.close();
							
							grades.set(a, tempG);
							ListViewCategoryAdapter.this.notifyDataSetChanged();
						}
					}
				}

				edit_dialog.dismiss();
			}
		});

		edit_dialog.show();
	}
}
