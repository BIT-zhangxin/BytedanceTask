package com.zhangxin.videoapp.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.zhangxin.videoapp.MyApplication;
import com.zhangxin.videoapp.R;

public class InformationFragment extends Fragment {

    private TextView tv_student_id;
    private TextView tv_user_name;

    private EditText et_student_id;
    private EditText et_user_name;

    private Button btn_information_commit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_information, container, false);

        tv_student_id=view.findViewById(R.id.tv_student_id);
        tv_user_name=view.findViewById(R.id.tv_user_name);
        tv_student_id.setText(((MyApplication)getActivity().getApplication()).getStudent_id());
        tv_user_name.setText(((MyApplication)getActivity().getApplication()).getUser_name());

        et_student_id=view.findViewById(R.id.et_student_id);
        et_user_name=view.findViewById(R.id.et_user_name);
        btn_information_commit=view.findViewById(R.id.btn_information_commit);

        btn_information_commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String student_id=et_student_id.getText().toString();
                String user_name=et_user_name.getText().toString();
                tv_student_id.setText(student_id);
                tv_user_name.setText(user_name);
                ((MyApplication)getActivity().getApplication()).setStudent_id(student_id);
                ((MyApplication)getActivity().getApplication()).setUser_name(user_name);
            }
        });

        return view;
    }
}
