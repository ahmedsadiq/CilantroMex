package com.dg.android.lcp.activities;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dg.android.lcp.objects.RestaurantAddressObject;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;
import com.google.gson.Gson;

public class RewardClaimActivity extends Activity {
	Context context;

	private RelativeLayout mClaimLinearParnt;
	private LinearLayout mRedeemLinearParnt;
	private LinearLayout mTimeremainingRelativeParnt;

	private TextView claim_btn;
	private TextView timeremainingvalueTV;
	private TextView timeredeemTV;
	private TextView codevalTV;
	TextView addresstitleTV;

	Bundle extras;
	String rewardname;
	String TAG = "Reward Claim Activity";
	String rewardId;
	String chainName;
	String posCode;
	String pointsClaim;

	private TextView whendoneTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_claimnew);
		context = this;

		Log.i(TAG, "Activity called");
		extras = getIntent().getExtras();
		rewardname = extras.getString("rewardName");
		rewardId = extras.getString("rewardId");
		posCode = extras.getString("posCode");
		pointsClaim = extras.getString("pointsClaim");
		chainName = extras.getString("chainName");

		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L,
				500.0f, locationListener);
		myLocation = locManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		TextView titleTV = (TextView) findViewById(R.id.reward_claim_text_title);
		titleTV.setText(rewardname);
		int fontsize = 28;
		String title = rewardname;
		if (title != null && !title.equals("")) {
			if (title.length() > 15 && title.length() < 19)
				fontsize = 24;
			else if (title.length() > 18)
				fontsize = 19;
		}
		AndroidUtil.setTextViewAttributeBold(titleTV, fontsize, "28765B",
				getAssets());

		addresstitleTV = (TextView) findViewById(R.id.reward_claim_text_address);
		AndroidUtil.setTextViewAttributeBold(addresstitleTV, 16, "3F2513",
				getAssets());

		mClaimLinearParnt = (RelativeLayout) findViewById(R.id.reward_claim_linear_claim_btn);
		claim_btn = (TextView) findViewById(R.id.reward_text_claim_redeembutton);

		mRedeemLinearParnt = (LinearLayout) findViewById(R.id.reward_claim_linear_redeemedbutton);
		TextView redeeemTV = (TextView) findViewById(R.id.reward_claim_text_redeemtext);
		AndroidUtil.setTextViewAttributeBold(redeeemTV, 28, "FFFFFF",
				getAssets());// ED4622
		codevalTV = (TextView) findViewById(R.id.reward_claim_text_redeemcode);
		AndroidUtil.setTextViewAttributeBold(codevalTV, 19, "FFFFFF",
				getAssets());

		mTimeremainingRelativeParnt = (LinearLayout) findViewById(R.id.reward_claim_linear_timeremaining);
		TextView timeremainingTV = (TextView) findViewById(R.id.enjoy_freebrownie_text_timeremaining);
		AndroidUtil.setTextViewAttributeBold(timeremainingTV, 15, "666666",
				getAssets());
		timeremainingvalueTV = (TextView) findViewById(R.id.enjoy_freebrownie_text_timeremainingvalue);
		AndroidUtil.setTextViewAttributeBold(timeremainingvalueTV, 28,
				"666666", getAssets());

		mTimeremainingRelativeParnt.setVisibility(View.VISIBLE);
		timeremainingvalueTV.setText("2:00");
		whendoneTV = (TextView) findViewById(R.id.reward_claim_text_message);
		AndroidUtil.setTextViewAttributeBold(whendoneTV, 15, "666666",
				getAssets());

		createClaimView();
		claim_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				latitude = myLocation.getLatitude();
				longitude = myLocation.getLongitude();
