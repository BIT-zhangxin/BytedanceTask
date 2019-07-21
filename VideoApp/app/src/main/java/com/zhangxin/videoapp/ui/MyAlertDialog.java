package com.zhangxin.videoapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.zhangxin.videoapp.R;

public class MyAlertDialog extends AlertDialog implements View.OnClickListener{

    private Context context;//上下文
    private ScrollView mMsgScroll;//内容的父节点，用于内容较多时可以滚动
    private View mCancelLine;//取消按钮判断的竖线

    private ImageView iv_icon;//图标
    private TextView tv_title;//标题
    private TextView tv_message;//内容
    private Button btn_true;//确认按钮
    private Button btn_false;//取消按钮

    private int iconImgResId = 0;//图标的resId值
    private String titleStr="";//标题文本
    private String messageStr="";//内容文本
    private String trueButtonStr="";//确认按钮文本
    private String falseButtonStr="";//取消按钮文本
    private boolean falseButtonHidden =false;//是否隐藏取消按钮

    public MyAlertDialog(Context context,int iconImgResId,String titleStr,String messageStr, String confirmBtnTvStr, String cancelBtnTvStr, boolean falseButtonHidden) {
        //设置对话框样式
        super(context, R.style.my_alert_dialog);
        //设置为false，按对话框以外的地方不起作用
        setCanceledOnTouchOutside(true);
        //设置为false，按返回键不能退出
        setCancelable(true);

        this.context = context;
        this.iconImgResId = iconImgResId;
        this.titleStr=titleStr;
        this.messageStr = messageStr;
        this.trueButtonStr = confirmBtnTvStr;
        this.falseButtonStr = cancelBtnTvStr;
        this.falseButtonHidden = falseButtonHidden;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_alert_dialog_layout);
        initComponent();
        initView();
        initEvent();
    }

    private void initComponent(){
        iv_icon= findViewById(R.id.iv_my_alert_dialog);
        tv_title= findViewById(R.id.tv_my_alert_dialog_title);
        mMsgScroll= findViewById(R.id.scrollView_my_alert_dialog);
        tv_message= findViewById(R.id.tv_my_alert_dialog_text);
        btn_true= findViewById(R.id.btn_my_alert_dialog_true);
        btn_false= findViewById(R.id.btn_my_alert_dialog_false);
        mCancelLine=findViewById(R.id.cancel_line);
    }

    private void initView() {
        //设置标题和图标，如果图标为空，则显示标题
        if(iconImgResId!=0){
            iv_icon.setBackgroundResource(iconImgResId);
            tv_title.setVisibility(View.GONE);
        }else{
            iv_icon.setVisibility(View.GONE);
            tv_title.setVisibility(View.VISIBLE);
        }

        //设置标题和内容文本
        tv_title.setText(titleStr);
        tv_message.setText(messageStr);

        //设置按钮文本
        if(!TextUtils.isEmpty(trueButtonStr)){
            btn_true.setText(trueButtonStr);
        }
        if(!TextUtils.isEmpty(falseButtonStr)){
            btn_false.setText(falseButtonStr);
        }

        //隐藏取消按钮
        if(falseButtonHidden){
            btn_false.setVisibility(View.GONE);
            mCancelLine.setVisibility(View.GONE);
            btn_true.setBackgroundResource(R.drawable.alert_dialog_confirm_one_btn_bg);//设置确认按钮的背景
        }

        //设置内容文本居中对齐
        FrameLayout.LayoutParams msgParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        msgParams.gravity = Gravity.CENTER_HORIZONTAL;
        tv_message.setLayoutParams(msgParams);

        //设置内容区域的父节点的高度和内容文本大小
        final DisplayMetrics display = new DisplayMetrics();
        ((Activity)this.context).getWindowManager().getDefaultDisplay().getMetrics(display);
        if (display.widthPixels <= 480) {
            tv_message.setTextSize(15.0F);
        }
        final int screenHeight = display.heightPixels;

        //runnable中的方法会在View的measure、layout等事件后触发
        mMsgScroll.post(new Runnable() {
            @Override
            public void run() {
                if (mMsgScroll.getMeasuredHeight() > screenHeight / 2) {
                    mMsgScroll.setLayoutParams(new LinearLayout.LayoutParams(display.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.alert_dialog_confirm_margin) * 2,
                            screenHeight / 2));
                }else{
                    mMsgScroll.setLayoutParams(new LinearLayout.LayoutParams(display.widthPixels - context.getResources().getDimensionPixelOffset(R.dimen.alert_dialog_confirm_margin) * 2,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }
        });
    }

    private void initEvent(){
        btn_true.setOnClickListener(this);
        btn_false.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_my_alert_dialog_true:
                if(mOnMyAlertDialogListener !=null){
                    mOnMyAlertDialogListener.onCertainButtonClick();
                }
                break;
            case R.id.btn_my_alert_dialog_false:
                if(mOnMyAlertDialogListener !=null){
                    mOnMyAlertDialogListener.onCancelButtonClick();
                }
                break;
        }
        dismiss();
    }

    @Override
    public void dismiss(){
        super.dismiss();
        if(mOnMyAlertDialogListener !=null){
            mOnMyAlertDialogListener.onDismissListener();
        }
    }

    public interface onMyAlertDialogListener
    {
        //取消按钮的点击事件接口
        void onCancelButtonClick();
        //确认按钮的点击事件接口
        void onCertainButtonClick();
        //返回键触发的事件接口
        void onDismissListener();
    }

    private onMyAlertDialogListener mOnMyAlertDialogListener;

    public void setOnCertainButtonClickListener(onMyAlertDialogListener mOnMyAlertDialogListener)
    {
        this.mOnMyAlertDialogListener = mOnMyAlertDialogListener;
    }
}