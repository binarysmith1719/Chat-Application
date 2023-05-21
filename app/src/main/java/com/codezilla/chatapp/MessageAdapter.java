package com.codezilla.chatapp;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Integer SENT = 2;
    Integer RECIEVE = 1;
    private ArrayList<Message> MessageList;
    TextToSpeech tts;
    int textToSpeech_RESULT;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> messageList) {
        MessageList = messageList;
        this.context = context;
        tts= new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                    textToSpeech_RESULT=tts.setLanguage(Locale.US);
                }
            }
        });
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
                          ((SendViewHolder) holder).voice.setOnClickListener(new View.OnClickListener() {
                              @Override
                              public void onClick(View v) {
                                  speakvoice(currentMessage.getMessage());
                              }
                          });
                          if(currentMessage.getDate().equals(""))
                          ((SendViewHolder) holder).Txttime.setText("");
                          else
                              ((SendViewHolder) holder).Txttime.setText(currentMessage.getDate());
                 }
                 else
                 {
                     ((RecieveViewHolder) holder).TxtReciv.setText(currentMessage.getMessage());
                     ((RecieveViewHolder) holder).voice.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             speakvoice(currentMessage.getMessage());
                         }
                     });
                     if(currentMessage.getDate().equals(""))
                         ((RecieveViewHolder) holder).Txttime.setText("");
                     else
                         ((RecieveViewHolder) holder).Txttime.setText(currentMessage.getDate());
                 }
    }
    public void speakvoice(String s)
    {
        if(!(textToSpeech_RESULT==TextToSpeech.LANG_NOT_SUPPORTED||textToSpeech_RESULT==TextToSpeech.LANG_MISSING_DATA)){
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }
        else
        {
            Toast.makeText(context, "Cant Process", Toast.LENGTH_SHORT).show();
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
       public TextView TxtSent,Txttime;
       public ImageView voice;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","Viewholder_send");
            TxtSent=itemView.findViewById(R.id.txtsent);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
        }
    }
    class RecieveViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtReciv,Txttime;
        public ImageView voice;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","Viewholder_recieve");
            TxtReciv=itemView.findViewById(R.id.txtrecieve);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
        }
    }
}
