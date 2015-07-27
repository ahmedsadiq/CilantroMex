package com.dg.android.lcp.activities;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dg.android.lcp.objects.ActivityDataObject;
import com.dg.android.lcp.objects.BalancePoints;
import com.dg.android.lcp.objects.ReceiptsObject;
import com.dg.android.lcp.objects.RewardDataObject;
import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;
import com.google.gson.Gson;

public class RewardListActivity extends Activity {

	Context context;
	String TAG = "RewardListActivity";
	TableRow aboutvivo;
	TableRow weekActivity;
	ProgressDialog progressDialog;
	RelativeLayout body;
	String errorMessage;
	TextView totalPointsNumber;
	private LayoutInflater mInflater;
	String pointsEarn;
	String chainName;
	String errormessage = "";
	TextView choseRewards;
	int totalNodes;
	int totalNodesweek;
	List<ActivityDataObject> rewardLocations = new ArrayList<ActivityDataObject>();
	boolean isActivity = false;
	String rewardUrl;
	String activityUrl;
	Location myLocation;

	Gson gson = new Gson();
	JSONObject responseJson;
	// JSONObject jsonObject;
	Bundle extras;
	JSONArray rewardListArray;
	RewardDataObject rewardDataObjects[];

	JSONArray balancePointArray;
	BalancePoints balancePointDataObjects;
	String totalPoints;
	String param;

	JSONArray receiptsListArray;
	ReceiptsObject receiptsObject[];
	TableLayout table;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reward_list_view);
		context = this;
		LocationManager locManager;
		locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 500.0f, locationListener);
		myLocation = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		rewardUrl = AndroidUtil.BASE_URL + "/rewards?auth_token=" + SessionManager.getLoggedInToken(context) + 
				"&appkey=" + ApplicationController.APP_ID + 
				"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
        activityUrl = AndroidUtil.BASE_URL + "/user/activity?auth_token=" + SessionManager.getLoggedInToken(context) + 
        		"&appkey=" + ApplicationController.APP_ID + 
        		"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//        
