package com.donaldson.gradetracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class DialogLoginFragment extends DialogFragment {
	private String userName;
	private String userPassword;
	private String sendInfo;
	private String sendPhoneNumber;
	private String sendEmail;
	private int incorrectTimes = 0;
	private boolean ableToSend = false;

	public static DialogLoginFragment newInstance() {
		Bundle args = new Bundle();

		DialogLoginFragment fragment = new DialogLoginFragment();
		fragment.setArguments(args);

		return fragment;
	}

	public void sendResult(int resultCode) {
		if (getTargetFragment() == null) {
			return;
		}

		Intent i = new Intent();
		getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		//Bundle args = getArguments();

		View dialogLogin = getActivity().getLayoutInflater().inflate(R.layout.dialog_login, null);

		final EditText loginUsername = (EditText) dialogLogin.findViewById(R.id.editLoginUsername);
		final EditText loginPassword = (EditText) dialogLogin.findViewById(R.id.editLoginPassword);
		final ImageButton loginBtn = (ImageButton) dialogLogin.findViewById(R.id.imageButton1);
		final ImageButton cancelBtn = (ImageButton) dialogLogin.findViewById(R.id.imageButton2);

		// The password edittext gets focus first so at creation of the dialog
		// we need to make the username request focus.
		loginUsername.requestFocus();

		loginBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				userName = loginUsername.getText().toString();
				userPassword = loginPassword.getText().toString();
				UserAccount user = UserLab.get(getActivity()).selectUser(userName, userPassword);

				//Toast.makeText(getActivity(), user.getUsername() + " " + user.getPassword(), Toast.LENGTH_SHORT).show();
				if (loginUsername.getText().toString().equals("") || 
						loginPassword.getText().toString().equals("")) {
					Toast.makeText(getActivity(), getString(R.string.empty_login),
							Toast.LENGTH_SHORT).show();
				} else {
					if (user.getUsername().equals(userName) && user.getPassword().equals(userPassword)) {
						Intent i = new Intent(getActivity(), ClassListActivity.class);
						i.putExtra(ClassListActivity.ARG_USER_ID, user.getId());
						startActivity(i);
						DialogLoginFragment.this.getDialog().cancel();
					} else {
						incorrectTimes++;
						String toastText = getString(R.string.incorrectLogin, incorrectTimes);
						Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
					}

					/*if (incorrectTimes == 3) {
						create_failedLoginDialog();
					}*/
				}
			}
		});

		cancelBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogLoginFragment.this.getDialog().cancel();
			}
		});

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		AlertDialog dialog = builder.create();
		dialog.setView(dialogLogin, 0, 0, 0, 0);
		return dialog;
	}

	/*
	 * This function creates and shows a dialog when the user enters their login information
	 * incorrectly after three times. 
	 */
	private void create_failedLoginDialog() {
		findUserInformation();

		if (ableToSend) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setTitle(R.string.failed_login);
			builder.setItems(new String[]{getString(R.string.send)}, null);
			builder.setPositiveButton(R.string.send_text, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendInformationThroughText();
					DialogLoginFragment.this.getDialog().cancel();
				}
			});

			builder.setNeutralButton(R.string.send_email, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendInformationThroughEmail();
					DialogLoginFragment.this.getDialog().cancel();
				}
			});

			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getActivity(), getString(R.string.failed_to_send), 
							Toast.LENGTH_SHORT).show();
					DialogLoginFragment.this.getDialog().cancel();
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	/*
	 * The username and password stored in userName and userPassword
	 * will be used to find the user's information.
	 * 
	 * If the username is found, the password is sent
	 * If the password is found, the username is sent.
	 */
	private void findUserInformation() {
		UserAccount lookupUser = UserLab.get(getActivity())
				.loginFailSelectUser(userName, userPassword);

		// Checks to see if the user was even in the database.
		if (lookupUser.getId() == -1) {
			Toast.makeText(getActivity(), R.string.failed_to_find_user, Toast.LENGTH_SHORT).show();
			ableToSend = false;
		} else if (lookupUser.getUsername().equals(userName)) {
			sendInfo = lookupUser.getPassword();
			ableToSend = true;
		} else if (lookupUser.getPassword().equals(userPassword)) {
			sendInfo = lookupUser.getUsername();
			ableToSend = true;
		}

		if (ableToSend) {
			sendPhoneNumber = lookupUser.getPhoneNumber();
			sendEmail = lookupUser.getEmail();
		}
	}

	private void sendInformationThroughText() {
		Toast.makeText(getActivity(), getString(R.string.sent_text, sendPhoneNumber),
				Toast.LENGTH_SHORT).show();
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(sendPhoneNumber, null, sendInfo, null, null);

	}

	private void sendInformationThroughEmail() {
		Toast.makeText(getActivity(), getString(R.string.sent_email, sendEmail),
				Toast.LENGTH_SHORT).show();
	}
}
