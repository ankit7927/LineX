package com.x64technology.linex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.x64technology.linex.R;
import com.x64technology.linex.databinding.LayoutContactBinding;
import com.x64technology.linex.models.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    Context context;
    List<Contact> contacts = new ArrayList<>();

    public ContactAdapter(Context context, List<Contact> contacts1) {
        this.context = context;
        contacts.addAll(contacts1);
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.contactBinding.contactName.setText(contact.name);
        holder.contactBinding.contactUsername.setText(contact.username);
        holder.contactBinding.requestType.setText(contact.reqType);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        LayoutContactBinding contactBinding;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactBinding = LayoutContactBinding.bind(itemView);
        }
    }
}