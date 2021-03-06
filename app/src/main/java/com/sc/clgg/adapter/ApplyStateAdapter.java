package com.sc.clgg.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sc.clgg.R;
import com.sc.clgg.bean.ApplyStateList;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：lvke
 * @date：2018/11/16 16:39
 */
public class ApplyStateAdapter extends RecyclerView.Adapter<ApplyStateAdapter.MyHolder> {
    private List<ApplyStateList.ApplyState> dataList = new ArrayList<>();
    private Context mContext;

    public void clear() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public void refresh(List<ApplyStateList.ApplyState> list) {
        dataList.clear();
        dataList.addAll(list);
        notifyDataSetChanged();
    }

    public void notifyItemInserted(List<ApplyStateList.ApplyState> list) {
        if (list != null && list.size() > 0) {
            dataList.addAll(list);
            notifyItemRangeChanged(dataList.size(), list.size());
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MyHolder(LayoutInflater.from(mContext).inflate(R.layout.item_apply_state, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        ApplyStateList.ApplyState bean = dataList.get(holder.getAdapterPosition());
        holder.tv_apply_time.setText("申请时间:  " + bean.getCreateTime());
        holder.tv_card_type.setText("卡类型:  " + ("3".equals(bean.getCardType()) ? "鲁通B卡" : ""));
        holder.tv_car_no.setText("车牌:  " + bean.getCarNo());
        holder.tv_etc_card.setText("ETC:  " + bean.getCardId());
        holder.tv_audit_opinion.setText("审核意见:  " + bean.getRemarkFalse());
        if (TextUtils.isEmpty(bean.getCheckStatus())) {
            isSuccess(holder, bean);
        } else {
            checkStatus(holder, bean.getCheckStatus());
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : dataList.size();
    }

    /**
     * 根据 isSuccess 属性判断
     */
    private void isSuccess(@NonNull MyHolder holder, ApplyStateList.ApplyState bean) {
        switch (bean.isSuccess()) {
            case "0":
                holder.iv_apply_state.setVisibility(View.GONE);

                holder.tv_audit_state.setVisibility(View.VISIBLE);
                holder.tv_audit_state.setText("审核通过");
                holder.tv_audit_state.setTextColor(ContextCompat.getColor(mContext, R.color._6ccd37));
                holder.tv_audit_state.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_audit_ing));

                holder.tv_etc_card.setVisibility(View.GONE);
                holder.ll_audit_opinion.setVisibility(View.GONE);
                break;
            case "1":
                holder.tv_audit_state.setVisibility(View.GONE);

                holder.iv_apply_state.setVisibility(View.VISIBLE);
                if (TextUtils.isEmpty(bean.getExpressInfo())){
                    holder.iv_apply_state.setImageResource(R.drawable.open_card_success_icon);
                }else {
                    holder.iv_apply_state.setImageResource(R.drawable.card_send_off_icon);
                }

                holder.tv_etc_card.setVisibility(View.VISIBLE);
                holder.ll_audit_opinion.setVisibility(View.GONE);
                break;
            case "2":
                holder.tv_audit_state.setVisibility(View.GONE);
                holder.iv_apply_state.setVisibility(View.VISIBLE);

                holder.iv_apply_state.setImageResource(R.drawable.open_card_failure_icon);

                holder.tv_etc_card.setVisibility(View.GONE);
                holder.ll_audit_opinion.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    /**
     * 根据 checkStatus 属性判断
     */
    private void checkStatus(@NonNull MyHolder holder, String state) {
        switch (state) {
            case "0":
                holder.iv_apply_state.setVisibility(View.GONE);

                holder.tv_audit_state.setVisibility(View.VISIBLE);
                holder.tv_audit_state.setText("审核中");
                holder.tv_audit_state.setTextColor(ContextCompat.getColor(mContext, R.color._ee8031));
                holder.tv_audit_state.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_audit_ing));

                holder.tv_etc_card.setVisibility(View.GONE);
                holder.ll_audit_opinion.setVisibility(View.GONE);
                break;
            case "2":
                holder.tv_audit_state.setVisibility(View.VISIBLE);
                holder.iv_apply_state.setVisibility(View.GONE);

                holder.tv_audit_state.setText("申请驳回");
                holder.tv_audit_state.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                holder.tv_audit_state.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_audit_rejected));

                holder.tv_etc_card.setVisibility(View.GONE);
                holder.ll_audit_opinion.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private TextView tv_apply_time, tv_car_no, tv_etc_card, tv_card_type, tv_audit_opinion, tv_audit_state;
        private LinearLayout ll_audit_opinion;
        private ImageView iv_apply_state;


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tv_apply_time = itemView.findViewById(R.id.tv_apply_time);
            tv_car_no = itemView.findViewById(R.id.tv_car_no);
            tv_etc_card = itemView.findViewById(R.id.tv_etc_card);
            tv_card_type = itemView.findViewById(R.id.tv_card_type);
            ll_audit_opinion = itemView.findViewById(R.id.ll_audit_opinion);
            tv_audit_opinion = itemView.findViewById(R.id.tv_audit_opinion);
            tv_audit_state = itemView.findViewById(R.id.tv_audit_state);
            iv_apply_state = itemView.findViewById(R.id.iv_apply_state);
        }
    }
}
