package com.x64technology.linex.adapters;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.x64technology.linex.R;
import com.x64technology.linex.databinding.LayoutMessageBinding;
import com.x64technology.linex.models.Message;
import com.x64technology.linex.utils.diffUtils.MessageDiffUtil;

import java.util.List;

public class MessageAdapter  extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    Context context;
    List<Message> messages;

    public MessageAdapter(Context context, List<Message> messages1) {
        this.context = context;
        this.messages = messages1;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_message, parent, false);
        return new MessageAdapter.MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.messageBinding.msgContent.setText(message.content);
        holder.messageBinding.msgDate.setText(message.time);

        if (message.from.equals("")) {
            holder.messageBinding.layout.setGravity(Gravity.END);
            holder.messageBinding.msgCard.setCardElevation(2);
        } else {
            holder.messageBinding.layout.setGravity(Gravity.START);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public void setMessages(List<Message> newMessages) {
        MessageDiffUtil messageDiffUtil = new MessageDiffUtil(messages, newMessages);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(messageDiffUtil);

        messages.addAll(newMessages);

        result.dispatchUpdatesTo(this);
    }


    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        LayoutMessageBinding messageBinding;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageBinding = LayoutMessageBinding.bind(itemView);
        }
    }
}
