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
import com.x64technology.linex.utils.Constants;
import com.x64technology.linex.utils.diffUtils.ContactDiffUtil;
import com.x64technology.linex.interfaces.ContactProfile;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    Context context;
    ContactProfile contactProfile;
    List<Contact> contacts = new ArrayList<>();

    public ContactAdapter(Context context) {
        this.context = context;
        this.contactProfile = (ContactProfile) context;
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
        holder.contactBinding.connectionCode.setText(contact.userId);
        holder.contactBinding.reqType.setText(contact.reqType);

        System.out.println("we are chaning");

        switch (contact.reqType) {
            case Constants.REQUEST_ACCEPTED:
                holder.itemView.setOnClickListener(view -> contactProfile.onContactClicked(contact));
                break;
            case Constants.REQUEST_RECEIVED:
                holder.contactBinding.btnReqAccpt.setVisibility(View.VISIBLE);
                holder.contactBinding.btnReqReject.setVisibility(View.VISIBLE);

                holder.contactBinding.btnReqAccpt.setOnClickListener(view -> contactProfile.onRequestAcceptClicked(contact));
                holder.contactBinding.btnReqReject.setOnClickListener(view -> contactProfile.onRequestRejectClicked(contact));
                break;
            case Constants.REQUEST_SENT:
                holder.contactBinding.btnReqCancel.setVisibility(View.VISIBLE);
                holder.contactBinding.btnReqCancel.setOnClickListener(view -> contactProfile.onRequestCancelClicked(contact));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public void setContacts(List<Contact> newCont) {
        ContactDiffUtil contDiff = new ContactDiffUtil(contacts, newCont);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(contDiff);

        contacts.clear();
        contacts.addAll(newCont);

        result.dispatchUpdatesTo(this);
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        LayoutContactBinding contactBinding;
        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            contactBinding = LayoutContactBinding.bind(itemView);
        }
    }
}
