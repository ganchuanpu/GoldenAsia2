package com.goldenasia.lottery.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.app.BaseFragment;
import com.goldenasia.lottery.app.FragmentLauncher;
import com.goldenasia.lottery.app.GoldenAsiaApp;
import com.goldenasia.lottery.base.net.GsonHelper;
import com.goldenasia.lottery.base.net.JsonString;
import com.goldenasia.lottery.base.net.RestCallback;
import com.goldenasia.lottery.base.net.RestRequest;
import com.goldenasia.lottery.base.net.RestRequestManager;
import com.goldenasia.lottery.base.net.RestResponse;
import com.goldenasia.lottery.component.CustomDialog;
import com.goldenasia.lottery.component.DialogLayout;
import com.goldenasia.lottery.data.Additional;
import com.goldenasia.lottery.data.GetSinglePickCommand;
import com.goldenasia.lottery.data.Lottery;
import com.goldenasia.lottery.data.PayMoneyCommand;
import com.goldenasia.lottery.data.Trace;
import com.goldenasia.lottery.data.TraceData;
import com.goldenasia.lottery.data.TraceIssue;
import com.goldenasia.lottery.data.TraceIssueCommand;
import com.goldenasia.lottery.game.PromptManager;
import com.goldenasia.lottery.material.ChaseRuleData;
import com.goldenasia.lottery.material.ConstantInformation;
import com.goldenasia.lottery.material.ShoppingCart;
import com.goldenasia.lottery.pattern.ChaseRowView;
import com.goldenasia.lottery.pattern.TaskPlanView;
import com.goldenasia.lottery.pattern.TitleTimingSalesView;
import com.goldenasia.lottery.user.UserCentre;
import com.goldenasia.lottery.util.DanTiaoUtils;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by ACE-PC on 2016/3/7.
 */
public class ChaseFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, TaskPlanView.OnArrangeChangedListener {
    private static final String TAG = ChaseFragment.class.getSimpleName();
    private static final int BUY_TRACE_ID = 1;
    private static final int ID_TRACE_CHASE_ISSUE_INFO = 3;
    private static final int GET_SINGLE_PICK = 5;

    /**
     * 　信息提示
     */
    private static final int TRACK_TURNED_PAGE_LOGIN = 1;
    private static final int TRACK_TURNED_PAGE_RECHARGE = 2;
    private static final int TRACK_TURNED_PAGE_PICK = 3;
    private static final int TRACK_PROMPT_TIP = 4;

    @BindView(R.id.mode_radiogroup)
    RadioGroup modeRadiogroup;
    @BindView(R.id.definition)
    RadioButton definition;
    @BindView(R.id.interest)
    RadioButton interest;
    @BindView(R.id.chase_timing)
    View chaseTiming;
    @BindView(R.id.plan_ruleview)
    View planLayout;
    @BindView(R.id.chaseTitle)
    View chaseTitle;
    @BindView(R.id.chaselistview)
    ViewGroup chaseLVLayout;
    @BindView(R.id.chase_mete)
    TextView chaseMete;
    @BindView(R.id.chase_total)
    TextView chaseTotal;
    @BindView(R.id.plan_buybutton)
    Button buybutton;

    private TitleTimingSalesView timingView;
    private Lottery lottery;
    private Additional additional;
    private ShoppingCart cart;
    private UserCentre userCentre;
    private TaskPlanView planView;
    private TraceIssue traceIssue = null;
    //cost 当前成本　grandTotal 累计投入　Bonus 资金 total 累计利润　TotalProfit 利润率 Total Cost 购物车总价
    private double bonus = 0, totalCost = 0, cost = 0, tempAmount = 0;

    //multiple 倍数　tagType 模式(0自定义模式、1利润率模式)
    private int tagType = 0, share = 0;
    private List<View> views = new ArrayList<>();
    private SparseIntArray multipleArray = new SparseIntArray();
    private ArrayList<ChaseRowView> chaseRowLV = new ArrayList<>();
    private List<String> serialsList = new ArrayList<>();
    private List<String> issueList = new ArrayList<>();

    private Handler handler;

    public static void launch(BaseFragment fragment, Lottery lottery, Additional additional) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("lottery", lottery);
        bundle.putString("additional", GsonHelper.toJson(additional));
        FragmentLauncher.launch(fragment.getActivity(), ChaseFragment.class, bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflateView(inflater, container, "智能追号", R.layout.fragment_chase);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parameter();
        loadTimingView();
        loadPlanRuleView();
        initInfo();
        GetSinglePick();
    }

    private void GetSinglePick() {
        GetSinglePickCommand command = new GetSinglePickCommand();
        executeCommand(command, restCallback, GET_SINGLE_PICK);
    }

