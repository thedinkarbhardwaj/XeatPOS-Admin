package com.xeatpos.fragment

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.xeatpos.R
import com.xeatpos.adapter.TabReportAdapter
import com.xeatpos.databinding.FragmentReportBinding
import kotlinx.android.synthetic.main.fragment_home.view.*

class ReportFragment : Fragment() {
    lateinit var binding: FragmentReportBinding
    private var indicatorWidth = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_report, container, false)

        init()


        return binding.root
    }

    fun init() {
        binding.tabLayout.apply {
            addTab(this.tabLayout.newTab().setText(context.getString(R.string.daily)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.weekly)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.monthly)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.select_range)))
            this.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter =
                TabReportAdapter(requireContext(), childFragmentManager, tabLayout.tabCount)
            binding.viewPager.adapter = adapter
            binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(this.tabLayout))
        }


        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

                binding.viewPager.currentItem = tab.position
                tab.view.setBackgroundResource(
                    R.drawable.gradient_btn
                );
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.view.setBackgroundResource(
                    R.drawable.tab_bg
                );


            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        binding.tabLayout.post(Runnable {
            indicatorWidth = binding.tabLayout.getWidth() / binding.tabLayout.getTabCount()

            //Assign new width
            val indicatorParams = binding.indicator.getLayoutParams() as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            binding.indicator.setLayoutParams(indicatorParams)
        })

        for (i in 0 until 4) {

            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            if (i == 0) {
                tab.setBackgroundResource(
                    R.drawable.gradient_btn
                );
            } else {
                tab.setBackgroundResource(
                    R.drawable.tab_bg
                );
            }
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(30, 0, 10, 0)
            tab.requestLayout()
        }

    }


}