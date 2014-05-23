package com.donaldson.gradetracker.settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.donaldson.gradetracker.R;
import com.donaldson.gradetracker.classlist.ClassListActivity;
import com.donaldson.gradetracker.database.DatabaseHelper.UserCursor;
import com.donaldson.gradetracker.database.user.UserAccount;
import com.donaldson.gradetracker.database.user.UserLab;

public class SettingsActivity extends Activity {
	private long user_id = -1;
	private UserAccount mUser;
	
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.activity_settings);
		getUserInfo();
		
		ImageButton btnChangeUserInfo = (ImageButton) findViewById(R.id.change_user_info);
		ImageButton btnViewUpdates = (ImageButton) findViewById(R.id.view_updates);
		ImageButton btnViewBugs = (ImageButton) findViewById(R.id.view_bugs);
		ImageButton btnViewAbout = (ImageButton) findViewById(R.id.view_about);
		
		btnChangeUserInfo.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				create_user_dialog();
			}
		});

		btnViewUpdates.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				create_info_dialog(load_text_file(R.raw.updates));
			}
		});

		btnViewBugs.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				create_info_dialog(load_text_file(R.raw.bugs));
			}
		});
		
		btnViewAbout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loadAbout();
			}
		});
	}


	private void create_user_dialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_login, null);
		final AlertDialog dialog = builder.create();
	
		final EditText username = (EditText) dialogView.findViewById(R.id.editLoginUsername);
		final EditText password = (EditText) dialogView.findViewById(R.id.editLoginPassword);
		
		username.setText(mUser.getUsername());
		password.setText(mUser.getPassword());
		
		ImageView imgHeader = (ImageView) dialogView.findViewById(R.id.imageView1);
		imgHeader.setImageResource(R.drawable.update);
		
		ImageButton btnLogin = (ImageButton) dialogView.findViewById(R.id.imageButton1);
		btnLogin.setImageResource(R.drawable.update_button);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mUser.setUsername(username.getText().toString());
				mUser.setPassword(password.getText().toString());
				
				updateDatabase();
				Toast.makeText(getApplicationContext(), R.string.user_update_done, Toast.LENGTH_SHORT).show();
				dialog.dismiss();
			}
		});
		
		ImageButton btnCancel = (ImageButton) dialogView.findViewById(R.id.imageButton2);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.setView(dialogView, 0, 0, 0,0);
		dialog.show();
	}
	
	private void create_info_dialog(String fileInfo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setMessage(fileInfo);
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
		
	private String load_text_file(int resourceNumber) {
		String text = "";
		InputStream is = getResources().openRawResource(resourceNumber);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line = "";

		try {
			while ((line = br.readLine()) != null) {
				text += (line + "\n");
			}
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				isr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return text;
	}
	
	private void getUserInfo() {
		user_id = getIntent().getLongExtra(ClassListActivity.ARG_USER_ID, -1);
		mUser = UserLab.get(getApplicationContext()).selectUser(user_id);
	}
	
	private void updateDatabase() {
		UserCursor cursor = UserLab.get(getApplicationContext()).updateUser(mUser);
		cursor.moveToFirst();
		UserAccount update = cursor.getUserInfo();
		cursor.close();
	}
	
	private void loadAbout() {
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}
}
