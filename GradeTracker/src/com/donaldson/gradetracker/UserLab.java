package com.donaldson.gradetracker;

import android.content.Context;

import com.donaldson.gradetracker.DatabaseHelper.UserCursor;

public class UserLab {
	private static UserLab sUserLab;
	private Context mAppContext;
	private DatabaseHelper mUserHelper;
	
	private UserLab(Context appContext) {
		mAppContext = appContext;
		mUserHelper = new DatabaseHelper(mAppContext);
	}
	
	public static UserLab get(Context c) {
		if (sUserLab == null) {
			sUserLab = new UserLab(c.getApplicationContext());
		}
		return sUserLab;
	}
	
	public long insertUser(UserAccount user) {
		return mUserHelper.insertUser(user);
	}
	
	/*	 * 
	 * The name and password are used as the comparisons in the database to 
	 * remove a user's account. This is sufficient since the username
	 * will be unique, but will need the password to verify that the user 
	 * is actually the person deleting the account.
	 * 
	 * UserCursor will be null for the following reasons:
	 * 		1. User doesn't exist.
	 * 		2. User password doesn't match in the database
	 * 		3. User username doesn't match in the database.
	 * 
	 * This applies for the following functions:
	 * 		1. deleteUser
	 * 		2. updateUser
	 * 		3. selectUser
	 */
	public UserCursor deleteUser(UserAccount user) {
		return mUserHelper.deleteUser(user);
	}
	
	public UserCursor updateUser(UserAccount user) {
		return mUserHelper.updateUser(user);
	}
	
	public UserAccount selectUser(long id) {
		UserAccount u = new UserAccount();
		UserCursor cursor = mUserHelper.getUser(id);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			u = cursor.getUserInfo();
		}
		
		cursor.close();
		return u;
	}
	
	public UserAccount selectUser(String username, String password) {
		UserAccount u = new UserAccount();
		UserCursor cursor = mUserHelper.getUser(username, password);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			u = cursor.getUserInfo();
		}
		
		cursor.close();
		return u;
	}
	
	/*
	 * This function is used when the user has entered their information incorrectly
	 * three times. This will return the user's information if either the username or 
	 * password is correct.
	 */
	public UserAccount loginFailSelectUser(String username, String password) {
		UserAccount u = new UserAccount();
		UserCursor cursor = mUserHelper.getUserLoginFail(username, password);
		cursor.moveToFirst();
		
		if (!cursor.isAfterLast()) {
			u = cursor.getUserInfo();
		}
		
		cursor.close();
		return u;
	}
	
	// We need to get the number of users in the database so we can
	// tell the user if they need to sign up for an account. This should
	// only take place at initial load of the application and the user 
	// clicks Login instead of signup.
	public int selectUsers() {
		UserCursor cursor = mUserHelper.getUser();
		return cursor.getCount();
	}
	
	public UserCursor selectAllUsers() {
		return mUserHelper.getUser();
	}
}
