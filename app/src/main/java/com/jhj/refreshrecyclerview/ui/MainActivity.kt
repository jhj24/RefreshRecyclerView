package com.jhj.refreshrecyclerview.ui

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.base.BaseActivity
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
import com.jhj.slimadapter.holder.ViewInjector
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {
    override val title: String
        get() = "首页"

    private val list = listOf("单纯的下拉刷新", "下拉刷新与Fragment结合")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        SlimAdapter.creator(LinearLayoutManager(this))
            .register<String>(R.layout.list_item_white, object : ItemViewBind<String>() {
                override fun convert(p0: ViewInjector, p1: String?, p2: Int) {
                    p0.text(R.id.textView, p1)
                }
            })
            .setOnItemClickListener { recyclerView, view, i ->
                if (i == 0) {
                    startActivity<RecyclerViewActivity>()
                } else if (i == 1) {
                    startActivity<FragmentRefreshActivity>()
                }

            }

            .attachTo(recyclerview)
            .addItemDecoration(LineItemDecoration())
            .setDataList(list)

    }
}
