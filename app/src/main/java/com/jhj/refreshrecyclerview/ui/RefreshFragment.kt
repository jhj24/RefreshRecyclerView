package com.jhj.refreshrecyclerview.ui

import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.bean.ApplyBean
import com.jhj.refreshrecyclerview.net.HttpConfig
import com.jhj.refreshrecyclerview.refresh.BaseRefreshFragment
import com.jhj.slimadapter.holder.ViewInjector

class RefreshFragment: BaseRefreshFragment<ApplyBean>() {

    override val url: String
        get() = HttpConfig.a

    override val itemLayoutRes: Int
        get() = R.layout.list_item_white

    override fun initParam() {
        super.initParam()
        httpParams.put("memberId", "754")
    }

    override fun itemViewConvert(injector: ViewInjector, data: ApplyBean?, position: Int) {
        injector.text(R.id.textView, "${data?.leaveTypeName}->${data?.reason}")
    }


}