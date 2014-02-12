package com.donaldson.gradetracker;

import java.text.DecimalFormat;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GPACalculatorActivity extends Activity {
	public static final String ARGS_LIST_CLASSES = "classes";

	public ImageButton imgBtnCalculate;
	public ImageView imgHeader;
	public ListView list_classes;
	private double scaled_gpa = 0;
	private double non_scaled_gpa = 0;
	private double points = 0;
	private double final_points = 0;
	private int num_credits = 0;
	public ArrayList<Class> classes = new ArrayList<Class>();
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gpa_calculator);
		
		list_classes = (ListView) findViewById(R.id.listview_classes);		 
		imgHeader = (ImageView) findViewById(R.id.imgViewGPAHeader);
		imgBtnCalculate = (ImageButton) findViewById(R.id.calculate_gpa_final);
		
		initialLoad();
		
		final GPAClassesArrayAdapter adapter = new GPAClassesArrayAdapter(this, R.id.listview_classes, classes);
		list_classes.setAdapter(adapter);
		
		final DecimalFormat df = new DecimalFormat("0.00");
		
		if (classes.size() == 0) {
			imgBtnCalculate.setEnabled(false);
		}
		
		imgBtnCalculate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.notifyDataSetChanged();
				getPoints();
				scaled_gpa = Double.valueOf(df.format(CalculateGrades.calculate_scaled_gpa(final_points, num_credits)));
				non_scaled_gpa = Double.valueOf(df.format(CalculateGrades.calculate_non_scaled_gpa(points, classes.size())));
				createGPADialog();
				resetValues();
			}
		});
	}
	
	private void resetValues() {
		points = 0; 
		num_credits = 0;
		final_points = 0;
	}

	private void getPoints() {
		double p, c;
		
		for (int a = 0; a < classes.size(); a++) {
			p = classes.get(a).getGPAScale();
			c = classes.get(a).getCredits();
			
			points += p;
			num_credits += c;
			final_points += (p * c);
		}
	}
	
	private void createGPADialog() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View dialogView = inflater.inflate(R.layout.dialog_calculated_gpa, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		dialog.setView(dialogView, 0, 0, 0, 0);
		dialog.show();
		
		ImageButton btnCancel = (ImageButton) dialogView.findViewById(R.id.cancel_gpa_calculated);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		
		TextView txtScaled = (TextView) dialogView.findViewById(R.id.textScaledGPA);
		TextView txtNotScaled = (TextView) dialogView.findViewById(R.id.textNotScaledGPA);
		
		txtScaled.setText(String.valueOf(scaled_gpa));
		txtNotScaled.setText(String.valueOf(non_scaled_gpa));
	}
	
	private void getClasses(ArrayList c) {
		for (int a = 0; a < c.size(); a++) {
			classes.add((Class) c.get(a));
		}
	}

	private void initialLoad() {
		Intent i = this.getIntent();
		ArrayList classList = i.getParcelableArrayListExtra(ARGS_LIST_CLASSES);
		getClasses(classList);
		classList.clear();
		
		scaleListView();
		scaleAddButton();
	}
	/* 
	 * This function is used to scale the expandable list view according to
	 * the size of the screen. This is necessary so that static buttons can exist 
	 * to add a new class or student to a specific class.
	 */
	private void scaleListView() {
		long height = getLayoutHeight();
		list_classes.getLayoutParams().height = (int) (height * 0.60);
		list_classes.setPadding(0, (int)(height*0.025), 0, (int)(height*0.025));
	}

	/*
	 * Scale the add button to the rest of the screen after the expandable list has
	 * has sized accordingly.
	 */
	private void scaleAddButton() {
		long height = getLayoutHeight();
		imgBtnCalculate.getLayoutParams().height = (int) (height * 0.10);
		imgBtnCalculate.setPadding(0, (int)(height*0.025), 0, (int)(height*0.025));
	}
	
	private long getLayoutHeight() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		return metrics.heightPixels;
	}
	
	
}
