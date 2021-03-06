package com.sc.clgg.activity.vehicle.tally

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.google.gson.Gson
import com.sc.clgg.R
import com.sc.clgg.adapter.TallyBookAdapter
import com.sc.clgg.base.BaseImmersionActivity
import com.sc.clgg.bean.Check
import com.sc.clgg.bean.TallyBook
import com.sc.clgg.retrofit.RetrofitHelper
import com.sc.clgg.tool.helper.CalendarHelper
import com.sc.clgg.tool.helper.LogHelper
import kotlinx.android.synthetic.main.activity_tally_book.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TallyBookActivity : BaseImmersionActivity() {
    private var mTallyBookAdapter: TallyBookAdapter? = null
    private var call: Call<TallyBook>? = null
    var dataList: ArrayList<TallyBook.Info>? = null
    private var http: Call<Check>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tally_book)

        initTitle("记账本")
        rv.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        mTallyBookAdapter = TallyBookAdapter()
        mTallyBookAdapter?.setOnDelListener { id, pos ->
            http = RetrofitHelper().remove(id)
            http?.enqueue(object : Callback<Check> {
                override fun onFailure(call: Call<Check>?, t: Throwable?) {
                    toast("删除失败")
                    loadData(year, month)
                }

                override fun onResponse(call: Call<Check>?, response: Response<Check>?) {
                    LogHelper.e("response: " + Gson().toJson(response?.body()))
                    val check = response?.body()
                    if (check?.success!!) {
                        dataList?.removeAt(pos)
                        mTallyBookAdapter?.notifyItemRemoved(pos)
                    }
                    loadData(year, month)
                }
            })
        }
        rv.adapter = mTallyBookAdapter

        initTimePickerView()

        year = CalendarHelper.getCurrentYear().toString()
        month = CalendarHelper.getCurrentMonth().toString()

        tv_year?.text = year
        tv_month?.text = month

        //loadData(year, month)

        tv_year.setOnClickListener { mTimePickerView?.show() }

        record_income.setOnClickListener {
            startActivity<RecordActivity>(Pair("title", "记录收入"), Pair("str", "工资"), Pair("recordType", "0"))
        }
        record_spending.setOnClickListener {
            startActivity<RecordActivity>(Pair("title", "记录支出"), Pair("str", "高速费"), Pair("recordType", "1"))
        }
    }

    private var year: String? = ""
    private var month: String? = ""
    override fun onResume() {
        super.onResume()
        loadData(year, month)
    }

    private fun loadData(queryYear: String?, queryMonth: String?) {
        call = RetrofitHelper().tallybook(queryYear, queryMonth)
        call?.enqueue(object : Callback<TallyBook> {
            override fun onResponse(call: Call<TallyBook>?, response: Response<TallyBook>?) {
                val bean = response?.body()
                tv_income?.text = bean?.allIncome
                tv_spending?.text = bean?.allExpense
                bean?.allInfo?.let {
                    if (it.isNotEmpty()) {
                        it[0].show = true
                        for (i in 0 until it.size) {
                            if (i == it.size - 1) continue
                            it[i + 1].show = !it[i].costDate.equals(it[i + 1].costDate)
                        }
                    }
                    dataList = it as ArrayList<TallyBook.Info>
                    mTallyBookAdapter?.refresh(it)
                }
            }

            override fun onFailure(call: Call<TallyBook>?, t: Throwable?) {
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        call?.cancel()
    }

    private var mTimePickerView: TimePickerView? = null
    private val selectedCalendar = Calendar.getInstance()
    private val startCalendar = Calendar.getInstance()
    private val endCalendar = Calendar.getInstance()
    private fun initTimePickerView() {
        startCalendar.set(CalendarHelper.getCurrentYear(), CalendarHelper.getCurrentMonth() - 1, 0)
        mTimePickerView = TimePickerBuilder(this, OnTimeSelectListener { date, _ ->
            // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
            selectedCalendar.time = date

            year = selectedCalendar.get(Calendar.YEAR).toString()
            month = (selectedCalendar.get(Calendar.MONTH) + 1).toString()

            tv_year?.text = year
            tv_month?.text = month
            loadData(year, month)
        }).setType(booleanArrayOf(true, true, false, false, false, false))//年月日时分秒 的显示与否，不设置则默认全部显示
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentTextSize(21)
                .setDate(selectedCalendar)
                .setRangDate(startCalendar, endCalendar)
                .setOutSideColor(ContextCompat.getColor(this, R.color.white)) //设置外部遮罩颜色
                .setDecorView(null).build()
    }

}
