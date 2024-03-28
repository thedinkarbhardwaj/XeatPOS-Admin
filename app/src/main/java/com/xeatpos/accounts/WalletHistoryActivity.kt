package com.xeatpos.accounts

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.activities.BaseActivity
import com.xeatpos.adapter.WalletTransactionAdapter
import com.xeatpos.data.ResponseWalletTransaction
import com.xeatpos.databinding.FragmentMenuBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WalletHistoryActivity : BaseActivity() {

    lateinit var binding: FragmentMenuBinding
    var list: MutableList<ResponseWalletTransaction.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.fragment_menu)

        supportActionBar.hashCode()

        binding.search.visibility = View.GONE

        binding.layoutTopMenus.card.visibility = View.VISIBLE

        binding.textNoMenuFound.text = getString(R.string.no_transaction_found)
        binding.layoutTopMenus.txtScreenName.text = getString(R.string.wallet_transactions)
        binding.layoutTopMenus.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.btnOutOfStock.visibility = View.GONE

        binding.swipeToRefreshMenus.setOnRefreshListener {
            // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
            binding.swipeToRefreshMenus.isRefreshing = false

        }

        progressDialog.show(this, "Loading Transactions...")


        getWalletTransactions()
    }

    fun getWalletTransactions() {
        val apiInterface =
            APIService.create().getWalletTransaction("application/json", "Bearer " + prefs.token,"1000","1")

        apiInterface.enqueue(object : Callback<ResponseWalletTransaction.Wallet> {
            override fun onResponse(
                call: Call<ResponseWalletTransaction.Wallet>?,
                response: Response<ResponseWalletTransaction.Wallet>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        binding.swipeToRefreshMenus.isRefreshing = false

                        list!!.clear()

                        binding.textNoMenuFound.visibility = View.GONE
                        binding.menuRecycler.visibility = View.VISIBLE

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        walletList(response.body()!!.data)

                    } else {

                        binding.textNoMenuFound.visibility = View.VISIBLE
                        binding.menuRecycler.visibility = View.GONE

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseWalletTransaction.Wallet>?, t: Throwable?) {

                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun walletList(courses: List<ResponseWalletTransaction.Data>): List<ResponseWalletTransaction.Wallet> {
        val newList = mutableListOf<ResponseWalletTransaction.Wallet>()
        courses.forEach {
            list!!.add(
                ResponseWalletTransaction.Data(
                    it.amount,
                    it.amount_from,
                    it.closing_balance,
                    it.created_at,
                    it.id,
                    it.order_id,
                    it.res_id,
                    it.status,
                    it.transaction_type
                )
            )
        }

        binding.menuRecycler.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = WalletTransactionAdapter(context, list!!)
            this.adapter = adapter
        }
        return newList
    }

}