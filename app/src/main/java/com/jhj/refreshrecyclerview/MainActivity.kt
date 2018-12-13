package com.jhj.refreshrecyclerview

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jhj.refreshrecyclerview.base.BaseActivity
import com.jhj.refreshrecyclerview.ui.RecyclerViewActivity
import com.jhj.refreshrecyclerview.ui.RecyclerViewLoadActivity
import com.jhj.refreshrecyclerview.ui.RefreshFragmentActivity
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class MainActivity : BaseActivity() {
    override val title: String
        get() = "首页"

    private val list = listOf("下拉刷新-样式1", "下拉刷新-样式2", "下拉刷新与Fragment结合")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        SlimAdapter.creator(LinearLayoutManager(this))
            .register<String>(R.layout.list_item_white) { p0, p1, p2 ->
                p0.text(R.id.textView, p1)
            }
            .setOnItemClickListener { recyclerView, view, i ->
                when (i) {
                    0 -> startActivity<RecyclerViewActivity>()
                    1 -> startActivity<RecyclerViewLoadActivity>()
                    2 -> startActivity<RefreshFragmentActivity>()
                }
            }
            .attachTo(recyclerview)
            .addItemDecoration(LineItemDecoration())
            .setDataList(list)

    }
}
