package com.xeatpos.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xeatpos.data.ResponseNewOrders
import com.xeatpos.databinding.ListNewOrderBinding
import com.xeatpos.order.orders.DriverAssignActivity
import com.xeatpos.order.orders.NewOrderDetailsActivity
import com.xeatpos.utils.Constants

class NewOrderAdapter(var context: Context, var list: MutableList<ResponseNewOrders.Data>) :
    RecyclerView.Adapter<NewOrderAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListNewOrderBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ListNewOrderBinding.inflate(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.textOrderIDNewOrder.text = ""+list[position].order_id
        holder.binding.textOrderByNewOrder.text = list[position].orderby
        holder.binding.textOrderDateNewOrder.text = list[position].order_at
        holder.binding.textOrderTypeNewOrder.text = list[position].order_type
        holder.binding.textOrderTotalNewOrder.text = list[position].total_amount
        if(list[position].schedule_type.equals("later"))
        {
            holder.binding.schLl.visibility = View.VISIBLE
            holder.binding.schDateTime.text = list[position].schedule_date+", "+list[position].schedule_time
        }
        else{
            holder.binding.schLl.visibility = View.GONE
        }

        if(list[position].driver_from == "0"){
            if(list[position].status == "4") {
                holder.binding.textWaitingForDriver.visibility = View.VISIBLE
                //holder.binding.layoutAcceptReject.visibility = View.GONE
            }else{
                holder.binding.textWaitingForDriver.visibility = View.GONE
                //holder.binding.layoutAcceptReject.visibility = View.VISIBLE
            }
        }else{
            holder.binding.textWaitingForDriver.visibility = View.GONE
        }


        holder.binding.layoutRootNewOrder.setOnClickListener {

            if(list[position].status == "4"){
                //waiting for driver status showing
                context.startActivity(Intent(context, NewOrderDetailsActivity::class.java)
                    .putExtra(Constants.ORDER_ID, "" + list[position].order_id)
                    .putExtra(Constants.ORDER_TYPE, "" + list[position].order_type)
                    .putExtra("order_type", "" + list[position].driver_from)
                    .putExtra("sch_type", "")
                    .putExtra("order_status", "4")
                )
                //updating code end

            }else if(list[position].status == "15" || list[position].status == "6"){
            //}else if(list[position].status == "6"){
                if(list[position].order_type == "Pickup") {
                    context.startActivity(Intent(context, DriverAssignActivity::class.java)
                            .putExtra("order_id", "" + list[position].order_id)
                            .putExtra("order_type", "" + list[position].driver_from))
                }

            }
            else if(list[position].status == "17")
            {
                context.startActivity(Intent(context, NewOrderDetailsActivity::class.java)
                    .putExtra(Constants.ORDER_ID, "" + list[position].order_id)
                    .putExtra(Constants.ORDER_TYPE, "" + list[position].order_type)
                    .putExtra("order_type", "" + list[position].driver_from)
                    .putExtra("sch_type", list[position].schedule_type)
                    .putExtra("order_status", "17")
                )
            }
            else {
                context.startActivity(Intent(context, NewOrderDetailsActivity::class.java)
                        .putExtra(Constants.ORDER_ID, "" + list[position].order_id)
                        .putExtra(Constants.ORDER_TYPE, "" + list[position].order_type)
                        .putExtra("order_type", "" + list[position].driver_from)
                        .putExtra("sch_type", "")
                        .putExtra("order_status", "0")
                )
            }

           // context.startActivity(Intent(context, PrintOrderActivity::class.java).putExtra(Constants.ORDER_ID, ""+list[position].order_id))


            /* context.startActivity(
                 Intent(context, PrintSlipNewActivity::class.java)
             )*/

        }

    }

    override fun getItemCount() = list.size
}