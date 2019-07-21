package com.zhangxin.videoapp.activity;

import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.media.ExifInterface;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePhotoActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    private ImageView iv_take_photo;
    private ImageView iv_change_image;

    private int CAMERA_TYPE = CameraInfo.CAMERA_FACING_BACK;

    private int rotationDegree = 0;

    private boolean isTakingPhoto=false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_take_photo);
        mSurfaceView = findViewById(R.id.surfaceView_image);
        iv_take_photo=findViewById(R.id.iv_take_photo);
        iv_change_image=findViewById(R.id.iv_change_image);

        getCamera(CameraInfo.CAMERA_FACING_BACK);

        iv_take_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });

        iv_change_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CAMERA_TYPE == CameraInfo.CAMERA_FACING_BACK) {
                    getCamera(CameraInfo.CAMERA_FACING_FRONT);
                } else {
                    getCamera(CameraInfo.CAMERA_FACING_BACK);
                }
            }
        });
    }

    private void takePicture(){
        if(!isTakingPhoto){
            isTakingPhoto=true;
            Toast.makeText(getApplication(), "正在对焦,请保持手机静止",
                Toast.LENGTH_LONG).show();
            mCamera.autoFocus(new AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if(success){
                        mCamera.takePicture(null, null, mPicture);
                        mCamera.cancelAutoFocus();
                    }
                    else{
                        Toast.makeText(getApplication(), "对焦失败,请重拍!",
                            Toast.LENGTH_LONG).show();
                        isTakingPhoto = false;
                    }
                }
            });
        }
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

    private Camera.PictureCallback mPicture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            if(data==null){
                return;
            }

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                String path=pictureFile.getAbsolutePath();
                setPictureDegreeZero(path);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                isTakingPhoto=false;
                releaseCameraAndPreview();
            }
            Intent intent=new Intent();
            Uri fileUri=Uri.fromFile(pictureFile);
            intent.setData(fileUri);
            setResult(RESULT_OK,intent);
            finish();
        }
    };


    private void setPictureDegreeZero(String path) {
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            if(CAMERA_TYPE==CameraInfo.CAMERA_FACING_BACK){
                exifInterface.setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_90));
            }
            else {
                exifInterface.setAttribute(
                    ExifInterface.TAG_ORIENTATION,
                    String.valueOf(ExifInterface.ORIENTATION_ROTATE_270));
            }
            exifInterface.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                "IMG_" + timeStamp + ".jpg");
        return mediaFile;
    }
}
