package com.pratilipi.assignment.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pratilipi.assignment.R;
import com.pratilipi.assignment.databinding.RowBlockedCallBinding;
import com.pratilipi.assignment.models.BlockedCalls;

import java.util.ArrayList;
import java.util.List;

public class BlockedCallAdapter extends RecyclerView.Adapter<BlockedCallAdapter.BlockViewHolder> {

  private ArrayList<BlockedCalls> blockedCalls = new ArrayList<>();

  @NonNull
  @Override
  public BlockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    assert inflater != null;
    RowBlockedCallBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_blocked_call, parent, false);
    return new BlockViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull BlockViewHolder holder, int position) {
    holder.bind(blockedCalls.get(position));
  }

  @Override
  public int getItemCount() {
    return blockedCalls.size();
  }

  public void addAll(List<BlockedCalls> contacts) {
    for (BlockedCalls contactsModel : contacts) {
      add(contactsModel);
    }
  }

  private void add(BlockedCalls contactsModel) {
    if (!blockedCalls.contains(contactsModel)) {
      blockedCalls.add(contactsModel);
      notifyItemInserted(blockedCalls.size() - 1);
    }
  }

  public static class BlockViewHolder extends RecyclerView.ViewHolder {

    RowBlockedCallBinding binding;

    public BlockViewHolder(@NonNull RowBlockedCallBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(BlockedCalls number) {
      binding.setBlockedNumber(number);
    }

  }

}
