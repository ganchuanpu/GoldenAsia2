package com.goldenasia.lottery.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.data.Method;
import com.google.gson.JsonArray;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Gan on 2018/3/5.
 * 山东快乐扑克
 */

public class ShanDongKuaiLePuKeGame extends Game {

    private String TAG=ShanDongKuaiLePuKeGame.class.getName();

    private ArrayList<CharSequence> mPickList;//当前选中的图片的中文名字集合

    public ShanDongKuaiLePuKeGame(Method method) {
        super(method);
        mPickList = new ArrayList<>();
    }

    @Override
    public void onInflate() {

//        try {
//            java.lang.reflect.Method function = getClass().getMethod(method.getName(), Game.class);
//            function.invoke(null, this);
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("SscCommonGame", "onInflate: " + "//" + method.getCname() + " " + method.getName() + " public static " +
//                    "" + "void " + method.getName() + "(Game game) {}");
//            Toast.makeText(topLayout.getContext(), "不支持的类型", Toast.LENGTH_LONG).show();
//        }

        if("PKBX".equals(method.getName())){
            PKBX(this);
        }
    }

    @Override
    public String getWebViewCode()
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mPickList.size(); i++)
        {
            stringBuilder.append(i);
        }
        JsonArray jsonArray = new JsonArray();
        jsonArray.add(stringBuilder.toString());
        return jsonArray.toString();
    }


    //确定按钮按下后 带到 购物车中的
    @Override
    public String getSubmitCodes()
    {

        StringBuilder stringBuilder = new StringBuilder();
        int length = mPickList.size();
        for (int i = 0; i < length; i++)
        {
            stringBuilder.append(mPickList.get(i));
            if (i != length - 1)
            {
                stringBuilder.append('_');
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void reset()
    {
        setAllImageState(false);
    }

    private void setAllImageState(boolean state) {
        //界面变化
        if("PKBX".equals(method.getName())){//R.id.image_01_01, R.id.image_01_02, R.id.image_01_03, R.id.image_01_04, R.id.image_01_05//包选界面
            topLayout.findViewById(R.id.image_01_01).setSelected(state);
            topLayout.findViewById(R.id.image_01_02).setSelected(state);
            topLayout.findViewById(R.id.image_01_03).setSelected(state);
            topLayout.findViewById(R.id.image_01_04).setSelected(state);
            topLayout.findViewById(R.id.image_01_05).setSelected(state);

            //数据变化
            if(state){
                mPickList.clear();
                mPickList.add("同花包选");
                mPickList.add("顺子包选");
                mPickList.add("同花顺包选");
                mPickList.add("豹子包选");
                mPickList.add("对子包选");
            }else{
                mPickList.clear();
            }

        }//同花  PKTH



        notifyListener();
    }

    @OnClick({R.id.image_01_01, R.id.image_01_02, R.id.image_01_03, R.id.image_01_04, R.id.image_01_05,//包选界面


    R.id.sdklpk_all, R.id.sdklpk_clear//全 清
    })
    public void onLayoutClick(View view)
    {

        switch (view.getId()){
            case R.id.sdklpk_all://全选
                setAllImageState(true);
                break;
            case R.id.sdklpk_clear://清除
                setAllImageState(false);
                break;
            default://点击图片
                if (view.isSelected())
                {
                    view.setSelected(false);
                    mPickList.remove(view.getTag().toString());
                } else
                {
                    view.setSelected(true);
                    mPickList.add(view.getTag().toString());
                }
        }



        notifyListener();
    }

    /*====================================具体玩法添加开始===========================================================================*/
    private static void addTopLayout(Game game, View view) {
        ViewGroup topLayout = game.getTopLayout();
        topLayout.addView(view);
    }

    //包选
    public static void PKBX(Game game) {
        View view = LayoutInflater.from(game.getTopLayout().getContext()).inflate(R.layout.pick_shandongkuailepuke_baoxuan, null, false);
        ButterKnife.bind(game, view);
        addTopLayout(game, view);
    }

    //同花  PKTH
    public static void PKTH(Game game) {

    }


    //顺子 PKSZ

    public static void PKSZ(Game game) {

    }

    //同花顺 PKTHS
    public static void PKTHS(Game game) {

    }

    //豹子         PKBZ
    public static void PKBZ(Game game) {

    }
    //对子 PKDZ

    public static void PKDZ(Game game) {

    }

    //任选一 PKRX1
    public static void PKRX1(Game game) {

    }

    //任选二  PKRX2
    public static void PKRX2(Game game) {

    }

    //任选三 PKRX3
    public static void PKRX3(Game game) {

    }

    //任选四  PKRX4
    public static void PKRX4(Game game) {

    }

    //任选五 PKRX5
    public static void PKRX5(Game game) {

    }

    //任选六  PKRX6
    public static void PKRX6(Game game) {

    }
    /*====================================具体玩法添加结束===========================================================================*/
}
