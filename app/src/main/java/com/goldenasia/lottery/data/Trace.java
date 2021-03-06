package com.goldenasia.lottery.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alashi on 2016/1/27.
 */
public class Trace {

    /**
     * wrap_id : YF012608530000212T
     * trace_id : 212
     * lottery_id : 11
     * total_amount : 40.00
     * create_time : 2016-01-26 15:12:41
     * status : 2
     */
    @SerializedName("wrap_id")
    private String wrapId;
    @SerializedName("trace_id")
    private int traceId;
    @SerializedName("lottery_id")
    private int lotteryId;
    @SerializedName("stop_on_win")
    private boolean stopOnWin;
    @SerializedName("modes")
    private double modes;
    @SerializedName("single_num")
    private int singleNum;
    @SerializedName("total_amount")
    private String totalAmount;
    @SerializedName("create_time")
    private String createTime;
    /** '追号状态(0:未开始 1:正在进行;2:已完成;3:已取消)' */
    private int status;

    public String getWrapId() {
        return wrapId;
    }

    public int getTraceId() {
        return traceId;
    }

    public int getLotteryId() {
        return lotteryId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setWrapId(String wrapId) {
        this.wrapId = wrapId;
    }

    public boolean isStopOnWin() {
        return stopOnWin;
    }

    public double getModes() {
        return modes;
    }

    public int getSingleNum() {
        return singleNum;
    }

    /** '追号状态(0:未开始 1:正在进行;2:已完成;3:已取消)' */
    public int getStatus() {
        return status;
    }
}
