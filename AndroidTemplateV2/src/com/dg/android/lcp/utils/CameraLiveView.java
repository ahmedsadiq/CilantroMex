package com.dg.android.lcp.utils;
 
import java.io.IOException;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dg.android.lcp.activities.CustomCameraActivity;


public class CameraLiveView extends SurfaceView implements SurfaceHolder.Callback {

    /**
     * Constructs a new ScannerLiveView with layout parameters and a default style.
     * 
     * @param context
     *            a Context object used to access application assets
     */
    public CameraLiveView(Context context) {
        super(context);

        initialize(context);
    }

    /**
     * Constructs a new ScannerLiveView with layout parameters.
     * 
     * @param context
     *            a Context object used to access application assets
     * @param attrs
     *            an AttributeSet passed to our parent
     */
    public CameraLiveView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initialize(context);
    }

    /**
     * Constructs a new ScannerLiveView with a Context object.
     * 
     * @param context
     *            a Context object used to access application assets
     * @param attrs
     *            an AttributeSet passed to our parent
     * @param defStyle
     *            the default style resource ID
     */
    public CameraLiveView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initialize(context);
    }


    /**
     * Initializes the object (constructor helper method).
     * 
     * @param context
     *            a Context object used to access application assets
     */
    private void initialize(Context context) {
        Log.d(TAG, "initialize()");

        // get main activity
        mMainActivity = (CustomCameraActivity) context;

        // install a SurfaceHolder.Callback so we get notified when the underlying surface is created, changed or destroyed
        mHolder = getHolder();
        mHolder.addCallback(this);

        // set "direct" surface type, and set keep screen on flag
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mHolder.setKeepScreenOn(true);

        // create auto-focus callback to store if auto-focus has succeeded
        mAutoFocusCallback = new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (jpegCallback != null && !mTakePicture) {
                    Log.d(TAG, "onAutoFocus(" + success + ", ...)");
                    
                    // store if auto-focus has succeeded
                    mHasAutoFocusSucceeded = success;

                    // set auto-focus no running anymore
                    mIsAutoFocusInProgress = false;
                    
                    mTakePicture = true;
                    mPictureTaken = false;
                    mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                }
               
            }
        };

        // create preview callback
//        mPreviewCallback = new PreviewCallback() {
//
//            public void onPreviewFrame(byte[] data, Camera camera) {
//                Log.d(TAG, "onPreviewFrame()");
//
//                // if (!mTakePicture) {
//                // CameraPreview.this.invalidate();
//                // } else {
//                //
//                // if (jpegCallback != null && !mPictureTaken) {
//                // int rgb[] = new int[mPreviewSize.width * mPreviewSize.height];
//                // decodeYUV420SP(rgb, data, mPreviewSize.width, mPreviewSize.height);
//                // Bitmap memoryImage = Bitmap.createBitmap(rgb, mPreviewSize.width, mPreviewSize.height, Bitmap.Config.ARGB_8888);
//                // ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                // memoryImage.compress(CompressFormat.JPEG, 100, baos);
//                // // shutterSound();
//                // setBackgroundDrawable(new BitmapDrawable(getContext().getResources(), memoryImage));
//                // jpegCallback.onPictureTaken(baos.toByteArray(), camera);
//                // }
//                // mPictureTaken = true;
//                // camera.stopPreview();
//                // }
//            }
//        };
    }

    // Method from Ketai project! Not mine! See below...
