package com.jhj.refreshrecyclerview.ui

import android.os.Bundle
import android.view.LayoutInflater
import com.jhj.navigation.GradientPageChangeListener
import com.jhj.navigation.NavigationBarItem
import com.jhj.refreshrecyclerview.R
import com.jhj.refreshrecyclerview.base.BaseFragmentActivity
import kotlinx.android.synthetic.main.activity_fragment_recyclerview_refresh.*

class FragmentRefreshActivity : BaseFragmentActivity() {

    override val title: String
        get() = "标题"

    private val bottomBarList = listOf("界面一", "界面二", "界面三", "界面四")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentList.add(getFragment())
        fragmentList.add(getFragment())
        fragmentList.add(getFragment())
        fragmentList.add(getFragment())


        fragmentList.forEachIndexed { index, fragment ->
            val view = LayoutInflater.from(this).inflate(R.layout.layout_navigation_text, layout_navigation, false)
            val navigationBarItem = NavigationBarItem(
                textViewDefault = view.findViewById(R.id.tv_navigation_default),
                textViewSelected = view.findViewById(R.id.tv_navigation_selected)
            )

            view.setOnClickListener {
                viewPager.setCurrentItem(index, false)
            }
            layout_navigation.addView(view)

            navigationBarItem.textViewDefault?.text = bottomBarList[index]
            navigationBarItem.textViewSelected?.text = bottomBarList[index]
            navigationBarItemList.add(navigationBarItem)
        }


        viewPager.adapter = Adapter(supportFragmentManager, fragmentList)

        val listener = GradientPageChangeListener(viewPager, fragmentList, navigationBarItemList)
        //listener.setGradientResultColor(R.color.colorAccent)
        //listener.setItemSelectedImgBigger(false)
        //listener.setOnPageChangeListener(pageChangeListener)
        viewPager.addOnPageChangeListener(listener)


    }


    fun getFragment(): RefreshFragment {
        val fragment = RefreshFragment()


        return fragment
    }

}