    @OnClick(R.id.plan_buybutton)
    public void total() {
        if (timingView.getIssue() == null || timingView.getIssue().length() <= 0) {
            tipDialog("温馨提示", "请稍等，正在加载销售奖期信息……", 0);
            return;
        }

        // ①判断：购物车中是否有投注
        if (!cart.isEmpty()) {
            // ②判断：用户是否登录——被动登录
            if (userCentre.isLogin()) {
                // ③判断：用户的余额是否满足投注需求
                if (cart.getPlanAmount() <= userCentre.getUserInfo().getBalance()) {
                    // ④界面跳转：跳转到追期和倍投的设置界面
                    DanTiaoUtils danTiaoUtils = new DanTiaoUtils();
                    String danTiaoString = danTiaoUtils.isShowDialogChase(lottery.getLotteryId());
                    if (TextUtils.isEmpty(danTiaoString)) {
                        verificationData();
                    } else {
                        tipDialog2("提示", danTiaoString);
                        return;
                    }
                } else {
                    // 提示用户：充值去；界面跳转：用户充值界面
                    tipDialog("温馨提示", "请充值", TRACK_TURNED_PAGE_RECHARGE);
                }
            } else {
                // 提示用户：登录去；界面跳转：用户登录界面
                tipDialog("温馨提示", "请重新登录", TRACK_TURNED_PAGE_LOGIN);
            }
        } else {
            // 提示用户需要选择一注
            tipDialog("温馨提示", "您需要选择一注", TRACK_TURNED_PAGE_PICK);
        }
    }