//				RewardClaimCall();
				showRedeemConfirmMsgDialog(
						getString(R.string.claimmesageheader),
						getString(R.string.claimmesages));
			}
		});
	}

	private void RewardClaimCall() {
		Log.i("latitude ", latitude + "");
		Log.i("longitude", longitude + "");

		new AlertDialog.Builder(RewardClaimActivity.this)
				.setTitle("")
				.setMessage(getString(R.string.reward_claim_popup))
				.setPositiveButton(R.string.confirm,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								showRedeemConfirmMsgDialog(
										getString(R.string.claimmesageheader),
										getString(R.string.claimmesages));
								dialog.cancel();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
								finish();
							}
						}).show();
	}

	@Override
	protected void onResume() {
		super.onResume();
		new GetRestaurantAddressAsyncTask().execute();
	}

	private void createClaimView() {
		mClaimLinearParnt.setVisibility(View.VISIBLE);
		mRedeemLinearParnt.setVisibility(View.GONE);
//		mTimeremainingRelativeParnt.setVisibility(View.GONE);
	}

	private void createTimeRemainingView() {
		mClaimLinearParnt.setVisibility(View.GONE);
		mRedeemLinearParnt.setVisibility(View.VISIBLE);
		mTimeremainingRelativeParnt.setVisibility(View.VISIBLE);
		countDownTimer = new RedeemCountDownTimer(startTime, interval);
	}

	public void createRedeemView() {
//		mClaimLinearParnt.setVisibility(View.GONE);
//		mRedeemLinearParnt.setVisibility(View.VISIBLE);
		whendoneTV.setText(getString(R.string.waitertext));
		timeremainingvalueTV.setText("0:00");
		countDownTimer.cancel();
		timerHasStarted = false;
		countDownTimer = null;
		try {
			Calendar c = Calendar.getInstance();
			System.out.println("Current time => " + c.getTime());
			SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy"); // HH:mm:ss
			String formattedDate = df.format(c.getTime());
			timeredeemTV.setText(formattedDate);
		} catch (Exception e) {
		}
	}

	double latitude;
	double longitude;

	private class ClaimRewardAsyncTask extends AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMessage = "";
			progressDialog = new ProgressDialog(context);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setMessage(getString(R.string.loading));
			progressDialog.show();

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			try {
				if (errorMessage != null && errorMessage.length() > 0)
					AndroidUtil.showMessageDialog(context, errorMessage);

				else if (responseJson.getBoolean("status") == true) {
					String reward_code = "";
					String reward_timer = "";
					if (responseJson.has("reward_code")
							&& responseJson.has("reward_timer")) {
						reward_code = responseJson.getString("reward_code");
						codevalTV.setText(getString(R.string.code) + " "
								+ reward_code);
						reward_timer = responseJson.getString("reward_timer");
						try {
							startTime = Integer.parseInt(reward_timer);
							startTime -= 1;
							startTime = startTime * 1000;
							formatTime(startTime);
							createTimeRemainingView();
							if (!timerHasStarted) {
								countDownTimer.start();
								timerHasStarted = true;
							} else {
								countDownTimer.cancel();
								timerHasStarted = false;
							}
						} catch (Exception e) {
						}
					}
//					AndroidUtil.showMessageDialog(context,
//							responseJson.getString("notice"));
				} else if (responseJson.getBoolean("status") == false) {
					AndroidUtil.showMessageDialog(context, "Error");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			requestServerForClaim();
			return null;
		}

	}

	private void requestServerForClaim() {
		Log.i("requestServerver Method is called", "....................");
		try {
			String url = AndroidUtil.BASE_URL + "/rewards/claim?";
			String param = "appkey=" + ApplicationController.APP_ID
					+ "&auth_token=" + SessionManager.getLoggedInToken(context)
					+ "&reward_id=" + rewardId + "&lat=" + latitude + "&lng="
					+ longitude + "&location=" + restaurantId + "&locale="
					+ AndroidUtil.LOCALE_HEADER_VALUE;
			Log.i("request", "url " + url + "  param " + param);
			responseJson = ServerRequest.sendPostRequest(url, param);
			if (responseJson.getBoolean("status") == true) {
				Log.i("RewardClaimActivity ", "reward claim" + responseJson);
				errorMessage = "";

			} else if (responseJson.getBoolean("status") == false) {
				errorMessage = responseJson.getString("notice");
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private boolean timerHasStarted = false;
	private RedeemCountDownTimer countDownTimer;
	private long startTime = 60000;
	private final long interval = 1000;
	private long timeElapsed = 0;

	public class RedeemCountDownTimer extends CountDownTimer {
		public RedeemCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
		}

		@Override
		public void onFinish() {
			createRedeemView();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			startTime = startTime - interval;// millisUntilFinished;
			timeElapsed = startTime;// millisUntilFinished;
			formatTime(timeElapsed);
		}
	}

	public void showRedeemConfirmMsgDialog(String title, final String message) {
		try {
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("Cancelar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
									dialog.cancel();
								}
							})
					.setNegativeButton("Confirmar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									new ClaimRewardAsyncTask().execute();
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setTitle(title);
			alert.setIcon(AlertDialog.BUTTON_NEUTRAL);
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	boolean isPaused = false;

	@Override
	protected void onPause() {
		super.onPause();
		if (locManager != null)
			locManager.removeUpdates(locationListener);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (countDownTimer == null) {
				finish();
				return true;
			} else
				showBackConfirmMsgDialog(
						getString(R.string.claibackmmessageheader),
						getString(R.string.claibackmmessages));
		}
		return super.onKeyDown(keyCode, event);
	}

	public void showBackConfirmMsgDialog(String title, final String message) {
		try {
			AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);
			alt_bld.setMessage(message)
					.setCancelable(false)
					.setPositiveButton("Cancelar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									// isPaused = false;
								}
							})
					.setNegativeButton("Confirmar",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
									// isPaused = false;
									countDownTimer.cancel();
									timerHasStarted = false;
									countDownTimer = null;
									finish();
								}
							});
			AlertDialog alert = alt_bld.create();
			alert.setTitle(title);
			alert.setIcon(AlertDialog.BUTTON_NEUTRAL);
			alert.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void formatTime(long time) {
		long min = time / 60000;
		long sec = time - min * 60000;
		long showTime = sec / 1000;
		String delim = ":";
		if (showTime < 10)
			delim = ":0";
		if (min < 1 && showTime < 1)
			showTime = 0;
		timeremainingvalueTV.setText(min + delim + showTime);
	}

	String errorMessage;
	ProgressDialog progressDialog;
	Gson gson = new Gson();
	JSONObject responseJson;
	JSONArray restaurantAddressArray;
	RestaurantAddressObject restaurantAddressObjects[];
	public static String restaurantadd;
	public static int restaurantId;
	Location myLocation;
	LocationManager locManager;

	private class GetRestaurantAddressAsyncTask extends
			AsyncTask<String, Void, Void> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMessage = "";
			progressDialog = ProgressDialog.show(context, "",
					getString(R.string.reward_list), true);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			try {
				if (errorMessage != null && errorMessage.length() > 0
						&& errorMessage.equals("401")) {
					AndroidUtil.showMessageDialogWithNewIntent(
							RewardClaimActivity.this, context,
							getString(R.string.errorMessage401),
							LoginSignupActivity.class, "true");
				} else if (errorMessage != null && errorMessage.length() > 0) {
					AndroidUtil.showMessageDialog(context, errorMessage);
				} else {

					restaurantAddressArray = new JSONArray(
							responseJson.getString("restaurants"));
					restaurantAddressObjects = gson.fromJson(
							restaurantAddressArray.toString(),
							RestaurantAddressObject[].class);

					restaurantadd = restaurantAddressObjects[0].getAddress();
					restaurantId = Integer.parseInt(restaurantAddressObjects[0]
							.getId());
					Log.i("Address", restaurantadd);
					addresstitleTV.setText(restaurantadd);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			requestServerForAddress();
			return null;
		}
	}

	private void requestServerForAddress() {
		Log.i("requestServerver1 Method is called", "....................");
		try {
			String url = AndroidUtil.BASE_URL + "/rewards/locate?auth_token="
					+ SessionManager.getLoggedInToken(context) + "&appkey="
					+ ApplicationController.APP_ID;
			url += "&locale=" + AndroidUtil.LOCALE_HEADER_VALUE;
			Log.i("request", "url " + url);
			responseJson = ServerRequest.sendRequestWithoAuthToken(url, null);
			if (responseJson.getBoolean("status") == true) {
				Log.i("RewardListActivity ", "reward list  " + responseJson);
				errorMessage = "";
			} else if (responseJson.getBoolean("status") == false
					&& !(responseJson.isNull("errorMsg"))) {
				Log.i("Response Json Failure:", "" + responseJson.toString());
				errorMessage = responseJson.getString("errorMsg");
			} else if (responseJson.getBoolean("status") == false) {
				errorMessage = responseJson.getString("message");
			}
		} catch (Exception e) {

			e.printStackTrace();
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

	private void updateWithNewLocation(Location location) {

		if (location != null) {
			myLocation = location;
		}

	}
}