package com.zhangxin.videoapp.fragment;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.zhangxin.videoapp.MyApplication;
import com.zhangxin.videoapp.R;
import com.zhangxin.videoapp.activity.ImageActivity;
import com.zhangxin.videoapp.activity.TakePhotoActivity;
import com.zhangxin.videoapp.activity.TakeVideoActivity;
import com.zhangxin.videoapp.activity.VideoPlayerActivity;
import com.zhangxin.videoapp.bean.PostVideoResponse;
import com.zhangxin.videoapp.network.IMiniDouyinService;
import com.zhangxin.videoapp.network.RetrofitManager;
import com.zhangxin.videoapp.ui.MyAlertDialog;
import com.zhangxin.videoapp.utils.ResourceUtils;
import java.io.File;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateFragment extends Fragment implements View.OnClickListener {

    int permission=0;

    private static final int PERMISSION_REQUEST_STORAGE_IMAGE=100;
    private static final int PERMISSION_REQUEST_STORAGE_VIDEO=101;
    private static final int PERMISSION_REQUEST_IMAGE=102;
    private static final int PERMISSION_REQUEST_VIDEO=103;

    private static final int IMAGE_SELECT_REQUEST_CODE=1;
    private static final int VIDEO_SELECT_REQUEST_CODE=2;

    private ImageView btn_add_image;
    private ImageView btn_select_image;
    private ImageView btn_preview_image;
    private ImageView btn_add_video;
    private ImageView btn_select_video;
    private ImageView btn_preview_video;
    private Button btn_empty;
    private Button btn_upload;

    private Uri imageUri;
    private Uri videoUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_create, container, false);
        initComponent(view);
        initEvent();
        return view;
    }

    void initComponent(View view){
        btn_add_image=view.findViewById(R.id.iv_add_image);
        btn_select_image=view.findViewById(R.id.iv_select_image);
        btn_preview_image=view.findViewById(R.id.iv_preview_image);
        btn_add_video=view.findViewById(R.id.iv_add_video);
        btn_select_video=view.findViewById(R.id.iv_select_video);
        btn_preview_video=view.findViewById(R.id.iv_preview_video);
        btn_empty=view.findViewById(R.id.btn_empty);
        btn_upload=view.findViewById(R.id.btn_upload);
    }

    void initEvent(){
        btn_add_image.setOnClickListener(this);
        btn_select_image.setOnClickListener(this);
        btn_preview_image.setOnClickListener(this);
        btn_add_video.setOnClickListener(this);
        btn_select_video.setOnClickListener(this);
        btn_preview_video.setOnClickListener(this);
        btn_empty.setOnClickListener(this);
        btn_upload.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_add_image:
                takePhoto();
                break;
            case R.id.iv_select_image:
                selectImage();
                break;
            case R.id.iv_preview_image:
                previewImage();
                break;
            case R.id.iv_add_video:
                takeVideo();
                break;
            case R.id.iv_select_video:
                selectVideo();
                break;
            case R.id.iv_preview_video:
                previewVideo();
                break;
            case R.id.btn_empty:
                empty();
                break;
            case R.id.btn_upload:
                pressUpload();
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case IMAGE_SELECT_REQUEST_CODE:
                    imageUri=data.getData();
                    break;
                case VIDEO_SELECT_REQUEST_CODE:
                    videoUri=data.getData();
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_STORAGE_IMAGE:
                if(checkPermissions(1)){
                    selectImage();
                }
                else {
                    Toast.makeText(getContext(), "授权失败", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_STORAGE_VIDEO:
                if(checkPermissions(1)){
                    selectVideo();
                }
                else {
                    Toast.makeText(getContext(), "授权失败", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_IMAGE:
                if(checkPermissions(3)){
                    takePhoto();
                }
                else{
                    Toast.makeText(getContext(), "授权失败", Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSION_REQUEST_VIDEO:
                if(checkPermissions(7)){
                    takeVideo();
                }
                else{
                    Toast.makeText(getContext(), "授权失败", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private boolean checkPermissions(int requestCode){
        if((permission|requestCode)==permission){
            return true;
        }
        else{
            if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                permission=permission|1;
            }
            if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                permission=permission|2;
            }
            if(ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
                permission=permission|4;
            }
            return (permission|requestCode) == permission;
        }
    }

    private void getPermissions(int permissionCode,int requestCode){
        if(permissionCode==1){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                requestCode);
        }
        if(permissionCode==3){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                requestCode);
        }
        if(permissionCode==7){
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO},
                requestCode);
        }

    }

    private void selectImage(){
        if(!checkPermissions(1)){
            getPermissions(1,PERMISSION_REQUEST_STORAGE_IMAGE);
            return;
        }
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_SELECT_REQUEST_CODE);
    }

    private void selectVideo(){
        if(!checkPermissions(1)){
            getPermissions(1,PERMISSION_REQUEST_STORAGE_VIDEO);
            return;
        }
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("video/*");
        startActivityForResult(intent,VIDEO_SELECT_REQUEST_CODE);
    }

    private void takePhoto(){
        if(!checkPermissions(3)){
            getPermissions(3,PERMISSION_REQUEST_IMAGE);
            return;
        }
        Intent intent=new Intent(getContext(), TakePhotoActivity.class);
        startActivityForResult(intent,IMAGE_SELECT_REQUEST_CODE);
    }

    private void takeVideo(){
        if(!checkPermissions(7)){
            getPermissions(7,PERMISSION_REQUEST_VIDEO);
            return;
        }
        Intent intent=new Intent(getContext(), TakeVideoActivity.class);
        startActivityForResult(intent,VIDEO_SELECT_REQUEST_CODE);
    }

    private void previewImage(){
        if(imageUri==null){
            Toast.makeText(getContext(),"当前没有图片",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent=new Intent(getContext(), ImageActivity.class);
        intent.putExtra("file_path",getRealPath(imageUri));
        startActivity(intent);
    }

    private void previewVideo(){
        if(videoUri==null){
            Toast.makeText(getContext(),"当前没有视频",Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent=new Intent(getContext(), VideoPlayerActivity.class);
        intent.putExtra("video_url",getRealPath(videoUri));
        startActivity(intent);
    }

    private void empty(){
        emptyTip();
    }

    private void pressUpload(){
        if(imageUri==null){
            Toast.makeText(getContext(),"还未选择图片",Toast.LENGTH_LONG).show();
            return;
        }
        if(videoUri==null){
            Toast.makeText(getContext(),"还未选择视频",Toast.LENGTH_LONG).show();
            return;
        }
        uploadTip();
    }

    private void upload() {
        setEnabledFalse();
        String student_id=((MyApplication)getActivity().getApplication()).getStudent_id();
        String user_name=((MyApplication)getActivity().getApplication()).getUser_name();
        Call<PostVideoResponse> postVideoResponseCall = RetrofitManager
            .get("http://test.androidcamp.bytedance.com/mini_douyin/invoke/")
            .create(IMiniDouyinService.class)
            .upload(
                student_id,
                user_name,
                getMultipartFromUri("cover_image",imageUri),
                getMultipartFromUri("video",videoUri));
        postVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {

                if(response.body()==null||!response.body().isSuccess()){
                    Toast.makeText(getContext(),"上传异常",Toast.LENGTH_LONG).show();
                    setEnabledTrue();
                    return;
                }
                Toast.makeText(getContext(),"上传成功",Toast.LENGTH_LONG).show();
                setEnabledTrue();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast.makeText(getContext(),"上传失败",Toast.LENGTH_LONG).show();
                setEnabledTrue();
            }
        });

    }

    private void setEnabledTrue(){
        btn_upload.setText("上传");
        btn_upload.setEnabled(true);
        btn_empty.setEnabled(true);
    }

    private void setEnabledFalse(){
        btn_upload.setText("正在上传中");
        btn_upload.setEnabled(false);
        btn_empty.setEnabled(false);
    }

    private String getRealPath(Uri uri){
        return ResourceUtils.getRealPath(getContext(), uri);
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(getContext(), uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    void emptyTip(){
        MyAlertDialog myAlertDialog=new MyAlertDialog(getContext(),0,
            "提示","你确认清空么？","确认","返回",false);
        myAlertDialog.setOnCertainButtonClickListener(new MyAlertDialog.onMyAlertDialogListener() {
            public void onCancelButtonClick() {

            }
            public void onCertainButtonClick() {
                imageUri=null;
                videoUri=null;
                Toast.makeText(getContext(),"清空成功",Toast.LENGTH_SHORT).show();
            }
            public void onDismissListener() {

            }
        });
        myAlertDialog.show();
    }

    void uploadTip(){
        MyAlertDialog myAlertDialog=new MyAlertDialog(getContext(),0,
            "提示","你确认上传么？","确认","返回",false);
        myAlertDialog.setOnCertainButtonClickListener(new MyAlertDialog.onMyAlertDialogListener() {
            public void onCancelButtonClick() {

            }
            public void onCertainButtonClick() {
                upload();
            }
            public void onDismissListener() {

            }
        });
        myAlertDialog.show();
    }

}
