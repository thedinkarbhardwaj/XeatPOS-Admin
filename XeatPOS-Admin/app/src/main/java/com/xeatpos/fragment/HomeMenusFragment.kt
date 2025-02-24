package com.xeatpos.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.xeatpos.R
import com.xeatpos.adapter.TabAdapter
import com.xeatpos.databinding.FragmentHomeBinding
import com.xeatpos.prefs
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeMenusFragment : Fragment() {
    lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.textRestName.text = prefs.restaurantName

        context?.let { Glide.with(it).load(prefs.restaurantImage).into(binding.imageRestaurantHome) }


        binding.tabLayout.apply {

            addTab(this.tabLayout.newTab().setText(context.getString(R.string.menu)))
            this.tabLayout.addTab(this.tabLayout.newTab().setText(context.getString(R.string.category)))
            this.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = TabAdapter(requireContext(), childFragmentManager, tabLayout.tabCount)
            binding.viewPager.adapter = adapter
            binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(this.tabLayout))

        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                binding.viewPager.currentItem = tab.position
                if(tab.position == 0){
                    tab.view.setBackgroundResource(
                        R.drawable.left_selected
                    );
                }else{
                    tab.view.setBackgroundResource(
                        R.drawable.right_selected
                    );
                }
               /* tab.view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.active_tab_color
                    )
                );*/
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                /*tab.view.setBackgroundColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.inactive_tab_color
                    )
                );*/
               /* tab.view.setBackgroundResource(
                    R.drawable.tab_bg
                );*/

                if(tab.position == 0){
                    tab.view.setBackgroundResource(
                        R.drawable.unselected_left
                    );
                }else{
                    tab.view.setBackgroundResource(
                        R.drawable.unselected_right
                    );
                }

            }

            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        for (i in 0 until 2) {

            val tab = (binding.tabLayout.getChildAt(0) as ViewGroup).getChildAt(i)
            if (i == 0) {
                tab.setBackgroundResource(
                    R.drawable.left_selected
                );
            } else {
                tab.setBackgroundResource(
                    R.drawable.unselected_right
                );
            }
           // val p = tab.layoutParams as ViewGroup.MarginLayoutParams
         //   p.setMargins(30, 0, 40, 0)
            tab.requestLayout()
        }
        return binding.root
    }

}