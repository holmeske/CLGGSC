package com.sc.clgg.activity

import android.os.Bundle
import com.sc.clgg.R
import com.sc.clgg.base.BaseImmersionActivity
import com.sc.clgg.util.startActivity
import kotlinx.android.synthetic.main.activity_read_card.*
import kotlinx.android.synthetic.main.view_titlebar_blue.*
import org.jetbrains.anko.sdk25.coroutines.onClick

class ReadCardActivity : BaseImmersionActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_card)

        titlebar_title.text = getString(R.string.read_card)

        tv_read_card.onClick { startActivity(WriteCardActivity::class.java) }
    }
}