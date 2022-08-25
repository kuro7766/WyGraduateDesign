package com.example.admin.wirelessspectrometer;


import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Parameters extends android.support.v4.app.Fragment implements View.OnClickListener {

    private RadioButton rb_intensity,rb_camera;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private ArrayList<android.support.v4.app.Fragment> fragmentList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_parameters,container,false);
        init(view);
        return view;
    }

    @Override
    public void onClick(View view) {
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (view.getId()){
            case R.id.rb_intensity:
                fragmentTransaction.hide(fragmentList.get(1));  //第2个元素
                fragmentTransaction.show(fragmentList.get(0));
                rb_intensity.setTextColor(Color.BLACK);
                rb_camera.setTextColor(Color.WHITE);
                break;
            case R.id.rb_camera:
                fragmentTransaction.hide(fragmentList.get(0));
                fragmentTransaction.show(fragmentList.get(1));
                rb_camera.setTextColor(Color.BLACK);
                rb_intensity.setTextColor(Color.WHITE);
                break;
        }
        fragmentTransaction.commit();
    }
    public void init(View view) {
        rb_intensity = view.findViewById(R.id.rb_intensity);
        rb_camera = view.findViewById(R.id.rb_camera);
        rb_intensity.setOnClickListener(this);
        rb_camera.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentManager = getChildFragmentManager();            //fragment 里子容器的管理器
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentList = new ArrayList<>();
        fragmentList.add(new ParametersIntensity());
        fragmentList.add(new ParametersCamera());
        fragmentTransaction.add(R.id.fl_view,fragmentList.get(0));
        fragmentTransaction.add(R.id.fl_view,fragmentList.get(1));
        fragmentTransaction.hide(fragmentList.get(1));
        fragmentTransaction.show(fragmentList.get(0));
        fragmentTransaction.commit();
        rb_intensity.setTextColor(Color.BLACK);
    }
}
