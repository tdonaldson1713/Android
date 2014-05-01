package com.donaldson.gradetracker;

import java.util.ArrayList;
import java.util.Collection;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

public class DialogGradeViewer extends DialogFragment {
	private static final String ARGS_GRADES = "grades";
	private static final String ARGS_USER_NAME = "name";
	private ArrayList<Grade> grades = new ArrayList<Grade>();
	private String username;
	
	ImageButton btnSettings;
	
	public static DialogGradeViewer newInstance(ArrayList<Grade> grades, String name) {
		Bundle args = new Bundle();
		args.putSerializable(ARGS_GRADES, grades);
		args.putString(ARGS_USER_NAME, name);
		
		DialogGradeViewer fragment = new DialogGradeViewer();
		fragment.setArguments(args);

		return fragment;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Bundle args = getArguments();
		
		
		grades.addAll((Collection<? extends Grade>) args.getSerializable(ARGS_GRADES));
		username = args.getString(ARGS_USER_NAME);
		
		View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_grade_overview, null);
		
		TextView textTitle = (TextView) view.findViewById(R.id.txtGradeTitle);
		textTitle.setText(getString(R.string.grade_overview_title, username));
		
		
		Toast.makeText(getActivity(), btnSettings.getId(), Toast.LENGTH_SHORT).show();

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		return dialog;
	}
}
