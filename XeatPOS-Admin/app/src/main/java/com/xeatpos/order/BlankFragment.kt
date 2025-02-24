package com.xeatpos.order

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.activities.HomeActivity
import com.xeatpos.activities.PrintOrderActivity
import com.xeatpos.adapter.NewOrderAdapter
import com.xeatpos.adapter.ScheduleOrderAdapter
import com.xeatpos.comman.SharedPreferenceData
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseNewOrders
import com.xeatpos.data.ResponseScheduledOrders
import com.xeatpos.databinding.FragmentBlankBinding
import com.xeatpos.databinding.FragmentNewOrderBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BlankFragment : Fragment() {

    lateinit var binding: FragmentBlankBinding
    var list: MutableList<ResponseScheduledOrders.Data>? = ArrayList()

    // private val progressDialog = CustomProgressDialog()
    lateinit var mainHandler: Handler

    var flag: Boolean = false
    var flagTask: Boolean = false
    val handler = Handler()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_blank, container, false)

        mainHandler = Handler(Looper.getMainLooper())

        // init()

        binding.swipeRefreshNewOrder.setOnRefreshListener {
            // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
            getAllNewOrders()

        }

        if (prefs.newOrderId == "" || prefs.newOrderId == null) {
            getAllNewOrders()
        }

//        handler.postDelayed(object : Runnable {
//            override fun run() {
//                CheckNewOrder(prefs.newOrderId)
//                handler.postDelayed(this, 8000)
//            }
//        }, 8000)

        return binding.root
    }

    /* private val updateTextTask = object : Runnable {
         override fun run() {
             Log.i("===========", "API Hit for check new order")
             CheckNewOrder(prefs.newOrderId)
             mainHandler.postDelayed(this, 5000)
         }
     }*/


    /* val my_runnable = Runnable {
         // your code here
         Log.i("===========","API Hit for check new order")
         CheckNewOrder(prefs.newOrderId)
     }


     fun start() {
         handler.postDelayed(my_runnable, 5000)
     }

     // to stop the handler
     fun stop() {
         handler.removeCallbacks(my_runnable)
     }

     // to reset the handler
     fun restart() {
         handler.removeCallbacks(my_runnable)
         handler.postDelayed(my_runnable, 5000)
     }*/

    override fun onResume() {
        super.onResume()
        // if(!flag) {
        // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
        binding.progressNewOrder.visibility = View.VISIBLE
        getAllNewOrders()
        if (!flagTask) {

            flagTask
            // mainHandler.post(updateTextTask)

        }
        //  }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            // flag = true
            // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
            // getAllNewOrders()
        }
    }


    fun getAllNewOrders() {
        Log.e("token",prefs.token)
        val apiInterface =
            APIService.create().getAllAdvanceOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseScheduledOrders.NewOrders> {
            override fun onResponse(
                call: Call<ResponseScheduledOrders.NewOrders>?,
                response: Response<ResponseScheduledOrders.NewOrders>?
            ) {

                if (response?.body() != null) {

                    //  progressDialog.dialog.dismiss()

                    binding.progressNewOrder.visibility = View.GONE

                    if (response.body()!!.status == "1") {

                        binding.swipeRefreshNewOrder.isRefreshing = false

                        list!!.clear()

                        binding.textNoNewOrder.visibility = View.GONE
                        binding.newRecylerView.visibility = View.VISIBLE

                        // Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT).show()

                        newOrderList(response.body()!!.data)

                        //  mainHandler.post(updateTextTask)

                    } else if (response.body()!!.status == "9") {

                        try {
                            Toast.makeText(context, response.body()!!.message, Toast.LENGTH_SHORT)
                                .show()
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }

                    } else {

                        //  mainHandler.post(updateTextTask)

                        binding.swipeRefreshNewOrder.isRefreshing = false

                        binding.textNoNewOrder.visibility = View.VISIBLE
                        binding.newRecylerView.visibility = View.GONE

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

            override fun onFailure(call: Call<ResponseScheduledOrders.NewOrders>?, t: Throwable?) {
                //progressDialog.dialog.dismiss()
                try {

                    binding.swipeRefreshNewOrder.isRefreshing = false
                    Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                        .show()

                    Log.i("Exception: ", t.toString())

                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
        })

    }

    fun newOrderList(courses: List<ResponseScheduledOrders.Data>): List<ResponseScheduledOrders.Data> {
        val newList = mutableListOf<ResponseScheduledOrders.Data>()
        courses.forEach {
            list!!.add(
                ResponseScheduledOrders.Data(
                    it.order_at,
                    it.order_id,
                    it.order_type,
                    it.orderby,
                    it.driver_type,
                    it.driver_from,
                    it.status,
                    it.total_amount,
                    it.schedule_type,
                    it.schedule_date,
                    it.schedule_time,
                    it.today_order
                )
            )
        }

        prefs.newOrderId = "" + list!![0].order_id

        binding.newRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            if (!flag) {
                val adapter = ScheduleOrderAdapter(context, list!!, this@BlankFragment)
                this.adapter = adapter
                flag = true
            } else {
                if(this.adapter != null) {
                    this.adapter!!.notifyDataSetChanged()
                }else{
                    val adapter = ScheduleOrderAdapter(context, list!!, this@BlankFragment)
                    this.adapter = adapter
                    flag = true
                }
            }
            return newList
        }
    }


    fun CheckNewOrder(orderId: String) {
        val apiInterface =
            APIService.create().checkNewOrder(
                "application/json", "Bearer " + prefs.token,
                orderId
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    // progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        //  mainHandler.removeCallbacks(updateTextTask)

                        // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
                        getAllNewOrders()

                    } else {
                        // if(orderId == ""){
                        //  mainHandler.removeCallbacks(updateTextTask)
                        getAllNewOrders()
                        //  }
                        //  mainHandler.removeCallbacks(updateTextTask)
                        //  getAllNewOrders()
                        /*   Toast.makeText(
                               context,
                               response.body()!!.message,
                               Toast.LENGTH_SHORT
                           ).show()*/


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {
                getAllNewOrders()
                binding.progressNewOrder.visibility = View.GONE
                // Toast.makeText(context, "Exception: " + t.toString(), Toast.LENGTH_SHORT)
                //  .show()

                // Log.i("Exception: ", t.toString())

            }
        })

    }


    fun proceed(orderId: Int, pick_del:String) {
        binding.progressNewOrder.visibility = View.VISIBLE
        val apiInterface =
            APIService.create().proceedOrderAdvance(
                "application/json", "Bearer " + prefs.token,
                orderId.toString(), "accepted"
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    binding.progressNewOrder.visibility = View.GONE

                    if (response.body()!!.status == "1") {
                        var shf= context?.let { SharedPreferenceData.getInstance(it) };
                        if(pick_del.equals("Pickup"))
                        {
                            shf!!.saveData1("Tab_sel","1")
                        }
                        else{
                            shf!!.saveData1("Tab_sel","0")
                        }
                        startActivity(Intent(context, HomeActivity::class.java))
                        (context as Activity).finishAffinity()
                    } else {

                        Toast.makeText(
                            context,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                t: Throwable?
            ) {
                binding.progressNewOrder.visibility = View.GONE
                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

}