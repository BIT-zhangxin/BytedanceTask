package com.zhangxin.videoapp.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.VideoSource;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;
import com.zhangxin.videoapp.R;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakeVideoActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private Camera mCamera;

    private ImageView iv_take_video;
    private ImageView iv_change_video;

    private int CAMERA_TYPE = CameraInfo.CAMERA_FACING_BACK;

    private File file;

    private int rotationDegree = 0;

    private boolean isRecording = false;

    private MediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_video);
        mSurfaceView=findViewById(R.id.surfaceView_video);

        mCamera=getCamera(CameraInfo.CAMERA_FACING_BACK);

        SurfaceHolder mSurfaceHolder=mSurfaceView.getHolder();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mSurfaceHolder.addCallback(new Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                releaseCameraAndPreview();
            }
        });

        iv_take_video=findViewById(R.id.iv_take_video);
        iv_take_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    releaseMediaRecorder();
                    Intent intent=new Intent();
                    Uri fileUri=Uri.fromFile(file);
                    intent.setData(fileUri);
                    setResult(RESULT_OK,intent);
                    finish();
                } else {
                    isRecording=prepareVideoRecorder();
                }
            }
        });
        iv_change_video=findViewById(R.id.iv_change_video);
        iv_change_video.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TakeVideoActivity.this,"我的手机切换前置摄像头无法录像！\n暂时禁用该功能！",Toast.LENGTH_LONG).show();
//                mCamera.stopPreview();
//                if(Camera.getNumberOfCameras()>1){
//                    switch (CAMERA_TYPE) {
//                        case CameraInfo.CAMERA_FACING_FRONT:
//                            mCamera=getCamera(CameraInfo.CAMERA_FACING_BACK);
//                            break;
//                        case CameraInfo.CAMERA_FACING_BACK:
//                            mCamera=getCamera(CameraInfo.CAMERA_FACING_FRONT);
//                            break;
//                    }
//                    try {
//                        mCamera.setPreviewDisplay(mSurfaceView.getHolder());
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    mCamera.startPreview();
//                }
//                else {
//                    Toast.makeText(TakeVideoActivity.this,"只有一个摄像头！",Toast.LENGTH_LONG).show();
//                }
            }
        });
    }

    private boolean prepareVideoRecorder(){
        mCamera.unlock();
        mMediaRecorder=new MediaRecorder();
        mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setAudioSource(AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(VideoSource.CAMERA);
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        file=getOutputMediaFile();
        mMediaRecorder.setOutputFile(file.getAbsolutePath());
        mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());
        mMediaRecorder.setOrientationHint(rotationDegree);
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private File getOutputMediaFile() {
        File mediaStorageDir = new File(getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_" + timeStamp + ".mp4");
        return mediaFile;
    }

    private void releaseMediaRecorder() {
        if(mMediaRecorder!=null){
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder=null;
            mCamera.lock();
            isRecording=false;
        }
    }

    public Camera getCamera(int position) {
        releaseCameraAndPreview();
        CAMERA_TYPE = position;
        Camera camera = Camera.open(position);
        camera.getParameters().setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        rotationDegree=getCameraDisplayOrientation(position);
        camera.setDisplayOrientation(rotationDegree);
        return camera;
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
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera=null;
        }
    }

}
