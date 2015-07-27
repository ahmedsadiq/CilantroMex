package com.dg.android.lcp.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dg.android.facebook.BaseRequestListener;
import com.dg.android.facebook.SessionEvents;
import com.dg.android.facebook.SessionEvents.AuthListener;
import com.dg.android.facebook.SessionEvents.LogoutListener;
import com.dg.android.facebook.SessionStore;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.google.android.gcm.GCMRegistrar;

public class LoginSignupActivity extends Activity {
	public static String regId =""; 
	Context context;
	String TAG = "Log in Sign up Screen";
	TextView restaurentName;
	TextView alreadyMember;
	TextView termsOfUse;
	Button login;
	Button signupButtonBottom;
	LinearLayout signuplayout;
	TextView loginText;
	EditText repeatPassword;
	EditText email;
	EditText password;
	String errorMessage, message;
	ProgressDialog progressDialog;
	EditText dummyEditText;
	Button signup;
	String fbemail;
	Location myLocation;
	TextView forgotPassword;
	TextView openSignupScreen;
	TextView signText;
	TextView signupText;
	boolean isLoginCal = false;
	private SharedPreferences prefs;
	String deviceId;
	private String[] mPermissions = new String[] { "read_stream", "email" };
	private Handler mHandler;
	private SessionListener mSessionListener = new SessionListener();
	private Activity mActivity;

	JSONObject responseJson;
	private Facebook mFacebook;
	private AsyncFacebookRunner mAsyncRunner;
	public static final String APP_ID = "382662371799993";

	Button facebookSignUp;
	Button facebookLogin;
	String userType = "";