//    void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
//
//        final int frameSize = width * height;
//
//        for (int j = 0, yp = 0; j < height; j++) {
//            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
//            for (int i = 0; i < width; i++, yp++) {
//                int y = (0xff & ((int) yuv420sp[yp])) - 16;
//                if (y < 0) y = 0;
//                if ((i & 1) == 0) {
//                    v = (0xff & yuv420sp[uvp++]) - 128;
//                    u = (0xff & yuv420sp[uvp++]) - 128;
//                }
//
//                int y1192 = 1192 * y;
//                int r = (y1192 + 1634 * v);
//                int g = (y1192 - 833 * v - 400 * u);
//                int b = (y1192 + 2066 * u);
//
//                if (r < 0)
//                    r = 0;
//                else if (r > 262143) r = 262143;
//                if (g < 0)
//                    g = 0;
//                else if (g > 262143) g = 262143;
//                if (b < 0)
//                    b = 0;
//                else if (b > 262143) b = 262143;
//
//                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
//            }
//        }
//    }

    /**
     * When surface is created:
     * <ul>
     * <li>acquires camera,
     * <li>sets camera preview display,
     * <li>registers camera preview callback.
     * </ul>
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated()");

        // acquire the camera
        mCamera = Camera.open();
        try {
            // set camera preview display
            // android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
            // android.hardware.Camera.getCameraInfo(0, info);
            int rotation = mMainActivity.getWindowManager().getDefaultDisplay().getRotation();
            int degrees = 0;
            switch (rotation) {
                case Surface.ROTATION_0:
                    degrees = 0;
                    break;
                case Surface.ROTATION_90:
                    degrees = 90;
                    break;
                case Surface.ROTATION_180:
                    degrees = 180;
                    break;
                case Surface.ROTATION_270:
                    degrees = 270;
                    break;
            }

            // int result;
            // if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // result = (info.orientation + degrees) % 360;
            // result = (360 - result) % 360; // compensate the mirror
            // } else { // back-facing
            int result = (90 - degrees + 360) % 360;
            // }
            mCamera.setDisplayOrientation(result);
            mCamera.setPreviewDisplay(holder);
            mIsPreviewStarted = false;
        } catch (IOException e) {
            Log.d(TAG, "IOException while setting preview camera preview display: " + e.getMessage());
        }
    }

    /**
     * When surface is destroyed:
     * <ul>
     * <li>stops camera preview and auto-focus,
     * <li>releases camera.
     * </ul>
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed()");

        // stop camera preview and auto-focus
        stopCamera();

        // release camera
        mCamera.release();
        mCamera = null;
    }

    /**
     * When surface has changed (i.e. when the size is known):
     * <ul>
     * <li>sets camera preview size,
     * <li>starts camera preview and auto-focus.
     * </ul>
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged(..., " + format + ", " + width + ", " + height + ")");

        // stop camera preview if it was started
        if (mIsPreviewStarted) {
            mCamera.stopPreview();
            mIsPreviewStarted = false;
        }

//        setCameraParameters(width, height);

        // start camera preview and auto-focus if view is visible
        if (isShown()) {
            startCamera();
        }
    }

    /**
     * Method to be called when activity is paused, to stop camera and stop and destroy the decoding thread.
     */
    public void onPause() {
        stopCamera();

    }

    /**
     * Method to be called when activity is resumed, to create and start the decoding thread and start camera.
     */
    public void onResume() {
        // get scanner overlay
        // mScannerOverlay = (ScannerOverlay) mMainActivity.findViewById(R.id.scanner_overlay);

        startCamera();
    }

    /**
     * Starts camera preview and auto-focus, and requests one preview frame.
     */
    public void startCamera() {
        Log.d(TAG, "startCamera()");

        if (mCamera != null && !mIsPreviewStarted) {
            // reinitialize variables
            mIsAutoFocusInProgress = false;
            mHasAutoFocusSucceeded = false;

            // start camera preview
            mCamera.setPreviewCallback(mPreviewCallback);
            mCamera.startPreview();
            mIsPreviewStarted = true;

            // start camera auto-focus
            requestAutoFocus();
        }
    }

    /**
     * Stops camera preview.
     */
    public void stopCamera() {
        Log.d(TAG, "stopCamera()");

        if (mCamera != null && mIsPreviewStarted) {
            // stop camera preview
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mIsPreviewStarted = false;

            // mScannerHandler.removeMessages(R.id.msg_auto_focus);
            // mScannerHandler.removeMessages(R.id.msg_barcode_found);
        }
    }

    /**
     * Starts one auto-focus cycle, if camera is capable and it is not already in progress.
     */
    private void requestAutoFocus() {
        if (mCamera != null && mHasCameraAutoFocus && mIsPreviewStarted && !mIsAutoFocusInProgress) {
            mIsAutoFocusInProgress = true;
            mHasAutoFocusSucceeded = false;
            mCamera.autoFocus(mAutoFocusCallback);
        }
    }

    /**
     * Sets the camera parameters.
     * 
     * @param screenWidth
     *            screen width
     * @param screenHeight
     *            screen height
     */
    private void setCameraParameters(int screenWidth, int screenHeight) {
        // get camera parameters
        final Camera.Parameters parameters = mCamera.getParameters();

        mCamera.setParameters(parameters);

        // set camera preview size
        setPreviewSize(screenWidth, screenHeight, parameters);

        // set flash off
//        parameters.set("flash-mode", "off");
//        parameters.set("flash-value", 2);

        // set best zoom
        setZoom(parameters);

        // detect if camera has auto-focus
        int apiLevel = Integer.parseInt(Build.VERSION.SDK);
        if (apiLevel <= 3) {
            // Android 1.5 and earlier didn't support fixed-focus cameras
            mHasCameraAutoFocus = true;
        } else if (apiLevel >= 5) {
            // Android 2.0+: use focus-mode-values parameter value to detect best focus mode
            final String focusModeValues = parameters.get("focus-mode-values");
            if ((focusModeValues != null) && focusModeValues.contains("macro")) {
                parameters.set("focus-mode", "macro");
                mHasCameraAutoFocus = true;
            } else if ((focusModeValues != null) && focusModeValues.contains("auto")) {
                parameters.set("focus-mode", "auto");
                mHasCameraAutoFocus = true;
            } else {
                // fixed-focus
                mHasCameraAutoFocus = false;
            }
        } else {
            // Android 1.6: detect known fixed-focus camera models
            final String model = Build.MODEL.toLowerCase();
            if (model.contains("devour") || model.contains("tattoo")) {
                // Motorola Devour and HTC Tattoo are fixed-focus
                mHasCameraAutoFocus = false;
            } else {
                // by default, suppose camera is auto-focus
                mHasCameraAutoFocus = true;
            }
        }

        // set camera parameters
        mCamera.setParameters(parameters);

        // get camera preview size (can be different from set size) and format
        mPreviewSize = parameters.getPreviewSize();
        int previewFormat = parameters.getPreviewFormat();
        final String previewFormatString = parameters.get("preview-format");
        Log.d(TAG, String.format("Preview size=%dx%d, format=%d(%s)", mPreviewSize.width, mPreviewSize.height, previewFormat, previewFormatString));
    }

    /**
     * Sets camera preview size in camera parameters.
     * 
     * @param screenWidth
     *            screen width
     * @param screenHeight
     *            screen height
     * @param parameters
     *            the camera parameters
     */
    private void setPreviewSize(int screenWidth, int screenHeight, Camera.Parameters parameters) {
        String previewSizeValuesString = parameters.get("preview-size-values");
        if (previewSizeValuesString == null) {
            previewSizeValuesString = parameters.get("preview-size-value");
        }

        Point bestPreviewSize = null;
        if (previewSizeValuesString != null) {
            // Log.d(TAG, "preview-size-values parameter: " + previewSizeValuesString);
            bestPreviewSize = findBestPreviewSize(previewSizeValuesString, screenWidth, screenHeight);
        }

        if (bestPreviewSize != null) {
            parameters.setPreviewSize(bestPreviewSize.x, bestPreviewSize.y);
        } else {
            // ensure that the camera preview dimensions are a multiple of 8
            parameters.setPreviewSize((screenWidth >> 3) << 3, (screenHeight >> 3) << 3);
        }
    }

    /**
     * Finds best preview size.
     * 
     * @param previewSizeValuesString
     *            comma-separated string of supported preview size values
     * @param screenWidth
     *            screen width
     * @param screenHeight
     *            screen height
     * @return best preview size for given screen dimensions
     */
    private static Point findBestPreviewSize(CharSequence previewSizeValuesString, int screenWidth, int screenHeight) {
        int bestPreviewWidth = 0, bestPreviewHeight = 0;
        int minDifference = Integer.MAX_VALUE;
        for (String previewSizeValueString : COMMA_PATTERN.split(previewSizeValuesString)) {
            previewSizeValueString = previewSizeValueString.trim();
            int separatorPosition = previewSizeValueString.indexOf('x');
            if (separatorPosition < 0) {
                // Log.w(TAG, "Bad preview-size: " + previewSizeValueString);
                continue;
            }

            int previewSizeValueWidth, previewSizeValueHeight;
            try {
                previewSizeValueWidth = Integer.parseInt(previewSizeValueString.substring(0, separatorPosition));
                previewSizeValueHeight = Integer.parseInt(previewSizeValueString.substring(separatorPosition + 1));
            } catch (NumberFormatException e) {
                // Log.w(TAG, "Bad preview-size: " + previewSizeValueString);
                continue;
            }

            int difference = Math.abs(previewSizeValueWidth - screenWidth) + Math.abs(previewSizeValueHeight - screenHeight);
            if (difference == 0) {
                bestPreviewWidth = previewSizeValueWidth;
                bestPreviewHeight = previewSizeValueHeight;
                break;
            } else if (difference < minDifference) {
                bestPreviewWidth = previewSizeValueWidth;
                bestPreviewHeight = previewSizeValueHeight;
                minDifference = difference;
            }
        }

        if (bestPreviewWidth > 0 && bestPreviewHeight > 0) {
            return new Point(bestPreviewWidth, bestPreviewHeight);
        } else {
            return null;
        }
    }

    /**
     * Sets camera zoom parameter in camera parameters.
     * 
     * @param parameters
     *            the camera parameters
     */
    private void setZoom(Camera.Parameters parameters) {
        String zoomSupportedString = parameters.get("zoom-supported");
        if (zoomSupportedString != null && !Boolean.parseBoolean(zoomSupportedString)) { return; }

        int desiredZoomX10 = DESIRED_ZOOM_X10;

        String maxZoomString = parameters.get("max-zoom");
        if (maxZoomString != null) {
            try {
                int maxZoomX10 = (int) (10.0 * Double.parseDouble(maxZoomString));
                if (desiredZoomX10 > maxZoomX10) {
                    desiredZoomX10 = maxZoomX10;
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Bad max-zoom: " + maxZoomString);
            }
        }

        String takingPictureZoomMaxString = parameters.get("taking-picture-zoom-max");
        if (takingPictureZoomMaxString != null) {
            try {
                int maxZoomX10 = Integer.parseInt(takingPictureZoomMaxString);
                if (desiredZoomX10 > maxZoomX10) {
                    desiredZoomX10 = maxZoomX10;
                }
            } catch (NumberFormatException e) {
                Log.w(TAG, "Bad taking-picture-zoom-max: " + takingPictureZoomMaxString);
            }
        }

        String motZoomValuesString = parameters.get("mot-zoom-values");
        if (motZoomValuesString != null) {
            desiredZoomX10 = findBestMotZoomX10(motZoomValuesString, desiredZoomX10);
        }

        String motZoomStepString = parameters.get("mot-zoom-step");
        if (motZoomStepString != null) {
            try {
                double motZoomStep = Double.parseDouble(motZoomStepString.trim());
                int motZoomStepX10 = (int) (10.0 * motZoomStep);
                if (motZoomStepX10 > 1) {
                    desiredZoomX10 -= desiredZoomX10 % motZoomStepX10;
                }
            } catch (NumberFormatException e) {
                // continue
            }
        }

        if (maxZoomString != null || motZoomValuesString != null) {
            parameters.set("zoom", String.valueOf(desiredZoomX10 / 10.0));
        }

        if (takingPictureZoomMaxString != null) {
            parameters.set("taking-picture-zoom", desiredZoomX10);
        }
    }

    /**
     * Finds best mot-zoom multiplied by 10.
     * 
     * @param motZoomValuesString
     *            comma-separated list of mot-zoom values
     * @param desiredZoomX10
     *            desired zoom multiplied by 10
     * @return the best mot-zoom from motZoomValuesString for desired zoom desiredZoomX10, multiplied by 10
     */
    private static int findBestMotZoomX10(CharSequence motZoomValuesString, int desiredZoomX10) {
        int bestMotZoomX10 = 0;
        for (String motZoomValueString : COMMA_PATTERN.split(motZoomValuesString)) {
            motZoomValueString = motZoomValueString.trim();
            double motZoomValue;
            try {
                motZoomValue = Double.parseDouble(motZoomValueString);
            } catch (NumberFormatException e) {
                return desiredZoomX10;
            }
            int motZoomValueX10 = (int) (10.0 * motZoomValue);
            if (Math.abs(desiredZoomX10 - motZoomValueX10) < Math.abs(desiredZoomX10 - bestMotZoomX10)) {
                bestMotZoomX10 = motZoomValueX10;
            }
        }
        return bestMotZoomX10;
    }

    /**
     * Scanner handler, for communicating with main UI thread from the decoding thread.
     * <p>
     * Handled messages:
     * <ul>
     * <li>msg_auto_focus: request auto-focus;
     * <li>msg_preview_frame: request a preview frame;
     * <li>msg_barcode_found: notify that a barcode has been found, message.obj = barcode (String).
     * </ul>
     */
    // private class ScannerHandler extends Handler {
    //
    // /**
    // * Handles a message;
    // */
    // @Override
    // public void handleMessage(Message message) {
    // switch (message.what) {
    // // case R.id.msg_auto_focus:
    // // requestAutoFocus();
    // // break;
    // // case R.id.msg_barcode_found:
    // // // don't handle message in the case the preview has been stopped (queued message coming late)
    // // if (mIsPreviewStarted) {
    // // final String barcode = (String) message.obj;
    // // }
    // // break;
    // }
    // }
    // }

    public void setShutterCallback(ShutterCallback shutterCallback) {
        this.shutterCallback = shutterCallback;
    }

    public void setRawCallback(PictureCallback rawCallback) {
        this.rawCallback = rawCallback;
    }

    public void setJpegCallback(PictureCallback jpegCallback) {
        this.jpegCallback = jpegCallback;
    }

    public synchronized void takePicture() {
        mCamera.autoFocus(mAutoFocusCallback);
    }

    public synchronized void startPreview() {
        mTakePicture = false;
        mPictureTaken = true;
        mCamera.startPreview();
    }

    private boolean mTakePicture;
    private boolean mPictureTaken;

    private ShutterCallback shutterCallback;
    private PictureCallback rawCallback;
    private PictureCallback jpegCallback;

    private static final String TAG = CameraLiveView.class.getSimpleName();
    private static final int DESIRED_ZOOM_X10 = 27;
    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private CustomCameraActivity mMainActivity;
    // private ScannerOverlay mScannerOverlay;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean mHasCameraAutoFocus;
    private Camera.AutoFocusCallback mAutoFocusCallback;
    private PreviewCallback mPreviewCallback;
    private Camera.Size mPreviewSize;
    // private ScannerHandler mScannerHandler = new ScannerHandler();
    private boolean mIsPreviewStarted = false;
    private boolean mIsAutoFocusInProgress = false;
    private boolean mHasAutoFocusSucceeded = false;
    private boolean mFrameDataIsAutoFocusInProgress = false;
    private boolean mFrameDataHasAutoFocusSucceeded = false;

}
