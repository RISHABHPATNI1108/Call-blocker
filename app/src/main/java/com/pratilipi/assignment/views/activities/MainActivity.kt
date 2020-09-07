package com.pratilipi.assignment.views.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.pratilipi.assignment.R;
import com.pratilipi.assignment.databinding.ActivityMainBinding;
import com.pratilipi.assignment.models.BlockedNumber;
import com.pratilipi.assignment.models.ContactsModel;
import com.pratilipi.assignment.viewModel.ContactsViewModel;
import com.pratilipi.assignment.views.adapters.BlockedContactsAdapter;
import com.pratilipi.assignment.views.adapters.ContactsAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class MainActivity extends AppCompatActivity implements ContactsAdapter.OnBlockContactListener, BlockedContactsAdapter.OnUnblockContactListener {

  private static final int REQUEST_READ_CONTACTS = 1001;
  private static final String TAG = MainActivity.class.getName();
  private ContactsViewModel viewModel;
  private ActivityMainBinding binding;
  private ContactsAdapter contactsAdapter;
  private BlockedContactsAdapter blockedContactsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
    viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ContactsViewModel.class);
    binding.setVm(viewModel);
    contactsAdapter = new ContactsAdapter(this);
    binding.rvContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    blockedContactsAdapter = new BlockedContactsAdapter(this);
    binding.rvContacts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    binding.rvContacts.setAdapter(contactsAdapter);
    Handler handler = new Handler();
    handler.postDelayed(() -> {
      if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
        viewModel.getContacts();
        viewModel.getBlockedNumbers().observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableSubscriber<List<BlockedNumber>>() {
          @Override
          public void onNext(List<BlockedNumber> blockedNumbers) {
            Log.d(TAG, "blockNumberListReceived " + blockedNumbers);
            runOnUiThread(() -> blockedContactsAdapter.addAll(blockedNumbers));
          }

          @Override
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onComplete() {
            Log.d(TAG, "on Complete ");
          }
        });
      } else {
        requestPermission();
      }
    }, 500);

    binding.etPhoneNumber.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        contactsAdapter.getFilter().filter(s);
      }
    });

    observeDataFromViewModel();
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {

    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.activity_main_menu, menu);
    if (menu != null) {
      if (menu.findItem(R.id.nav_view_blocked_calls) != null) {
        if (binding.etPhoneNumber.getVisibility() == View.VISIBLE) {
          menu.findItem(R.id.nav_view_blocked_calls).setIcon(R.drawable.ic_person_blocked);
        } else {
          menu.findItem(R.id.nav_view_blocked_calls).setIcon(R.drawable.ic_unblocked);
        }
      }
    }
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    switch (item.getItemId()) {
      case R.id.nav_see_blocked_contacts:
        if (binding.etPhoneNumber.getVisibility() == View.VISIBLE) {
          binding.etPhoneNumber.setVisibility(View.GONE);
          binding.btnBlock.setVisibility(View.GONE);
          binding.rvContacts.setAdapter(blockedContactsAdapter);
        } else {
          binding.etPhoneNumber.setVisibility(View.VISIBLE);
          binding.btnBlock.setVisibility(View.VISIBLE);
          binding.rvContacts.setAdapter(contactsAdapter);
        }
        invalidateOptionsMenu();
        return true;
      case R.id.nav_view_blocked_calls:
        Intent intent = new Intent(MainActivity.this, BlockedCallsActivity.class);
        startActivity(intent);
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  protected void requestPermission() {
    if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
        Manifest.permission.READ_CONTACTS)) {


      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE},
            REQUEST_READ_CONTACTS);
      } else {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG, Manifest.permission.READ_PHONE_STATE},
            REQUEST_READ_CONTACTS);
      }


    } else {
      // show UI part if you want here to show some rationale !!!
      // TODO : show a dialog to tell users, why this permission is needed
      Log.d(TAG , " rational UI ");
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode,
                                         @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
    if (requestCode == REQUEST_READ_CONTACTS) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

        viewModel.getContacts();

      } else {

        Toast.makeText(MainActivity.this, R.string.provide_contacts_permission, Toast.LENGTH_SHORT).show();

        // permission denied,Disable the
        // functionality that depends on this permission.
      }
    }
  }

  private void observeDataFromViewModel() {

    viewModel.getMessageErrorData().observe(this, s -> {
      if (!TextUtils.isEmpty(s)) {
        Snackbar.make(binding.getRoot(), s, Snackbar.LENGTH_LONG).show();
      }
    });

    viewModel.getContactsData().observe(this, contactsModels -> {
      if (contactsModels != null) {
        if (binding.progressBar.getVisibility() == View.VISIBLE) {
          binding.progressBar.setVisibility(View.GONE);
        }

        contactsAdapter.addAll(contactsModels);
      }
    });

  }


  @Override
  public void onContactBlockClicked(ContactsModel contactsModel) {
    Log.d(TAG, "onContactBlockClicked " + contactsModel);
    if (contactsModel != null) {
      viewModel.blockContact(contactsModel);
    }
  }

  @Override
  public void onContactUnblockClicked(BlockedNumber phoneNumber) {
    if (phoneNumber != null) {
      Log.d(TAG, "unblock " + phoneNumber);
      viewModel.unBlockContact(phoneNumber);
    }
  }
}