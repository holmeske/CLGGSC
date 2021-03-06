package com.sc.clgg.activity.friendscircle

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.sc.clgg.R
import com.sc.clgg.bean.Check
import com.sc.clgg.bean.CommentEvent
import com.sc.clgg.bean.TruckFriend
import com.sc.clgg.retrofit.RetrofitHelper
import com.sc.clgg.tool.helper.LogHelper
import com.sc.clgg.util.ConfigUtil
import com.sc.clgg.util.hideSoftInputFromWindow
import kotlinx.android.synthetic.main.activity_comment.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Response


class CommentActivity : AppCompatActivity(), View.OnLayoutChangeListener {
    private var circleMessageId: Int = 0
    private var commentUserId: Int = 0

    companion object {
        var position = -1
        var commen: TruckFriend.Commen? = null

        fun initValue() {
            LogHelper.e("initValue()")
            position = -1
            commen = null
        }
    }

    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
//        LogHelper.e("left = " + left + "  top = " + top + "  right = " + right + "  bottom = " + bottom + "\n"
//                + "oldLeft = " + oldLeft + "  oldTop = " + oldTop + "  oldRight = " + oldRight + "  oldBottom = " + oldBottom)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)

        circleMessageId = intent.getIntExtra("circleMessageId", 0)
        commentUserId = intent.getIntExtra("commentUserId", 0)
        position = intent.getIntExtra("position", -1)
        root?.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(et_input, InputMethodManager.SHOW_FORCED)
            imm.hideSoftInputFromWindow(et_input.windowToken, 0)
            finish()
        }
        root?.addOnLayoutChangeListener(this)

        tv_send.setOnClickListener { it ->
            val comment = et_input?.text?.toString()
            hideSoftInputFromWindow(it)
            if (!comment?.isBlank()!!)
                RetrofitHelper().sendOpinion(circleMessageId, commentUserId, comment).enqueue(object : retrofit2.Callback<Check> {
                    override fun onFailure(call: Call<Check>?, t: Throwable?) {
                        toast("评论失败")
                    }

                    override fun onResponse(call: Call<Check>?, response: Response<Check>?) {
                        response?.body()?.let {
                            if (it.success) {
                                toast("评论成功")
                                commen = TruckFriend.Commen(it.id?.toInt()!!,
                                        circleMessageId,
                                        commentUserId,
                                        ConfigUtil().userid.toInt(),
                                        System.currentTimeMillis().toString(),
                                        0,
                                        comment,
                                        0, ConfigUtil().nickName)
                                EventBus.getDefault().post(CommentEvent(0))
                            } else {
                                toast("${it.msg}")
                            }
                        }
                    }
                })
            finish()
        }
    }

}
