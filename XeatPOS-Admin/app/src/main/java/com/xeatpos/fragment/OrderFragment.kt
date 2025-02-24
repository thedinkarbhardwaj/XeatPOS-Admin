package com.xeatpos.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.xeatpos.R
import com.xeatpos.adapter.TabOrderAdapter
import com.xeatpos.comman.SharedPreferenceData
import com.xeatpos.databinding.FragmentOrderBinding
import kotlinx.android.synthetic.main.fragment_home.view.*

class OrderFragment : Fragment() {
    lateinit var binding: FragmentOrderBinding
    private var indicatorWidth = 0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_order, container, false)

        binding.tabLayout.apply {
            addTab(this.tabLayout.newTab().setText(context.getString(R.string.newe)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.preparing)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.ready)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.picked)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.sch_order)))
            this.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter =
                TabOrderAdapter(requireContext(), childFragmentManager, tabLayout.tabCount)
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

       //Determine indicator width at runtime
        binding.tabLayout.post(Runnable {
            indicatorWidth = binding.tabLayout.getWidth() / binding.tabLayout.getTabCount()

            //Assign new width
            val indicatorParams = binding.indicator.getLayoutParams() as FrameLayout.LayoutParams
            indicatorParams.width = indicatorWidth
            binding.indicator.setLayoutParams(indicatorParams)
        })

        for (i in 0 until 5) {

            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            if(i == 0){
                tab.setBackgroundResource(
                    R.drawable.gradient_btn
                );
            }else{
                tab.setBackgroundResource(
                    R.drawable.tab_bg
                );
            }
            val p = tab.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(8, 0, 20, 0)
            tab.requestLayout()
        }

        var shf= context?.let { SharedPreferenceData.getInstance(it) };
        var type = shf!!.getData1("Tab_sel").toString()
        Log.e("check tab sel", type)
//        if(!type.isEmpty())
//            setFragment2(type.toInt())

        return binding.root
    }

    fun setFragment2(pos:Int) {
        val tab: TabLayout.Tab = binding.tabLayout.getTabAt(pos)!!
        tab.select()
    }
}