	Bundle extras;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.login_signup);
		Log.i(TAG, "Activity called");
		extras = getIntent().getExtras();
		LoginSignupActivity.regId =SessionManager.getResId(context);
		Log.i(TAG,"registration Id ="+LoginSignupActivity.regId);
		
		deviceId = Secure.getString(getBaseContext().getContentResolver(),Secure.ANDROID_ID);
		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		LocationManager locManager;
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L,
				500.0f, locationListener);
		myLocation = locManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		signuplayout = (LinearLayout) findViewById(R.id.loginlayout);
		dummyEditText = (EditText) findViewById(R.id.email_field_signup);
		login = (Button) findViewById(R.id.loginButton);
		loginText = (TextView) findViewById(R.id.logintext);
		facebookSignUp = (Button) findViewById(R.id.fb_button_signup);
		facebookLogin = (Button) findViewById(R.id.fb_button_login);
		alreadyMember = (TextView) findViewById(R.id.alreadyMember);
		termsOfUse = (TextView) findViewById(R.id.terms);
		email = (EditText) findViewById(R.id.email_field_signup);
		password = (EditText) findViewById(R.id.password_field_signup);
		forgotPassword = (TextView) findViewById(R.id.forgetPasswordText);
		openSignupScreen = (TextView) findViewById(R.id.openSignupScreenText);
		signup = (Button) findViewById(R.id.signupButton);

		SpannableString content = new SpannableString(getString(R.string.terms));
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		termsOfUse.setText(content);

		ApplicationController applicationController = (ApplicationController) LoginSignupActivity.this
				.getApplication();
		String appId = applicationController
				.getProperty("facebook.application.id");
		Log.v(TAG, "facebook application id = " + appId);

		mFacebook = new Facebook(APP_ID);
		mAsyncRunner = new AsyncFacebookRunner(mFacebook);
		mActivity = this;

		SessionStore.restore(mFacebook, this);
		SessionEvents.addAuthListener(new SampleAuthListener());

		mHandler = new Handler();

		SessionEvents.addAuthListener(mSessionListener);
		SessionEvents.addLogoutListener(mSessionListener);

		forgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, ForgetPasswordActivity.class);
				startActivity(i);
				finish();
			}
		});

		openSignupScreen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				LinearLayout ll = (LinearLayout) findViewById(R.id.loginlayout);
				ll.setVisibility(View.GONE);
				LinearLayout sul = (LinearLayout) findViewById(R.id.signUpLayout);
				sul.setVisibility(View.VISIBLE);
				
				email = (EditText) findViewById(R.id.email_field_signup);
				password = (EditText) findViewById(R.id.password_field_signup);
			}
		});
		
		facebookSignUp.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userType = "FBSignup";
				Log.i("Inside>>", "onClick ELSE........");
				mFacebook.authorize(mActivity, mPermissions,
						new LoginDialogListener());
				Log.i("Inside>>", "AfteronClick ELSE........");
			}
		});

		facebookLogin.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userType = "FBSignin";
				Log.i("Inside>>", "onClick ELSE........");
				mFacebook.authorize(mActivity, mPermissions,
						new LoginDialogListener());
				Log.i("Inside>>", "Afteron Click ELSE........");
			}
		});

		login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userType = "Signin";
				new LoginAsyncTask().execute();

			}
		});

		signup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				userType = "Signup";
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(dummyEditText.getWindowToken(), 0);

				new AlertDialog.Builder(LoginSignupActivity.this)
						.setTitle("")
						.setMessage(
								getString(R.string.confirm_email_prompt)+"\n\""
										+ email.getText().toString() + "\"")
						.setPositiveButton(R.string.confirm,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {
										new SignupAsyncTask().execute();
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int whichButton) {
										dialog.dismiss();
									}
								}).show();
			}
		});
		alreadyMember.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				LinearLayout sul = (LinearLayout) findViewById(R.id.signUpLayout);
				sul.setVisibility(View.GONE);
				LinearLayout ll = (LinearLayout) findViewById(R.id.loginlayout);
				ll.setVisibility(View.VISIBLE);

				email = (EditText) findViewById(R.id.email_field_login);
				password = (EditText) findViewById(R.id.password_field_login);

			}
		});

		termsOfUse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(context, TermsOfUseActivity.class);
				startActivity(i);
			}
		});
		
		
		if(extras!=null){
			if(extras.containsKey("OAuthError")){

				LinearLayout sul = (LinearLayout) findViewById(R.id.signUpLayout);
				sul.setVisibility(View.GONE);
				LinearLayout ll = (LinearLayout) findViewById(R.id.loginlayout);
				ll.setVisibility(View.VISIBLE);

				email = (EditText) findViewById(R.id.email_field_login);
				password = (EditText) findViewById(R.id.password_field_login);
			}
		}

	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mFacebook.authorizeCallback(requestCode, resultCode, data);
	}

	public class SampleAuthListener implements AuthListener {

		public void onAuthSucceed() {
			Log.i("inside sampleRequest>>", "onAuthSucceed mehtod called......");
		}

		public void onAuthFail(String error) {
		}
	}

	// //////////////////////////////////////////////
	// ////// log in ////////////////////

	private class LoginAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(dummyEditText.getWindowToken(), 0);
			errorMessage = "";
			progressDialog = new ProgressDialog(context);
			progressDialog.setMessage(getString(R.string.sign_in_wait));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (errorMessage != null && errorMessage.length() > 0)
				AndroidUtil.showMessageDialog(context, errorMessage);
			
			else if (extras!=null&&extras.containsKey("OAuthError")) {
				Log.i("LOGINSIGNUP","====================");
				Log.i("LOGINSIGNUP","====================");
				LoginSignupActivity.this.finish();
			}
			else if (SessionManager.getNextActivity(context).equals("1")) {
				String restaurentId = extras.getString("restaurantId");
				String offerId = extras.getString("offerId");
				Intent i = new Intent(context, LocationDetailActivity.class);
				i.putExtra("restaurantId", restaurentId);
				i.putExtra("offerId", offerId);
				startActivity(i);
				finish();
			} else if (SessionManager.getNextActivity(context).equals("2")) {
				Intent i = new Intent(context, RewardListActivity.class);
				startActivity(i);
				finish();
			} else if (SessionManager.getNextActivity(context).equals("4")) {
				Intent i = new Intent(context, InfoActivity.class);
				startActivity(i);
				finish();
			} else if (SessionManager.getNextActivity(context).equals("7")) {
				Intent i = new Intent(context, UpdatePasswordActivity.class);
				startActivity(i);
				finish();
			} else if (SessionManager.getNextActivity(context).equals("10")) {
				Intent i = new Intent(context, PromoCodeActivity.class);
				startActivity(i);
				finish();
			}

			else {
				Intent i = new Intent(context, DealConfirmation.class);
				startActivity(i);
				finish();
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			doSignIn();
			return null;
		}

	}

	private void doSignIn() {
		String email = ((EditText) findViewById(R.id.email_field_login))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.password_field_login))
				.getText().toString();

		String url = AndroidUtil.BASE_URL_HTTPS + "/user/login?";
