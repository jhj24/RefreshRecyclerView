package com.jhj.refreshrecyclerview.refresh

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.jhj.httplibrary.HttpCall
import com.jhj.httplibrary.callback.base.BaseHttpCallback
import com.jhj.httplibrary.model.HttpParams
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.base.BaseActivity
import com.jhj.slimadapter.SlimAdapter
import com.jhj.slimadapter.callback.ItemViewBind
import com.jhj.slimadapter.holder.ViewInjector
import kotlinx.android.synthetic.main.activity_recyclerview_refresh.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

abstract class BaseRefreshActivity<T> : BaseActivity() {

    abstract val url: String
    abstract val itemLayoutRes: Int

    open val startPageNum = 1
    open val pageSize = 10
    open val httpParams: HttpParams = HttpParams()

    private var isFirstLoading = true
    private var pageNo = 1

    lateinit var adapterLocal: SlimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recyclerview_refresh)
        adapterLocal = initAdapter()
        initParam()
        httpRequest()
        smartRefreshLayout.autoLoadMore()//自动加载
        smartRefreshLayout.setOnRefreshListener {
            isFirstLoading = true
            pageNo = startPageNum
            httpRequest()
        }

        smartRefreshLayout.setOnLoadMoreListener {
            isFirstLoading = false
            httpRequest()
        }

    }


    private fun httpRequest() {
        HttpCall.post(url)
            .addParam("pageNo", pageNo.toString())
            .addParam("pageSize", pageSize.toString())
            .addParams(httpParams)
            .enqueue(object : BaseHttpCallback<RefreshResult<T>>() {

                override val genericArrayType: Type?
                    get() = type(RefreshResult::class.java, getTClazz())

                override fun onSuccess(data: RefreshResult<T>?, resultType: ResultType) {
                    val list = data?.data
                    if (list != null && list.isNotEmpty()) {
                        pageNo++
                    } else {
                        smartRefreshLayout.finishLoadMoreWithNoMoreData();//显示全部加载完成，并不再触发加载更事件
                    }
                    if (isFirstLoading) {
                        adapterLocal.setDataList(list)
                        smartRefreshLayout.finishRefresh()
                    } else {
                        adapterLocal.addDataList(list)
                        smartRefreshLayout.finishLoadMore()
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

    open fun initParam() {}

    abstract fun itemViewConvert(injector: ViewInjector, data: T?, position: Int)


}