package com.dg.android.lcp.activities;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.dg.android.lcp.objects.SessionManager;
import com.dg.android.lcp.utils.AndroidUtil;
import com.dg.android.lcp.utils.CameraLiveView;
import com.dg.android.lcp.utils.ExceptionHandler;


public class CustomCameraActivity extends Activity {

    private static String TAG = "CustomCameraActivity";

    private CameraLiveView cameraLiveView;
    private Button captureBtn;
    private Button uploadBtn;
    private Button cancelBtn;
    private byte[] imageData;
    ImageView questionMark;
    Context context;
    String message;
    Boolean isStatus;
    ProgressDialog progressDialog;
    String claimId = "";
    String offerId = "";
    String restaurentId;
    String address;
    String name = "";
    JSONObject responseJson;
    String contact;
    Bundle extras;
    String receiptId = "";
    String surveyId = "";
    JSONObject restaurant;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        context = this;
        Log.i(TAG, "Activity called");
        try {
            restaurant = new JSONObject(SessionManager.getRestaurant(context));
            restaurentId = restaurant.getString("id");
            JSONObject offer = new JSONObject(SessionManager.getOffer(context));
            offerId = offer.getString("id");

        } catch (JSONException e) {
            Log.i("Exception:", "Exception:" + e.getMessage());
            e.printStackTrace();
        }

        questionMark = (ImageView) findViewById(R.id.questonMarkBtn);
        isStatus = false;
        questionMark.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, PhotoTips.class);
                startActivity(intent);
            }
        });
        cameraLiveView = (CameraLiveView) findViewById(R.id.camera_live_view);
        cameraLiveView.setShutterCallback(shutterCallback);
        cameraLiveView.setRawCallback(rawCallback);
        cameraLiveView.setJpegCallback(jpegCallback);

        captureBtn = (Button) findViewById(R.id.cameraCaptureBtn);
        uploadBtn = (Button) findViewById(R.id.uploadBtn);
        cancelBtn = (Button) findViewById(R.id.retakeBtn);

        captureBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                cameraLiveView.takePicture();
            }
        });

        uploadBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String filename = android.text.format.DateFormat.format("yyyyMMddhhmmss", new java.util.Date()).toString()+".jpeg";
                Log.i("File Name", "file name is :" + filename);
                String url = AndroidUtil.BASE_URL + "/receipts?appkey=" + ApplicationController.APP_ID + 
                		"&locale="+AndroidUtil.LOCALE_HEADER_VALUE;
