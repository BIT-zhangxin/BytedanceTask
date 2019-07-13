package com.example.chapter3.homework;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.airbnb.lottie.LottieAnimationView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlaceholderFragment extends Fragment {

    LottieAnimationView lottieAnimationView;
    ListView listView;
    List<Friend> friendList =new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_placeholder, container, false);

        lottieAnimationView=view.findViewById(R.id.lottie_animation_view);
        listView=view.findViewById(R.id.list_view_friend);
        listView.setVisibility(View.GONE);
        iniData();

        FriendAdapter friendAdapter=new FriendAdapter(getContext(),R.layout.friend_item,friendList);
        listView.setAdapter(friendAdapter);

        // TODO ex3-3: 修改 fragment_placeholder，添加 loading 控件和列表视图控件
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Objects.requireNonNull(getView()).postDelayed(new Runnable() {
            @Override
            public void run() {

                ObjectAnimator animator1=ObjectAnimator.ofFloat(lottieAnimationView,
                    "alpha",1f,0f);
                animator1.setDuration(1000);
                animator1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        lottieAnimationView.setVisibility(View.GONE);
                    }
                });
                AnimatorSet animatorSet1 = new AnimatorSet();
                animatorSet1.playTogether(animator1);
                animatorSet1.start();

                listView.setVisibility(View.VISIBLE);
                ObjectAnimator animator2=ObjectAnimator.ofFloat(listView,
                    "alpha",0f,1f);
                animator2.setDuration(1000);
                AnimatorSet animatorSet2 = new AnimatorSet();
                animatorSet2.playTogether(animator2);
                animatorSet2.start();

                // 这里会在 5s 后执行
                // TODO ex3-4：实现动画，将 lottie 控件淡出，列表数据淡入
            }
        }, 5000);
    }

    void iniData(){

        for(int i=1;i<=30;i++){
            Friend friend=new Friend();
            friend.setId(i);
            friend.setTitle("好友"+i);
            friend.setDescription("描述"+i);
            friendList.add(friend);
        }
    }
}
