package com.goldenasia.lottery.view.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.goldenasia.lottery.R;
import com.goldenasia.lottery.data.ReceiveBoxResponse;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

/**
 * Created by Gan on 2017/11/20.
 * 收件箱   Adapter
 */

public class InBoxAdapter extends BaseAdapter {

    private List<ReceiveBoxResponse.ListBean> list;
    private boolean mStateIsEdit=false;
    private ArrayList <String> mUnreadPositionList;

    public InBoxAdapter(boolean stateIsEdit) {
        mStateIsEdit=stateIsEdit;
    }

    public void setList(List list,ArrayList<String> unreadPositionList) {
        this.list = list;
        this.mUnreadPositionList=unreadPositionList;
        notifyDataSetChanged();
    }

    public void setmStateIsEdit(boolean mStateIsEdit) {
        this.mStateIsEdit = mStateIsEdit;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_list_item, parent, false);
            holder = new ViewHolder(convertView);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ReceiveBoxResponse.ListBean bean = list.get(position);
        holder.from_username.setText(bean.getFrom_username());
        holder.content.setText(bean.getTitle());
        holder.time.setText(bean.getCreate_time());

        //是否已读
        Object  tag=holder.time.getTag();
        if (tag == null) {
            QBadgeView qBadgeView=new QBadgeView(parent.getContext());//
            qBadgeView.bindTarget(holder.overlay_badge);
            qBadgeView.setBadgeGravity(Gravity.START | Gravity.TOP);
            if("0".equals(bean.getHas_read())&&!mUnreadPositionList.contains(bean.getMt_id())) {
                qBadgeView.setBadgeNumber(1);////1:已读,0:未读
            }else {
                qBadgeView.setBadgeNumber(0);
            }

            holder.time.setTag(qBadgeView);
        }else{
            QBadgeView qQBadgeView=(QBadgeView)tag;
            if("0".equals(bean.getHas_read())&&!mUnreadPositionList.contains(bean.getMt_id())) {
                qQBadgeView.setBadgeNumber(1);////1:已读,0:未读
            }else {
                qQBadgeView.setBadgeNumber(0);
            }
        }

        if(mStateIsEdit){
            holder.check_box.setVisibility(View.VISIBLE);
            if(bean.isState()){
                holder.check_box.setChecked(true);
            }else{
                holder.check_box.setChecked(false);
            }
        }else{
            holder.check_box.setVisibility(View.GONE);
        }
        return convertView;
    }


    static class ViewHolder {
        @BindView(R.id.from_username)
        TextView from_username;
        @BindView(R.id.content)
        TextView content;
        @BindView(R.id.time)
        TextView time;
        @BindView(R.id.check_box)
        CheckBox check_box;
        @BindView(R.id.overlay_badge)
        TextView overlay_badge;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
            convertView.setTag(this);
        }
    }
}