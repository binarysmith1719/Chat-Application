package com.codezilla.chatapp;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.codezilla.chatapp.RsaEncryption.MyKeyPair;
import com.codezilla.chatapp.RsaEncryption.RsaAlgo;
import com.codezilla.chatapp.RsaEncryption.RsaEncryptionHandler;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<Message,RecyclerView.ViewHolder> {
    Integer SENT = 2;
    Integer RECIEVE = 1;
    TextToSpeech tts;
    int textToSpeech_RESULT;
    private Context context;

    public ChatAdapter(Context context) {
//        super();
        super(DIFF_CALLBACK);
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
    private static final DiffUtil.ItemCallback<Message> DIFF_CALLBACK = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            Log.d("bugg"," areItemsTheSame -> "+oldItem.id+" == "+newItem.id+"*************************************");
//            if(oldItem.id.equals(""))
//                return false;

            return oldItem.id.equals(newItem.id);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            Log.d("bugg"," areContentsTheSame -> "+oldItem.id+" == "+newItem.id+"*************************************");
//            if(oldItem.date.equals(newItem.date) && oldItem.getMessage().equals(newItem.getMessage()) && oldItem.getSenderId().equals(newItem.getSenderId())){
//                return true;
//            }
            return true;

        }
    };
    //    public ChatAdapter(Context context, ArrayList<Message> messageList) {
//        super();
//        MessageList = messageList;
//        this.context = context;
//        tts= new TextToSpeech(context.getApplicationContext(), new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if(status==TextToSpeech.SUCCESS){
//                    textToSpeech_RESULT=tts.setLanguage(Locale.US);
//                }
//            }
//        });
//    }
    public Message getNoteAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("bugg","here onCreate--------------------------------------------------------------------"+getItemCount());
        if(viewType==1)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_card,parent,false);
            return new RecieveViewHolder(view);
        }
        else
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_layout_card,parent,false);
            return new SendViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d("bugg","here onBind---------------------------------------------------------------------"+position);

        Log.d("bugg","here onBind---------------------------------------------------------------------"+getItemCount());
        Message currentMessage= getItem(position);

        String isEncrypted="";
        if(currentMessage.publickey.equals("1")){
            isEncrypted="e ";
        }

        if(SendViewHolder.class==holder.getClass())
        {
            String text;
            if(currentMessage.getMessage()==null){
                text="SECURITY KEY CHANGED";
//                             ((SendViewHolder) holder).TxtSent.setTextColor(Color.parseColor("#2A2F4F"));
            }
            else{
                text= currentMessage.getMessage();
//                              ((SendViewHolder) holder).TxtSent.setTextColor(Color.parseColor("#FFFFFFFF"));
            }
            ((SendViewHolder) holder).isEncrypted.setText(isEncrypted);
            ((SendViewHolder) holder).TxtSent.setText(text);
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
            String text;
            if(currentMessage.getMessage()==null){
                text="SECURITY KEY CHANGED";
//                         ((RecieveViewHolder) holder).TxtReciv.setTextColor(Color.parseColor("#F07900"));
            }
            else{
                text= currentMessage.getMessage();
//                         ((RecieveViewHolder) holder).TxtReciv.setTextColor(Color.parseColor("#FFFFFFFF"));
            }

            ((RecieveViewHolder) holder).isEncrypted.setText(isEncrypted);
            ((RecieveViewHolder) holder).TxtReciv.setText(text);
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
        Message currentMessage = getItem(position);
        if(FirebaseAuth.getInstance().getUid().equals(currentMessage.getSenderId()))
        {
            return SENT;
        }
        else
            return RECIEVE;
    }

    class SendViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtSent,Txttime,isEncrypted;
        public ImageView voice;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bugg","Viewholder_send");
            TxtSent=itemView.findViewById(R.id.txtsent);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
        }
    }
    class RecieveViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtReciv,Txttime,isEncrypted;
        public ImageView voice;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bugg","Viewholder_recieve");
            TxtReciv=itemView.findViewById(R.id.txtrecieve);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
        }
    }
}
