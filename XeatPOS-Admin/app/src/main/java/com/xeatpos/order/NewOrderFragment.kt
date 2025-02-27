package com.xeatpos.order

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.NOTIFICATION_SERVICE
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xeatpos.R
import com.xeatpos.activities.PrintOrderActivity
import com.xeatpos.adapter.NewOrderAdapter
import com.xeatpos.data.ResponseAcceptRejectOrder
import com.xeatpos.data.ResponseNewOrderDetails
import com.xeatpos.data.ResponseNewOrders
import com.xeatpos.databinding.FragmentNewOrderBinding
import com.xeatpos.prefs
import com.xeatpos.retrofit.APIService
import com.xeatpos.utils.Constants
import com.xeatpos.utils.CustomProgressDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewOrderFragment : Fragment() {

    lateinit var binding: FragmentNewOrderBinding
    var list: MutableList<ResponseNewOrders.Data>? = ArrayList()

    private val progressDialog = CustomProgressDialog()


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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_order, container, false)

        mainHandler = Handler(Looper.getMainLooper())

        // init()

        binding.swipeRefreshNewOrder.setOnRefreshListener {
            // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
            getAllNewOrders()

        }

        if (prefs.newOrderId == "" || prefs.newOrderId == null) {
            getAllNewOrders()
        }




        handler.postDelayed(object : Runnable {
            override fun run() {
                CheckNewOrder(prefs.newOrderId)
                handler.postDelayed(this, 8000)
            }
        }, 8000)

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


    fun getAllNewOrders(progress:Boolean = false) {
        Log.e("token",prefs.token)
        val apiInterface =
            APIService.create().getNewOrders("application/json", "Bearer " + prefs.token)

        apiInterface.enqueue(object : Callback<ResponseNewOrders.NewOrders> {
            override fun onResponse(
                call: Call<ResponseNewOrders.NewOrders>?,
                response: Response<ResponseNewOrders.NewOrders>?
            ) {

                if (response?.body() != null) {

                    if (progress == true) {
                        progressDialog.dialog.dismiss()
                    }

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

            override fun onFailure(call: Call<ResponseNewOrders.NewOrders>?, t: Throwable?) {
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

    fun newOrderList(courses: List<ResponseNewOrders.Data>): List<ResponseNewOrders.Data> {
        val newList = mutableListOf<ResponseNewOrders.Data>()
        courses.forEach {
            list!!.add(
                ResponseNewOrders.Data(
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
                )
            )
        }

        prefs.newOrderId = "" + list!![0].order_id

        binding.newRecylerView.apply {
            var layoutManager = LinearLayoutManager(context)
            this.layoutManager = layoutManager
            if (!flag) {
                val adapter = NewOrderAdapter(context, list!!,this@NewOrderFragment)
                this.adapter = adapter
                flag = true
            } else {
                if(this.adapter != null) {
                    this.adapter!!.notifyDataSetChanged()
                }else{
                    val adapter = NewOrderAdapter(context, list!!, this@NewOrderFragment)
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


    fun acceptRejectOrderBtn(status: String, sch_now_type: String, orderId: Int) {

        try {
            if (Constants.player_ring.isPlaying) {
                Constants.player_ring.stop()
            }
        } catch (e: java.lang.Exception) {
        }

        val notify_manager = context?.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notify_manager.cancelAll();

        context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }
        if(sch_now_type.equals("later")) {
            AcceptRejectOrderAdv(status,orderId)
        }
        else
            AcceptRejectOrder(status,orderId)

        //  val gson = Gson()
        //  val personString = gson.toJson(responseData)

    }


    fun AcceptRejectOrder(status: String, orderId: Int) {
        val apiInterface =
            APIService.create().acceptrejectorder(
                "application/json", "Bearer " + prefs.token,
                orderId.toString(), status
            )
        // Toast.makeText(this@NewOrderDetailsActivity, "api/2.0/accept_reject_order_new", Toast.LENGTH_LONG).show()


        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

//                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        if (status == "accepted") {

                            progressDialog.dialog.dismiss()
                           // if(context.intent.getStringExtra(Constants.DRIVER_FROM) == "0") {
                            if(Constants.DRIVER_FROM == "0") {

                                Toast.makeText(context,
                                    response.body()!!.message,
                                    Toast.LENGTH_SHORT).show()

//                                startActivity(
//                                    Intent(context, PrintOrderActivity::class.java)
//                                        .putExtra("response", responseData)
//                                        .putExtra("order_id", orderId)
//                                        .putExtra("driver_type", driverType)
//                                        .putExtra("order_type", intent.getStringExtra("order_type"))
//                                        .putExtra(Constants.ORDER_TYPE, intent.getStringExtra(Constants.ORDER_TYPE))
//                                        .putExtra("order_type", orderType))
//
//                                // to update add order din
//                                getAllNewOrders()

                                getNewOrderDetail(orderId)


                            }
                            else{

                             //   Log.i("=========driver_type", driverType);
//                                startActivity(
//                                    Intent(context, PrintOrderActivity::class.java)
//                                        .putExtra("response", responseData)
//                                        .putExtra("order_id", orderId)
//                                        .putExtra(Constants.ORDER_TYPE, intent.getStringExtra(Constants.ORDER_TYPE))
//                                        .putExtra("driver_type", driverType)
//                                        .putExtra("order_type", intent.getStringExtra("order_type")))

                                // to update add order din
                              //  getAllNewOrders()

                                getNewOrderDetail(orderId)

                            }
                        }

                        else if (status == "rejected"){
                           // context?.let { progressDialog.show(it, getString(R.string.loading_orders)) }

                            getAllNewOrders(true)
                        }

                        else {
                            progressDialog.dialog.dismiss()
                        }

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

                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }

    fun AcceptRejectOrderAdv(status: String, orderId: Int) {
        val apiInterface =
            APIService.create().acceptrejectorderAdvance(
                "application/json", "Bearer " + prefs.token,
                orderId.toString(), status
            )

        apiInterface.enqueue(object : Callback<ResponseAcceptRejectOrder.AcceptRejectOrder> {
            override fun onResponse(
                call: Call<ResponseAcceptRejectOrder.AcceptRejectOrder>?,
                response: Response<ResponseAcceptRejectOrder.AcceptRejectOrder>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    if (response.body()!!.status == "1") {

                        if (status == "accepted") {
                            Log.e("resp success","Order Accepted advance")
                           // finish()


                            // to update add order din
                            getAllNewOrders()
                        } else {
                            // to update add din
                            getAllNewOrders()

                            // finish()

                        }

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

                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }


    fun getNewOrderDetail(orderId: Int) {
        val apiInterface =
            APIService.create().getNewOrderDetails(
                "application/json", "Bearer " + prefs.token,
                orderId.toString()
            )


        apiInterface.enqueue(object : Callback<ResponseNewOrderDetails.NewOrderDetails> {
            override fun onResponse(
                call: Call<ResponseNewOrderDetails.NewOrderDetails>?,
                response: Response<ResponseNewOrderDetails.NewOrderDetails>?
            ) {

                if (response?.body() != null) {

                    progressDialog.dialog.dismiss()

                    var responseData = response.body()!!.data

                    if (response.body()!!.status == "1") {

                        var orderId = "" + response.body()!!.data.order_id
                        var orderType = response.body()!!.data.order_type
                        var driverType = response.body()!!.data.driver_type




                        context?.startActivity(
                            Intent(context, PrintOrderActivity::class.java)
                                .putExtra("response", responseData)
                                .putExtra("order_id", orderId)
//                                        .putExtra(Constants.ORDER_TYPE, orderType)
                                .putExtra("driver_type", driverType)
                                .putExtra("order_type", orderType)
                        )



                    } else {


                    }


                }
                //    recyclerAdapter.setMovieListItems(response.body()!!)
            }

            override fun onFailure(
                call: Call<ResponseNewOrderDetails.NewOrderDetails>?,
                t: Throwable?
            ) {

                Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT)
                    .show()

                Log.i("Exception: ", t.toString())

            }
        })

    }


}