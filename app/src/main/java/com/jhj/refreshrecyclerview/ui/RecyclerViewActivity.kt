package com.jhj.refreshrecyclerview.ui

import android.os.Bundle
import android.view.View
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.bean.ApplyBean
import com.jhj.refreshrecyclerview.net.HttpConfig
import com.jhj.refreshrecyclerview.refresh.BaseRefreshActivity
import com.jhj.slimadapter.holder.ViewInjector
import org.jetbrains.anko.toast

class RecyclerViewActivity : BaseRefreshActivity<ApplyBean>() {

    override val url: String
        get() = HttpConfig.a

    override val title: String
        get() = "刷新加载"

    override val itemLayoutRes: Int
        get() = R.layout.list_item_white

    override fun initParam() {
        super.initParam()
        httpParams.put("memberId", "754")

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRightText("提交", View.OnClickListener {
            toast("提交")
        })

    }

    override fun itemViewConvert(injector: ViewInjector, data: ApplyBean?, position: Int) {
        injector.text(R.id.textView, "${data?.leaveTypeName}->${data?.reason}")
    }


}