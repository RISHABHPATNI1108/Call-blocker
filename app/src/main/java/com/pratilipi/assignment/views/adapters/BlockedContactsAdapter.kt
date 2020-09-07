package com.pratilipi.assignment.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pratilipi.assignment.R;
import com.pratilipi.assignment.databinding.RowBlockedContactBinding;
import com.pratilipi.assignment.models.BlockedNumber;

import java.util.ArrayList;
import java.util.List;

public class BlockedContactsAdapter extends RecyclerView.Adapter<BlockedContactsAdapter.BlockedContactsViewHolder> {

  private ArrayList<BlockedNumber> blockedNumbers = new ArrayList<>();
  private OnUnblockContactListener listener;

  public BlockedContactsAdapter(OnUnblockContactListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public BlockedContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    assert inflater != null;
    RowBlockedContactBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_blocked_contact, parent, false);
    return new BlockedContactsAdapter.BlockedContactsViewHolder(binding);
  }

  public void addAll(List<BlockedNumber> contacts) {
    blockedNumbers.clear();
    notifyDataSetChanged();
    for (BlockedNumber contactsModel : contacts) {
      add(contactsModel);
    }
  }

  private void add(BlockedNumber contactsModel) {
    if (!blockedNumbers.contains(contactsModel)) {
      blockedNumbers.add(contactsModel);
      notifyItemInserted(blockedNumbers.size() - 1);
    }

  }

  @Override
  public void onBindViewHolder(@NonNull BlockedContactsViewHolder holder, int position) {
    holder.bind(blockedNumbers.get(position), position);
  }

  @Override
  public int getItemCount() {
    return blockedNumbers.size();
  }

  public class BlockedContactsViewHolder extends RecyclerView.ViewHolder {

    RowBlockedContactBinding binding;

    public BlockedContactsViewHolder(@NonNull RowBlockedContactBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(BlockedNumber blockedNumber, int position) {
      binding.setContact(blockedNumber);
      binding.ivBlock.setOnClickListener(v -> listener.onContactUnblockClicked(blockedNumbers.get(position)));
    }

  }

  public interface OnUnblockContactListener {

    void onContactUnblockClicked(BlockedNumber phoneNumber);

  }


}
