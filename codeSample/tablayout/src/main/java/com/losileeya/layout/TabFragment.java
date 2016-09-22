package com.losileeya.layout;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-22
 * Time: 12:23
 * 类描述：
 *
 * @version :
 */
public class TabFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
     View   view = inflater.inflate(R.layout.item, container,false);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String content = getArguments().getString("content");
        TextView tvContent = (TextView) getView().findViewById(R.id.tv_tab_content);
        tvContent.setText(content + "");
    }
}
