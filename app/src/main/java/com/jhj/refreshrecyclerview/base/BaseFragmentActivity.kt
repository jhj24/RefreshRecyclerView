package com.jhj.refreshrecyclerview.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.jhj.navigation.NavigationBarItem
import com.jhj.refreshrecyclerview.R

abstract class BaseFragmentActivity : BaseActivity() {


    val fragmentList = arrayListOf<Fragment>()
    val navigationBarItemList = arrayListOf<NavigationBarItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_recyclerview_refresh)
    }


    class Adapter(manager: FragmentManager, private val fragmentList: List<Fragment>) : FragmentPagerAdapter(manager) {

        override fun getItem(p0: Int): Fragment {
            return fragmentList[p0]
        }

        override fun getCount(): Int {
            return fragmentList.size
        }
    }

}