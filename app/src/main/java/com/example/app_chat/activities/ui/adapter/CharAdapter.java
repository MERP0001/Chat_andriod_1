package com.example.app_chat.activities.ui.adapter;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app_chat.R;
import com.example.app_chat.activities.ui.modelo.ChatMessage;
import com.example.app_chat.databinding.ItemContainesSentMessageBinding;
import com.example.app_chat.databinding.ItemContainerReceivedMessageBinding;
import org.jetbrains.annotations.NotNull;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

public class CharAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> chatMessages;
    private String senderId;
    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;



    public CharAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public @NotNull RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(ItemContainesSentMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        } else {
            return new ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int i) {
        if (getItemViewType(i) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) viewHolder).setData(chatMessages.get(i));
        } else {
            ((ReceivedMessageViewHolder) viewHolder).setData(chatMessages.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public int getItemViewType(int position) {
        if (chatMessages.get(position).senderId.equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainesSentMessageBinding binding;

        SentMessageViewHolder(ItemContainesSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.dateTime);
            if (chatMessage.imageUrl == null) {
                binding.imageMessage.setVisibility(View.GONE);
            } else {
                binding.imageMessage.setVisibility(View.VISIBLE);
//                binding.imageMessage.setImageURI(Uri.parse(chatMessage.imageUrl));
                Glide.with(binding.imageMessage.getContext()).load(chatMessage.imageUrl).into(binding.imageMessage);
            }
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.message);
            binding.textDatetime.setText(chatMessage.dateTime);
            if (chatMessage.imageUrl == null) {
                binding.imageMessage.setVisibility(View.GONE);
            } else {
                binding.imageMessage.setVisibility(View.VISIBLE);
                Glide.with(binding.imageMessage.getContext()).load(chatMessage.imageUrl).into(binding.imageMessage);
            }

        }
    }



}