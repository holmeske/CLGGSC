package com.sc.clgg.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sc.clgg.R;
import com.sc.clgg.activity.vehicle.mileage.PathRecordActivity;
import com.sc.clgg.bean.MileageDetail;
import com.sc.clgg.tool.helper.LogHelper;
import com.sc.clgg.util.TimeHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author：lvke
 * @date：2018/5/23 15:35
 */
public class StatisticalDetailAdapter extends RecyclerView.Adapter<StatisticalDetailAdapter.MyHolder> {
    private List<MileageDetail.Data.Detail> data = new ArrayList<>();
    private int year, month;
    private Context mContext;
    private String carno, vin;

    public StatisticalDetailAdapter(int year, int month, String carno, String vin) {
        this.year = year;
        this.month = month;
        this.carno = carno;
        this.vin = vin;
    }

    public void refresh(List<MileageDetail.Data.Detail> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new MyHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistical_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        MileageDetail.Data.Detail bean = data.get(holder.getAdapterPosition());
        if (!TextUtils.isEmpty(bean.getDate())) {
            holder.tv_date.setText(month + "月" + bean.getDate() + "日");
        }
        holder.tv_mileage.setText(bean.getDayMileage() == null ? "" : bean.getDayMileage() + "km");

        holder.itemView.setOnClickListener(v -> {
            LogHelper.e("position = " + position);
            if (!TextUtils.isEmpty(bean.getDayMileage()) && Double.parseDouble(bean.getDayMileage()) > 0) {
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                calendar.set(year, month - 1, Integer.parseInt(bean.getDate()));

                mContext.startActivity(new Intent(mContext, PathRecordActivity.class)
                        .putExtra("carno", carno)
                        .putExtra("vin", vin)
                        .putExtra("startDate", TimeHelper.long2time(TimeHelper.JAVA_DATE_FORAMTER_2, calendar.getTimeInMillis()) + "000000")
                        .putExtra("endDate", TimeHelper.long2time(TimeHelper.JAVA_DATE_FORAMTER_2, calendar.getTimeInMillis()) + "235959"));
            }
        });

    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        public TextView tv_date, tv_mileage;

        public MyHolder(View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_mileage = itemView.findViewById(R.id.tv_mileage);
        }
    }

}
