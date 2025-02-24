package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xeatpos.data.ResponsePickedOrders
import com.xeatpos.data.ResponseReadyOrdersList
import com.xeatpos.databinding.ListPickedOrderBinding
import com.xeatpos.databinding.ListReadyOrderBinding
import com.xeatpos.model.CategoryModel

class PickedOrderAdapter(var context: Context, var list: MutableList<ResponsePickedOrders.CompleteOrder>,
                        var onitemClick : ItemClick) :
    RecyclerView.Adapter<PickedOrderAdapter.ViewHolder>() {
    class ViewHolder(val binding: ListReadyOrderBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = ListReadyOrderBinding.inflate(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context).load(list[position].driver_pic).into(holder.binding.imageDriverReadyList)

        holder.binding.textOrderIdReady.text = ""+list[position].order_id
        holder.binding.textOrderByReady.text = list[position].orderby
        holder.binding.textOrderAtReady.text = list[position].order_at
        holder.binding.textOrderTypeReady.text = list[position].order_type
        holder.binding.textOrderTotalReady.text = list[position].total_amount
        holder.binding.textDriverNameReady.text = list[position].driver_name
        holder.binding.textDriverPhoneReady.text = list[position].driver_phone


        if(list[position].order_type == "Pickup"){
            holder.binding.layoutDriverReady.visibility = View.GONE
        }else{
            holder.binding.layoutDriverReady.visibility = View.VISIBLE
            if(list[position].driver_type == "0"){
                holder.binding.layoutDriverReady.visibility = View.GONE
            }else {
                holder.binding.layoutDriverReady.visibility = View.VISIBLE
            }
        }


        holder.binding.btnPickedUpReady.visibility = View.GONE

        holder.binding.layoutTopPreparing.setOnClickListener {

            onitemClick.itemClick(position)

            /*   context.startActivity(Intent(context, PreparingOrderDetailsActivity::class.java).putExtra(
                   "order_id", ""+list[position].order_id))*/


        }

    }

    override fun getItemCount() = list.size

    open interface ItemClick {
        fun itemClick(position: Int)
    }
}