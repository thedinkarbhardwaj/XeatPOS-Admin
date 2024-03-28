package com.xeatpos.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.xeatpos.R
import com.xeatpos.databinding.FragmentPreparingOrderDetailBinding

class TestActivity : AppCompatActivity() {
    lateinit var binding: FragmentPreparingOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_preparing_order_detail)




    }







}