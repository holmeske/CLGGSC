package com.sc.clgg.application

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.android.recharge.ObuInterface
import com.sc.clgg.service.AppService
import com.sc.clgg.tool.helper.LogHelper
import com.sc.clgg.util.UmengHelper
import com.squareup.leakcanary.LeakCanary
import org.jetbrains.anko.doAsync

/**
 * @author lvke
 */
class App : Application() {
    lateinit var mObuInterface: ObuInterface

    companion object {
        lateinit var app: App

        fun getInstance(): App {
            return app
        }
    }


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(base)
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        init()
        initObuInterface()
        doAsync { lateInit() }
    }

    /**
     * 延迟初始化
     */
    fun lateInit() {

        LogHelper.e("初始化Umeng")
        UmengHelper().init(this)

        LogHelper.e("初始化JobIntentService")
        AppService.start(this)

        LogHelper.e("初始化Bugly")
        initBugly()

        LogHelper.e("初始化LeakCanary")
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
        // Normal app init code...
    }

    fun initObuInterface() {
        LogHelper.e("初始化ObuInterface")
        mObuInterface = ObuInterface(this)
        mObuInterface.initialize()
        /*mObuInterface.initializeObu(this, object : BluetoothObuCallback {
            override fun onTransferTimeout() {
                LogHelper.v("onTransferTimeout()")
            }

            override fun onConnectSuccess() {
                LogHelper.v("onConnectSuccess()")
            }

            override fun onDisconnect() {
                LogHelper.v("onDisconnect()")
            }

            override fun onConnectTimeout() {
                LogHelper.v("onConnectTimeout()")
            }

            override fun onReceiveObuCmd(p0: String?, p1: String?) {
                LogHelper.v("onReceiveObuCmd() p0:${p0}  p1:${p1}")
            }

            override fun onError(p0: String?, p1: String?) {
                LogHelper.v("onError() p0:${p0}  p1:${p1}")
            }
        })*/
        //mObuInterface.openLog(BuildConfig.LOG_DEBUG) //10
    }
}