package com.xeatpos.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xeatpos.R
import com.xeatpos.data.ResponseWalletTransaction
import com.xeatpos.databinding.ListNewOrderBinding
import com.xeatpos.databinding.WalletTransactionViewBinding
import com.xeatpos.model.CategoryModel

class WalletTransactionAdapter(var context: Context, var list: MutableList<ResponseWalletTransaction.Data>) :
    RecyclerView.Adapter<WalletTransactionAdapter.ViewHolder>() {
    class ViewHolder(val binding: WalletTransactionViewBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding = WalletTransactionViewBinding.inflate(view)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.txtOrderWallet.text = context.getString(R.string.invoice_id)+" "+list[position].order_id
        holder.binding.txtAmountWallet.text = list[position].amount
        holder.binding.txtDateWallet.text = list[position].created_at

    }

    override fun getItemCount() = list.size
}