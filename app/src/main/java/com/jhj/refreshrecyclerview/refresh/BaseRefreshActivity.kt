package com.jhj.refreshrecyclerview.refresh

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.jhj.httplibrary.HttpCall
import com.jhj.httplibrary.callback.base.BaseHttpCallback
import com.jhj.httplibrary.model.HttpParams
import com.jhj.prompt.dialog.alert.AlertFragment
import com.jhj.prompt.dialog.alert.constants.DialogStyleEnum
import com.jhj.prompt.dialog.alert.interfaces.OnCustomListener
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.base.BaseActivity
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.holder.ViewInjector
import com.jhj.slimadapter.itemdecoration.LineItemDecoration
import kotlinx.android.synthetic.main.activity_recyclerview_refresh.*
import kotlinx.android.synthetic.main.layout_recyclerview_refresh_filter.view.*
import kotlinx.android.synthetic.main.layout_search_bar.*
import org.jetbrains.anko.toast
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


abstract class BaseRefreshActivity<T> : BaseActivity() {


    companion object {
        private const val REQUEST_FIRST = 0
        private const val REQUEST_OTHER = 1
    }

    abstract val url: String
    abstract val itemLayoutRes: Int

    //分页大小
    open val pageSize = 10
    //起始页
    open val startPageNum = 1
    //分割线
    open val hasSplitLine = true
    //请求参数
    open val httpParams: HttpParams = HttpParams()
    //筛选搜索
    open val filterSearch = false
    //输入搜索
    open val inputSearch = false
    //要搜索的key
    open val inputSearchKey = ""
    //输入框变化就开始搜索
    open val isInputSearchEach = true

    private var selectorSearchParams: HttpParams = HttpParams()
    private val inputSearchParams: HttpParams = HttpParams()
    private var pageNo = 1
    private var isFirstLoading = true
    private var filterLayoutRes: Int? = null

