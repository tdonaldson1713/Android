package com.gotu.socialshare;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;

public class MainActivity extends Activity implements 
com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks, 
com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener,
OnClickListener {

	/* Request code used to invoke sign in user interactions. */
	private static final int RC_SIGN_IN = 0;

	/* Client used to interact with Google APIs. */
	private GoogleApiClient mGoogleApiClient;

	private boolean mIntentInProgress;
	private boolean mSignInClicked;
	private ConnectionResult mConnectionResult;

	private com.google.android.gms.common.SignInButton signInButton;
	private Button signOutButton;
	private Button shareButton;
	private EditText edit_share_info;
	private String browserShareString;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this)
		.addApi(Plus.API) // just removed the second parameter of null.
		.addScope(Plus.SCOPE_PLUS_LOGIN)
		.build();

		edit_share_info = (EditText)  findViewById(R.id.edit_share_info);
		browserShareString = getIntent().getStringExtra(Intent.EXTRA_TEXT);
		edit_share_info.setText("'" + browserShareString + "'"); // Have to surround in quotes to be able to share properly
		edit_share_info.setEnabled(false); // Disable the edittext so users can't edit the link, might have another edittext to allow the user to type what they want.
		
		signInButton = (com.google.android.gms.common.SignInButton) findViewById(R.id.sign_in_button);
		signInButton.setOnClickListener(this);

		signOutButton = (Button) findViewById(R.id.sign_out_button);
		signOutButton.setOnClickListener(this);

		shareButton = (Button) findViewById(R.id.share_button);
		shareButton.setOnClickListener(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
	}

	@Override
	protected void onStop() {
		super.onStop();

		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}

	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;
			edit_share_info.setText("");
			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
	}

	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		mSignInClicked = false;
		Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();
	}

	public void onConnectionSuspended(int cause) {
		mGoogleApiClient.connect();
	}

	@Override
	public void onClick(View view) {
		if (view.getId() == R.id.sign_in_button
				&& !mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
		}

		if (view.getId() == R.id.sign_out_button) {
			if (mGoogleApiClient.isConnected()) {
				Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
				mGoogleApiClient.disconnect();
				Toast.makeText(this, "User has been disconnected!", Toast.LENGTH_SHORT).show();
				mGoogleApiClient.connect();
			}
		}

		if (view.getId() == R.id.share_button) {		

			if (edit_share_info.getText().toString().equals("")) {
				Toast.makeText(this, "Please enter text into the text box", Toast.LENGTH_SHORT).show();
			} else {
				Intent shareIntent= new PlusShare.Builder(this)
				.setType("text/plain")
				.setText(edit_share_info.getText().toString())
				.getIntent();

				startActivityForResult(shareIntent, 0);
			}
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		if (!mIntentInProgress) {
			mConnectionResult = result;

			if (mSignInClicked) {
				resolveSignInError();
			}
		}
	}
}
