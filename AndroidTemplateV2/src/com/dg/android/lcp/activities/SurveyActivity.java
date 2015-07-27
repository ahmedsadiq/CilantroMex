package com.dg.android.lcp.activities;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.objects.Survey;
import com.dg.android.lcp.objects.SurveyObject;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.ServerRequest;
import com.google.gson.Gson;

public class SurveyActivity extends Activity {

    Context context;
    String TAG = "Survey Activity";
    RadioGroup satisfyGroup;
    String satifyValue = "";
    String deliciousValue = "";
    String cortiousValue = "";
    Button submit;
    EditText comments;
    String commmentsText;
    Button skipButton;
    String pointsEarn;
    private LayoutInflater mInflater;
    LinkedList<String> choiceId = new LinkedList<String>();
    LinkedList<String> choiceString = new LinkedList<String>();
    LinkedList<String> choiceQuestion = new LinkedList<String>();
    String errorMessage;
    String commentQuestion;
    ProgressDialog progressDialog;
    List<SurveyObject> surveyValues = new ArrayList<SurveyObject>();
    int[] questionAnswer;
    String dynamicUrl = "";
    Bundle extras;
    String reaturentId = "";
    String claimId = "";
    String contact = "";
    String address = "";
    String name = "";
    ScrollView scrollView;
    TextView tellUs;
    JSONObject responseJson;
    JSONArray responseArray;
    Survey surveyArray[];
    Gson gson = new Gson();
    int textViewType;
    String questionsId[];
    String answersId[];
    EditText ans[];
    String surveyId = "";
    String receiptId = "";
    String questId = "", ansId = "";
    InputFilter[] filters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.survey_view);
        Log.i(TAG, "Activity called");
        extras = getIntent().getExtras();
        surveyId = extras.getString("surveyId");
        receiptId = extras.getString("receiptId");

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(10);

        tellUs = (TextView) findViewById(R.id.tellus);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
        scrollView.pageScroll(ScrollView.FOCUS_UP);
        scrollView.pageScroll(ScrollView.FOCUS_UP);

        submit = (Button) findViewById(R.id.surveysubmit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new SendServerAsyncTask().execute();
            }

        });

        new LoaddataAsyncTask().execute();
        tellUs.requestFocus();


    }

    private class LoaddataAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            choiceId.clear();
            choiceString.clear();
            choiceQuestion.clear();
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
            if (errorMessage != null && errorMessage.length() > 0 &&errorMessage.equals("401")) {
                AndroidUtil.showMessageDialogWithNewIntent(SurveyActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
            }
      	  else if (errorMessage != null && errorMessage.length() > 0) {
              AndroidUtil.showMessageDialog(context, errorMessage);
          } else {
            ShowData();
          }

        }

        @Override
        protected Void doInBackground(String... arg0) {
            sendRequest();
            return null;
        }
    }


    private void ShowData() {
        int rowForTF = 0;
        for (int i = 0; i < surveyArray.length; i++) {
            final TextView[] myTextViews = new TextView[surveyArray[i].getQuestions().length];
            TableLayout choicesLayout = (TableLayout) findViewById(R.id.helptable);
            TableRow questions[] = new TableRow[surveyArray[i].getQuestions().length];
            TableRow answers[] = new TableRow[surveyArray[i].getQuestions().length];

            ans = new EditText[textViewType];

            Log.i("survey ID ", "id" + surveyArray[i].getId());

            for (int j = 0; j < surveyArray[i].getQuestions().length; j++) {
                myTextViews[j] = new TextView(this);
                questions[j] = new TableRow(this);
                answers[j] = new TableRow(this);
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setPadding(5, 0, 0, 5);

                myTextViews[j].setPadding(5, 0, 0, 5);
                myTextViews[j].setTextColor(context.getResources().getColor(R.color.default_green));
                myTextViews[j].setTextSize(TypedValue.COMPLEX_UNIT_DIP,18);
                myTextViews[j].setTypeface(null,Typeface.BOLD);
                myTextViews[j].setText(surveyArray[0].getQuestions()[j].getText());
                questions[j].setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
                questions[j].addView(myTextViews[j]);
                choicesLayout.addView(questions[j]);

                Log.i("question ID", "id " + surveyArray[i].getQuestions()[j].getId());

                if (surveyArray[i].getQuestions()[j].getQuestion_type().equals("2")) {
                    final RadioButton[] rb = new RadioButton[surveyArray[i].getQuestions()[j].getQuestion_choices().length];

                    RadioGroup rg = new RadioGroup(this); // create the RadioGroup
                    double d = (double)surveyArray[i].getQuestions()[j].getQuestion_choices().length;
                    int mid = (int)Math.ceil(d/2.0);
                    for (int k = 0; k < surveyArray[i].getQuestions()[j].getQuestion_choices().length; k++) {
                        Log.i("question_choices ID ", "id" + surveyArray[i].getQuestions()[j].getQuestion_choices()[k].getId());
                        final int item = k;
                        final int row = j;
                        final int table = i;
                        rb[k] = new RadioButton(this);
                        rb[k].setPadding(2, 0, 0, 2);
                        
                   
                        
                        if (k == (mid-1)) {
                            rb[item].setButtonDrawable(R.drawable.selected);
                            answersId[j] = surveyArray[table].getQuestions()[row].getQuestion_choices()[k].getId();
                        } else {
                            rb[item].setButtonDrawable(R.drawable.radial);
                        }

                        rb[k].setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                Log.i("Row------", "row#=" + row);
                                Log.i("Click------", "" + item);
                                for (int z = 0; z < surveyArray[table].getQuestions()[row].getQuestion_choices().length; z++) {
                                    if (item == z) {
                                        rb[z].setButtonDrawable(R.drawable.selected);
                                        Log.i("selected z------", "" + z);
                                        Log.i("selected ID------", "" + surveyArray[table].getQuestions()[row].getQuestion_choices()[z].getId());
                                        answersId[row] = surveyArray[table].getQuestions()[row].getQuestion_choices()[z].getId();
                                    } else {
                                        Log.i("UNselected z------", "" + z);
                                        rb[z].setButtonDrawable(R.drawable.radial);
                                    }
                                }
                            }
                        });

                        rg.setOrientation(RadioGroup.HORIZONTAL);// or RadioGroup.VERTICAL
                        rg.addView(rb[k]);

                    }

                    ImageView thumbsDown;
                    thumbsDown = new ImageView(this);
                    thumbsDown.setImageResource(R.drawable.thumbs_down);
                    thumbsDown.setPadding(0, 10, 10, 0);
                    thumbsDown.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    ImageView thumbsUp;
                    thumbsUp = new ImageView(this);
                    thumbsUp.setImageResource(R.drawable.thumbs_up);
                    thumbsUp.setPadding(0, 10, 0, 0);
                    thumbsUp.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    ll.addView(thumbsDown);
                    ll.addView(rg);
                    ll.addView(thumbsUp);

                    answers[j].addView(ll);
                    choicesLayout.addView(answers[j]);


                }

                else {

                    ans[rowForTF] = new EditText(this);
                    ans[rowForTF].setFilters(new InputFilter[]{new InputFilter.LengthFilter(200)});
                    ans[rowForTF].setGravity(Gravity.TOP|Gravity.LEFT);

                    ans[rowForTF].setWidth(LayoutParams.FILL_PARENT);
                    ans[rowForTF].setHeight(80);
                    ans[rowForTF].setSingleLine(false);
                    ans[rowForTF].setPadding(5, 4, 4, 5);
                    answers[j].setPadding(5, 0, 0, 0);
                    answers[j].addView(ans[rowForTF]);

                    choicesLayout.addView(answers[j]);

                    rowForTF++;
                }

            }

        }

    }

    private void sendRequest() {

        try {
            String url = AndroidUtil.BASE_URL + "/survey/" + surveyId + "?appkey=" + ApplicationController.APP_ID + "&auth_token="
                    + SessionManager.getLoggedInToken(context);
            url += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//            String url = getString(R.string.http_url) + "/survey/" + surveyId + "?appkey=" + ApplicationController.APP_ID + "&auth_token="
//                    + SessionManager.getLoggedInToken(context);
//            url += "&locale="+getString(R.string.locale_header_value);
            String params = null;

            Log.i("request", "url " + url + "  param " + params);
            responseJson = ServerRequest.sendRequestWithoAuthToken(url, params);
            if (responseJson.getBoolean("status") == true) {
                responseArray = new JSONArray(responseJson.getString("survey"));
                surveyArray = gson.fromJson(responseArray.toString(), Survey[].class);

                textViewType = 0;

                for (int i = 0; i < surveyArray.length; i++) {
                    Log.i("survey ID ", "id" + surveyArray[i].getId());
                    questionsId = new String[surveyArray[i].getQuestions().length];
                    answersId = new String[surveyArray[i].getQuestions().length];
                    for (int j = 0; j < surveyArray[i].getQuestions().length; j++) {
                        Log.i("question ID", "id " + surveyArray[i].getQuestions()[j].getId());
                        questionsId[j] = surveyArray[i].getQuestions()[j].getId();
                        if (surveyArray[i].getQuestions()[j].getQuestion_type().equals("2")) {
                            for (int k = 0; k < surveyArray[i].getQuestions()[j].getQuestion_choices().length; k++) {
                                Log.i("question_choices ID ", "id" + surveyArray[i].getQuestions()[j].getQuestion_choices()[k].getId());
                            }
                        } else {
                            textViewType++;
                        }

                    }

                }

                Log.i("FeaturedEventActivity ", "User detail  " + responseJson);
                errorMessage = "";
            }
            else if (responseJson.getBoolean("status") == false&&!(responseJson.isNull("errorMsg"))) {
                Log.i("Response Json Failure:", "" + responseJson.toString());
                errorMessage=responseJson.getString("errorMsg");
            }
            else if (responseJson.getBoolean("status") == false) {
                errorMessage = responseJson.getString("message");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private class SendServerAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.sending));
            progressDialog.show();
            errorMessage="";
            int tf = 0;
            for (int i = 0; i < surveyArray.length; i++) {
                for (int j = 0; j < surveyArray[i].getQuestions().length; j++) {
                    if (surveyArray[i].getQuestions()[j].getQuestion_type().equals("2")) {
                        // ques
                    } else {
                        answersId[j] = ans[tf].getText().toString();
                        tf++;
                    }

                }

            }

            for (int i = 0; i < questionsId.length; i++) {
                questId = questId + questionsId[i] + ",";
            }

            for (int i = 0; i < answersId.length; i++) {
                if (i==answersId.length-1) {
                    try {
                        ansId= ansId +"answers["+questionsId[i]+"]="+URLEncoder.encode(answersId[i],"UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        ansId= ansId +"answers["+questionsId[i]+"]="+URLEncoder.encode(answersId[i],"UTF-8")+"&";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
            questId = questId.substring(0, questId.length() - 1);

            Log.i("the Q#", "the Q# = " + questId);
            Log.i("the A#", "the A# = " + ansId);

        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            try {
                if (responseJson.getBoolean("status") == true) {
                    errorMessage = responseJson.getString("message");
                } else if (responseJson.getBoolean("status") == false) {
                    errorMessage = responseJson.getString("message");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(errorMessage).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(context, DealConfirmation.class);
                    startActivity(intent);
                }
            });
            AlertDialog alert = builder.create();
            alert.show();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            SendServer();
            return null;
        }
    }

    private void SendServer() {
        String url = AndroidUtil.BASE_URL + "/survey/" + surveyId + "/answer";
//        String url = getString(R.string.http_url) + "/survey/" + surveyId + "/answer";
        String params;
        params = "appkey=" + ApplicationController.APP_ID + "&auth_token=" + SessionManager.getLoggedInToken(context) + "&receiptId="
                        + receiptId + "&rewardId=0&" +ansId;
        params += "&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//        params += "&locale="+getString(R.string.locale_header_value);
        Log.i("request", "url " + url + "  param " + params);
        responseJson = ServerRequest.sendPostRequest(url, params);

    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, DealConfirmation.class);
        i.putExtra("address", address);
        i.putExtra("phone", contact);
        i.putExtra("name", name);
        startActivity(i);

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
    }

    public void showWall() {
        Intent i = new Intent(this, FaceBookTabActivity.class);
        startActivity(i);
    }

}