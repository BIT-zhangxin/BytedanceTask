package com.bytedance.android.lesson.restapi.solution;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bytedance.android.lesson.restapi.solution.bean.Feed;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Solution2C2Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int GRANT_PERMISSION = 3;
    private static final String TAG = "Solution2C2Activity";
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution2_c2);
        initRecyclerView();
        initBtns();
    }

    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    if (requestReadExternalStoragePermission("select an image")) {
                        chooseImage();
                    }
                } else if (getString(R.string.select_a_video).equals(s)) {
                    if (requestReadExternalStoragePermission("select a video")) {
                        chooseVideo();
                    }
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });

        mBtnRefresh = findViewById(R.id.btn_refresh);
    }

    private boolean requestReadExternalStoragePermission(String explanation) {
        if (ActivityCompat.checkSelfPermission(Solution2C2Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Solution2C2Activity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "You should grant external storage permission to continue " + explanation, Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(Solution2C2Activity.this, new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                }, GRANT_PERMISSION);
            }
            return false;
        } else {
            return true;
        }
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new Solution2C1Activity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
                String url = mFeeds.get(i).getImage_url();
                Glide.with(iv.getContext()).load(url).into(iv);
            }

            @Override public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(Solution2C2Activity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        clearList();
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        Call<PostVideoResponse> postVideoResponseCall = RetrofitManager
            .get("http://test.androidcamp.bytedance.com/mini_douyin/invoke/")
            .create(IMiniDouyinService.class)
            .upload(
                "120161967",
                "张歆",
                getMultipartFromUri("cover_image",mSelectedImage),
                getMultipartFromUri("video",mSelectedVideo));
        postVideoResponseCall.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {

                if(response.body()==null||!response.body().isSuccess()){
                    showToast("上传异常");
                    resetUploadBtn();
                    return;
                }
                Feed feed=response.body().getResult();
                List<Feed> list=new ArrayList<>();
                list.add(feed);
                loadPics(list);
                showToast("上传成功");
                resetUploadBtn();
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                showToast("上传失败");
                resetUploadBtn();
            }
        });

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
    }

    public void fetchFeed(View view) {
        clearList();
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        Call<FeedResponse> feedResponseCall = RetrofitManager
            .get("http://test.androidcamp.bytedance.com/mini_douyin/invoke/")
            .create(IMiniDouyinService.class)
            .getFeed();
        feedResponseCall.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {

                if(response.body()==null||!response.body().isSuccess()){
                    showToast("获取数据异常");
                    resetRefreshBtn();
                    return;
                }
                List<Feed> list=response.body().getFeeds();
                loadPics(list);
                showToast("获取数据成功");
                resetRefreshBtn();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                showToast("获取数据失败");
                resetRefreshBtn();
            }
        });

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
    }

    //清空list
    private void clearList(){
        mFeeds.clear();
        mRv.getAdapter().notifyDataSetChanged();
    }

    //显示通知
    private void showToast(String message){
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    //加载图片
    private void loadPics(List<Feed> feeds) {
        mFeeds = feeds;
        mRv.getAdapter().notifyDataSetChanged();
    }

    //重新设置上传按钮
    private void resetUploadBtn(){
        mBtn.setText(R.string.select_an_image);
        mBtn.setEnabled(true);
    }

    //重新设置获取数据按钮
    private void resetRefreshBtn() {
        mBtnRefresh.setText(R.string.refresh_feed);
        mBtnRefresh.setEnabled(true);
    }
}
