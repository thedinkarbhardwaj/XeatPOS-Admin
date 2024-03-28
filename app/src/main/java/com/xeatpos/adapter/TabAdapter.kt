package com.xeatpos.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.xeatpos.home.MenuFragment
import com.xeatpos.home.CategoryFragment

@Suppress("DEPRECATION")
internal class TabAdapter(
    var context: Context, fm: FragmentManager, var totalTabs: Int
) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MenuFragment()
            }
            1 -> {
                CategoryFragment()
            }
            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}