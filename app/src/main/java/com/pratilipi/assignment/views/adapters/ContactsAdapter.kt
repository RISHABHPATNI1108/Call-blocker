package com.pratilipi.assignment.views.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pratilipi.assignment.R
import com.pratilipi.assignment.databinding.RowContactBinding
import com.pratilipi.assignment.models.ContactsModel
import com.pratilipi.assignment.views.adapters.ContactsAdapter.ContactsViewHolder
import java.util.*

@Suppress("UNCHECKED_CAST")
class ContactsAdapter(private val listener: OnBlockContactListener) : RecyclerView.Adapter<ContactsViewHolder>(), Filterable {

    private val contactsList = ArrayList<ContactsModel?>()
    private val completeContactsList = ArrayList<ContactsModel?>()

    companion object {
        private val TAG = ContactsAdapter::class.java.name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val inflater = (parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater)
        val binding: RowContactBinding = DataBindingUtil.inflate(inflater, R.layout.row_contact, parent, false)
        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        holder.bind(contactsList[position], position)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                Log.d(TAG, " constraint $constraint")
                val filteredList: MutableList<ContactsModel?> = ArrayList()
                if (constraint.isEmpty()) {
                    filteredList.addAll(completeContactsList)
                } else {
                    for (contactsModel in completeContactsList) {
                        Log.d(TAG, " constraint " + contactsModel?.phoneNumber)
                        for (phoneNumber in contactsModel?.phoneNumber!!) {
                            if (phoneNumber.contains(constraint)) {
                                filteredList.add(contactsModel)
                            }
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                contactsList.clear()
                Log.d(TAG, " publish result " + results.values)
                contactsList.addAll((results.values as Collection<ContactsModel?>))
                notifyDataSetChanged()
            }
        }
    }

    fun addAll(contacts: ArrayList<ContactsModel?>) {
        for (contactsModel in contacts) {
            add(contactsModel)
        }
    }

    private fun add(contactsModel: ContactsModel?) {
        completeContactsList.add(contactsModel)
        contactsList.add(contactsModel)
        notifyItemInserted(contactsList.size - 1)
    }

    inner class ContactsViewHolder(var binding: RowContactBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(model: ContactsModel?, position: Int) {
            binding.contact = model
            binding.ivBlock.setOnClickListener { listener.onContactBlockClicked(contactsList[position]) }
        }
    }

    interface OnBlockContactListener {
        fun onContactBlockClicked(contactsModel: ContactsModel?)
    }

}