package com.example.zhangxin;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    Button button;
    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    CheckBox checkBox;
    EditText editText;
    ProgressBar progressBar;
    SeekBar seekBar;
    Switch aSwitch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        initComponent();
        initEvent();
    }

    void initComponent(){
        button=findViewById(R.id.button);
        radioGroup=findViewById(R.id.radioGroup);
        radioButton1=findViewById(R.id.radioButton1);
        radioButton2=findViewById(R.id.radioButton2);
        checkBox=findViewById(R.id.checkbox);
        editText=findViewById(R.id.editText);
        progressBar=findViewById(R.id.progressBar);
        seekBar=findViewById(R.id.seekBar);
        aSwitch=findViewById(R.id.aSwitch);
    }

    void initEvent(){
        button.setOnClickListener(this);
        radioButton1.setOnClickListener(this);
        radioButton2.setOnClickListener(this);
        checkBox.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                clickButton();
                break;
            case R.id.radioButton1:
                clickRadioButton1();
                break;
            case R.id.radioButton2:
                clickRadioButton2();
                break;
            case R.id.checkbox:
                clickCheckBox();
                break;
        }
    }

    void clickButton(){
        Log.d("输出日志","点击Button");
        reportAll();
    }

    void clickRadioButton1(){
        Log.d("输出日志","点击按钮1");
    }

    void clickRadioButton2(){
        Log.d("输出日志","点击按钮2");
    }

    void clickCheckBox(){
        Log.d("输出日志","点击按钮3");
    }

    void reportAll(){
        int radioGroupId=radioGroup.getCheckedRadioButtonId();
        String radioButtonName=((RadioButton)findViewById(radioGroupId)).getText().toString();
        boolean checkBoxStatus=checkBox.isChecked();
        String inputString=editText.getText().toString();
        int seekBarNumber=seekBar.getProgress();
        boolean aSwitchStatus=aSwitch.isChecked();
        String output="\n"
            +"选中的按钮是"+radioButtonName+"\n"
            +"按钮3为"+booleanText(checkBoxStatus)+"\n"
            +"输入的文字为"+inputString+"\n"
            +"滑动条的数值为"+seekBarNumber+"\n"
            +"开关的状态为"+booleanText(aSwitchStatus)+"\n";
        Toast.makeText(this,output,Toast.LENGTH_LONG).show();
    }

    String booleanText(boolean input){
        if(input){
            return "true";
        }
        else return "false";
    }
}
