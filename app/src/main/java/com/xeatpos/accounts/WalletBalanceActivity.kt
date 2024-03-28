package com.xeatpos.accounts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.data.ResponseWalletBalance
import com.xeatpos.databinding.FragmentWalletBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletBalanceActivity : BaseActivity() {

    lateinit  var binding : FragmentWalletBinding
    private val progressDialog = CustomProgressDialog()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_wallet)

        supportActionBar?.hide()

        binding.layoutHeaderWallet.txtScreenName.text = getString(R.string.wallet)

        binding.layoutHeaderWallet.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.layoutAmountWallet.visibility = View.VISIBLE



        binding.btnTransaction.setOnClickListener {

            startActivity(Intent(applicationContext, WalletHistoryActivity::class.java))

        }


    }

    override fun onResume() {
        super.onResume()
        progressDialog.show(this,"Loading Wallet...")

        getWalletBalance()
    }

    fun getWalletBalance(){
        val apiInterface = APIService.create().getWalletBalance("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseWalletBalance.WalletBalance> {
            override fun onResponse(
                call: Call<ResponseWalletBalance.WalletBalance>?,
                response: Response<ResponseWalletBalance.WalletBalance>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        binding.txtPrice.text = response.body()!!.wallet_money
                        binding.txtPriceLine.text = getString(R.string.your_total_wallet_balance_is)+" "+response.body()!!.wallet_money


                    } else {

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseWalletBalance.WalletBalance>?, t: Throwable?) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT).show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

}