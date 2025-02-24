package com.xeatpos.order

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.adapter.PickedOrderAdapter
import com.xeatpos.data.ResponsePickedOrders
import com.xeatpos.databinding.FragmentPreparingOrderBinding
import com.xeatpos.order.orders.PickedOrderDetailsActivity
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PickedUpOrderFragment : Fragment() {
    private lateinit var binding: FragmentPreparingOrderBinding
    private var list: MutableList<ResponsePickedOrders.CompleteOrder>? = ArrayList()
   // private val progressDialogReady = CustomProgressDialog()
    private var flagPicked : Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_preparing_order, container, false)
        // init()

        binding.swipeRefreshPreparingOrder.setOnRefreshListener {
            binding.progressReady.visibility = View.VISIBLE
            //context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
            getAllPickedOrders()

        }

        binding.textNoPreparingOrder.text = getString(R.string.no_order_picked_yet)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if(flagPicked == false) {
            // if (progressDialogReady != null) {
            // context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
            binding.progressReady.visibility = View.VISIBLE
            getAllPickedOrders()
            //  }
        }

    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // if(progressDialogReady != null) {
            flagPicked = true
            // context?.let { progressDialogReady.show(it, getString(R.string.loading_orders)) }
            try {
                binding.progressReady.visibility = View.VISIBLE
                getAllPickedOrders()
            }catch (e:UninitializedPropertyAccessException){
               // binding.progressReady.visibility = View.VISIBLE
                getAllPickedOrders()
                e.printStackTrace()
            }
            // }
        }
    }

    fun getAllPickedOrders() {
        val apiInterface =
            APIService.create().getPickedOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponsePickedOrders.PickedOrdersRoot> {
            override fun onResponse(
                call: Call<ResponsePickedOrders.PickedOrdersRoot>?,
                response: Response<ResponsePickedOrders.PickedOrdersRoot>?
            ) {

                if (response?.body() != null) {
                    binding.progressReady.visibility = View.GONE
                    /*   if(progressDialogReady != null) {
                           progressDialogReady.dialog.dismiss()

                       }*/
                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        list!!.clear()

                        binding.textNoPreparingOrder.visibility = View.GONE
                        binding.prepairingRecylerView.visibility = View.VISIBLE

                        //   Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        preparingOrderList(response.body()!!.complete_order)


                    }  else  if (response.body()!!.status == "9") {

                        try{
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()
                        }catch (e : NullPointerException){
                            e.printStackTrace()
                        }

                    }else {
                      //  Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        binding.swipeRefreshPreparingOrder.isRefreshing = false

                        binding.textNoPreparingOrder.visibility = View.VISIBLE
                        binding.prepairingRecylerView.visibility = View.GONE

                        /*  Toast.makeText(
                              context,
                              response.body()!!.message,
                              Toast.LENGTH_SHORT
                          )
                              .show()*/

                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(call: Call<ResponsePickedOrders.PickedOrdersRoot>?, t: Throwable?) {
                // progressDialogReady.dialog.dismiss()
                binding.progressReady.visibility = View.GONE
                binding.swipeRefreshPreparingOrder.isRefreshing = false
                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun preparingOrderList(courses: List<ResponsePickedOrders.CompleteOrder>): List<ResponsePickedOrders.CompleteOrder> {

        val newList = mutableListOf<ResponsePickedOrders.CompleteOrder>()
        courses.forEach {
            list!!.add(
                ResponsePickedOrders.CompleteOrder(
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


        binding.prepairingRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            val adapter = PickedOrderAdapter(context, list!!, object : PickedOrderAdapter.ItemClick {
                override fun itemClick(position: Int) {

                    flagPicked = false

                    context.startActivity(
                        Intent(context, PickedOrderDetailsActivity::class.java)
                            .putExtra("order_id", ""+ list!![position].order_id)
                            .putExtra("driver_type", ""+ list!![position].driver_type))

                }

            })
            this.adapter = adapter
        }
        return newList
    }


}