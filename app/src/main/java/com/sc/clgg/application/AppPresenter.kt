package com.sc.clgg.application

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.lzy.ninegrid.NineGridView
import com.sc.clgg.BuildConfig
import com.sc.clgg.R
import com.sc.clgg.bean.LocationBean
import com.sc.clgg.config.ConstantValue
import com.sc.clgg.tool.helper.AMapLocationHelper
import com.sc.clgg.tool.helper.LogHelper
import com.tencent.bugly.crashreport.CrashReport
import pub.devrel.easypermissions.AppSettingsDialogHolderActivity
import pub.devrel.easypermissions.EasyPermissions
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException

/**
 * @author：lvke
 * @date：2018/9/11 16:39
 */

lateinit var CURRENT_LOCATION: LocationBean

fun Application.init() {

    LogHelper.setLogSwitch(BuildConfig.LOG_DEBUG)

    NineGridView.setImageLoader(GlideImageLoader())

    registerActivityLifecycleCallbacks()

    CURRENT_LOCATION = LocationBean()
    AMapLocationHelper(this, AMapLocationHelper.OnLocationListener { bean -> CURRENT_LOCATION = bean })
}

private fun Application.registerActivityLifecycleCallbacks() {
    registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            LogHelper.v("activity", "onActivityCreated    " + activity?.localClassName)

            if (activity?.findViewById<View>(R.id.titlebar_title) != null) {
                (activity.findViewById<View>(R.id.titlebar_title) as TextView).text = activity.title
            }
            if (activity?.findViewById<View>(R.id.titlebar_left) != null) {
                activity.findViewById<View>(R.id.titlebar_left).setOnClickListener { activity.finish() }
            }
        }

        override fun onActivityStarted(activity: Activity?) {
            LogHelper.v("activity", "onActivityStarted    " + activity?.localClassName)
        }

        override fun onActivityResumed(activity: Activity?) {
            LogHelper.v("activity", "onActivityResumed    " + activity?.localClassName)
            if (activity is AppSettingsDialogHolderActivity && EasyPermissions.hasPermissions(activity, *ConstantValue.PERMISSION_NEED)) {
                activity.finish()
            }
        }

        override fun onActivityPaused(activity: Activity?) {
            LogHelper.v("activity", "onActivityPaused    " + activity?.localClassName)
        }

        override fun onActivityStopped(activity: Activity?) {
            LogHelper.v("activity", "onActivityStopped    " + activity?.localClassName)
        }

        override fun onActivitySaveInstanceState(activity: Activity?, bundle: Bundle?) {
            LogHelper.v("activity", "onActivitySaveInstanceState    " + activity?.localClassName)
        }

        override fun onActivityDestroyed(activity: Activity?) {
            LogHelper.v("activity", "onActivityDestroyed    " + activity?.localClassName)
        }

    })
}

private class GlideImageLoader : NineGridView.ImageLoader {

    override fun onDisplayImage(context: Context, imageView: ImageView, url: String) {
        Glide.with(context).load(url).apply(RequestOptions().error(R.drawable.ic_launcher).placeholder(R.drawable.ic_launcher)).into(imageView)
    }

    override fun getCacheImage(url: String): Bitmap? {
        return null
    }
}

private fun getProcessName(pid: Int): String? {
    var reader: BufferedReader? = null
    try {
        reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
        var processName = reader.readLine()
        if (!TextUtils.isEmpty(processName)) {
            processName = processName.trim { it <= ' ' }
        }
        return processName
    } catch (throwable: Throwable) {
        throwable.printStackTrace()
    } finally {
        try {
            reader?.close()
        } catch (exception: IOException) {
            exception.printStackTrace()
        }

    }
    return null
}

fun Application.initBugly() {
    BuildConfig.LOG_DEBUG.let {
        if (!it) {
            val packageName = packageName
            // 获取当前进程名
            val processName = getProcessName(android.os.Process.myPid())
            // 设置是否为上报进程
            val strategy = CrashReport.UserStrategy(this)
            strategy.isUploadProcess = processName == null || processName == packageName
            // 初始化Bugly
            CrashReport.initCrashReport(applicationContext, "0edd50c749", true, strategy)
        }
    }
}