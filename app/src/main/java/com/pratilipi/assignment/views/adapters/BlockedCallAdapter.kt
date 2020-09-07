package com.pratilipi.assignment.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pratilipi.assignment.R
import com.pratilipi.assignment.databinding.RowBlockedCallBinding
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.views.adapters.BlockedCallAdapter.BlockViewHolder
import java.util.*

class BlockedCallAdapter : RecyclerView.Adapter<BlockViewHolder>() {

    private val blockedCalls = ArrayList<BlockedCalls>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val inflater = (parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val binding: RowBlockedCallBinding = DataBindingUtil.inflate(inflater, R.layout.row_blocked_call, parent, false)
        return BlockViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.bind(blockedCalls[position])
    }

    override fun getItemCount(): Int {
        return blockedCalls.size
    }

    fun addAll(contacts: List<BlockedCalls?>?) {
        for (contactsModel in contacts!!) {
            add(contactsModel)
        }
    }

    private fun add(contactsModel: BlockedCalls?) {
        if (!blockedCalls.contains(contactsModel)) {
            blockedCalls.add(contactsModel!!)
            notifyItemInserted(blockedCalls.size - 1)
        }
    }

    class BlockViewHolder(var binding: RowBlockedCallBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(number: BlockedCalls?) {
            binding.blockedNumber = number
        }
    }
}