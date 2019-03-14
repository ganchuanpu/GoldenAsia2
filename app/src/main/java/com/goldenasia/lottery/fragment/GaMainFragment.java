package com.goldenasia.lottery.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.app.BaseFragment;
import com.goldenasia.lottery.app.GoldenAsiaApp;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sakura on 2017/3/14.
 */

public class GaMainFragment extends BaseFragment {
    private static final String TAG = GaMainFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ga_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @OnClick({R.id.imageView1, R.id.imageView2, R.id.imageView3, R.id.imageView4})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView1:
//                launchFragment(GaFragment.class);
                Bundle bundle = new Bundle();
                bundle.putString("url", GoldenAsiaApp.BASEURL + "/index.jsp? c=game&a=gagame");
                launchFragment(WebViewFragment.class, bundle);

                break;
            case R.id.imageView2:
                bundle = new Bundle();//BASEURL = "http://ta.jinyazhou88.org"
                bundle.putString("url", GoldenAsiaApp.BASEURL + "/index.jsp? c=game&a=egame");
                launchFragment(WebViewFragment.class, bundle);

//                openBrowser(GoldenAsiaApp.BASEURL + "/index.jsp? c=game&a=egame");
                break;
            case R.id.imageView3:
                bundle = new Bundle();
                bundle.putString("url", GoldenAsiaApp.BASEURL + "/index.jsp? c=game&a=mggame");
                launchFragment(WebViewFragment.class, bundle);

//                openBrowser(GoldenAsiaApp.BASEURL +"/index.jsp? c=game&a=mggame");
                break;
            case R.id.imageView4:
                bundle = new Bundle();
                bundle.putString("url", GoldenAsiaApp.BASEURL + "/index.jsp? c=game&a=bbingame");
                launchFragment(WebViewFragment.class, bundle);

//                openBrowser(GoldenAsiaApp.BASEURL  +"/index.jsp? c=game&a=bbingame");
                break;


            default:
                break;
        }
    }


    private void openBrowser(String url) {
        Uri CONTENT_URI_BROWSERS = Uri.parse(url);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(CONTENT_URI_BROWSERS);//Url 就是你要打开的网址x
        // 注意此处的判断intent.resolveActivity()可以返回显示该Intent的Activity对应的组件名
        // 官方解释 : Name of the component implementing an activity that can display the intent
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            final ComponentName componentName = intent.resolveActivity(getActivity().getPackageManager());
            // 打印Log   ComponentName到底是什么
            Log.e(TAG, "componentName = " + componentName.getClassName());
            startActivity(Intent.createChooser(intent, "请选择浏览器"));
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "没有匹配的程序", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}