    @Override
    public void onDestroyView() {
        cart.setChaserule(new ChaseRuleData());
        if (timingView != null) {
            timingView.stop();
        }
        super.onDestroyView();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.definition:
                selectPage(0);
                break;
            case R.id.interest:
                selectPage(1);
                break;
        }
    }

    private void parameter() {
        this.lottery = (Lottery) getArguments().getSerializable("lottery");
        String addit = getArguments().getString("additional");
        if (!TextUtils.isEmpty(addit)) {
            this.additional = GsonHelper.fromJson(addit, Additional.class);
        } else {
            this.additional = null;
        }
        this.cart = ShoppingCart.getInstance();
        this.userCentre = GoldenAsiaApp.getUserCentre();
    }

    private void initInfo() {
        cart.init(lottery);
        setTitle(lottery.getCname());
        planPrompt(0, 0);
        modeRadiogroup.setOnCheckedChangeListener(this);
        chaseIssueCommand();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0: //清除ＵＩ
                        if (chaseLVLayout != null) {
                            chaseLVLayout.removeAllViews();
                        }
                        break;
                    case 1:
                        if (chaseLVLayout != null && views != null) {
                            for (View view : views) {
                                chaseLVLayout.removeView(view);
                                chaseLVLayout.addView(view);
                            }
                        }
                        break;
                }
            }
        };
    }

    private void loadTimingView() {
        timingView = new TitleTimingSalesView(getActivity(), findViewById(R.id.chase_timing), lottery);
        timingView.setOnSalesIssueListener((salesIssue) ->
        {
            if (chaseRowLV.size() > 0) {
                share = 0;
                selectPage(tagType);
            }
        });
    }

    private void loadPlanRuleView() {
        planView = new TaskPlanView(getActivity(), planLayout, this, additional);
    }

    private void planPrompt(int cmete, double ctotal) {
        String isCmeteTips = getContext().getResources().getString(R.string.is_chasemete_tips);
        isCmeteTips = StringUtils.replaceEach(isCmeteTips, new String[]{"CHASEMETE",
                "CHASETOTAL"}, new String[]{String.format("%d", cmete), String.format("%.3f", ctotal)});
        chaseMete.setText(Html.fromHtml(isCmeteTips));
        String isCtotalTips = getContext().getResources().getString(R.string.is_balance_tips);
        isCtotalTips = StringUtils.replaceEach(isCtotalTips, new String[]{"BALANCE"}, new
                String[]{String.format("%.3f", userCentre.getUserInfo().getBalance())});
        chaseTotal.setText(Html.fromHtml(isCtotalTips));
    }

    private void createRowLayout() {
        if (cart.getPlanNotes() == 0 || traceIssue == null || timingView.getIssue().isEmpty()) {
            return;
        }
        cleanViews();
        cleanData();

        if (planView.getMultiple() == 0 || planView.getIssueno() == 0) {
            tipDialog("温馨提示", "倍数和追号期数不能为0", 0);
            return;
        }
//        this.bonus = /*userCentre.getBonusMode(lottery.getLotteryId()) == 0 */!()?   : ;
        if (cart.getPrizeMode() > 0) {
            this.bonus = traceIssue.getMaxPrize() * cart.getLucreMode().getFactor();
        } else {
            this.bonus = traceIssue.getBasePrize() * cart.getLucreMode().getFactor();
        }
        serialsList = getIssueSerials();
        if (serialsList.size() > 0) {
            for (int i = 0, size = serialsList.size(); i < size; i++) {
                View view = LayoutInflater.from(chaseLVLayout.getContext()).inflate(R.layout.fragment_chase_item, null, false);
                ChaseRowView chaseRowView = new ChaseRowView(view, serialsList.get(i), bonus, i, tagType);
                chaseRowView.setOnInvestmentListener((int newQuantity) ->
                {
                    if (multipleArray.size() == size) {
                        share = 0;
                        this.multipleArray.put(chaseRowView.getId(), newQuantity);
                        updateData(false);
                    }
                });
                chaseRowLV.add(chaseRowView);
                views.add(view);
            }
        }
    }

    /**
     * 添加显示视图
     */
    private void addLayoutView() {
        if (handler != null) {
            handler.sendEmptyMessage(1);
        }
    }

    /**
     * 过滤最大追号
     *
     * @param removeRow
     */
    private void maxFilter(String[] removeRow) {
        if (share == 0 && (removeRow == null || removeRow.length == 0)) {
            return;
        }

        for (int i = 0, size = removeRow.length; i < size; i++) {
            chaseRowLV.remove(removeRow[i]);
        }
        List<View> removeView = new ArrayList<>();
        for (int i = 0; i < removeView.size(); i++) { //倒序
            removeView.add(views.get(i));
        }
        views = removeView;
    }

    /**
     * 更新视图数据
     *
     * @param dupe
     */
    private void updateData(boolean dupe) {
        ChaseRuleData rule = this.cart.getChaserule();
        if (dupe) {
            if (this.additional != null) {
                for (int i = 0; i < this.additional.getMultipleArray().size(); i++) {
                    multipleArray.put(i, additional.getMultipleArray().get(i));
                }
            } else {
                for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
                    multipleArray.put(i, rule.getMultiple());
                }
            }
        }
        double totalCost = 0;
        switch (tagType) {
            case 0:
                totalCost = sameMultiple(rule);
                break;
            case 1:
                switch (rule.getType()) {
                    case 0:
                        totalCost = getGrowthType(rule);
                        break;
                    case 1:
                        totalCost = balancedRatioType(rule);
                        break;
                    case 2:
                        totalCost = getBonusType(rule);
                        break;
                    case 3:
                        totalCost = balancedIncomeType(rule);
                        break;
                }
                break;
        }
        tempAmount = totalCost;
        if (views.size() == 0) {
            planPrompt(0, 0);
        } else {
            planPrompt(chaseRowLV.size(), tempAmount);
            /*20171005 gan 修复追 *  期 超过一定数的时候 界面回调 start*/
            planView.setIssueno(String.valueOf(chaseRowLV.size()));
            /*20171005 gan 修复追 *  期 超过一定数的时候 界面回调 end*/
        }
    }

    private void verificationData() {
        if (traceIssue == null || timingView.getIssue().isEmpty()) {
            return;
        }
        int index = traceIssue.getIssues().indexOf(timingView.getIssue());

        if (planView.getIssueno() == 0 || planView.getIssueno() != chaseRowLV.size()) {
            if (traceIssue.getIssues().size() < planView.getIssueno()) {
                int nois = traceIssue.getIssues().size() - index;
                planView.setIssueno(String.valueOf(nois));
                tipDialog("温馨提示", "最大追期" + nois, 0);
            } else {
                tipDialog("温馨提示", "请生成追号", 0);
            }
            return;
        }

        if (views.size() == 0) {
            tipDialog("温馨提示", "请生成追号", 0);
            return;
        }

        if ((issueList.size() == 0 || multipleArray.size() == 0) && issueList.size() != multipleArray.size()) {
            tipDialog("温馨提示", "数据不匹配", 0);
            return;
        }

        List<TraceData> traceDatas = new ArrayList<>();
        for (int i = 0, size = issueList.size(); i < size; i++) {
            String issueno = issueList.get(i);
            TraceData traceData = new TraceData(issueno, multipleArray.get(i));
            traceDatas.add(traceData);
        }
        final PayMoneyCommand command = new PayMoneyCommand();
        command.setLotteryId(lottery.getLotteryId());
        command.setIssue(timingView.getIssue());
//        command.setPrizeMode(!(userCentre.getBonusMode(lottery.getLotteryId()) == 0));
        command.setPrizeMode(cart.getPrizeMode() > 0 ? 1 : 0);
        command.setModes(cart.getLucreMode().getModes());
        if (lottery.getLotteryId() == 14) {//山东快乐扑克
            command.setCodes(cart.getCodeStr().replace("10", "T"));
        } else {
            command.setCodes(cart.getCodeStr());
        }
        command.setMultiple(cart.getMultiple());
        command.setTraceNum(chaseRowLV.size());
        command.setTraceData(traceDatas);
        command.setStopOnWin(planView.isStopOnWin());
        command.setToken(ConstantInformation.randomToken());
        /*String chooseCodes = "";
        for (Ticket tk : cart.getCodesMap()) {
            chooseCodes += tk.getChooseMethod().getCname() + "\t" + tk.getCodes() + "\t";
        }*/

        String msg = getActivity().getResources().getString(R.string.is_shopping_list_chasetip);
        msg = StringUtils.replaceEach(msg, new String[]{"CHASENUM", "STATUS", "NOTE", "UNIT",
                "MONEY"}, new String[]{String.valueOf(planView.getIssueno()), planView
                .isStopOnWin() ? "是" : "否", String.valueOf(cart.getPlanNotes()), cart
                .getLucreMode().getName(), String.format("%.3f", tempAmount)});

        if (!timingView.getIssue().isEmpty()) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_buy_succeed, null);
            ImageView icon = view.findViewById(R.id.icon);
            icon.setImageResource(ConstantInformation.getLotteryLogo(lottery.getLotteryId(), true));
            TextView name = view.findViewById(R.id.name);
            name.setText(lottery.getCname());
            TextView issue = view.findViewById(R.id.issue);
            issue.setText("起始期号:" + timingView.getIssue());
            TextView info = view.findViewById(R.id.info);
            info.setText(Html.fromHtml(msg).toString());

            CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
            builder.setContentView(view);
            builder.setTitle("温馨提示");
            builder.setLayoutSet(DialogLayout.SINGLE);
            builder.setPositiveButton("确认投注", (dialog, which) ->
            {
                buybutton.setEnabled(false);
                buybutton.setBackgroundResource(R.drawable.button_type_un);
                PayCommand(command);
                dialog.dismiss();
            });
            builder.create().show();
        }
    }

    private void PayCommand(PayMoneyCommand paycommand) {
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), paycommand, restCallback, BUY_TRACE_ID, this);
        restRequest.execute();
    }


    private void chaseIssueCommand() {
        TraceIssueCommand command = new TraceIssueCommand();
        command.setLotteryId(lottery.getLotteryId());
        command.setMids(cart.getMids());
        TypeToken typeToken = new TypeToken<RestResponse<TraceIssue>>() {
        };
        RestRequest restRequest = RestRequestManager.createRequest(getActivity(), command, typeToken, restCallback, ID_TRACE_CHASE_ISSUE_INFO, this);
        restRequest.execute();
    }

    private void receiptOrderDialog(final String orderTip) {
        cart.clear();
        //防止内存泄露--静态集合对象注意释放
//        ConstantInformation.ticketList.clear();
        cleanData();
        cleanViews();
        CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
        builder.setMessage(orderTip.isEmpty() ? "投注失败!请重新投注" : "订单：" + orderTip);
        builder.setTitle(orderTip.isEmpty() ? "投注失败!" : "投注成功");
        if (!orderTip.isEmpty()) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.popup_buy_succeed, null);
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(ConstantInformation.getLotteryLogo(lottery.getLotteryId(), true));
            TextView name = (TextView) view.findViewById(R.id.name);
            name.setText(lottery.getCname());
            TextView issue = (TextView) view.findViewById(R.id.issue);
            issue.setText("起始期号:" + timingView.getIssue());
            TextView info = (TextView) view.findViewById(R.id.info);
            String isBuySucceedTips = getActivity().getResources().getString(R.string
                    .is_buy_succeed_tips);
            isBuySucceedTips = StringUtils.replaceEach(isBuySucceedTips, new
                    String[]{"ORDER_TIP", "BALANCE"}, new String[]{orderTip, String.format("%.3f", tempAmount)});
            info.setText(Html.fromHtml(isBuySucceedTips));
            //info.setText("订单编号：" + orderTip + "\n\n总金额：￥" + tempAmount);

            builder.setContentView(view);
        }

        builder.setLayoutSet(DialogLayout.LEFT_AND_RIGHT);
        builder.setPositiveButton("查看投注记录", (dialog, which) ->
        {
            dialog.dismiss();
            if (!orderTip.isEmpty()) {
                Trace trace = new Trace();
                trace.setWrapId(orderTip);
                BetOrTraceDetailFragment.launch(ChaseFragment.this, lottery, trace);
            }
            getActivity().finish();
        });

        builder.setNegativeButton("继续投注", (dialog, which) ->
        {
            getActivity().finish();
            dialog.dismiss();
        });
        CustomDialog dialog = builder.create();
        dialog.setOnDismissListener((d) -> getActivity().finish());
        dialog.show();
    }

    private void tipDialog2(String title, String msg) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setLayoutSet(DialogLayout.LEFT_AND_RIGHT);
        builder.setNegativeButton("取消", (dialog, which) ->
        {
            dialog.dismiss();
        });
        builder.setPositiveButton("确认", (dialog, which) ->
        {
            dialog.dismiss();
            verificationData();
        });
        builder.create().show();
    }

    private void tipDialog(String title, String msg, final int track) {
        CustomDialog.Builder builder = new CustomDialog.Builder(getContext());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setLayoutSet(DialogLayout.SINGLE);
        builder.setPositiveButton("知道了", (dialog, which) ->
        {
            dialog.dismiss();
            if (track == TRACK_PROMPT_TIP) {
                planPrompt(0, 0);
            }
            if (track == TRACK_TURNED_PAGE_PICK) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    private RestCallback restCallback = new RestCallback() {
        @Override
        public boolean onRestComplete(RestRequest request, RestResponse response) {
            switch (request.getId()) {
                case BUY_TRACE_ID:
                    String jsonStr = (String) response.getData();
                    if (jsonStr != null) {
                        initInfo();
                        buybutton.setEnabled(true);
                        buybutton.setBackgroundResource(R.drawable.button_type);
                        receiptOrderDialog(jsonStr);
                    }
                    break;
                case ID_TRACE_CHASE_ISSUE_INFO:
                    traceIssue = (TraceIssue) response.getData();
                    selectPage(0);
                    break;
                case GET_SINGLE_PICK:
                    String jsonString = ((JsonString) response.getData()).getJson();
                    ConstantInformation.sigle_pick = jsonString;
                    break;
            }
            return true;
        }

        @Override
        public boolean onRestError(RestRequest request, int errCode, String errDesc) {
            buybutton.setEnabled(true);
            buybutton.setBackgroundResource(R.drawable.button_type);
            if (errCode == 7006) {
                CustomDialog dialog = PromptManager.showCustomDialog(getActivity(), "重新登录", errDesc, "重新登录", errCode);
                dialog.setCancelable(false);
                dialog.show();
                return true;
            } else if (errCode == 2220) {
                showToast(errDesc, Toast.LENGTH_LONG);
                MobclickAgent.reportError(getActivity(), ConstantInformation.getInfogather());
                Log.e(TAG, ConstantInformation.getInfogather());
                return true;
            }
            return false;
        }

        @Override
        public void onRestStateChanged(RestRequest request, @RestRequest.RestState int state) {
        }
    };

    /**
     * 选择某页
     */
    private void selectPage(int position) {
        if (traceIssue == null) {
            return;
        }
        if (position == 1 && cart.getCountMethod() > 1) {
            tipDialog("温馨提示", "多个玩法不支持利润追号", 0);
            definition.setChecked(true);
            interest.setClickable(false);
            modeRadiogroup.getChildAt(1).setEnabled(false);
        } else {
            this.tagType = position;
            setShowMode();
            planPrompt(0, 0);
            modeRadiogroup.check(modeRadiogroup.getChildAt(position).getId());
            planView.setTagHideAndShow(position != 0);
            UiThread();
        }
    }

    private void setShowMode() {
        if (tagType == 0) {
            chaseTitle.findViewById(R.id.chase_deficit_title).setVisibility(View.GONE);
            chaseTitle.findViewById(R.id.chase_deficit_line).setVisibility(View.GONE);
            chaseTitle.findViewById(R.id.chase_total_title).setVisibility(View.GONE);
            chaseTitle.findViewById(R.id.chase_interestrate_title).setVisibility(View.GONE);
            chaseTitle.findViewById(R.id.chase_total_line).setVisibility(View.GONE);
            chaseTitle.findViewById(R.id.chase_interestrate_line).setVisibility(View.GONE);
        } else {
            chaseTitle.findViewById(R.id.chase_deficit_title).setVisibility(View.VISIBLE);
            chaseTitle.findViewById(R.id.chase_deficit_line).setVisibility(View.VISIBLE);
            chaseTitle.findViewById(R.id.chase_total_title).setVisibility(View.VISIBLE);
            chaseTitle.findViewById(R.id.chase_interestrate_title).setVisibility(View.VISIBLE);
            chaseTitle.findViewById(R.id.chase_total_line).setVisibility(View.VISIBLE);
            chaseTitle.findViewById(R.id.chase_interestrate_line).setVisibility(View.VISIBLE);
        }
    }

    private void cleanViews() {
        //chaseLVLayout.removeAllViews();
        if (handler != null) {
            handler.sendEmptyMessage(0);
        }
    }

    private void cleanData() {
        views.clear();
        chaseRowLV.clear();
        multipleArray.clear();
        serialsList.clear();
    }

    private void removeRow(int position) {
        chaseRowLV.remove(position);
        views.remove(position);
    }

    private List<String> getIssueSerials() {
        if (traceIssue.getIssues().size() < planView.getIssueno()) {
            int index = traceIssue.getIssues().indexOf(timingView.getIssue());
            if (index != -1) {
                issueList = traceIssue.getIssues().subList(index, traceIssue.getIssues().size() -
                        index);
                ConstantInformation.setIssueSerials(issueList);
            }
            return ConstantInformation.getIssueSerials();
        } else {
            int index = traceIssue.getIssues().indexOf(timingView.getIssue());
            if (index != -1) {
                issueList = traceIssue.getIssues().subList(index, index + planView.getIssueno());
                ConstantInformation.setIssueSerials(issueList);
            }
            return ConstantInformation.getIssueSerials();
        }
    }

    /**
     * 处理小数点的位数
     *
     * @param d
     * @return
     */
    public BigDecimal keepThree(double d) {
        if (Double.isInfinite(d)) {
            BigDecimal bg = new BigDecimal(0.00);
            return bg.setScale(3, BigDecimal.ROUND_HALF_UP);
        } else {
            BigDecimal bg = new BigDecimal(d);
            return bg.setScale(3, BigDecimal.ROUND_HALF_UP);
        }
    }

    /**
     * 相同倍数 (自定义)
     */
    private double sameMultiple(ChaseRuleData rule) {
        this.cost = cart.getPlanAmount() * rule.getMultiple();
        this.totalCost = cost;
        boolean fool = true;
        StringBuilder stopString = new StringBuilder();
        for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
            if (share == 0) {
                ChaseRowView row = chaseRowLV.get(i);
                int mlp = multipleArray.get(i);
                double mustBonus = bonus * mlp;
                if (traceIssue.getPrizeLimit() < mustBonus) {
                    share = i;
                    int baisu = 0;
                    if (i != 0) {
                        baisu = (int) (traceIssue.getPrizeLimit() / bonus - totalCost / cart.getPlanAmount());
                    } else {
                        baisu = (int) (traceIssue.getPrizeLimit() / bonus);
                    }
                    multipleArray.put(i, baisu);
                    cost = cart.getPlanAmount() * baisu;
                    totalCost = totalCost + cost;
                    if (fool) {
                        fool = false;
                        tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                        removeRow(i);
                    }
                } else {
                    cost = cart.getPlanAmount() * mlp;
                    if (i != 0) {
                        this.totalCost += cost;
                    } else {
                        this.totalCost = cost;
                    }
                }
                row.updateData(mustBonus, multipleArray.get(i), totalCost, cost, 0);
            } else {
                stopString.append(i).append(",");
            }
        }
        if (stopString.length() > 0) {
            maxFilter(stopString.substring(0, stopString.length() - 1).split(","));
        }
        addLayoutView();
        return totalCost;
    }

    /**
     * 全程利润率
     *
     * @param rule
     */
    private double getGrowthType(ChaseRuleData rule) {
        this.cost = cart.getPlanAmount() * rule.getMultiple();
        this.totalCost = cost;
        boolean fool = true;
        StringBuilder stopString = new StringBuilder();
        //cost 当前成本、　grandTotal 累计投入、　Bonus 资金、 total 累计利润、　totalProfit 利润率、 totalCost 购物车总价
        for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
            if (share == 0) {
                ChaseRowView row = chaseRowLV.get(i);
                int mlp = multipleArray.get(i);
                double mustBonus = bonus * mlp;
                cost = cart.getPlanAmount() * mlp;
                if (i != 0) {
                    this.totalCost += cost;
                } else {
                    this.totalCost = cost;
                }
                double totalProfit = (mustBonus - totalCost) / totalCost * 100; //计算净利率
                if (totalProfit < rule.getGainMode()) {
                    double predict = 0;
                    int plus = 0;
                    if (i != 0) {
                        plus = multipleArray.get(i - 1);
                    } else {
                        plus = multipleArray.get(i);
                    }
                    while (true) {
                        if (traceIssue.getPrizeLimit() < mustBonus) {
                            share = i;
                            if (fool) {
                                fool = false;
                                tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                removeRow(i);
                            }
                            break;
                        }
                        plus = plus + 1;
                        predict = totalCost + (cart.getPlanAmount() * plus - cost);
                        mustBonus = bonus * plus;
                        totalProfit = (mustBonus - predict) / predict * 100;
                        if (totalProfit >= rule.getGainMode()) {
                            totalCost = predict; //净利润
                            cost = cart.getPlanAmount() * plus; //改变当前投入金额
                            multipleArray.put(i, plus);
                            break;
                        }
                    }
                }
                row.updateData(mustBonus, multipleArray.get(i), totalCost, cost, keepThree(totalProfit).intValue());
            } else {
                stopString.append(i).append(",");
            }
        }
        if (stopString.length() > 0) {
            maxFilter(stopString.substring(0, stopString.length() - 1).split(","));
        }
        addLayoutView();
        return totalCost;
    }

    /**
     * 均衡型 (利润率)
     *
     * @param rule
     */
    private double balancedRatioType(ChaseRuleData rule) {
        this.cost = cart.getPlanAmount() * rule.getMultiple();
        this.totalCost = cost;
        boolean fool = true;
        StringBuilder stopString = new StringBuilder();
        for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
            if (share == 0) {
                ChaseRowView row = chaseRowLV.get(i);
                int mlp = multipleArray.get(i);
                double mustBonus = bonus * mlp;
                cost = cart.getPlanAmount() * mlp;

                if (i != 0) {
                    this.totalCost += cost;
                } else {
                    this.totalCost = cost;
                }
                double totalProfit = (mustBonus - totalCost) / totalCost * 100; //计算净利率
                if (i < rule.getIssueGap()) {
                    if (totalProfit < rule.getAgoValue()) {
                        double predict = 0;
                        int plus = 0;
                        if (i != 0) {
                            plus = multipleArray.get(i - 1);
                        } else {
                            plus = multipleArray.get(i);
                        }
                        while (true) {
                            if (traceIssue.getPrizeLimit() < mustBonus) {
                                share = i;
                                if (fool) {
                                    fool = false;
                                    tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                    removeRow(i);
                                }
                                break;
                            }
                            plus = plus + 1;
                            predict = totalCost + (cart.getPlanAmount() * plus - cost);
                            mustBonus = bonus * plus;
                            totalProfit = (mustBonus - predict) / predict * 100;
                            if (totalProfit >= rule.getAgoValue()) {
                                totalCost = predict; //净利润
                                cost = cart.getPlanAmount() * plus; //改变当前投入金额
                                multipleArray.put(i, plus);
                                break;
                            }
                        }
                    }
                } else if (i >= rule.getIssueGap()) {
                    if (totalProfit < rule.getLaterValue()) {
                        double predict = 0;
                        int plus = 0;
                        if (i != 0) {
                            plus = multipleArray.get(i - 1);
                        } else {
                            plus = multipleArray.get(i);
                        }
                        while (true) {
                            if (traceIssue.getPrizeLimit() < mustBonus) {
                                share = i;
                                if (fool) {
                                    fool = false;
                                    tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                    removeRow(i);
                                }
                                break;
                            }
                            plus = plus + 1;
                            predict = totalCost + (cart.getPlanAmount() * plus - cost);
                            mustBonus = bonus * plus;
                            totalProfit = (mustBonus - predict) / predict * 100;
                            if (totalProfit >= rule.getLaterValue()) {
                                totalCost = predict; //净利润
                                cost = cart.getPlanAmount() * plus; //改变当前投入金额
                                multipleArray.put(i, plus);
                                break;
                            }
                        }
                    }
                }
                row.updateData(mustBonus, multipleArray.get(i), totalCost, cost, keepThree(totalProfit).intValue());
            } else {
                stopString.append(i).append(",");
            }
        }
        if (stopString.length() > 0) {
            maxFilter(stopString.substring(0, stopString.length() - 1).split(","));
        }
        addLayoutView();
        return totalCost;
    }

    /**
     * 全程利润(元)
     *
     * @param rule
     */
    private double getBonusType(ChaseRuleData rule) {
        this.cost = cart.getPlanAmount() * rule.getMultiple();
        this.totalCost = cost;
        boolean fool = true;
        StringBuilder stopString = new StringBuilder();
        for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
            if (share == 0) {
                ChaseRowView row = chaseRowLV.get(i);
                int mlp = multipleArray.get(i);
                double mustBonus = bonus * mlp;
                cost = cart.getPlanAmount() * mlp;
                if (i != 0) {
                    this.totalCost += cost;
                } else {
                    this.totalCost = cost;
                }
                double total = mustBonus - totalCost;
                if (total < rule.getGainMode()) {
                    int plus = 0;
                    if (i != 0) {
                        plus = multipleArray.get(i - 1);
                    } else {
                        plus = multipleArray.get(i);
                    }
                    while (true) {
                        if (traceIssue.getPrizeLimit() < mustBonus) {
                            share = i;
                            if (fool) {
                                fool = false;
                                tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                removeRow(i);
                            }
                            break;
                        }
                        plus = plus + 1;
                        mustBonus = bonus * plus;
                        total = mustBonus - (i != 0 ? totalCost + cart.getPlanAmount() * plus -
                                cost : cart.getPlanAmount() * plus);
                        if (total > rule.getGainMode()) {
                            if (i != 0) {
                                totalCost = totalCost + cart.getPlanAmount() * plus - cost;
                                //6+2*3=12
                            } else {
                                totalCost = cart.getPlanAmount() * plus; //2*3=6
                            }
                            cost = cart.getPlanAmount() * plus;
                            multipleArray.put(i, plus);
                            break;
                        }
                    }
                }
                double totalProfit = (total / totalCost) * 100;
                row.updateData(mustBonus, multipleArray.get(i), totalCost, cost, keepThree(totalProfit).intValue());
            } else {
                stopString.append(i).append(",");
            }
        }
        if (stopString.length() > 0) {
            maxFilter(stopString.substring(0, stopString.length() - 1).split(","));
        }
        addLayoutView();
        return totalCost;
    }

    /**
     * 均衡型 (利润)
     *
     * @param rule
     */
    private double balancedIncomeType(ChaseRuleData rule) {
        this.cost = cart.getPlanAmount() * rule.getMultiple();
        this.totalCost = cost;
        boolean fool = true;
        StringBuilder stopString = new StringBuilder();
        for (int i = 0, size = chaseRowLV.size(); i < size; i++) {
            if (share == 0) {
                ChaseRowView row = chaseRowLV.get(i);
                int mlp = multipleArray.get(i);
                double mustBonus = bonus * mlp;
                cost = cart.getPlanAmount() * mlp;

                if (i != 0) {
                    this.totalCost += cost;
                } else {
                    this.totalCost = cost;
                }
                double total = mustBonus - totalCost;
                if (i < rule.getIssueGap()) {
                    if (total < rule.getAgoValue()) {
                        int plus = 0;
                        if (i != 0) {
                            plus = multipleArray.get(i - 1);
                        } else {
                            plus = multipleArray.get(i);
                        }
                        while (true) {
                            if (traceIssue.getPrizeLimit() < mustBonus) {
                                share = i;
                                if (fool) {
                                    fool = false;
                                    tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                    removeRow(i);
                                }
                                break;
                            }
                            plus = plus + 1;
                            mustBonus = bonus * plus;
                            total = mustBonus - (i != 0 ? totalCost + cart.getPlanAmount() * plus
                                    - cost : cart.getPlanAmount() * plus);
                            if (total > rule.getAgoValue()) {
                                if (i != 0) {
                                    totalCost = totalCost + cart.getPlanAmount() * plus - cost;
                                    //6+2*3=12
                                } else {
                                    totalCost = cart.getPlanAmount() * plus; //2*3=6
                                }
                                cost = cart.getPlanAmount() * plus;
                                multipleArray.put(i, plus);
                                break;
                            }
                        }
                    }
                } else if (i >= rule.getIssueGap()) {
                    if (total < rule.getLaterValue()) {
                        int plus = 0;
                        if (i != 0) {
                            plus = multipleArray.get(i - 1);
                        } else {
                            plus = multipleArray.get(i);
                        }
                        while (true) {
                            if (traceIssue.getPrizeLimit() < mustBonus) {
                                share = i;
                                if (fool) {
                                    fool = false;
                                    tipDialog("温馨提示", "该计划超出无法实现，请调整目标", TRACK_PROMPT_TIP);
                                    removeRow(i);
                                }
                                break;
                            }
                            plus = plus + 1;
                            mustBonus = bonus * plus;
                            total = mustBonus - (i != 0 ? totalCost + cart.getPlanAmount() * plus
                                    - cost : cart.getPlanAmount() * plus);
                            if (total > rule.getLaterValue()) {
                                if (i != 0) {
                                    totalCost = totalCost + cart.getPlanAmount() * plus - cost;
                                    //6+2*3=12
                                } else {
                                    totalCost = cart.getPlanAmount() * plus; //2*3=6
                                }
                                cost = cart.getPlanAmount() * plus;
                                multipleArray.put(i, plus);
                                break;
                            }
                        }
                    }
                }
                double totalProfit = (total / totalCost) * 100;
                row.updateData(mustBonus, multipleArray.get(i), totalCost, cost, keepThree(totalProfit).intValue());
            } else {
                stopString.append(i).append(",");
            }
        }
        if (stopString.length() > 0) {
            maxFilter(stopString.substring(0, stopString.length() - 1).split(","));
        }
        addLayoutView();
        return totalCost;
    }

    @Override
    public void newArrange(ChaseRuleData chaserule, boolean status, boolean dupe) {
        arrange(chaserule, dupe);
    }

    private void arrange(ChaseRuleData chaserule, boolean dupe) {
        ShoppingCart.getInstance().setChaserule(chaserule);
        share = 0;
        if (dupe && !isFinishing()) {
            this.additional = null;
            ShoppingCart.getInstance().setAdditional(additional);
            UiThread();
        }
    }

    private void UiThread() {
        //在子线程中进行耗时运算，在主线程中更新UI
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                planLayout.findViewById(R.id.confirm_plan).setEnabled(false);
                createRowLayout();
                updateData(true);
                planLayout.findViewById(R.id.confirm_plan).setEnabled(true);
            }
        });
    }
}
