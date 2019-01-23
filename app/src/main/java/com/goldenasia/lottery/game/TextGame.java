package com.goldenasia.lottery.game;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.data.Method;
import com.goldenasia.lottery.material.ConstantInformation;
import com.goldenasia.lottery.pattern.PickNumber;
import com.goldenasia.lottery.util.SscLHHLuDan;
import com.goldenasia.lottery.view.LuDanContentView;
import com.goldenasia.lottery.view.MultiLineRadioGroup;
import com.google.gson.JsonArray;

/**
 * Created by ACE-PC on 2016/2/18.
 */
public class TextGame extends Game {
    private static String[] disText = new String[]{"大", "小", "单", "双"};
    private static String[] dragonTigerSumText = new String[]{"龙", "虎", "和"};
    private static final int TYPE_DIGIT = 0;
    private static final int TYPE_DRAGON_TIGER_SUM = 1;
    private static int TYPE;

    private static  LuDanContentView lu_dan_contentview;

    private static  String checkedLudan="[万千]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个

    public TextGame(Method method) {
        super(method);
        switch (method.getName()) {
            case "RELHH":
                isDigital = true;
                setHasRandom(false);
                break;
            default:
                isDigital = false;
                setHasRandom(true);
        }
    }

    @Override
    public void onInflate() {
        try {
            java.lang.reflect.Method function = getClass().getMethod(method.getName(), Game.class);
            function.invoke(null, this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(topLayout.getContext(), "不支持的类型", Toast.LENGTH_LONG).show();
        }
    }

    public String getWebViewCode() {
        JsonArray jsonArray = new JsonArray();
        if (isDigital)
//            jsonArray.add(transformDigitJsonArray(digits));
        for (PickNumber pickNumber : pickNumbers) {
            jsonArray.add(transform(pickNumber.getCheckedNumber(), true, true));
        }
        return jsonArray.toString();
    }

    public String getSubmitCodes() {
        StringBuilder builder = new StringBuilder();
        switch (TYPE) {
            case TYPE_DIGIT:
                for (int i = 0, size = pickNumbers.size(); i < size; i++) {
                    builder.append(transformtext(pickNumbers.get(i).getCheckedNumber(), disText, false));
                    if (i != size - 1) {
                        builder.append(",");
                    }
                }
                break;
            case TYPE_DRAGON_TIGER_SUM:
             /*   builder.append(transformDigit(digits));
                for (int i = 0, size = pickNumbers.size(); i < size; i++) {
                    builder.append(transformtext(pickNumbers.get(i).getCheckedNumber(), dragonTigerSumText, false));
                    if (i != size - 1) {
                        builder.append(",");
                    }
                }*/

                builder.append(checkedLudan);
                for (int i = 0, size = pickNumbers.size(); i < size; i++) {
                    builder.append(transformtext(pickNumbers.get(i).getCheckedNumber(), dragonTigerSumText, false));
                    if (i != size - 1) {
                        builder.append(",");
                    }
                }
                break;
        }

        return builder.toString();
    }

    public void onRandomCodes() {
        try {
            java.lang.reflect.Method function = getClass().getMethod(method.getName() + "Random", Game.class);
            function.invoke(null, this);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("SscCommonGame", "onInflate: " + "//" + method.getCname() + "随机 " + method.getName() + " public " +
                    "static void " + method.getName() + "Random" + "(Game game) {}");
            Toast.makeText(topLayout.getContext(), "不支持的类型", Toast.LENGTH_LONG).show();
        }
    }

    public static View createDefaultPickLayout(ViewGroup container) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.pick_column, null, false);
    }

    public static View createDigitPickLayout(ViewGroup container) {
        return LayoutInflater.from(container.getContext()).inflate(R.layout.pick_column_digits, null, false);
    }

    private static void addPickTextGame(Game game, View topView, String title, String[] disText) {
        PickNumber pickNumberText = new PickNumber(topView, title);
        pickNumberText.getNumberGroupView().setNumber(1, disText.length);
        pickNumberText.setDisplayText(disText);
        pickNumberText.setNumberStyle(null);
        pickNumberText.setChooseMode(true);
        pickNumberText.setColumnAreaHideOrShow(false);
        game.addPickNumber(pickNumberText);
    }

    private static void createDigitPicklayout(Game game, String[] name, String[] disText, int digit) {
        View[] views = new View[name.length];
        for (int i = 0; i < name.length; i++) {
            View view = createDigitPickLayout(game.getTopLayout());
            view.findViewById(R.id.digit).setVisibility(View.GONE);//去掉
//            game.initDigitPanel(view, digit);
            addPickTextGame(game, view, name[i], disText);
            views[i] = view;
        }

        ViewGroup topLayout = game.getTopLayout();

        View digits_panel_ludan = getLuDan(game.getTopLayout());

        topLayout.addView(digits_panel_ludan);


        for (View view : views) {
            topLayout.addView(view);
        }

        View  ludan_content = getLuDanContent(game.getTopLayout());
        topLayout.addView(ludan_content);

        game.setColumn(name.length);
    }

    private static View getLuDan(ViewGroup container){
        View  digits_panel_ludan= LayoutInflater.from(container.getContext()).inflate(R.layout.digits_panel_ludan, null, false);
        MultiLineRadioGroup ludan_01= digits_panel_ludan.findViewById(R.id.radioGroup_sex_id);
        ludan_01.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(MultiLineRadioGroup group, int checkedId) {

                switch (checkedId){
                    case   R.id.ludan_01:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(0,1);
                        lu_dan_contentview.setTitle("万千路单");//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        checkedLudan="[万千]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_02:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(0,2);
                        lu_dan_contentview.setTitle("万百路单");
                        checkedLudan="[万百]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_03:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(0,3);
                        lu_dan_contentview.setTitle("万十路单");
                        checkedLudan="[万十]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_04:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(0,4);
                        lu_dan_contentview.setTitle("万个路单");
                        checkedLudan="[万个]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_05:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(1,2);
                        lu_dan_contentview.setTitle("千百路单");
                        checkedLudan="[千百]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_06:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(1,3);
                        lu_dan_contentview.setTitle("千十路单");
                        checkedLudan="[千十]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_07:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(1,4);
                        lu_dan_contentview.setTitle("千个路单");
                        checkedLudan="[千个]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_08:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(2,3);
                        lu_dan_contentview.setTitle("百十路单");
                        checkedLudan="[百十]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_09:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(2,4);
                        lu_dan_contentview.setTitle("百个路单");
                        checkedLudan="[百个]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;
                    case   R.id.ludan_10:
                        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(3,4);
                        lu_dan_contentview.setTitle("十个路单");
                        checkedLudan="[十个]";//万千 万百 万十 万个 千百 千十 千个 百十 百个 十个
                        break;

                }
                lu_dan_contentview.refreshViewGroup();
            }
        });

        return digits_panel_ludan;
    }


    private static View getLuDanContent(ViewGroup container){
        ConstantInformation.luDanDataList= SscLHHLuDan.getLongHuHeList(0,1);
        View  digits_panel_ludan= LayoutInflater.from(container.getContext()).inflate(R.layout.digits_panel_ludan_content, null, false);
        lu_dan_contentview = digits_panel_ludan.findViewById(R.id.lu_dan_contentview);
        return digits_panel_ludan;
    }

    private static void createPicklayout(Game game, String[] name, String[] disText) {
        View[] views = new View[name.length];
        for (int i = 0; i < name.length; i++) {
            View view = createDefaultPickLayout(game.getTopLayout());
            addPickTextGame(game, view, name[i], disText);
            views[i] = view;
        }

        ViewGroup topLayout = game.getTopLayout();
        for (View view : views) {
            topLayout.addView(view);
        }
        game.setColumn(name.length);
    }

    //后二大小单双 EXDXDS
    public static void EXDXDS(Game game) {
        TYPE = TYPE_DIGIT;
        createPicklayout(game, new String[]{"十位", "个位"}, disText);
    }

    //后三大小单双 SXDXDS
    public static void SXDXDS(Game game) {
        TYPE = TYPE_DIGIT;
        createPicklayout(game, new String[]{"百位", "十位", "个位"}, disText);
    }

    //前二大小单双 QEDXDS
    public static void QEDXDS(Game game) {
        TYPE = TYPE_DIGIT;
        createPicklayout(game, new String[]{"百位", "十位"}, disText);
    }

    //龙虎和 RELHH
    public static void RELHH(Game game) {
        TYPE = TYPE_DRAGON_TIGER_SUM;
        createDigitPicklayout(game, new String[]{"龙虎和"}, dragonTigerSumText, 2);
    }

    //后二大小单双随机 EXDXDS
    public static void EXDXDSRandom(Game game) {
        for (PickNumber pickNumber : game.pickNumbers)
            pickNumber.onRandom(random(1, 4, 1));
        game.notifyListener();
    }

    //后三大小单双随机 SXDXDS
    public static void SXDXDSRandom(Game game) {
        for (PickNumber pickNumber : game.pickNumbers)
            pickNumber.onRandom(random(1, 4, 1));
        game.notifyListener();
    }

    //前二大小单双 QEDXDS
    public static void QEDXDSRandom(Game game) {
        for (PickNumber pickNumber : game.pickNumbers)
            pickNumber.onRandom(random(1, 4, 1));
        game.notifyListener();
    }

    //龙虎和 RELHH
    public static void RELHHRandom(Game game) {
        for (PickNumber pickNumber : game.pickNumbers)
            pickNumber.onRandom(random(1, 3, 1));
        game.notifyListener();
    }
}