//		String url = getString(R.string.https_url) + "/user/login?";
		String params;
		if (userType.equalsIgnoreCase("Signin")) {
			params = "appkey=" + ApplicationController.APP_ID
					+ "&email="	+ email
					+ "&password=" + password
					+ "&register_type=1&sign_in_device_type=android&device_id="+deviceId
					+ "&device_token="+LoginSignupActivity.regId 
					+ "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//					+ "&locale="+getString(R.string.locale_header_value);
            
			Log.i("LoginSignUpActivity", "PARAMS = " + params);
			errorMessage = validateLoginForm(email, password);
			if (errorMessage != null && errorMessage.length() > 0)
				return;

		} else {
			params = "appkey=" + ApplicationController.APP_ID + "&email="
					+ fbemail + "&password=" + "&register_type=2&sign_in_device_type=android&device_id="+deviceId+"&device_token="+LoginSignupActivity.regId;
		}

		Log.i("request", "url " + url + "  params " + params);
//		responseJson =ServerRequest.sendRequest(url, params);
		responseJson = ServerRequest.sendPostRequest(url, params);
		try {
			if (responseJson != null) {
				if (responseJson.getBoolean("status") == true) {
					// if successfully get the info of user from server
					String auth_token = responseJson.getString("auth_token");
					Log.i("Login ", "auth_token:" + auth_token);
					SessionManager.login(this, auth_token);
					errorMessage = "";
				} else if (responseJson.getBoolean("status") == false) {
					Log.i("Response Json Failure:", "" + responseJson);
					errorMessage = responseJson.getString("notice");
				}
			} else {
				errorMessage = getString(R.string.connection_not_available);
			}
		} catch (JSONException e) {
			Log.i("Exception: ", "" + e.getMessage());
		}

	}

	private String validateLoginForm(String email, String password) {
		if (email == null || email.length() == 0
				|| !AndroidUtil.checkEmail(email))
			return "Invalid username and/or password.";
		else if (password == null || password.length() == 0)
			return "Invalid username and/or password.";
		return null;
	}

	private class SignupAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMessage = "";
			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getString(R.string.sign_up_wait));
			progressDialog.setCancelable(false);
			progressDialog.show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (errorMessage != null && errorMessage.length() > 0)
				AndroidUtil.showMessageDialog(context, errorMessage);
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(message)
						.setCancelable(false)
						.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,
											int id) {
										showRewards();
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
				message = "";
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			doSignUp();
			return null;
		}

	}

	private void doSignUp() {

		String email = ((EditText) findViewById(R.id.email_field_signup))
				.getText().toString();
		String password = ((EditText) findViewById(R.id.password_field_signup))
				.getText().toString();
		String url = AndroidUtil.BASE_URL_HTTPS + "/user/signup?";
//		String url = getString(R.string.https_url) + "/user/signup?";
		String params;

		if (userType.equalsIgnoreCase("Signup")) {
			params = "appkey=" + ApplicationController.APP_ID
					+ "&email="	+ email
					+ "&password=" + password
					+ "&latitude=" + myLocation.getLatitude()
					+ "&longitude="	+ myLocation.getLongitude()
					+ "&register_device_type=android&register_type=1&device_id="+deviceId
					+ "&sign_in_device_type=android"
					+ "&device_token="+LoginSignupActivity.regId 
					+ "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//					+ "&locale="+getString(R.string.locale_header_value);
	        Log.i("LoginSignUpActivity", "PARAMS = " + params);
//			errorMessage = validateSignUpForm(email, password);
//			if (errorMessage != null && errorMessage.length() > 0) {
//				return;
//			} else if (password.length() < 6) {
//				errorMessage = getString(R.string.password_length);
//				return;
//
//			}
		} else {
			params = "appkey=" + ApplicationController.APP_ID + "&email="
					+ fbemail + "&password=" + "&latitude="
					+ myLocation.getLatitude() + "&longitude="
					+ myLocation.getLongitude()
					+ "&register_device_type=android&register_type=2&device_id="+deviceId
					+ "&sign_in_device_type=android"
					+ "&device_token="+LoginSignupActivity.regId
					+ "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//					+ "&locale="+getString(R.string.locale_header_value);
		}

		Log.i("request", "url " + url + "  params " + params);
		responseJson = ServerRequest.sendPostRequest(url, params);
		try {
			if (responseJson != null) {

				if (responseJson.getBoolean("status") == true) {
					// if successfully get the info of user from server
					String auth_token = responseJson.getString("auth_token");
					Log.i("Login ", "auth_token:" + auth_token);
					SessionManager.login(this, auth_token);
					errorMessage = "";
					message = responseJson.getString("notice");
				} else if (responseJson.getBoolean("status") == false) {
					Log.i("Response Json Failure:", "" + responseJson);
					errorMessage = responseJson.getString("notice");
				}
			} else {
				errorMessage = getString(R.string.connection_not_available);
			}
		} catch (JSONException e) {
			Log.i("Exception: ", "" + e.getMessage());
		}
	}

