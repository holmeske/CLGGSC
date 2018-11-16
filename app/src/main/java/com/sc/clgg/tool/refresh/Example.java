//package com.sc.clgg.tool.refresh;
//
//import android.os.Handler;
//
//import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
//import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
//import com.sc.clgg.R;
//
///**
// * Author：lvke
// * CreateDate：2017/9/22 13:44
// */
//
//public class Example {
//    void e1(){
//          TwinklingRefreshLayout refreshLayout;
//        refreshLayout = (TwinklingRefreshLayout) findViewById(R.id.refresh_layout);
//        new RefreshViewManager(refreshLayout).initSettings(this, true);
//        refreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
//            @Override
//            public void onRefresh(final TwinklingRefreshLayout refreshLayout) {
//                super.onRefresh(refreshLayout);
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.finishRefreshing();
//                    }
//                }, 2000);
//            }
//
//            @Override
//            public void onLoadMore(final TwinklingRefreshLayout refreshLayout) {
//                super.onLoadMore(refreshLayout);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.finishLoadmore();
//                    }
//                }, 2000);
//            }
//        });
//    }
//}
