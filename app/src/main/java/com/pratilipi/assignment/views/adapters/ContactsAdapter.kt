package com.pratilipi.assignment.views.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.pratilipi.assignment.R;
import com.pratilipi.assignment.databinding.RowContactBinding;
import com.pratilipi.assignment.models.ContactsModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> implements Filterable {

  private static final String TAG = ContactsAdapter.class.getName();
  private ArrayList<ContactsModel> contactsList = new ArrayList<>();
  private ArrayList<ContactsModel> completeContactsList = new ArrayList<>();
  private OnBlockContactListener listener;

  public ContactsAdapter(OnBlockContactListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    assert inflater != null;
    RowContactBinding binding = DataBindingUtil.inflate(inflater, R.layout.row_contact, parent, false);
    return new ContactsViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
    holder.bind(contactsList.get(position), position);
  }

  @Override
  public int getItemCount() {
    return contactsList.size();
  }

  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        Log.d(TAG, " constraint " + constraint);
        List<ContactsModel> filteredList = new ArrayList<>();
        if (constraint == null || constraint.length() == 0) {
          filteredList.addAll(completeContactsList);
        } else {
          for (ContactsModel contactsModel : completeContactsList) {
            Log.d(TAG, " constraint " + contactsModel.getPhoneNumber());
            for (String phoneNumber : contactsModel.getPhoneNumber()) {
              if (phoneNumber.contains(constraint)) {
                filteredList.add(contactsModel);
              }
            }
          }
        }
        FilterResults filterResults = new FilterResults();
        filterResults.values = filteredList;
        return filterResults;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        contactsList.clear();
        Log.d(TAG, " publish result " + results.values);
        contactsList.addAll((Collection<? extends ContactsModel>) results.values);
        notifyDataSetChanged();
      }
    };
  }

  public void addAll(ArrayList<ContactsModel> contacts) {
    for (ContactsModel contactsModel : contacts) {
      add(contactsModel);
    }
  }

  private void add(ContactsModel contactsModel) {
    completeContactsList.add(contactsModel);
    contactsList.add(contactsModel);
    notifyItemInserted(contactsList.size() - 1);
  }

  public class ContactsViewHolder extends RecyclerView.ViewHolder {

    RowContactBinding binding;

    public ContactsViewHolder(@NonNull RowContactBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(ContactsModel model, int position) {
      binding.setContact(model);
      binding.ivBlock.setOnClickListener(v -> listener.onContactBlockClicked(contactsList.get(position)));
    }

  }

  public interface OnBlockContactListener {

    void onContactBlockClicked(ContactsModel contactsModel);

  }

}