//		rewardUrl = getString(R.string.http_url) + "/rewards?auth_token=" + SessionManager.getLoggedInToken(context) + 
//				"&appkey=" + ApplicationController.APP_ID + 
//				"&locale="+getString(R.string.locale_header_value);
//        activityUrl = getString(R.string.http_url) + "/user/activity?auth_token=" + SessionManager.getLoggedInToken(context) + 
//        		"&appkey=" + ApplicationController.APP_ID + 
//        		"&locale="+getString(R.string.locale_header_value);
		Log.i(TAG, "Activity called");
		body = (RelativeLayout) findViewById(R.id.body);
		body.setVisibility(View.GONE);
		totalPointsNumber = (TextView) findViewById(R.id.totalPointsNumber);
		choseRewards = (TextView) findViewById(R.id.choserewards);
		param = "null";
		new GetRewardsDataAsyncTask(rewardUrl, param).execute();
	}

	private class GetRewardsDataAsyncTask extends AsyncTask<String, Void, Void> {

		public String url;
		public String param;

		public GetRewardsDataAsyncTask(String url, String param) {
			this.url = url;
			this.param = param;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog = ProgressDialog.show(context, "", getString(R.string.loading_rewards), true);
			errorMessage = "";
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			body.setVisibility(View.VISIBLE);
			SessionManager.setBackpressReward(false);
			isActivity = true;
			try {
				if (errorMessage != null && errorMessage.length() > 0 && errorMessage.equals("401")) {
					AndroidUtil.showMessageDialogWithNewIntent(RewardListActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
				} else if (errorMessage != null && errorMessage.length() > 0) {
					AndroidUtil.showMessageDialog(context, errorMessage);
				} else {

					rewardListArray = new JSONArray(responseJson.getString("rewards"));
					rewardDataObjects = gson.fromJson(rewardListArray.toString(), RewardDataObject[].class);

					String str = responseJson.getString("balance");
					balancePointDataObjects = gson.fromJson(str, BalancePoints.class);
					totalPoints = balancePointDataObjects.getPoints();
					renderRewardsData();
				}

			}

			catch (JSONException e) {
				e.printStackTrace();

			}

		}

		@Override
		protected Void doInBackground(String... params) {
			requestServer();
			return null;
		}

		private void requestServer() {
			Log.i("requestServerver Method is called", "....................");
			try {

				Log.i("request", "url " + url + "  param " + param);
				responseJson = com.dg.android.lcp.utils.ServerRequest.sendRequestWithoAuthToken(url, param);
				if (responseJson != null) {
					if (responseJson.getBoolean("status") == true) {
						// if successfully get the info of user from server
						Log.i("RewardListActivity ", "reward list  " + responseJson);
						errorMessage = "";
					} else if (responseJson.getBoolean("status") == false && !(responseJson.isNull("errorMsg"))) {
						Log.i("Response Json Failure:", "" + responseJson.toString());
						errorMessage = responseJson.getString("errorMsg");
					} else if (responseJson.getBoolean("status") == false) {
						errorMessage = responseJson.getString("notice");
					}
				} else {
					errorMessage = getString(R.string.connection_not_available);
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}
	}

	public void renderRewardsData() {
		totalPointsNumber.setText(totalPoints);
		weekActivity = (TableRow) findViewById(R.id.viewActivityRow);
		weekActivity.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// if (Integer.parseInt(totalPoints)> 0 ) {
				new ViewActvityAsyncTask(activityUrl, param).execute();
				// }

			}
		});
		table = (TableLayout) findViewById(R.id.rewards_detail_table);
		mInflater = getLayoutInflater();
		for (int i = 0; i < rewardDataObjects.length; i++) {

			TableRow tr = (TableRow) mInflater.inflate(R.layout.reward_list_row, null);

			TableRow offerRow = (TableRow) tr.findViewById(R.id.offerRow);
			// if (locations.get(i).getqualify().contains("no")) {

			int pointing = Integer.parseInt(rewardDataObjects[i].getPoints());
			Log.i("POINTING", pointing + "");

			if ((rewardDataObjects[i].getExpired().equals("true")) || (Integer.parseInt(rewardDataObjects[i].getPoints()) > Integer.parseInt(totalPoints))) {

				Drawable myImage = getResources().getDrawable(R.drawable.reward_unavailable);
				offerRow.setBackgroundDrawable(myImage);

			}
			final TextView offer = (TextView) tr.findViewById(R.id.offer);

			final Button pointButton = (Button) tr.findViewById(R.id.pointsButton);

			if (rewardDataObjects[i].getName().length() > 15) {
				// offer.setText(locations.get(i).getrewardTitle()
				// .subSequence(0, 20)
				// + "..");
				offer.setTextSize(15);
//				pointButton.setTextSize(17);
				offer.setText(rewardDataObjects[i].getName());
			}

			else {
				offer.setText(rewardDataObjects[i].getName());
			}
			
			
			if (rewardDataObjects[i].getExpired().equals("true")) {
				pointButton.setText(R.string.reward_expired);
				//pointButton.setTextSize(10);
				//pointButton.setBackgroundResource(R.drawable.reward_expired);
			}
			else if (rewardDataObjects[i].getPoints().equals("0")) {
				pointButton.setText(R.string.reward_free);
				//pointButton.setBackgroundResource(R.drawable.reward_free);
			}

			

			else {
				pointButton.setText(rewardDataObjects[i].getPoints());
				pointButton.setBackgroundResource(R.drawable.cost_sm);
			}
			final int k = i;
			tr.setId(i);

			tr.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// if (myLocation == null) {
					// AndroidUtil.showMessageDialog(context,
					// getString(R.string.no_location_error_message));
					// return;
					// }
					Log.i("Expired", rewardDataObjects[v.getId()].getExpired().toString());
					if (rewardDataObjects[v.getId()].getExpired().equals("true")) {

						Log.i("Expired1", rewardDataObjects[v.getId()].getExpired().toString());
						if (pointButton.getText().equals(getString(R.string.reward_delete))) {
							new RemoveClaimRewardAsyncTask(rewardDataObjects[v.getId()].getId()).execute();
						}
						pointButton.setText(R.string.reward_delete);
						// Intent intent = new Intent(context,
						// RemoveRewardActivity.class);
						// intent.putExtra("rewardName",
						// rewardDataObjects[k].getName());
						// intent.putExtra("rewardId",
						// rewardDataObjects[k].getId());
						// intent.putExtra("chainName",
						// rewardDataObjects[k].getChain_id());
						// intent.putExtra("pointsClaim",
						// rewardDataObjects[k].getPoints());
						// intent.putExtra("posCode",
						// rewardDataObjects[k].getPOSCode());
						// intent.putExtra("Expired",
						// rewardDataObjects[k].getExpired());
						//
						// startActivity(intent);
						// finish();
					}

					else if (Integer.parseInt(rewardDataObjects[v.getId()].getPoints()) > Integer.parseInt(totalPoints)) {
						Log.i("Expired2", rewardDataObjects[v.getId()].getExpired().toString());
					} else {
						Intent intent = new Intent(context, RewardClaimActivity.class);
						Log.i("Expired3", rewardDataObjects[v.getId()].getExpired().toString());
						intent.putExtra("rewardName", rewardDataObjects[v.getId()].getName());
						intent.putExtra("rewardId", rewardDataObjects[v.getId()].getId());
						intent.putExtra("chainName", rewardDataObjects[v.getId()].getChain_id());
						intent.putExtra("pointsClaim", rewardDataObjects[v.getId()].getPoints());
						intent.putExtra("posCode", rewardDataObjects[v.getId()].getPOSCode());
						intent.putExtra("Expired", rewardDataObjects[v.getId()].getExpired());

						startActivity(intent);
//						finish();
					}
				}
			});

			tr.setId(i);
			table.addView(tr);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(TAG, "Resumne value is------" + isActivity);
		if (isActivity) {
			Intent i = new Intent(this, RewardListActivity.class);
			startActivity(i);
			finish();
		}
	}

	private class ViewActvityAsyncTask extends AsyncTask<String, Void, Void> {

		public String url;
		public String param;

		public ViewActvityAsyncTask(String url, String param) {
			this.url = url;
			this.param = param;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			errorMessage = "";
			progressDialog = ProgressDialog.show(context, "", getString(R.string.loading), true);
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (errorMessage != null && errorMessage.length() > 0 && errorMessage.equals("401")) {
				AndroidUtil.showMessageDialogWithNewIntent(RewardListActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
			} else if (errorMessage != null && errorMessage.length() > 0) {
				AndroidUtil.showMessageDialog(context, errorMessage);
			}

			else if (responseJson == null) {
				AndroidUtil.showMessageDialog(context, "Network error");
			}

			else {
				try {
					receiptsListArray = new JSONArray(responseJson.getString("receipts"));
					receiptsObject = gson.fromJson(receiptsListArray.toString(), ReceiptsObject[].class);

					Intent intent = new Intent(context, WeekActivity.class);
					intent.putExtra("totalPoints", totalPoints);
					startActivity(intent);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

		}

		@Override
		protected Void doInBackground(String... params) {
			requestServer();
			return null;
		}

		private void requestServer() {
			Log.i("requestServerver Method is called", "....................");
			try {

				Log.i("request", "url " + url + "  param " + param);
				responseJson = com.dg.android.lcp.utils.ServerRequest.sendRequestWithoAuthToken(url, param);
				if (responseJson.getBoolean("status") == true) {
					// if successfully get the info of user from server
					Log.i("RewardListActivity ", "acitivity list  " + responseJson);
					errorMessage = "";
				} 
				else if (responseJson.getBoolean("status") == false && !(responseJson.isNull("errorMsg"))) {
					Log.i("Response Json Failure:", "" + responseJson.toString());
					errorMessage = responseJson.getString("errorMsg");
				} else if (responseJson.getBoolean("status") == false) {
					// if successfully get the info of user from server
					Log.i("RewardListActivity ", "acitivity list  " + responseJson);
					errorMessage = responseJson.getString("notice");
				}

			} catch (Exception e) {
				e.printStackTrace();

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
	}

	public void showRewards() {
		Intent i = new Intent(this, RewardListActivity.class);
		startActivity(i);
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

	private class RemoveClaimRewardAsyncTask extends AsyncTask<String, Void, Void> {
		String rewardId;

		public RemoveClaimRewardAsyncTask(String rewardId) {
			this.rewardId = rewardId;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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

				if (errorMessage != null && errorMessage.length() > 0 && errorMessage.equals("401")) {
					AndroidUtil.showMessageDialogWithNewIntent(RewardListActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
				} else if (errorMessage != null && errorMessage.length() > 0)
					AndroidUtil.showMessageDialog(context, errorMessage);
				else if (responseJson.getBoolean("status") == true) {
					param = "null";
					rewardUrl = AndroidUtil.BASE_URL + "/rewards?auth_token=" + SessionManager.getLoggedInToken(context) + "&appkey=" + ApplicationController.APP_ID +
							"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//					rewardUrl = getString(R.string.http_url) + "/rewards?auth_token=" + SessionManager.getLoggedInToken(context) + "&appkey=" + ApplicationController.APP_ID +
//							"&locale="+getString(R.string.locale_header_value);
	                new GetRewardsDataAsyncTask(rewardUrl, param).execute();
					table.removeAllViews();
					AndroidUtil.showMessageDialog(context, responseJson.getString("notice"));
				} else if (responseJson.getBoolean("status") == false) {

					AndroidUtil.showMessageDialog(context, errorMessage);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			requestServerForDelete();
			return null;
		}

		private void requestServerForDelete() {
			Log.i("requestServerver Method is called", "....................");
			try {

				String url = AndroidUtil.BASE_URL + "/rewards/" + rewardId + "?auth_token=" + SessionManager.getLoggedInToken(context) + 
						"&appkey=" + ApplicationController.APP_ID + 
						"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//				String url = getString(R.string.http_url) + "/rewards/" + rewardId + "?auth_token=" + SessionManager.getLoggedInToken(context) + 
//						"&appkey=" + ApplicationController.APP_ID + 
//						"&locale="+getString(R.string.locale_header_value);
                Log.i("request", "url " + url);
				responseJson = ServerRequest.sendDeleteRequestWithOAuthToken(url, null);
				if (responseJson.getBoolean("status") == true) {
					// if successfully get the info of user from server
					Log.i("RewardClaimActivity ", "reward claim" + responseJson);
					errorMessage = "";
				} else if (responseJson.getBoolean("status") == false && !(responseJson.isNull("errorMsg"))) {
					Log.i("Response Json Failure:", "" + responseJson.toString());
					errorMessage = responseJson.getString("errorMsg");
				}

				else if (responseJson.getBoolean("status") == false) {
					errorMessage = responseJson.getString("message");
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

}
