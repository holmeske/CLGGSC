package com.sc.clgg.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sc.clgg.R
import com.sc.clgg.activity.fragment.LoginFragment
import com.sc.clgg.activity.fragment.RegisterFragment
import com.sc.clgg.adapter.TabAdapter
import kotlinx.android.synthetic.main.activity_login_register.*


class LoginRegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_register)

        iv_back.setOnClickListener { finish() }
        tabLayout.setupWithViewPager(viewPager)
        viewPager?.adapter = TabAdapter(supportFragmentManager, listOf(LoginFragment(), RegisterFragment()), listOf("登录", "注册"))
    }

}
