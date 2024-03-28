package com.xeatpos.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.adapter.OrderHistoryAdapter
import com.xeatpos.data.ResponseHistoryOrdersList
import com.xeatpos.databinding.OrdersHistoryBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryListActivity  : BaseActivity(){

    private lateinit var binding: OrdersHistoryBinding
    private var list: MutableList<ResponseHistoryOrdersList.Data>? = ArrayList()
    private val progressDialog = CustomProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.orders_history)

        supportActionBar?.hide()

        binding.toolbarHistoryList.imgBackArrow.setOnClickListener {
            finish()
        }

        binding.toolbarHistoryList.txtScreenName.text =
            getString(R.string.orders_history)

       // progressDialog.show(this, getString(R.string.loading_orders))
        binding.progressHistory.visibility = View.VISIBLE
        getAllHistoryOrders()

        binding.textNoHistoryOrder.text = getString(R.string.no_order_found)

        binding.swipeRefreshHistory.setOnRefreshListener {

            binding.progressHistory.visibility = View.VISIBLE
            getAllHistoryOrders()

        }


    }
    fun getAllHistoryOrders() {
        val apiInterface =
            APIService.create().getHistoryOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseHistoryOrdersList.OrdersHistory> {
            override fun onResponse(
                call: Call<ResponseHistoryOrdersList.OrdersHistory>?,
                response: Response<ResponseHistoryOrdersList.OrdersHistory>?
            ) {

                if (response?.body() != null) {
                    binding.progressHistory.visibility = View.GONE
                     /* if(progressDialog != null) {
                           progressDialogReady.dialog.dismiss()
                       }*/
                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshHistory.isRefreshing = false

                        list!!.clear()

                        binding.textNoHistoryOrder.visibility = View.GONE
                        binding.historyRecylerView.visibility = View.VISIBLE

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        historyOrderList(response.body()!!.data)

                    } else {


                        binding.swipeRefreshHistory.isRefreshing = false

                        binding.textNoHistoryOrder.visibility = View.VISIBLE
                        binding.historyRecylerView.visibility = View.GONE

                        Toast.makeText(
                            applicationContext,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponseHistoryOrdersList.OrdersHistory>?, t: Throwable?) {
                // progressDialogReady.dialog.dismiss()
                binding.progressHistory.visibility = View.GONE
                binding.swipeRefreshHistory.isRefreshing = false
                Toast.makeText(applicationContext, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun historyOrderList(courses: List<ResponseHistoryOrdersList.Data>):
            List<ResponseHistoryOrdersList.Data> {

        val newList = mutableListOf<ResponseHistoryOrdersList.Data>()
        courses.forEach {
            list!!.add(
                ResponseHistoryOrdersList.Data(
                    it.discount_rate,
                    it.driver_id,
                    it.driver_name,
                    it.driver_phone,
                    it.driver_pic,
                    it.order_at,
                    it.order_id,
                    it.order_type,
                    it.driver_type,
                    it.orderby,
                    it.phone,
                    it.status,
                    it.total_amount,
                    it.user_id
                )
            )
        }


        binding.historyRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = OrderHistoryAdapter(context, list!!, object : OrderHistoryAdapter.ItemClick {
                override fun itemClick(position: Int) {

                    context.startActivity(
                        Intent(context, OrderHistoryItemDetailsActivity::class.java).putExtra(
                            "order_id", ""+ list!![position].order_id))

                }

            })
            this.adapter = adapter
        }
        return newList
    }



}