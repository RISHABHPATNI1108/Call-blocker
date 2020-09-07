package com.pratilipi.assignment.views.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.pratilipi.assignment.R
import com.pratilipi.assignment.databinding.ActivityMainBinding
import com.pratilipi.assignment.models.BlockedNumber
import com.pratilipi.assignment.models.ContactsModel
import com.pratilipi.assignment.viewModel.ContactsViewModel
import com.pratilipi.assignment.views.adapters.BlockedContactsAdapter
import com.pratilipi.assignment.views.adapters.BlockedContactsAdapter.OnUnblockContactListener
import com.pratilipi.assignment.views.adapters.ContactsAdapter
import com.pratilipi.assignment.views.adapters.ContactsAdapter.OnBlockContactListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import java.util.*

open class MainActivity : AppCompatActivity(), OnBlockContactListener, OnUnblockContactListener {

    private var viewModel: ContactsViewModel? = null
    private var binding: ActivityMainBinding? = null
    private var contactsAdapter: ContactsAdapter? = null
    private var blockedContactsAdapter: BlockedContactsAdapter? = null

    companion object {
        private const val REQUEST_READ_CONTACTS = 1001
        private val TAG = MainActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider.AndroidViewModelFactory(application).create(ContactsViewModel::class.java)
        binding?.vm = viewModel

        contactsAdapter = ContactsAdapter(this)
        binding?.rvContacts?.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        blockedContactsAdapter = BlockedContactsAdapter(this)
        binding?.rvContacts?.layoutManager = LinearLayoutManager(applicationContext)
        binding?.rvContacts?.adapter = contactsAdapter

        val handler = Handler()
        handler.postDelayed({
            if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                viewModel?.contacts
                viewModel?.blockedNumbers?.subscribeOn(AndroidSchedulers.mainThread())?.observeOn(Schedulers.io())?.subscribe(object : DisposableSubscriber<List<BlockedNumber?>?>() {
                    override fun onNext(blockedNumbers: List<BlockedNumber?>?) {
                        Log.d(TAG, "blockNumberListReceived $blockedNumbers")
                        runOnUiThread { blockedContactsAdapter!!.addAll(blockedNumbers) }
                    }

                    override fun onError(t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onComplete() {
                        Log.d(TAG, "on Complete ")
                    }
                })
            } else {
                requestPermission()
            }
        }, 500)

        binding?.etPhoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                contactsAdapter!!.filter.filter(s)
            }
        })

        observeDataFromViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        if (menu!!.findItem(R.id.nav_view_blocked_calls) != null) {
            if (binding!!.etPhoneNumber.visibility == View.VISIBLE) {
                menu.findItem(R.id.nav_view_blocked_calls).setIcon(R.drawable.ic_person_blocked)
            } else {
                menu.findItem(R.id.nav_view_blocked_calls).setIcon(R.drawable.ic_unblocked)
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_see_blocked_contacts -> {
                if (binding!!.etPhoneNumber.visibility == View.VISIBLE) {
                    binding!!.etPhoneNumber.visibility = View.GONE
                    binding!!.btnBlock.visibility = View.GONE
                    binding!!.rvContacts.adapter = blockedContactsAdapter
                } else {
                    binding!!.etPhoneNumber.visibility = View.VISIBLE
                    binding!!.btnBlock.visibility = View.VISIBLE
                    binding!!.rvContacts.adapter = contactsAdapter
                }
                invalidateOptionsMenu()
                return true
            }
            R.id.nav_view_blocked_calls -> {
                val intent = Intent(this@MainActivity, BlockedCallsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun requestPermission() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE),
                        REQUEST_READ_CONTACTS)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE),
                        REQUEST_READ_CONTACTS)
            }
        } else {
            // show UI part if you want here to show some rationale !!!
            // TODO : show a dialog to tell users, why this permission is needed
            Log.d(TAG, " rational UI ")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel?.contacts
            } else {
                Toast.makeText(this@MainActivity, R.string.provide_contacts_permission, Toast.LENGTH_SHORT).show()

                // permission denied,Disable the
                // functionality that depends on this permission.
            }
        }
    }

    private fun observeDataFromViewModel() {

        viewModel!!.getMessageErrorData().observe(this, { s: String? ->
            if (!TextUtils.isEmpty(s)) {
                Snackbar.make(binding!!.root, s!!, Snackbar.LENGTH_LONG).show()
            }
        })

        viewModel!!.contactsData.observe(this, { contactsModels: ArrayList<ContactsModel?>? ->
            if (contactsModels != null) {
                if (binding!!.progressBar.visibility == View.VISIBLE) {
                    binding!!.progressBar.visibility = View.GONE
                }
                contactsAdapter!!.addAll(contactsModels)
            }
        })
    }

    override fun onContactBlockClicked(contactsModel: ContactsModel?) {
        Log.d(TAG, "onContactBlockClicked $contactsModel")
        if (contactsModel != null) {
            viewModel!!.blockContact(contactsModel)
        }
    }

    override fun onContactUnblockClicked(phoneNumber: BlockedNumber?) {
        if (phoneNumber != null) {
            Log.d(TAG, "unblock $phoneNumber")
            viewModel!!.unBlockContact(phoneNumber)
        }
    }

}