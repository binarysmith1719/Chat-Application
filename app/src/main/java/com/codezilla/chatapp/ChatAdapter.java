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
import android.widget.LinearLayout;
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
import java.util.zip.Inflater;

public class ChatAdapter extends ListAdapter<Message,RecyclerView.ViewHolder> {
    Integer SENT = 2;
    Integer RECIEVE = 1;
    TextToSpeech tts;
    int textToSpeech_RESULT;
    private Context context;
    LayoutInflater inflator;
//    View card;
//    TextView tvDay;
    public ChatAdapter(Context context) {
//        super();
        super(DIFF_CALLBACK);
        this.context = context;
        inflator= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
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
//            Log.d("bugg"," areItemsTheSame -> "+oldItem.id+" == "+newItem.id+"*************************************");
//            if(oldItem.id.equals(""))
//                return false;
            return oldItem.id.equals(newItem.id) ;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
//            Log.d("bugg"," areContentsTheSame -> "+oldItem.id+" == "+newItem.id+"*************************************");
//            if(oldItem.date.equals(newItem.date) && oldItem.getMessage().equals(newItem.getMessage()) && oldItem.getSenderId().equals(newItem.getSenderId())){
//                return true;
//            }
            return oldItem.getSenderId().equals(newItem.getSenderId());
        }
    };

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("bugg","here onCreate--------------------------------------------------------------------"+getItemCount());
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
//        Log.d("bugg","here onBind---------------------------------------------------------------------"+position);
//        Log.d("bugg","here onBind---------------------------------------------------------------------"+getItemCount());
        Message currentMessage= getItem(position);
        boolean chatDayOk=false;
        if(position!=0){
            if(!currentMessage.chatDay.equals(getItem(position-1).chatDay)){
//                Toast.makeText(context, "llc here "+position, Toast.LENGTH_SHORT).show();
                chatDayOk=true;}
        }
        else
         chatDayOk=true;

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
            ((SendViewHolder) holder).Txttime.setText(currentMessage.getDate());
            ((SendViewHolder)holder).llc.removeAllViews();
//            if(currentMessage.firstChat!=null && currentMessage.firstChat.equals("1")){
            if(chatDayOk){
//                if(card.getParent() != null) {
//                    ((ViewGroup)card.getParent()).removeView(card); // <- fix
//                }
//                View card=inCardObj.cardlist.get(position);
//                if(card.getParent() != null) {
//                    ((ViewGroup)card.getParent()).removeView(card); // <- fix
//                }
                View card=inflator.inflate(R.layout.date_layout,null);
                TextView tvDay=card.findViewById(R.id.chtday);
                tvDay.setText(currentMessage.chatDay);
                ((SendViewHolder) holder).llc.addView(card, ((SendViewHolder) holder).llc.getChildCount());
            }



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
            ((RecieveViewHolder) holder).Txttime.setText(currentMessage.getDate());
            ((RecieveViewHolder) holder).llc.removeAllViews();
//            if(currentMessage.firstChat!=null && currentMessage.firstChat.equals("1")){
            if(chatDayOk){
//                LayoutInflater inflator= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
//                TextView chatday=card.findViewById(R.id.chtday);
//                if(card.getParent() != null) {
//                    ((ViewGroup)card.getParent()).removeView(card); // <- fix
//                }
//                View card=inCardObj.cardlist.get(position);
//                if(card.getParent() != null) {
//                    ((ViewGroup)card.getParent()).removeView(card); // <- fix
//                }
                View card=inflator.inflate(R.layout.date_layout,null);
                TextView tvDay=card.findViewById(R.id.chtday);
                tvDay.setText(currentMessage.chatDay);
                ((RecieveViewHolder) holder).llc.addView(card,((RecieveViewHolder) holder).llc.getChildCount());
            }
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
        public LinearLayout llc;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d("bugg","Viewholder_send");
            TxtSent=itemView.findViewById(R.id.txtsent);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
            llc=itemView.findViewById(R.id.llDay);
        }
    }
    class RecieveViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtReciv,Txttime,isEncrypted;
        public ImageView voice;
        public LinearLayout llc;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d("bugg","Viewholder_recieve");
            TxtReciv=itemView.findViewById(R.id.txtrecieve);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
            llc=itemView.findViewById(R.id.llDay);
        }
    }
}
