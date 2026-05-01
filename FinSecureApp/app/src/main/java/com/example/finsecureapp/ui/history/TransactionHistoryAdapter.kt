package com.example.finsecureapp.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.finsecureapp.data.remote.dto.TransactionHistoryItem
import com.example.finsecureapp.databinding.ItemTransactionBinding

class TransactionHistoryAdapter(
    private var items: List<TransactionHistoryItem> = emptyList()
) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(
        private val binding: ItemTransactionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: TransactionHistoryItem) {
            binding.tvAmount.text = "Amount: ${item.amount}"
            binding.tvType.text = "Type: ${item.type}"
            binding.tvStatus.text = "Status: ${item.status}"
            binding.tvSender.text = "From: ${item.senderAccount.accountNumber}"
            binding.tvReceiver.text = "To: ${item.receiverAccount.accountNumber}"
            binding.tvDate.text = "Date: ${item.createdAt}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<TransactionHistoryItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}