//	private String validateSignUpForm(String email, String password) {
//		if (email == null || email.length() == 0
//				|| !AndroidUtil.checkEmail(email))
//			return "Please enter a valid email id.";
//		else if (password == null || password.length() == 0)
//			return "Invalid username and/or password.";
//
//		return null;
//	}

	public class SampleRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			try {
				// process the response here: executed in background thread
				Log.d("Facebook-Example", "Response: " + response.toString());
				JSONObject json = Util.parseJson(response);
				fbemail = json.getString("email");
				Log.i("FB User Email:>", "FB User Email is: " + fbemail);
				// then post the processed result back to the UI thread
				// if we do not do this, an runtime exception will be generated
				// e.g. "CalledFromWrongThreadException: Only the original
				// thread that created a view hierarchy can touch its views."

				LoginSignupActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						if (userType.equalsIgnoreCase("FBSignup")) {
							new SignupAsyncTask().execute();
						} else if (userType.equalsIgnoreCase("FBSignin")) {
							new LoginAsyncTask().execute();
						}
					}
				});
				//
			} catch (JSONException e) {
				Log.w("Facebook-Example", "JSON Error in response");
				errorMessage = getString(R.string.reward_acess_email);
				LoginSignupActivity.this.runOnUiThread(new Runnable() {

					public void run() {
						AndroidUtil.showMessageDialog(context, errorMessage);
					}
				});

			} catch (FacebookError e) {
				Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
			}
		}
	}

	private final LocationListener locationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * update user current location.s
	 * 
	 * @param location
	 */
	private void updateWithNewLocation(Location location) {
		if (location != null) {
			myLocation = location;
		}
	}

	/**
	 * FB work here:
	 * 
	 */
	private final class LoginDialogListener implements DialogListener {

		public void onComplete(Bundle values) {
			Log.i("inside>>", "LoginDialogListener calles......");
			SessionEvents.onLoginSuccess();
			mAsyncRunner.request("me", new SampleRequestListener());
		}

		public void onFacebookError(FacebookError error) {
			Log.i("inside>>", "onFacebookError calles......"+error.getMessage());
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onError(DialogError error) {
			Log.i("inside>>", "onError calles......");
			SessionEvents.onLoginError(error.getMessage());
		}

		public void onCancel() {
			Log.i("inside>>", "onCancel calles......");
			SessionEvents.onLoginError("Action Canceled");
		}
	}

	private class LogoutRequestListener extends BaseRequestListener {

		public void onComplete(String response, final Object state) {
			// callback should be run in the original thread,
			// not the background thread
			SessionEvents.onLogoutFinish();
			// mHandler.post(new Runnable() {
			//
			// public void run() {
			// SessionEvents.onLogoutFinish();
			// }
			// });
		}
	}

	private class SessionListener implements AuthListener, LogoutListener {

		public void onAuthSucceed() {
			SessionStore.save(mFacebook, context);
		}

		public void onAuthFail(String error) {
		}

		public void onLogoutBegin() {
		}

		public void onLogoutFinish() {
			SessionStore.clear(context);
		}
	}

	/**
	 * Menu items are created here
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.aroma_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menulocations:
			showLocations();
			return true;
		case R.id.menurewards:
			showRewards();
			return true;
		case R.id.menuwall:
			showWall();
			return true;
		case R.id.menuinfo:
			showInfo();
			return true;
		default:
			return false;
		}
	}

	public void showLocations() {
		Intent i = new Intent(this, LocationListingActivity.class);
		startActivity(i);
		finish();
	}

	public void showRewards() {
		if (!SessionManager.isUserLoggedIn(this)) {
			Intent i = new Intent(this, LoginSignupActivity.class);
			SessionManager.setNextActivity(context, "2");
			startActivity(i);
			finish();
			return;
		}
		Intent i = new Intent(this, RewardListActivity.class);
		startActivity(i);
		finish();
	}

	public void showInfo() {
		Intent i = new Intent(this, InfoActivity.class);
		startActivity(i);
		finish();
	}

	public void showWall() {
		Intent i = new Intent(this, FaceBookTabActivity.class);
		startActivity(i);
		finish();
	}


	

}// ////////////Activity edned