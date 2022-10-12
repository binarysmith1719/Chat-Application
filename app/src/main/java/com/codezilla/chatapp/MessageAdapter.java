package com.codezilla.chatapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Integer SENT = 2;
    Integer RECIEVE = 1;
    private ArrayList<Message> MessageList;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> messageList) {
        MessageList = messageList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("bug","here onCreate");
         if(viewType==1)
         {
           View view = LayoutInflater.from(context).inflate(R.layout.message_layout_card,parent,false);
           return new RecieveViewHolder(view);
         }
         else
         {
             View view = LayoutInflater.from(context).inflate(R.layout.sent_layout_card,parent,false);
             return new SendViewHolder(view);
         }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                 Log.d("bug","here onBind");
                 Message currentMessage= MessageList.get(position);
                 if(SendViewHolder.class==holder.getClass())
                 {
                          ((SendViewHolder) holder).TxtSent.setText(currentMessage.getMessage());
                 }
                 else
                 {
                     ((RecieveViewHolder) holder).TxtReciv.setText(currentMessage.getMessage());
                 }
    }

    @Override
    public int getItemViewType(int position) {
        Message currentMessage = MessageList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(currentMessage.getSenderId()))
        {
            return SENT;
        }
        else
            return RECIEVE;
    }

    @Override
    public int getItemCount() {
        return MessageList.size();
    }

    class SendViewHolder extends RecyclerView.ViewHolder{
       public TextView TxtSent;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","Viewholder_send");
            TxtSent=itemView.findViewById(R.id.txtsent);
        }
    }
    class RecieveViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtReciv;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","Viewholder_recieve");
            TxtReciv=itemView.findViewById(R.id.txtrecieve);
        }
    }
}
