package com.pratilipi.assignment.views.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.pratilipi.assignment.R
import com.pratilipi.assignment.databinding.ActivityBlockedCallsAtivityBinding
import com.pratilipi.assignment.models.BlockedCalls
import com.pratilipi.assignment.viewModel.ContactsViewModel
import com.pratilipi.assignment.views.adapters.BlockedCallAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber

class BlockedCallsActivity : AppCompatActivity() {

    private var adapter: BlockedCallAdapter? = null
    private var binding: ActivityBlockedCallsAtivityBinding? = null
    private var viewModel: ContactsViewModel? = null

    companion object {
        private val TAG = BlockedCallsActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_blocked_calls_ativity)

        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(ContactsViewModel::class.java)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        initialiseRecyclerView()
        getDataFromViewModel()
    }

    private fun getDataFromViewModel() {
        viewModel?.blockedCall?.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())?.subscribe(object : DisposableSubscriber<List<BlockedCalls?>?>() {
            override fun onNext(blockedNumbers: List<BlockedCalls?>?) {
                Log.d(TAG, "blockNumberListReceived $blockedNumbers")
                adapter!!.addAll(blockedNumbers)
                binding?.progressBar?.visibility = View.GONE
            }

            override fun onError(t: Throwable) {
                Log.d(TAG, "blockNumberListReceived " + t.message)
                binding?.progressBar?.visibility = View.GONE
            }

            override fun onComplete() {
                Log.d(TAG, "on Complete ")
                binding?.progressBar?.visibility = View.GONE
            }
        })
    }

    private fun initialiseRecyclerView() {
        adapter = BlockedCallAdapter()
        binding?.rvContacts?.layoutManager = LinearLayoutManager(applicationContext)
        binding?.rvContacts?.adapter = adapter
        binding?.rvContacts?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}