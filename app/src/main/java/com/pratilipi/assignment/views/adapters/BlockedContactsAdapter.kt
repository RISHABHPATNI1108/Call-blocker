package com.pratilipi.assignment.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pratilipi.assignment.R
import com.pratilipi.assignment.databinding.RowBlockedContactBinding
import com.pratilipi.assignment.models.BlockedNumber
import com.pratilipi.assignment.views.adapters.BlockedContactsAdapter.BlockedContactsViewHolder
import java.util.*

class BlockedContactsAdapter(private val listener: OnUnblockContactListener) : RecyclerView.Adapter<BlockedContactsViewHolder>() {

    private val blockedNumbers = ArrayList<BlockedNumber>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockedContactsViewHolder {
        val inflater = (parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val binding: RowBlockedContactBinding = DataBindingUtil.inflate(inflater, R.layout.row_blocked_contact, parent, false)
        return BlockedContactsViewHolder(binding)
    }

    fun addAll(contacts: List<BlockedNumber?>?) {
        blockedNumbers.clear()
        notifyDataSetChanged()
        for (contactsModel in contacts!!) {
            add(contactsModel)
        }
    }

    private fun add(contactsModel: BlockedNumber?) {
        if (!blockedNumbers.contains(contactsModel)) {
            blockedNumbers.add(contactsModel!!)
            notifyItemInserted(blockedNumbers.size - 1)
        }
    }

    override fun onBindViewHolder(holder: BlockedContactsViewHolder, position: Int) {
        holder.bind(blockedNumbers[position], position)
    }

    override fun getItemCount(): Int {
        return blockedNumbers.size
    }

    inner class BlockedContactsViewHolder(var binding: RowBlockedContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(blockedNumber: BlockedNumber?, position: Int) {
            binding.contact = blockedNumber
            binding.ivBlock.setOnClickListener { listener.onContactUnblockClicked(blockedNumbers[position]) }
        }
    }

    interface OnUnblockContactListener {
        fun onContactUnblockClicked(phoneNumber: BlockedNumber?)
    }
}