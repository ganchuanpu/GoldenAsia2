package com.goldenasia.lottery.fragment;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.cpiz.android.bubbleview.BubbleLinearLayout;
import com.cpiz.android.bubbleview.BubblePopupWindow;
import com.cpiz.android.bubbleview.BubbleStyle;
import com.goldenasia.lottery.R;
import com.goldenasia.lottery.app.BaseFragment;
import com.goldenasia.lottery.app.FragmentLauncher;
import com.goldenasia.lottery.component.TabPageAdapter;
import com.goldenasia.lottery.pattern.CustomViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gan on 2017/10/31.
 * 游戏报表
 */

public class GameReportMainFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.title)
    TextView title;

    @BindView(R.id.main_money_sum)
    TextView main_money_sum;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.tab_menu1)
    RadioButton tabMenu1;
    @BindView(R.id.tab_menu2)
    RadioButton tabMenu2;
    @BindView(R.id.tab_menu3)
    RadioButton tabMenu3;
    @BindView(R.id.tab_menu4)
    RadioButton tabMenu4;
    @BindView(R.id.tab_menu5)
    RadioButton tabMenu5;
    @BindView(R.id.tab_menu6)
    RadioButton tabMenu6;
    @BindView(R.id.tab_menu7)
    RadioButton tabMenu7;

    @BindView(R.id.viewpager)
    CustomViewPager viewPager;

    private BubblePopupWindow bubblePopupWindow;

    private List<Fragment> fragments = new ArrayList<>();

    private String sumProfit1 = "0.0";//今天的总盈利
    private String sumProfit2 = "0.0";//昨天总盈利
    private String sumProfit3 = "0.0";//近3天总盈利
    private String sumProfit4 = "0.0";//近7天的总盈利
    private String sumProfit5 = "0.0";//近15天的总盈利
    private String sumProfit6 = "0.0";//当月的总盈利
    private String sumProfit7 = "0.0";//近35天的总盈利
    private int platformType = 1;

    public static void launch(BaseFragment fragment) {
        ArrayList gameList = new ArrayList();
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);
        gameList.add(GameReportSubFragment.class);

        Bundle bundle = new Bundle();
        bundle.putString("title", "");
        bundle.putStringArray("radiotitle", new String[]{"界面1", "界面2", "界面3", "界面4", "界面5", "界面6", "界面7"});
        bundle.putParcelableArrayList("fragmentlist", gameList);
        bundle.putSerializable("hasTab", true);

        FragmentLauncher.launch(fragment.getActivity(), GameReportMainFragment.class, bundle);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.game_report_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initTab();
        initPopupWindow();
    }

    private void initTab() {

        Bundle bundle = getArguments();
        if (bundle != null) {
            String[] radiotitle = bundle.getStringArray("radiotitle");
            for (int i = 0; i < radiotitle.length; i++)
                if (i == 0)
                    tabMenu1.setId(i);
                else if (i == 1)
                    tabMenu2.setId(i);
                else if (i == 2)
                    tabMenu3.setId(i);
                else if (i == 3)
                    tabMenu4.setId(i);
                else if (i == 4)
                    tabMenu5.setId(i);
                else if (i == 5)
                    tabMenu6.setId(i);
                else
                    tabMenu7.setId(i);

            ArrayList fragmentList = bundle.getParcelableArrayList("fragmentlist");
            for (int i = 0; i < fragmentList.size(); i++) {
                Class fragment = (Class) fragmentList.get(i);
                Bundle bundlelottery = new Bundle();
                bundlelottery.putInt("key", i);
                fragments.add(Fragment.instantiate(getActivity(), fragment.getName(), bundlelottery));
            }

            radioGroup.setOnCheckedChangeListener(this);
            TabPageAdapter tabPageAdapter = new TabPageAdapter(getFragmentManager(), fragments);
            viewPager.setAdapter(tabPageAdapter);
            radioGroup.check(radioGroup.getChildAt(0).getId());
            selectPage(0);
        }
    }

    @OnClick({R.id.title_text_layout, R.id.back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_text_layout://切换平台
                bubblePopupWindow.showArrowTo(v, BubbleStyle.ArrowDirection.Up, 0);
                break;
            case R.id.back:
                getActivity().finish();
                break;
            default:
                break;
        }
    }

    private void initPopupWindow() {
        View rootView = LayoutInflater.from(getActivity()).inflate(R.layout.member_report_popupwindow, null);
        BubbleLinearLayout bubbleLinearLayout = (BubbleLinearLayout) rootView.findViewById(R.id.popup_bubble);
        bubblePopupWindow = new BubblePopupWindow(rootView, bubbleLinearLayout);
        RadioGroup platform_group = (RadioGroup) rootView.findViewById(R.id.platform_group);
        RadioButton platform_one = (RadioButton) rootView.findViewById(R.id.platform_one);
        RadioButton platform_two = (RadioButton) rootView.findViewById(R.id.platform_two);
        RadioButton platform_three = (RadioButton) rootView.findViewById(R.id.platform_three);

        platform_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.platform_one:
                        changePlatform(1);

                        bubblePopupWindow.dismiss();
                        title.setText("全部平台");
                        break;
                    case R.id.platform_two:
                        changePlatform(2);

                        bubblePopupWindow.dismiss();
                        title.setText("彩票");
                        break;
                    case R.id.platform_three:
                        changePlatform(3);

                        bubblePopupWindow.dismiss();
                        title.setText("GA游戏");
                        break;
                }
            }
        });
    }

    /**
     * 1 代表 全部平台
     * 2 代表 彩票平台
     * 3 代表 GA游戏平台
     *
     * @param platformType
     */
    private void changePlatform(int platformType) {
        GameReportSubFragment gameReportSubFragment;
        this.platformType = platformType;
        switch (platformType) {
            case 1:
                gameReportSubFragment = (GameReportSubFragment) (fragments.get(viewPager.getCurrentItem()));
                gameReportSubFragment.changePlatForm(1);

                break;
            case 2:
                gameReportSubFragment = (GameReportSubFragment) (fragments.get(viewPager.getCurrentItem()));
                gameReportSubFragment.changePlatForm(2);
                break;
            case 3:
                gameReportSubFragment = (GameReportSubFragment) (fragments.get(viewPager.getCurrentItem()));
                gameReportSubFragment.changePlatForm(3);
                break;
            default:
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < fragments.size(); i++) {
            if (i == checkedId) {
                selectPage(i);
            }
        }
    }

    private void selectPage(int position) {
        radioGroup.check(radioGroup.getChildAt(position).getId());
        viewPager.setCurrentItem(position, true);
        viewPager.setScanScroll(false);

        //总盈利
        changeProfitTextView();
    }

    public void changeMoneySumText(HashMap<String, Integer> keyMap, HashMap<String, String> valueMap) {

        switch (keyMap.get("key")) {
            case 0:
                sumProfit1 = valueMap.get("value");
                break;
            case 1:
                sumProfit2 = valueMap.get("value");
                break;
            case 2:
                sumProfit3 = valueMap.get("value");
                break;
            case 3:
                sumProfit4 = valueMap.get("value");
                break;
            case 4:
                sumProfit5 = valueMap.get("value");
                break;
            case 5:
                sumProfit6 = valueMap.get("value");
                break;
            case 6:
                sumProfit7 = valueMap.get("value");
                break;
        }

        changeProfitTextView();
    }

    private void changeProfitTextView() {
        switch (viewPager.getCurrentItem()) {
            case 0:
                main_money_sum.setText(sumProfit1);
                break;
            case 1:
                main_money_sum.setText(sumProfit2);
                break;
            case 2:
                main_money_sum.setText(sumProfit3);
                break;
            case 3:
                main_money_sum.setText(sumProfit4);
                break;
            case 4:
                main_money_sum.setText(sumProfit5);
                break;
            case 5:
                main_money_sum.setText(sumProfit6);
                break;
            case 6:
                main_money_sum.setText(sumProfit7);
                break;
        }
    }

    public int getPlatformType() {
        return platformType;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}