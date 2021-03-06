package com.sc.clgg.activity.fragment

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sc.clgg.R
import com.sc.clgg.activity.contact.TruckManageContact
import com.sc.clgg.activity.login.LoginRegisterActivity
import com.sc.clgg.activity.presenter.TruckManagePresenter
import com.sc.clgg.activity.vehicle.RepairActivity
import com.sc.clgg.activity.vehicle.ServiceStationActivity
import com.sc.clgg.activity.vehicle.energy.ConsumptionStatisticalActivity
import com.sc.clgg.activity.vehicle.fault.FaultDiagnosisActivity
import com.sc.clgg.activity.vehicle.locate.LocateActivity
import com.sc.clgg.activity.vehicle.mileage.MileageActivity
import com.sc.clgg.activity.vehicle.tally.TallyBookActivity
import com.sc.clgg.base.BaseFragment
import com.sc.clgg.bean.Banner
import com.sc.clgg.bean.VersionInfoBean
import com.sc.clgg.mvvm.MyViewModel
import com.sc.clgg.retrofit.RetrofitHelper
import com.sc.clgg.tool.helper.ActivityHelper
import com.sc.clgg.tool.helper.LogHelper
import com.sc.clgg.tool.helper.MeasureHelper
import com.sc.clgg.util.*
import kotlinx.android.synthetic.main.fragment_car_net.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File.separator

/**
 * @author：lvke
 * @date：2018/2/27 17:02
 */
class CarNetFragment : BaseFragment(), TruckManageContact {

    private lateinit var myViewModel: MyViewModel
    private val changeObserver = Observer<Banner> { value ->
        banner.setData(activity, value.data?.banner, false)
    }

    private var viewCreated: Boolean = false
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_car_net, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        myViewModel = ViewModelProviders.of(this).get(MyViewModel::class.java)
        myViewModel.getBanner().observe(this, changeObserver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated = true

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            title.setPadding(0, 0, 0, 0)
            title.layoutParams.height = MeasureHelper.dp2px(activity, 64f) - activity!!.statusBarHeight()
        }

        TruckManagePresenter(this).checkUpdate()

        fun skip(stationType: String, title: String) {
            startActivity<ServiceStationActivity>(Pair("stationType", stationType), Pair("title", title))
        }

        service_station.setOnClickListener {
            if (activity.isOpenGps()) {
                skip("0", "陕汽服务站")
            }
        }

        operator.setOnClickListener {
            if (activity.isOpenGps()) {
                skip("1", "营运证服务商")
            }
        }
        accessory_dealer.setOnClickListener {
            if (activity.isOpenGps()) {
                skip("2", "配件经销商")
            }
        }

        hb_vehicle_monitor.setHomeButtonOnClickListener {
            ActivityHelper.startAcScale(activity, LocateActivity::class.java)
        }
        hb_my_vehicle.setHomeButtonOnClickListener {
            ActivityHelper.startAcScale(activity, MileageActivity::class.java)
        }
        hb_driving_score.setHomeButtonOnClickListener {
            /*startActivity(Intent(activity, PathRecordActivity::class.java)
                    .putExtra("carno", "晋C64989").putExtra("vin", "HX114675")
                    .putExtra("startDate", "20180716000000")
                    .putExtra("endDate", "20180716235959"))*/
            ActivityHelper.startAcScale(activity, ConsumptionStatisticalActivity::class.java)
        }
        hb_maintenance_home.setHomeButtonOnClickListener {
            ActivityHelper.startAcScale(activity, RepairActivity::class.java)
        }
        hb_my_tallybook.setHomeButtonOnClickListener {
            ActivityHelper.startAcScale(activity, FaultDiagnosisActivity::class.java)
        }
        hb_financial_after_market.setHomeButtonOnClickListener {
            if (ConfigUtil().userid.isEmpty()) {
                startActivity(Intent(activity, LoginRegisterActivity::class.java))
            } else {
                ActivityHelper.startAcScale(activity, TallyBookActivity::class.java)
            }
        }
        RetrofitHelper().bannerList.apply {
            enqueue(object : Callback<Banner> {
                override fun onFailure(call: Call<Banner>, t: Throwable) {
                    toast(R.string.network_anomaly)
                }

                override fun onResponse(call: Call<Banner>, response: Response<Banner>) {

                    response.body()?.let {
                        myViewModel.getBanner().value = it
                    }
                }
            })
        }
        /*job = GlobalScope.launch {
            LogHelper.e("---1")
            val deffered = async { RetrofitHelper().location().execute() }
            LogHelper.e("---2")
            val http = deffered.await()
            LogHelper.e("---3")
            launch(Dispatchers.Main) {
                LogHelper.e("---4")
                if (http.isSuccessful) {
                    LogHelper.e("协程结果 is "+deffered.await().body().toString())
                } else {
                    toast(R.string.network_anomaly)
                }
            }
        }
        job.start()*/
    }

    override fun onResume() {
        super.onResume()
        banner?.startAutoPlay()
    }

    override fun onPause() {
        super.onPause()
        banner?.stopAutoPlay()
    }

    override fun getVersionInfo(bean: VersionInfoBean?) {
        val path = """${getExternalStorageDirectory().path}${getDataDirectory()}$separator${activity?.packageName}${getDownloadCacheDirectory()}${separator}clggsc.apk"""
        LogHelper.e(path)
        bean?.single?.type?.let { UpdateHelper().checkUpdateInfo(activity, bean.single?.code, it, bean.single?.url, false) }
    }
}
