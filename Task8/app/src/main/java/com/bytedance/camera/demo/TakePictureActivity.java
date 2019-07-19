package com.bytedance.camera.demo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TakePictureActivity extends AppCompatActivity {

    private ImageView imageView;

    private static final int PERMISSION_REQUEST_CODE=4;//获取权限

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private boolean hasPermission=false;

    private File imageFile=null;

    @RequiresApi(api = VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);
        imageView = findViewById(R.id.img);
        findViewById(R.id.btn_picture).setOnClickListener(v -> {
            takePicture();
        });

    }

    @RequiresApi(api = VERSION_CODES.M)
    private void takePicture() {
        if(!checkPermissions()){
            getPermissions();
            return;
        }

        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageFile=createFile();
        Uri fileUri;
        if(imageFile!=null){
            fileUri=FileProvider.getUriForFile(
                this,
                "com.bytedance.camera.demo",
                imageFile);
        }
        else return;

        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
        startActivityForResult(intent,REQUEST_IMAGE_CAPTURE);
    }

    private File createFile(){
        File mediaStorageDir=new File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES),"CameraDemo");
        File mediaFile=new File(mediaStorageDir.getPath()+File.pathSeparator
        +"IMG_"+getTimeString()+".jpg");
        return mediaFile;
    }

    @SuppressLint("SimpleDateFormat")
    private String getTimeString(){
        Date currentTime=new Date();
        SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(currentTime);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            setPic();
        }
    }

    private void setPic() {

        int targetW=imageView.getWidth();
        int targetH=imageView.getHeight();

        BitmapFactory.Options options=new Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);

        int photoW=options.outWidth;
        int photoH=options.outHeight;

        int scaleFactor=Math.min(photoW/targetW,photoH/targetH);
        options.inJustDecodeBounds=false;
        options.inSampleSize=scaleFactor;
        options.inPurgeable=true;

        Bitmap bitmap=BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);
        bitmap=rotateBitmap(bitmap,imageFile.getAbsolutePath());
        imageView.setImageBitmap(bitmap);

        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap

        //todo 如果存在预览方向改变，进行图片旋转

        //todo 如果存在预览方向改变，进行图片旋转
    }

    private Bitmap rotateBitmap(Bitmap bitmap,String path){
        ExifInterface exifInterface=null;
        try {
            exifInterface=new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Matrix matrix=new Matrix();
        int angle=0;
        int orientation=exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL);
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle=90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle=180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle=270;
                break;
            default:
                break;
        }
        matrix.postRotate(angle);
        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            matrix,
            true);
    }

    @RequiresApi(api = VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(TakePictureActivity.this,
                    Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    hasPermission=true;
                    takePicture();
                }else {
                    hasPermission=false;
                    Toast.makeText(this,"授权失败",Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }


    private boolean checkPermissions(){
        if(hasPermission){
            return true;
        }
        else if (ContextCompat.checkSelfPermission(TakePictureActivity.this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(TakePictureActivity.this,
            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            hasPermission=true;
            return true;
        }
        else{
            return false;
        }
    }

    @RequiresApi(api = VERSION_CODES.M)
    private void getPermissions(){
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
            PERMISSION_REQUEST_CODE);
    }
}
