package jp.ac.hal.messagebottle


import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

import java.util.ArrayList

/**
 * Created by muto.masakazu on 2017/09/02.
 */

class MyFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val mFragmentsList = ArrayList<Fragment>()
    override fun getItem(position: Int): Fragment {
        return mFragmentsList[position]
    }

    override fun getCount(): Int {
        return mFragmentsList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        //positionごとのタブ名をreturn
        return when (position) {
            0 -> "camera"
            1 -> "main"
            2 -> "setting"
            else -> null
        }
    }

    fun addFragment(ft: Fragment) {
        mFragmentsList.add(ft)
    }

}
