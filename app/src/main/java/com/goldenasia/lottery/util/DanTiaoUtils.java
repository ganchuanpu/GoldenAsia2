package com.goldenasia.lottery.util;

import android.text.Html;
import android.text.TextUtils;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.app.GoldenAsiaApp;
import com.goldenasia.lottery.material.Ticket;
import com.goldenasia.lottery.user.UserCentre;

import java.util.List;

/**
 * Created by Gan on 2018/6/27.
 * 单挑模式工具类
 */

public class DanTiaoUtils {

    //是否弹出单挑模式 对话框
    public String isShowDialog(int lotteryId,List<Ticket> tickets){

        UserCentre userCentre = GoldenAsiaApp.getUserCentre();
        if(!userCentre.getUserIsNew()){
            return null;
        }

        switch (lotteryId){
            case 1://重庆时时彩
            case 3://黑龙江时时彩
            case 4://新疆时时彩
            case 8://天津时时彩
            case 11://亚洲分分彩
            case 49://腾讯分分差
            case 19://亚洲5分彩
            case 35://台湾五分彩
            case 37://亚洲2分彩
            case 15://亚洲妙妙彩
                return  sscIsShowDialog(tickets);
            case 2://山东11选5
            case 6://江西11选5
            case 7://广东11选5
            case 16://11选5分分彩
            case 20://北京11选5
            case 21://上海11选5
            case 32:
            case 33:
            case 34:
            case 36://山西11选5
            case 44://11选5秒秒彩
                return  isShowDialog11Select5(tickets);  //11选5 玩法说明
            case 24://超快3D
            case 9://福彩3D
                return  isShowDialog3D(tickets);
            case 27://北京PK10
            case 38://PK10分分彩
            case 47://PK10二分彩
                return  isShowDialogPk10(tickets);
            case 17://六合彩
            case 26://六合彩分分彩
                return  isShowDialogLiuHeCai(tickets);

        }
        return null;
    }