    lateinit var adapterLocal: SlimAdapter
    lateinit var inputManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_refresh)
        inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        initParam()
        adapterLocal = initAdapter()

        //分割线
        if (hasSplitLine) {
            adapterLocal.addItemDecoration(LineItemDecoration())
        }

        //输入搜索
        if (inputSearch) {
            layout_search_bar.visibility = View.VISIBLE
            inputSearch()
        } else {
            layout_search_bar.visibility = View.GONE
        }

        //其他条件筛选
        if (filterSearch) {
            layout_action_search.visibility = View.VISIBLE
            filterSearch()
        } else {
            layout_action_search.visibility = View.GONE
        }

        //数据刷新加载
        httpRequest(REQUEST_FIRST)
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(true)
        smartRefreshLayout.setOnRefreshListener {
            httpRequest(REQUEST_FIRST)
        }

        smartRefreshLayout.setOnLoadMoreListener {
            httpRequest(REQUEST_OTHER)
        }


        //重新加载
        tv_refresh_new.setOnClickListener {
            layout_refresh_new.visibility = View.GONE
            smartRefreshLayout.visibility = View.VISIBLE
            smartRefreshLayout.autoRefresh()
        }

    }

    private fun initAdapter(): SlimAdapter {

        recyclerView.setOnTouchListener { _, _ ->
            inputManager.hideSoftInputFromWindow(et_search_input.windowToken, 0)
            if (et_search_input.text.isNullOrBlank()) {
                layout_search_mark.visibility = View.VISIBLE
            }
            return@setOnTouchListener false
        }

        return SlimAdapter.creator(LinearLayoutManager(this))
            .setGenericActualType(getTClazz())
            .register<T>(itemLayoutRes) { injector, data, position ->
                itemViewConvert(injector, data, position)
            }
            .attachTo(recyclerView)
    }

    private fun httpRequest(type: Int) {

        if (type == REQUEST_FIRST) {
            isFirstLoading = true
            pageNo = startPageNum
            smartRefreshLayout.setNoMoreData(false)
        } else {
            isFirstLoading = false
        }

        HttpCall.post(url)
            .addParam("pageNo", pageNo.toString())
            .addParam("pageSize", pageSize.toString())
            .addParams(selectorSearchParams)
            .addParams(inputSearchParams)
            .addParams(httpParams)
            .enqueue(object : BaseHttpCallback<RefreshResult<T>>() {

                var isSuccess = false

                override val genericArrayType: Type?
                    get() = type(RefreshResult::class.java, getTClazz())

                override fun onSuccess(data: RefreshResult<T>?, resultType: ResultType) {
                    isSuccess = true
                    val list = data?.data
                    if (isFirstLoading) {
                        adapterLocal.dataList = list
                        smartRefreshLayout.finishRefresh()
                    } else {
                        adapterLocal.addDataList(list)
                        smartRefreshLayout.finishLoadMore()
                    }

                    if (list != null && list.isNotEmpty()) {
                        pageNo++
                    } else {
                        smartRefreshLayout.setNoMoreData(true)
                    }

                }

                override fun onFailure(msg: String, errorCode: Int) {
                    isSuccess = false
                    if (isFirstLoading) {
                        toast(msg)
                        smartRefreshLayout.finishRefresh(false)
                    } else {
                        smartRefreshLayout.finishLoadMore(false)
                    }
                }

                override fun onFinish() {
                    super.onFinish()
                    if (adapterLocal.dataList == null || adapterLocal.dataList?.size == 0) {
                        layout_refresh_new.visibility = View.VISIBLE
                        smartRefreshLayout.visibility = View.GONE
                        if (isSuccess) {
                            tv_refresh_reason.text = "没有数据"
                        } else {
                            tv_refresh_reason.text = "查询失败"
                        }

                    } else {
                        layout_refresh_new.visibility = View.GONE
                        smartRefreshLayout.visibility = View.VISIBLE
                    }
                }

            })
    }

    /**
     * 输入搜索
     */
    private fun inputSearch() {

        //显示软键盘
        layout_search_mark.setOnTouchListener { _, _ ->
            layout_search_mark.visibility = View.GONE
            inputManager.showSoftInput(et_search_input, 0)
            return@setOnTouchListener false
        }

        //输入搜索
        et_search_input.addTextChangedListener(object : TextWatcher {

            var repeatStr: String? = null

            override fun afterTextChanged(p0: Editable?) {

                if (inputSearchKey.isBlank()) {
                    throw NullPointerException("Please set the key corresponding to the content to be searched.")
                }

                val searchText: String? = p0?.toString()
                if (searchText == repeatStr) { //避免多次调用
                    return
                }
                repeatStr = searchText
                if (searchText.isNullOrBlank()) { //删除完成后自动搜索
                    inputSearchParams.clear()
                    httpRequest(REQUEST_FIRST)
                } else if (isInputSearchEach) { //是否每次输入后自动搜索
                    inputSearchParams.clear()
                    inputSearchParams.put(inputSearchKey, searchText)
                    httpRequest(REQUEST_FIRST)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        //监听软件盘的搜索按钮
        et_search_input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                inputManager.hideSoftInputFromWindow(et_search_input.windowToken, 0)
                when {
                    et_search_input.text.isNullOrBlank() -> {
                        toast("请输入要搜索的关键字")
                        return@setOnEditorActionListener false
                    }
                    inputSearchKey.isBlank() -> throw NullPointerException("Please set the key corresponding to the content to be searched.")
                    else -> httpRequest(REQUEST_FIRST)
                }
            }
            return@setOnEditorActionListener false
        }
    }

    /**
     * 过滤搜索
     */
    private fun filterSearch() {
        val dialog = AlertFragment.Builder(this)
            .setDialogStyle(DialogStyleEnum.DIALOG_BOTTOM)
            .setLayoutRes(R.layout.layout_recyclerview_refresh_filter, object : OnCustomListener {
                override fun onLayout(view: View, alertFragment: AlertFragment) {
                    filterLayoutRes?.let {
                        val filterView = layoutInflater.inflate(it, null, false)
                        view.tv_filter_reset.setOnClickListener {
                            inputManager.hideSoftInputFromWindow(view.tv_filter_reset.windowToken, 0)
                            alertFragment.dismiss()
                            resetFilterLayout()
                            selectorSearchParams.clear()
                            httpRequest(REQUEST_FIRST)
                        }

                        view.tv_filter_search.setOnClickListener {
                            inputManager.hideSoftInputFromWindow(view.tv_filter_reset.windowToken, 0)
                            alertFragment.dismiss()
                            selectorSearchParams = filterParams(filterView)
                            httpRequest(REQUEST_FIRST)

                        }
                        view.layout_filter_item.addView(filterView)
                    }
                }
            })
            .setDialogHeight(800)
            .setBackgroundResource(R.drawable.bg_dialog_no_corner)
            .setPaddingHorizontal(0)
            .setPaddingBottom(0)

        btn_action_search.setOnClickListener {
            if (!dialog.isShow()) {
                dialog.show()
            }

        }
    }


    /**
     * 获取泛参数实际类型
     */
    private fun getTClazz(): Type {
        //获取当前类带有泛型的父类
        val clazz: Type? = this.javaClass.genericSuperclass
        return if (clazz is ParameterizedType) {
            //获取父类的泛型参数（参数可能有多个，获取第一个）
            clazz.actualTypeArguments[0]
        } else {
            throw IllegalArgumentException()
        }

    }

    private fun type(raw: Class<*>, vararg args: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getRawType(): Type {
                return raw
            }

            override fun getActualTypeArguments(): Array<out Type> {
                return args
            }

            override fun getOwnerType(): Type? {
                return null
            }
        }
    }

    /**
     * 设置筛选界面
     */
    fun setFilterLayout(layoutRes: Int) {
        this.filterLayoutRes = layoutRes
    }

    /**
     * 设置请求参数
     */
    open fun filterParams(filterView: View): HttpParams {
        return HttpParams()
    }

    /**
     * 重置筛选界面
     */
    open fun resetFilterLayout() {}

    open fun initParam() {}

    abstract fun itemViewConvert(injector: ViewInjector, data: T?, position: Int)

}