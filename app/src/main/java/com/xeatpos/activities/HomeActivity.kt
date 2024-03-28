package com.xeatpos.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.xeatpos.databinding.ActivityHomeBinding
import com.google.android.gms.common.ConnectionResult

import com.google.android.gms.common.GoogleApiAvailability
import android.view.View

import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.annotation.NonNull
import com.xeatpos.R
import com.xeatpos.fragment.AccountFragment
import com.xeatpos.fragment.HomeMenusFragment
import com.xeatpos.fragment.OrderFragment
import com.xeatpos.fragment.ReportFragment
import com.xeatpos.home.MenuFragment


class HomeActivity : BaseActivity() {
    lateinit var navController: NavController
    lateinit var binding: ActivityHomeBinding

    val fragment1: Fragment = OrderFragment()
    val fragment2: Fragment = ReportFragment()
    val fragment3: Fragment = HomeMenusFragment()
    val fragment4: Fragment = AccountFragment()
    val fm: FragmentManager = supportFragmentManager
    var active: Fragment = fragment1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

      /*  var navHost =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHost.navController
        binding.bottomNav.setupWithNavController(navController)*/

        if(intent.getStringExtra("test") == null){
            binding.bottomNav.selectedItemId = R.id.orderFragemnt
        }else{
            binding.bottomNav.selectedItemId = R.id.orderFragemnt
        }


        val googleAPI = GoogleApiAvailability.getInstance()
        val result = googleAPI.isGooglePlayServicesAvailable(this)

        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                //prompt the dialog to update google play
                googleAPI.getErrorDialog(this, result, 1001)!!.show()
            }
        } else {
          //  Toast.makeText(applicationContext,"Success", Toast.LENGTH_SHORT).show()
        }


        val navigation = findViewById<View>(R.id.bottom_nav) as BottomNavigationView
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

      /*  fm.beginTransaction().add(R.id.fragmentContainerView, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.fragmentContainerView, fragment1, "1").commit()*/

        setFragment(fragment1, "1", 0)
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.orderFragemnt -> {
                    setFragment(fragment1, "1", 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.reportFragemnt -> {
                    setFragment(fragment2, "2", 1)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.menuFragemnt -> {
                    setFragment(fragment3, "3", 2)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.accountFragemnt -> {
                    setFragment(fragment4, "4", 3)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    fun setFragment(fragment: Fragment?, tag: String?, position: Int) {
        // if (fragment.isAdded()) {
        //   fm.beginTransaction().hide(active).show(fragment).commit();
        //} else {
        fm.beginTransaction()
            .replace(R.id.fragmentContainerView, fragment!!, tag).commit()
        //  }
        // navigation.getMenu().getItem(position).setChecked(true);
        /// active = fragment;
    }


}