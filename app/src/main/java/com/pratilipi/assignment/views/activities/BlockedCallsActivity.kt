package com.pratilipi.assignment.views.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pratilipi.assignment.R;
import com.pratilipi.assignment.databinding.ActivityBlockedCallsAtivityBinding;
import com.pratilipi.assignment.models.BlockedCalls;
import com.pratilipi.assignment.viewModel.ContactsViewModel;
import com.pratilipi.assignment.views.adapters.BlockedCallAdapter;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;

public class BlockedCallsActivity extends AppCompatActivity {

  private static final String TAG = BlockedCallsActivity.class.getName();
  private BlockedCallAdapter adapter;
  private ActivityBlockedCallsAtivityBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = DataBindingUtil.setContentView(this, R.layout.activity_blocked_calls_ativity);
    ContactsViewModel viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(ContactsViewModel.class);
    adapter = new BlockedCallAdapter();

    binding.rvContacts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
    binding.rvContacts.setAdapter(adapter);
    binding.rvContacts.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Log.d(TAG, " in Block Call Activity" + viewModel);

    viewModel.getBlockedCall().observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread()).subscribe(new DisposableSubscriber<List<BlockedCalls>>() {
      @Override
      public void onNext(List<BlockedCalls> blockedNumbers) {
        Log.d(TAG, "blockNumberListReceived " + blockedNumbers);
        runOnUiThread(() -> {
          adapter.addAll(blockedNumbers);
          binding.progressBar.setVisibility(View.GONE);
        });
      }

      @Override
      public void onError(Throwable t) {
        Log.d(TAG, "blockNumberListReceived " + t.getMessage());
        binding.progressBar.setVisibility(View.GONE);
      }

      @Override
      public void onComplete() {
        Log.d(TAG, "on Complete ");
        binding.progressBar.setVisibility(View.GONE);
      }
    });
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
    }
    return super.onOptionsItemSelected(item);
  }
}