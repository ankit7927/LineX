package com.x64technology.linex.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.x64technology.linex.R;
import com.x64technology.linex.databinding.LayoutMessageMineBinding;
import com.x64technology.linex.databinding.LayoutMessageOtherBinding;
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

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).isMine) return 0;
        else return 1;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType == 0 ? R.layout.layout_message_mine : R.layout.layout_message_other, parent, false);
        return new MessageAdapter.MessageViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        if (message.isMine) {
            holder.mineBinding.messageMine.setText(message.content);
            holder.mineBinding.msgTimeMine.setText(message.time);
        } else {
            holder.otherBinding.messageOther.setText(message.content);
            holder.otherBinding.msgTimeOther.setText(message.time);
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
        LayoutMessageMineBinding mineBinding;
        LayoutMessageOtherBinding otherBinding;

        public MessageViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 0) mineBinding = LayoutMessageMineBinding.bind(itemView);
            else otherBinding = LayoutMessageOtherBinding.bind(itemView);
        }
    }
}
