package com.github.blockchain.fragments

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.github.blockchain.AddApplicationActivity
import com.github.blockchain.adapters.ApkListAdapter
import com.github.blockchain.models.Apk
import com.github.blockchain.utils.Utilities
import io.github.rajdeep1008.apkwizard.R
import kotlinx.android.synthetic.main.fragment_apk_list.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class ApkListFragment : Fragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var apkList: ArrayList<Apk>
    private lateinit var mAdapter: ApkListAdapter
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    lateinit var mRecyclerView: RecyclerView

    companion object {
        const val APK_ARG: String = "apk-list"
        const val ADD_ARG: String = "can-add"
        fun newInstance(apkList: ArrayList<Apk>): ApkListFragment {
            val fragment = ApkListFragment()
            val args = Bundle()
            args.putParcelableArrayList(APK_ARG, apkList)
            fragment.arguments = args
            return fragment
        }

        fun newInstance(apkList: ArrayList<Apk>, add :Boolean): ApkListFragment {
            val fragment = ApkListFragment()
            val args = Bundle()
            args.putBoolean(ADD_ARG,add)
            args.putParcelableArrayList(APK_ARG, apkList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        apkList = if (arguments.getParcelableArrayList<Apk>(APK_ARG) != null) {
            arguments.getParcelableArrayList(APK_ARG)
        } else {
            ArrayList()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_apk_list, container, false)
        mRecyclerView = rootView.apk_list_rv
        mLinearLayoutManager = LinearLayoutManager(activity)
        mAdapter = ApkListAdapter(apkList, activity)

        mRecyclerView.layoutManager = mLinearLayoutManager
        mRecyclerView.adapter = mAdapter

        rootView.add_app_fab.setOnClickListener {
            val intent = Intent(context, AddApplicationActivity::class.java)
            startActivity(intent)
        }
        if(arguments.getBoolean(ADD_ARG,false))
            rootView.add_app_fab.visibility= View.VISIBLE

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
        preferences.registerOnSharedPreferenceChangeListener(this)

        updateSortingOrder(preferences.getInt(Utilities.PREF_SORT_KEY, 0))
        return rootView
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(activity).unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.main_menu, menu)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key.equals(Utilities.PREF_SORT_KEY)) {
            updateSortingOrder(sharedPreferences?.getInt(key, 0)!!)
        }
    }

    fun updateAdapter() = mAdapter.notifyDataSetChanged()

    private fun updateSortingOrder(order: Int) {
        val tempList: MutableList<Apk> = apkList.toMutableList()
        when (order) {
            Utilities.SORT_ORDER_NAME -> {
                tempList.sortWith(Comparator { p1, p2 ->
                    p1.appName.toLowerCase()
                            .compareTo(p2.appName.toLowerCase())
                })
            }
            Utilities.SORT_ORDER_INSTALLATION_DATE -> {
                tempList.sortWith(Comparator { p1, p2 ->
                    context.packageManager.getPackageInfo(p2.packageName, 0).firstInstallTime.toString()
                            .compareTo(context.packageManager.getPackageInfo(p1.packageName, 0).firstInstallTime.toString())
                })
            }
            Utilities.SORT_ORDER_UPDATE_DATE -> {
                tempList.sortWith(Comparator { p1, p2 ->
                    context.packageManager.getPackageInfo(p2.packageName, 0).lastUpdateTime.toString()
                            .compareTo(context.packageManager.getPackageInfo(p1.packageName, 0).lastUpdateTime.toString())
                })
            }
            Utilities.SORT_ORDER_SIZE -> {
                tempList.sortWith(Comparator { p1, p2 ->
                    (File(p2.sourceDir).length()).compareTo(File(p1.sourceDir).length())
                })
            }
        }
        mAdapter.updateData(tempList as ArrayList<Apk>)
    }
}