    private String isShowDialogLiuHeCai(List<Ticket> tickets) {
           /*有单挑的玩法名称*/
        StringBuilder hasDanTiaoMethods=new StringBuilder();

        for(int i=0;i<tickets.size();i++) {
            Ticket ticket = tickets.get(i);

            switch (ticket.getChooseMethod().getCname()) {
                case "四中二":
                    if(ticket.getChooseNotes()<46){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"四中二\"</font> ");
                    }
                    break;
                case "四中三":
                    if(ticket.getChooseNotes()<70){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"四中三\"</font> ");
                    }
                    break;
                case "四中四":
                    if(ticket.getChooseNotes()<140){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"四中四\"</font> ");
                    }
                    break;
                case "三中三":
                    if(ticket.getChooseNotes()<9){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"三中三\"</font> ");
                    }
                    break;
                case "三中二":
                    if(ticket.getChooseNotes()<4){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"三中二\"</font> ");
                    }
                    break;
            }
        }
        if(!TextUtils.isEmpty(hasDanTiaoMethods)){
            return hasDanTiaoMethods.append("投注含单挑注单，单挑注单盈利上限为3万元，是否继续投注？").toString();
        }
        return null;
    }

    private String isShowDialogPk10(List<Ticket> tickets) {
           /*有单挑的玩法名称*/
        StringBuilder hasDanTiaoMethods=new StringBuilder();

        for(int i=0;i<tickets.size();i++) {
            Ticket ticket = tickets.get(i);

            switch (ticket.getChooseMethod().getCname()) {
                case "后五名直选":
                case "前五名直选":
                    if(ticket.getChooseNotes()<300){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后五名组选":
                case "前五名组选":
                    if(ticket.getChooseNotes()<=2){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后四名直选":
                case "前四名直选":
                    if(ticket.getChooseNotes()<50){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后四名组选":
                case "前四名组选":
                    if(ticket.getChooseNotes()<=2){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后三名直选":
                case "前三名直选":
                    if(ticket.getChooseNotes()<7){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "前三名组六":
                case "后三名组六":
                    if(ticket.getChooseNotes()==1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后二名直选":
                case "前二名直选":
                    if(ticket.getChooseNotes()==1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后二名组选":
                case "前二名组选":
                    if(ticket.getChooseNotes()==1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
            }
        }
        if(!TextUtils.isEmpty(hasDanTiaoMethods)){
            return hasDanTiaoMethods.append("投注含单挑注单，单挑注单盈利上限为3万元，是否继续投注？").toString();
        }
        return null;
    }
    //3D 玩法说明
    private String isShowDialog3D(List<Ticket> tickets) {
           /*有单挑的玩法名称*/
        StringBuilder hasDanTiaoMethods=new StringBuilder();

        for(int i=0;i<tickets.size();i++) {
            Ticket ticket = tickets.get(i);

            switch (ticket.getChooseMethod().getCname()) {
                case "直选":
                case "直选和值":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "组三":
                case "组选和值":
                case "混合组选":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "组六":
                    if(ticket.getChooseNotes()<=2){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后二直选":
                case "前二直选":
                case "后二组选":
                case "前二组选":
                    if(ticket.getChooseNotes()<=1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
            }
        }
        if(!TextUtils.isEmpty(hasDanTiaoMethods)){
            return hasDanTiaoMethods.append("投注含单挑注单，单挑注单盈利上限为3万元，是否继续投注？").toString();
        }
        return null;
    }

    //11选5
    private String isShowDialog11Select5(List<Ticket> tickets) {
        /*有单挑的玩法名称*/
        StringBuilder hasDanTiaoMethods=new StringBuilder();

        for(int i=0;i<tickets.size();i++) {
            Ticket ticket = tickets.get(i);

            switch (ticket.getChooseMethod().getCname()) {
                case "五星直选":
                    if(ticket.getChooseNotes()<1000){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星直选\"</font> ");
                    }
                    break;
            }
        }
        if(!TextUtils.isEmpty(hasDanTiaoMethods)){
            return hasDanTiaoMethods.append("投注含单挑注单，单挑注单盈利上限为3万元，是否继续投注？").toString();
        }
        return null;
    }

    //时时彩是否弹出单挑模式 对话框
    private String sscIsShowDialog(List<Ticket> tickets) {
        /*有单挑的玩法名称*/
        StringBuilder hasDanTiaoMethods=new StringBuilder();

        for(int i=0;i<tickets.size();i++){
            Ticket ticket=tickets.get(i);

            switch (ticket.getChooseMethod().getCname()){
                case "五星直选":
                    if(ticket.getChooseNotes()<1000){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星直选\"</font> ");
                    }
                    break;
                case "五星连选":
                    if(ticket.getChooseNotes()<1000){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星连选\"</font> ");
                    }
                    break;
                case "组选120":
                    if(ticket.getChooseNotes()<6){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选120\"</font> ");
                    }
                    break;
                case "组选60":
                    if(ticket.getChooseNotes()<12){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选60\"</font> ");
                    }
                    break;
                case "组选30":
                    if(ticket.getChooseNotes()<30){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选30\"</font> ");
                    }
                    break;
                case "组选20":
                    if(ticket.getChooseNotes()<48){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选20\"</font> ");
                    }
                    break;
                case "组选10":
                    if(ticket.getChooseNotes()<45){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选10\"</font> ");
                    }
                    break;
                case "组选5":
                    if(ticket.getChooseNotes()<45){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星组选5\"</font> ");
                    }
                    break;
                case "五星和值":
                    if(ticket.getChooseNotes()<1000){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"五星和值\"</font> ");
                    }
                    break;
                case "后四直选":
                    if(ticket.getChooseNotes()<100){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后四直选\"</font> ");
                    }
                    break;
                case "后四组选24":
                    if(ticket.getChooseNotes()<5){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后四组选24\"</font> ");
                    }
                    break;
                case "前四组选24":
                    if(ticket.getChooseNotes()<5){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前四组选24\"</font> ");
                    }
                    break;
                case "后四组选12":
                    if(ticket.getChooseNotes()<9){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后四组选12\"</font> ");
                    }
                    break;
                case "前四组选12":
                    if(ticket.getChooseNotes()<9){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前四组选12\"</font> ");
                    }
                    break;
                case "后四组选6":
                    if(ticket.getChooseNotes()<24){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后四组选6\"</font> ");
                    }
                    break;
                case "前四组选6":
                    if(ticket.getChooseNotes()<24){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前四组选6\"</font> ");
                    }
                    break;
                case "后四组选4":
                    if(ticket.getChooseNotes()<24){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后四组选6\"</font> ");
                    }
                    break;
                case "前四组选4":
                    if(ticket.getChooseNotes()<24){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前四组选6\"</font> ");
                    }
                    break;
                case "后三直选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三直选\"</font> ");
                    }
                    break;
                case "后三连选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三连选\"</font> ");
                    }
                    break;
                case "前三直选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三直选\"</font> ");
                    }
                    break;
                case "前三连选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三连选\"</font> ");
                    }
                    break;
                case "中三直选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三直选\"</font> ");
                    }
                    break;
                case "中三连选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三连选\"</font> ");
                    }
                    break;
                case "后三和值":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三和值\"</font> ");
                    }
                    break;
                case "前三和值":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三和值\"</font> ");
                    }
                    break;
                case "中三和值":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三和值\"</font> ");
                    }
                    break;
                case "后三组三":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三组三\"</font> ");
                    }
                    break;
                case "后三混合组选":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三混合组选\"</font> ");
                    }
                    break;
                case "后三包胆":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"后三包胆\"</font> ");
                    }
                    break;
                case "前三组三":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三组三\"</font> ");
                    }
                    break;
                case "前三混合组选":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三混合组选\"</font> ");
                    }
                    break;
                case "前三包胆":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"前三包胆\"</font> ");
                    }
                    break;
                case "中三组三":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三组三\"</font> ");
                    }
                    break;
                case "中三混合组选":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三混合组选\"</font> ");
                    }
                    break;
                case "中三包胆":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\"中三包胆\"</font> ");
                    }
                    break;
                case "后三包点":
                case "中三包点":
                case "前三包点":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后二直选":
                case "前二直选":
                case "后二和值":
                case "前二和值":
                    if(ticket.getChooseNotes()<1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "后二组选":
                case "前二组选":
                    if(ticket.getChooseNotes()<1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "任二直选":
                case "任二组选":
                    if(ticket.getChooseNotes()<=1){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "任三直选":
                    if(ticket.getChooseNotes()<10){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "任三组三":
                    if(ticket.getChooseNotes()<2){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "任三组六":
                case "任三混合组选":
                    if(ticket.getChooseNotes()<3){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                case "任四直选":
                    if(ticket.getChooseNotes()<100){
                        hasDanTiaoMethods.append("<font color=\'#8F0000\'>\""+ticket.getChooseMethod().getCname()+"\"</font> ");
                    }
                    break;
                default:
                    ;
            }
        }

        if(!TextUtils.isEmpty(hasDanTiaoMethods)){
            return hasDanTiaoMethods.append("投注含单挑注单，单挑注单盈利上限为3万元，是否继续投注？").toString();
        }

        return null;
    }

}
