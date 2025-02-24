package com.xeatpos.fragment

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.xeatpos.R
import com.xeatpos.accounts.WalletBalanceActivity
import com.xeatpos.activities.HomeActivity
import com.xeatpos.activities.LoginActivity
import com.xeatpos.activities.OrderHistoryListActivity
import com.xeatpos.data.OnlineOfflineModel
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseCategory
import com.xeatpos.data.ResponseProfileModel
import com.xeatpos.databinding.FragmentAccountBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import kotlinx.android.synthetic.main.fragment_account.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException

class AccountFragment : Fragment() {
    lateinit var binding: FragmentAccountBinding
    private val progressDialog = CustomProgressDialog()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)

        context?.let { progressDialog.show(it, "Please Wait...") }
        getProfile()

        binding.llLogout.setOnClickListener {
            Logout()
        }

        binding.llWalletTransaction.setOnClickListener {
            startActivity(Intent(context, WalletBalanceActivity::class.java))
        }

        binding.llOrderHistory.setOnClickListener {
            startActivity(Intent(context, OrderHistoryListActivity::class.java))
        }

        binding.switchOnlineOffline.setOnClickListener {

            context?.let { it1 -> progressDialog.show(it1, "Please Wait...") }

         /*   binding.switchOnlineOffline?.setOnCheckedChangeListener{ _ , isChecked ->
                context?.let { it1 -> progressDialog.show(it1, "Please Wait123...") }
               if (isChecked)
                   OnlineOffline(true, "1")
               else
                   OnlineOffline(false,"0")
            }
*/
            if (binding.switchOnlineOffline.isChecked) {
                //binding.switchOnlineOffline.setChecked(false);

                OnlineOffline(true, "1")
            }else{
                //binding.switchOnlineOffline.setChecked(true);
                OnlineOffline(false,"0")
            }
        }

        return binding.root
    }

    private fun Logout() {
        context?.let {
            val dialog = Dialog(it)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.setCancelable(false)
            dialog.setContentView(R.layout.logout_dialog)
            val btnCancelDialog = dialog.findViewById(R.id.btnCancelDialog) as Button
            val btnLogoutDialog = dialog.findViewById(R.id.btnLogoutDialog) as Button


            btnCancelDialog.setOnClickListener {
                dialog.dismiss()
            }
            btnLogoutDialog.setOnClickListener {

                dialog.dismiss()
               // prefs.logout()
               // startActivity(Intent(context, LoginActivity::class.java))
                //activity?.finish()
                context?.let { progressDialog.show(it, "Please Wait...") }
                LogoutAPI()

            }
            dialog.show()
        }


    }

    fun getProfile() {
        val apiInterface =
            APIService.create().getProfile("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseProfileModel.Profile> {
            override fun onResponse(
                call: Call<ResponseProfileModel.Profile>?,
                response: Response<ResponseProfileModel.Profile>?
            ) {

                if (response?.body() != null) {

                    context?.let { progressDialog.dialog.dismiss() }

                    if (response.body()!!.status.equals("1")) {

                        try {
                            if (requireContext() != null) {
                                Glide.with(requireContext()).load(response.body()!!.data.image)
                                    .into(binding.profileImage)
                            }
                        }catch (e : IllegalStateException){
                            e.printStackTrace()
                        }

                        binding.switchOnlineOffline.isChecked = response.body()!!.data.online != "0"
                        binding.txtName.text = response.body()!!.data.rest_name
                        binding.txtPhone.text = response.body()!!.data.contact_number
                        binding.txtAddress.text = response.body()!!.data.location

                    } else {

                        Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                            .show()

                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseProfileModel.Profile>?, t: Throwable?) {
                progressDialog.dialog.dismiss()
                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT).show()

                Log.i("Exception: ", t.toString())

            }
        })
    }

    fun LogoutAPI() {
        val apiInterface =
            APIService.create().logout(
                "application/json", "Bearer " + prefs.token
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                     progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {
                        prefs.logout()
                        startActivity(Intent(context, LoginActivity::class.java))
                        activity?.finish()
                       // Toast.makeText(context, response?.body()!!.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, response?.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {
                // Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_SHORT)
                //  .show()

                // Log.i("Exception: ", t.toString())

            }
        })

    }

    fun OnlineOffline( status: Boolean, statusData : String) {
        val apiInterface =
            APIService.create().onlineoffline(
                "application/json", "Bearer " + prefs.token,statusData
            )

        apiInterface.enqueue(object : Callback<OnlineOfflineModel.OnlineOffline> {
            override fun onResponse(
                call: Call<OnlineOfflineModel.OnlineOffline>?,
                response: Response<OnlineOfflineModel.OnlineOffline>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {
                        binding.switchOnlineOffline.isChecked = status
                        // Toast.makeText(context, response?.body()!!.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, response?.body()!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<OnlineOfflineModel.OnlineOffline>?,
                t: Throwable?
            ) {
                // Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_SHORT)
                //  .show()

                // Log.i("Exception: ", t.toString())
            }
        })
    }
}