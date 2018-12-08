package com.jhj.refreshrecyclerview.base

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import kotlinx.android.synthetic.main.layout_activity_topbar.*

abstract class BaseActivity : AppCompatActivity() {

    abstract val title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
        )

    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        initTopBar()
    }


    fun initTopBar() {
        if (iv_topBar_finish != null) {
            iv_topBar_finish.setOnClickListener {
                finish()
            }
        }
        if (tv_topBar_confirm != null) {
            tv_topBar_confirm.setOnClickListener {

            }
        }
    }
}