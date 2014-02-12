package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

class PercentageGradeListArrayAdapter extends ArrayAdapter<String> {
	private Context context;
	ArrayList<String> categories = new ArrayList<String>();
	private boolean isTeacher;
	private long class_id = -1;
	private long student_id = -1;

	public PercentageGradeListArrayAdapter(Context context, int resource,
			ArrayList<String> objects, boolean teacher, long classId, long studentId) {
		super(context, resource, objects);

		this.context = context;
		categories.addAll(objects);
		isTeacher = teacher;
		class_id = classId;
		student_id = studentId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) { 
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.list_item_grade_overview, null);
		TextView gradePercentage = (TextView) v.findViewById(R.id.txtGradePercentage);
		TextView gradeOverall = (TextView) v.findViewById(R.id.txtGradeOverall);
		final TextView gradeCategory = (TextView) v.findViewById(R.id.txtCategoryTitle);
		Parse parse = new Parse();

		try {
			parse.parseString(categories.get(position));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		gradeCategory.setText(parse.category);
		gradeOverall.setText(parse.earnedGrade + "%");
		gradePercentage.setText(parse.percentage + "%");

		if (!parse.category.equals("Final Grade")) {
			v.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isTeacher) {
						Intent i = new Intent(context, GradeBreakdownActivity.class);
						i.putExtra(GradeBreakdownActivity.ARGS_CLASS_ID, class_id);
						i.putExtra(GradeBreakdownActivity.ARGS_ALL_GRADES, false);
						i.putExtra(GradeBreakdownActivity.ARGS_IS_TEACHER, true);
						i.putExtra(GradeBreakdownActivity.ARGS_STUDENT_ID, student_id);
						i.putExtra(GradeBreakdownActivity.ARGS_CATEGORY_NAME, gradeCategory.getText().toString());
						context.startActivity(i);
					} else {
						Intent i = new Intent(context, GradeBreakdownActivity.class);
						i.putExtra(GradeBreakdownActivity.ARGS_CLASS_ID, class_id);
						i.putExtra(GradeBreakdownActivity.ARGS_ALL_GRADES, false);
						i.putExtra(GradeBreakdownActivity.ARGS_IS_TEACHER, false);
						i.putExtra(GradeBreakdownActivity.ARGS_CATEGORY_NAME, gradeCategory.getText().toString());
						context.startActivity(i);
					}
				}
			});
		}

		return v;
	}

	class Parse {
		public String category;
		public String earnedGrade;
		public String percentage;

		public void parseString(String parse) throws ParseException {
			category = parse.substring(0, parse.indexOf(","));
			parse = parse.replace(category+",", "");

			earnedGrade = parse.substring(0, parse.indexOf(","));		
			parse = parse.replace(earnedGrade+",", "");
			DecimalFormat df1 = new DecimalFormat("0.00");
			earnedGrade = df1.format(Double.valueOf(earnedGrade));

			percentage = parse.substring(0);
			DecimalFormat df2 = new DecimalFormat("0");
			percentage = df2.format(Double.valueOf(percentage) * 100);
		}
	}
}
