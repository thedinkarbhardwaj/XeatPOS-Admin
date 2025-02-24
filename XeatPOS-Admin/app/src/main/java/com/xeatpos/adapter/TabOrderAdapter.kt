package com.xeatpos.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xeatpos.order.*

@Suppress("DEPRECATION")
internal class TabOrderAdapter(
    var context: Context, fm: FragmentManager, var totalTabs: Int
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                NewOrderFragment()
            }
            1 -> {
                PreparingOrderFragment()
            }
            2->{
                ReadyOrderFragment()
            }
            3->{
                PickedUpOrderFragment()
            }
            4->{
                BlankFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}