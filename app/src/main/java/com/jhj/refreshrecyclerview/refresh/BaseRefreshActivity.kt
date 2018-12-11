package com.jhj.refreshrecyclerview.refresh

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.jhj.httplibrary.HttpCall
import com.jhj.httplibrary.callback.base.BaseHttpCallback
import com.jhj.httplibrary.model.HttpParams
import com.jhj.prompt.dialog.alert.AlertFragment
import com.jhj.prompt.dialog.alert.constants.DialogStyleEnum
import com.jhj.prompt.dialog.alert.interfaces.OnCustomListener
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.base.BaseActivity
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
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

    open val pageSize = 10
    open val startPageNum = 1
    open val hasSplitLine = true
    open val httpParams: HttpParams = HttpParams()

    //筛选搜索
    open val selectorSearch = true
    private var selectorSearchParams: HttpParams = HttpParams()

    //输入搜索
    open val inputSearch = false
    open val inputSearchKey = ""
    open val isInputSearchEach = true
    private val inputSearchParams: HttpParams = HttpParams()


    private var isFirstLoading = true
    private var pageNo = 1
    private var inputManager: InputMethodManager? = null
    private var isSecondRequest = false

    lateinit var adapterLocal: SlimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_refresh)
        initParam()
        adapterLocal = initAdapter()

        //分割线
        if (hasSplitLine) {
            adapterLocal.addItemDecoration(LineItemDecoration())
        }

        //输入搜索
        if (inputSearch) {
            layout_search_bar.visibility = View.VISIBLE
            layout_search_mark.setOnTouchListener(keyboardState)
            et_search.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    inputManager?.hideSoftInputFromWindow(et_search.windowToken, 0)
                    when {
                        et_search.text.isNullOrBlank() -> {
                            toast("请输入要搜索的关键字")
                            return@OnEditorActionListener false
                        }
                        inputSearchKey.isBlank() -> throw NullPointerException("Please set the key corresponding to the content to be searched.")
                        else -> httpRequest(REQUEST_FIRST)
                    }
                }
                return@OnEditorActionListener false
            })
        }

        //其他条件筛选
        if (selectorSearch) {

            btn_action_search.setOnClickListener {
                AlertFragment.Builder(this)
                    .setDialogStyle(DialogStyleEnum.DIALOG_BOTTOM)
                    .setLayoutRes(R.layout.layout_recyclerview_refresh_filter, object : OnCustomListener {
                        override fun onLayout(view: View, alertFragment: AlertFragment) {
                            filterLayoutRes?.let {
                                val filterView = layoutInflater.inflate(it, null, false)
                                view.tv_filter_reset.setOnClickListener {
                                    resetFilterLayout()
                                    selectorSearchParams.clear()
                                    httpRequest(REQUEST_FIRST)
                                    alertFragment.dismiss()
                                    inputManager?.hideSoftInputFromWindow(view.tv_filter_reset.windowToken, 0)
                                }

                                view.tv_filter_search.setOnClickListener {
                                    selectorSearchParams = filterParams(filterView)
                                    httpRequest(REQUEST_FIRST)
                                    alertFragment.dismiss()
                                    inputManager?.hideSoftInputFromWindow(view.tv_filter_search.windowToken, 0)
                                }
                                view.layout_filter_item.addView(filterView)
                            }
                        }
                    })
                    .setDialogHeight(800)
                    .setBackgroundResource(R.drawable.bg_dialog_no_corner)
                    .setPaddingHorizontal(0)
                    .setPaddingBottom(0)
                    .show()
            }

        }

        //数据刷新加载
        smartRefreshLayout.autoLoadMore()//自动加载
        smartRefreshLayout.setEnableLoadMoreWhenContentNotFull(true)
        smartRefreshLayout.setOnRefreshListener {
            httpRequest(REQUEST_FIRST)
        }

        smartRefreshLayout.setOnLoadMoreListener {
            httpRequest(REQUEST_OTHER)
        }
        httpRequest(REQUEST_FIRST)


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

                override val genericArrayType: Type?
                    get() = type(RefreshResult::class.java, getTClazz())

                override fun onSuccess(data: RefreshResult<T>?, resultType: ResultType) {
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
                    if (isFirstLoading) {
                        smartRefreshLayout.finishRefresh(false)
                    } else {
                        smartRefreshLayout.finishLoadMore(false)
                    }
                }

            })
    }


    private fun initAdapter(): SlimAdapter {
        return SlimAdapter.creator(LinearLayoutManager(this))
            .setGenericActualType(getTClazz())
            .register<T>(itemLayoutRes, object : ItemViewBind<T>() {
                override fun convert(injector: ViewInjector, data: T, position: Int) {
                    itemViewConvert(injector, data, position)
                }
            })
            .attachTo(recyclerView)
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


    private val keyboardState = View.OnTouchListener { _, _ ->
        layout_search_mark.visibility = View.GONE
        et_search.addTextChangedListener(textChangedListener)
        if (inputManager == null) {
            inputManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager?.showSoftInput(et_search, 0)
        }
        recyclerView.setOnTouchListener { _, _ ->
            inputManager?.hideSoftInputFromWindow(et_search.windowToken, 0)
            if (et_search.text.isNullOrBlank()) {
                layout_search_mark.visibility = View.VISIBLE
            }
            return@setOnTouchListener false
        }
        return@OnTouchListener false
    }

    /**
     * 输入搜索
     */
    private val textChangedListener = object : TextWatcher {

        var s: String? = null

        override fun afterTextChanged(p0: Editable?) {

            if (inputSearchKey.isBlank()) {
                throw NullPointerException("Please set the key corresponding to the content to be searched.")
            }

            val searchText: String? = p0?.toString()
            if (searchText == s) { //避免多次调用
                return
            }
            s = searchText
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
    }


    private var filterLayoutRes: Int? = null

    /**
     * 设置筛选界面
     */
    protected fun setFilterLayout(layoutRes: Int) {
        this.filterLayoutRes = layoutRes
    }


    protected open fun filterParams(filterView: View): HttpParams {
        return HttpParams()
    }

    /**
     * 重置筛选界面
     */
    protected open fun resetFilterLayout() {

    }


    open fun initParam() {}

    abstract fun itemViewConvert(injector: ViewInjector, data: T?, position: Int)

}