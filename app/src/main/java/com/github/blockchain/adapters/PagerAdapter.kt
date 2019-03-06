package com.github.blockchain.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.github.blockchain.fragments.ApkListFragment
import com.github.blockchain.models.Apk

/**
 * Created by blockchain on 05/03/18.
 */
class PagerAdapter(fm: FragmentManager, userApkList: List<Apk>, systemApkList: List<Apk>, myApkList: List<Apk>) : FragmentStatePagerAdapter(fm) {

    private val tabNames = arrayOf("Installed", "System", "MyApps")
    var mUserApkList: List<Apk> = userApkList
    var mSystemApkList: List<Apk> = systemApkList
    var mMyApkList: List<Apk> =myApkList

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> ApkListFragment.newInstance(mUserApkList as ArrayList<Apk>)
            1 -> ApkListFragment.newInstance(mSystemApkList as ArrayList<Apk>)
            2 -> ApkListFragment.newInstance(mMyApkList as ArrayList<Apk>,true)
            else -> ApkListFragment()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? = tabNames[position]
}