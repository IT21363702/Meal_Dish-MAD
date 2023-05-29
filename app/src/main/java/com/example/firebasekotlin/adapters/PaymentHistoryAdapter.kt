package com.example.firebasekotlin.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.ui.text.substring
import androidx.compose.ui.text.toUpperCase
import androidx.recyclerview.widget.RecyclerView
import com.example.firebasekotlin.R
import com.example.firebasekotlin.models.PaymentModel

class PaymentHistoryAdapter (private val payHistList: ArrayList<PaymentModel>)
    : RecyclerView.Adapter<PaymentHistoryAdapter.ViewHolder>()
{
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentHistoryAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycleitem_payment_history, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PaymentHistoryAdapter.ViewHolder, position: Int) {
        val currentPayEntry = payHistList[position]

        val amount = currentPayEntry.amount.toString().dropLast(2)
        val subAmount = currentPayEntry.amount.toString().takeLast(2)

        holder.itemPaymentAmount.text = "${amount}.${subAmount} ${currentPayEntry.currency.toString().uppercase()}"
        holder.itemPaymentDateTime.text = "Payment on ${currentPayEntry.time.toString()}"
        holder.itemPaymentDesc.text = currentPayEntry.description.toString()
//        holder.itemPaymentUid.text = "uid: ${currentPayEntry.uid.toString()}"
    }

    override fun getItemCount(): Int {
        return payHistList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val itemPaymentAmount:TextView = itemView.findViewById(R.id.item_payment_amount)
        val itemPaymentDateTime:TextView = itemView.findViewById(R.id.item_payment_date)
        val itemPaymentDesc:TextView = itemView.findViewById(R.id.item_payment_description)
    }
}