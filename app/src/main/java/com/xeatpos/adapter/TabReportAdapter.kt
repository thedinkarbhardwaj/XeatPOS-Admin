package com.xeatpos.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xeatpos.order.NewOrderFragment
import com.xeatpos.order.PickedUpOrderFragment
import com.xeatpos.order.PreparingOrderFragment
import com.xeatpos.order.ReadyOrderFragment
import com.xeatpos.report.DailyReportFragment
import com.xeatpos.report.MonthlyReportFragment
import com.xeatpos.report.SelectRangeFragment
import com.xeatpos.report.WeeklyReportFragment

@Suppress("DEPRECATION")
internal class TabReportAdapter(
    var context: Context, fm: FragmentManager, var totalTabs: Int
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                DailyReportFragment()
            }
            1 -> {
                WeeklyReportFragment()
            }
            2 -> {
                MonthlyReportFragment()
            }
            3 -> {
                SelectRangeFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}