//                String url = getString(R.string.http_url) + "/receipts?appkey=" + ApplicationController.APP_ID + 
//                		"&locale="+getString(R.string.locale_header_value);
                new SubmitReceiptTask().execute(url, getExternalCacheDir() + "/receipt_upload_aroma.jpeg", filename);
            }

        });

        cancelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                cancelPreviewImage();
            }
        });

    }

    public void cancelPreviewImage() {
        captureBtn.setVisibility(View.VISIBLE);
        uploadBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        cameraLiveView.startPreview();
    }

    private class SubmitReceiptTask extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context, "", getString(R.string.uploading), true);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
        	if (message != null && message.length() > 0 &&message.equals("401")) {
                AndroidUtil.showMessageDialogWithNewIntent(CustomCameraActivity.this, context, getString(R.string.errorMessage401), LoginSignupActivity.class, "true");
            }
            
        	else if (isStatus) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message).setCancelable(false).setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        // finish();
                        Intent intent = new Intent(context, SurveyActivity.class);
                        intent.putExtra("receiptId", receiptId);
                        intent.putExtra("surveyId", surveyId);
                        startActivity(intent);
                        return;
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                Log.i("URL >>>>>", ">>>>>>>>>>>>>>> the message is :" + message);

            } 
                   
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(message).setCancelable(false).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int id) {
                        cancelPreviewImage();
                        message = "";
                        return;
                        // new
                        // LoadMyGobblesAsyncTask().execute(url);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        }

        @Override
        protected Void doInBackground(String... params) {
            uploadData(params[0], params[1], params[2]);
            return null;
        }

    }


    public Bitmap bitmapDecode(String path) {
        try {
            int inWidth = 0;
            int inHeight = 0;

            InputStream in = new FileInputStream(path);

            // decode image size (decode metadata only, not the whole image)
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            in = null;

            // save width and height
            inWidth = options.outWidth;
            inHeight = options.outHeight;

            // decode full image pre-resized
            in = new FileInputStream(path);
            options = new BitmapFactory.Options();
            // calc rought re-size (this is no exact resize)

            int imageSize = (int) (in.available() / 1024);
            int memorySize = (int) (Runtime.getRuntime().freeMemory() / 1024);
            Log.v(TAG, "Image size is " + imageSize + " Memory Size is " + memorySize);

            if (imageSize > 500 && memorySize > 1000) {
                options.inSampleSize = Math.max(inWidth / 1024, inHeight / 768);
                // decode full image
                Bitmap roughBitmap = BitmapFactory.decodeStream(in, null, options);

                // calc exact destination size
                Matrix m = new Matrix();
                RectF inRect = new RectF(0, 0, roughBitmap.getWidth(), roughBitmap.getHeight());
                RectF outRect = new RectF(0, 0, 1024, 768);
                m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
                float[] values = new float[9];
                m.getValues(values);
                Matrix mat = new Matrix();
                mat.preRotate(90);// /in degree
                // resize bitmap
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(roughBitmap, (int) (roughBitmap.getWidth() * values[0]),
                        (int) (roughBitmap.getHeight() * values[4]), true);

                Bitmap rotateImage = Bitmap.createBitmap(resizedBitmap, 0, 0, resizedBitmap.getWidth(), resizedBitmap.getHeight(), mat, true);
                // save image
                try {
                    FileOutputStream out = new FileOutputStream(path);
                    resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out);
                    return rotateImage;
                } catch (Exception e) {
                    Log.e("Image", e.getMessage(), e);
                    return rotateImage;
                }
            } else {
                //Bitmap rotateImage = Bitmap.createBitmap(path);
            }


        } catch (IOException e) {
            Log.e("Image", e.getMessage(), e);
        }
        return null;

    }

    public void uploadData(String url, String path, String fileName) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);
            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            FormBodyPart token = new FormBodyPart("auth_token", new StringBody(SessionManager.getLoggedInToken(context)));
            FormBodyPart offer = new FormBodyPart("offer", new StringBody(offerId));
            FormBodyPart restaurant = new FormBodyPart("restaurant", new StringBody(restaurentId));

            FileInputStream in = new FileInputStream(path);
            int imageSize = (int) (in.available() / 1024);
            int memorySize = (int) (Runtime.getRuntime().freeMemory() / 1024);
            Log.v(TAG, "Image size is " + imageSize + " Memory Size is " + memorySize);

            Bitmap image=null;
            if (imageSize > 500 && memorySize > 1000) {
                image = bitmapDecode(path);
            } else {
                try {
                    Bitmap rotateImage = BitmapFactory.decodeStream(in);
                    Matrix mat = new Matrix();
                    mat.preRotate(90);// /in degree

                    image = Bitmap.createBitmap(rotateImage, 0, 0, rotateImage.getWidth(), rotateImage.getHeight(), mat, true);
                    Log.i("Image>>", "Image not resized.....");
                } catch (Exception e) {
                    Log.e("Image", e.getMessage(), e);
                }
            }


            entity.addPart(token);
            entity.addPart(offer);
            entity.addPart(restaurant);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(CompressFormat.JPEG, 80, bos);
            byte[] data = bos.toByteArray();

            entity.addPart("image", new ByteArrayBody(data, "image/jpeg", fileName));

            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost, localContext);
            String sResponse = AndroidUtil.inputStreamToString(response.getEntity().getContent()).toString();
            Log.i("ResponseString", sResponse);
            responseJson = new JSONObject(sResponse);
            Log.i("Register", "Register Button Response " + sResponse);
            try {
            	if (response.getStatusLine().getStatusCode()==401) {
                    Log.i("Response Json Failure:", "" + responseJson.toString());
                    message="401";
                }
            	else if (responseJson.getBoolean("status") == true) {
                    isStatus = true;
                    message = responseJson.getString("notice");
                    receiptId = responseJson.getString("receipt_id");
                    surveyId = responseJson.getString("survey_id");
                } 
                 
                else if (responseJson.getBoolean("status") == false) {
                    isStatus = false;
                    Log.i("Response Json Failure:", "" + responseJson);
                    message = responseJson.getString("notice");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            // errorMessage = getString(R.string.ExceptionGeneral);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // errorMessage = getString(R.string.ExceptionGeneral);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // errorMessage = getString(R.string.ExceptionNetworkProblem);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        } catch (UnknownHostException e) {
            // message = getString(R.string.ExceptionNetworkProblem);
            ExceptionHandler.logException(e);
            e.printStackTrace();

        } catch (SocketException e) {
            // message = getString(R.string.ExceptionNetworkProblem);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        } catch (IOException e) {
            // message = getString(R.string.ExceptionGeneral);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        } catch (Exception e) {
            // message = getString(R.string.ExceptionGeneral);
            ExceptionHandler.logException(e);
            e.printStackTrace();
        }

    }


    // Fast Implementation
    private StringBuilder inputStreamToString(InputStream is) throws IOException {
        String line = "";
        StringBuilder total = new StringBuilder();

        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
        while ((line = rd.readLine()) != null) {
            total.append(line);
        }

        rd.close();

        // Return full string
        return total;
    }

    public static File convertImageUriToFile(Uri imageUri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION};
            cursor = activity.managedQuery(imageUri, proj, // Which columns to
                                                           // return
                    null, // WHERE clause; which rows to return (all rows)
                    null, // WHERE clause selection arguments (none)
                    null); // Order-by clause (ascending by name)
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) { return new File(cursor.getString(file_ColumnIndex)); }
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        cameraLiveView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraLiveView.onPause();
    }

    ShutterCallback shutterCallback = new ShutterCallback() {

        public void onShutter() {
            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    PictureCallback rawCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            Log.d(TAG, "onPictureTaken - raw");
        }
    };

    /** Handles data for jpeg picture */
    PictureCallback jpegCallback = new PictureCallback() {

        public void onPictureTaken(byte[] data, Camera camera) {
            imageData = data;
            captureBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
            FileOutputStream outStream = null;
            try {
                /**
                 * writing the file on memory.
                 */
                outStream = new FileOutputStream(getExternalCacheDir() + "/receipt_upload_aroma.jpeg");
                outStream.write(data);
                outStream.close();
                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    /**
     * this will upload image to server
     * 
     * @param imageData
     */
    private void uploadImage(byte[] imageData) {

    }

}