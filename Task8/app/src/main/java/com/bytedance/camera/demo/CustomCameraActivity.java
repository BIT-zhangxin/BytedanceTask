package com.bytedance.camera.demo;

import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_IMAGE;
import static com.bytedance.camera.demo.utils.Utils.MEDIA_TYPE_VIDEO;
import static com.bytedance.camera.demo.utils.Utils.getOutputMediaFile;

import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.MediaRecorder.VideoSource;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CustomCameraActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private MediaRecorder mMediaRecorder;

    private int CAMERA_TYPE = CameraInfo.CAMERA_FACING_BACK;

    private boolean isRecording = false;

    private int rotationDegree = 0;

    @RequiresApi(api = VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_custom_camera);

        mSurfaceView = findViewById(R.id.img);

        getCamera(CameraInfo.CAMERA_FACING_BACK);


        findViewById(R.id.btn_picture).setOnClickListener(v -> {

            mCamera.takePicture(null, null, mPicture);

        });

        findViewById(R.id.btn_record).setOnClickListener(v -> {
            //todo 录制，第一次点击是start，第二次点击是stop
            if (isRecording) {
                releaseMediaRecorder();
                mCamera.startPreview();
                //todo 停止录制
            } else {
                prepareVideoRecorder();
                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    isRecording=true;
                }catch (IOException e){
                    releaseMediaRecorder();
                }
                //todo 录制
            }
        });

        findViewById(R.id.btn_facing).setOnClickListener(v -> {
            Toast.makeText(this,"我的手机切换前置摄像头无法录像！\n暂时禁用该功能！",Toast.LENGTH_LONG).show();
            if(CAMERA_TYPE==CameraInfo.CAMERA_FACING_BACK){
                getCamera(CameraInfo.CAMERA_FACING_FRONT);
            }
            else {
                getCamera(CameraInfo.CAMERA_FACING_BACK);
            }
        });

        findViewById(R.id.btn_zoom).setOnClickListener(v -> {

            if(mCamera.getParameters().isZoomSupported())
            {
                Camera.Parameters parameters = mCamera.getParameters();
                int Max = parameters.getMaxZoom();

                if(Max==0)
                    return;

                int zoomValue = parameters.getZoom();
                zoomValue += 5;
                if(zoomValue > Max)
                    zoomValue = Max;
                parameters.setZoom(zoomValue);
                mCamera.setParameters(parameters);
            }
            else
            {
                Toast.makeText(this,"不支持调焦",Toast.LENGTH_LONG).show();
            }

            mCamera.autoFocus(null);

        });
    }

    public void getCamera(int position) {
        releaseCameraAndPreview();
        CAMERA_TYPE = position;
        mCamera = Camera.open(position);
        rotationDegree= getCameraDisplayOrientation(position);
        mCamera.setDisplayOrientation(rotationDegree);
        mCamera.getParameters().setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        if(mSurfaceHolder!=null){
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            mSurfaceHolder=mSurfaceView.getHolder();
            mSurfaceHolder.addCallback(new Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        mCamera.setPreviewDisplay(holder);
                        mCamera.startPreview();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    releaseCameraAndPreview();
                }
            });
        }
    }

    private static final int DEGREE_90 = 90;
    private static final int DEGREE_180 = 180;
    private static final int DEGREE_270 = 270;
    private static final int DEGREE_360 = 360;

    private int getCameraDisplayOrientation(int cameraId) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = DEGREE_90;
                break;
            case Surface.ROTATION_180:
                degrees = DEGREE_180;
                break;
            case Surface.ROTATION_270:
                degrees = DEGREE_270;
                break;
            default:
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % DEGREE_360;
            result = (DEGREE_360 - result) % DEGREE_360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + DEGREE_360) % DEGREE_360;
        }
        return result;
    }


    private void releaseCameraAndPreview() {
        if(mCamera!=null){
            mCamera.stopPreview();
            mCamera.release();
            mCamera=null;
        }
    }

    private void prepareVideoRecorder() {

        mMediaRecorder=new MediaRecorder();
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);
        mMediaRecorder.setAudioSource(AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).getAbsolutePath());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);

        //todo 准备MediaRecorder
    }


    private void releaseMediaRecorder() {

        mMediaRecorder.stop();
        mMediaRecorder.reset();
        mMediaRecorder.release();
        mMediaRecorder=null;
        isRecording=false;
        mCamera.lock();

        //todo 释放MediaRecorder
    }


    private Camera.PictureCallback mPicture = (data, camera) -> {
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (IOException e) {
            Log.d("mPicture", "Error accessing file: " + e.getMessage());
        }

        mCamera.startPreview();
    };

//    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
//        final double ASPECT_TOLERANCE = 0.1;
//        double targetRatio = (double) h / w;
//
//        if (sizes == null) return null;
//
//        Camera.Size optimalSize = null;
//        double minDiff = Double.MAX_VALUE;
//
//        int targetHeight = Math.min(w, h);
//
//        for (Camera.Size size : sizes) {
//            double ratio = (double) size.width / size.height;
//            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
//            if (Math.abs(size.height - targetHeight) < minDiff) {
//                optimalSize = size;
//                minDiff = Math.abs(size.height - targetHeight);
//            }
//        }
//
//        if (optimalSize == null) {
//            minDiff = Double.MAX_VALUE;
//            for (Camera.Size size : sizes) {
//                if (Math.abs(size.height - targetHeight) < minDiff) {
//                    optimalSize = size;
//                    minDiff = Math.abs(size.height - targetHeight);
//                }
//            }
//        }
//        return optimalSize;
//    }

}
