package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xeatpos.databinding.RangeReportViewBinding
import com.xeatpos.response.rangereport.Orders

class RangeReportAdapter(
    val context: Context,
    val orders: List<Orders>,
    val listner: onClickCardRangeReport
) :
    RecyclerView.Adapter<RangeReportAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: RangeReportViewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Orders) {
            binding.txtOrdersDate.text = order.day
            binding.txtOrderCount.text = order.count.toString()
            binding.cardDateRange.setOnClickListener {
                listner.onClickCard(order.day)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RangeReportAdapter.ViewHolder {
        var view = LayoutInflater.from(parent.context)
        var binding = RangeReportViewBinding.inflate(view)

        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: RangeReportAdapter.ViewHolder, position: Int) {
        holder.bind(orders[position])

    }

    override fun getItemCount() = orders.size

    interface onClickCardRangeReport {
        fun onClickCard(day: String)
    }
}