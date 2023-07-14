package com.codezilla.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ChatAdapter extends ListAdapter<Message,RecyclerView.ViewHolder> {
    Integer SENT = 2;
    Integer RECIEVE = 1;
    TextToSpeech tts;
    String senderRoom;
    int textToSpeech_RESULT;
    private Context context;
    LayoutInflater inflator;
//    View card;
//    TextView tvDay;
    public ChatAdapter(Context context,String senderRoom) {
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
        this.senderRoom=senderRoom;
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

            if(currentMessage.imageURL!=null && !currentMessage.imageURL.equals("")){
                    ((SendViewHolder)holder).imgShrd.getLayoutParams().height=850;
                    ((SendViewHolder)holder).imgShrd.getLayoutParams().width=850;
                Log.d("SBS"," map size -->"+ChatActivity.map.size());
                Log.d("SBS","MAP ---> to ImageView");
                if(ChatActivity.map.containsKey(currentMessage.id)) {
                    Log.d("SBS","contains key");
                    ((SendViewHolder) holder).imgShrd.setImageBitmap((Bitmap) ChatActivity.map.get(currentMessage.id));
                }else{
//                    Log.d("images","USING PICASSO");
                    Log.d("SBS","USING PICASSO");
                    try {
                        Glide.with(context).load(currentMessage.imageURL).listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                Toast.makeText(context, "resouce in the image  "+currentMessage.getMessage(), Toast.LENGTH_SHORT).show();
                                ((SendViewHolder) holder).imgShrd.setImageResource(R.drawable.removed);
//                                ((SendViewHolder) holder).imgShrd.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.removed));
                                return true;
                            }
                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {return false;}
                        }).into(((SendViewHolder) holder).imgShrd);
                    }catch (ClassCastException e) {((SendViewHolder) holder).imgShrd.setImageResource(R.drawable.removed);}
//                         Picasso.get().load(currentMessage.imageURL).into(((SendViewHolder) holder).imgShrd, new Callback() {
                }
            }
            else if(currentMessage.imageURL==null ||currentMessage.imageURL.equals("") ){
                ((SendViewHolder)holder).imgShrd.setImageDrawable(null);
                ((SendViewHolder)holder).imgShrd.getLayoutParams().height=0;
                ((SendViewHolder)holder).imgShrd.getLayoutParams().width=0;
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
                View card=inflator.inflate(R.layout.date_layout,null);
                TextView tvDay=card.findViewById(R.id.chtday);
                tvDay.setText(currentMessage.chatDay);
                ((RecieveViewHolder) holder).llc.addView(card,((RecieveViewHolder) holder).llc.getChildCount());
            }


            if(currentMessage.imageURL!=null && !currentMessage.imageURL.equals("")){
                ((RecieveViewHolder)holder).imgShrd.getLayoutParams().height=850;
                ((RecieveViewHolder)holder).imgShrd.getLayoutParams().width=850;
                Log.d("SBS","rec MAP ---> to ImageView");
                if(ChatActivity.map.containsKey(currentMessage.id)) {
                            ((RecieveViewHolder) holder).imgShrd.setImageBitmap((Bitmap) ChatActivity.map.get(currentMessage.id));
                }else{
//                    Log.d("images","USING PICASSO");
//                    Log.d("SBS","USING PICASSO");
                    try {Glide.with(context).load(currentMessage.imageURL).listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
//                            Toast.makeText(context, "GLIDE: failed to load xxx", Toast.LENGTH_SHORT).show();
                            ((RecieveViewHolder) holder).imgShrd.setImageResource(R.drawable.removed);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    }).into(((RecieveViewHolder) holder).imgShrd);
                    }catch (ClassCastException e){
                        ((RecieveViewHolder) holder).imgShrd.setImageResource(R.drawable.removed);
                    }
//                    Picasso.get().load(currentMessage.imageURL).into(((RecieveViewHolder) holder).imgShrd);
                }
            }
            else if(currentMessage.imageURL==null ||currentMessage.imageURL.equals("") ){
                ((RecieveViewHolder)holder).imgShrd.setImageDrawable(null);
                ((RecieveViewHolder)holder).imgShrd.getLayoutParams().height=0;
                ((RecieveViewHolder)holder).imgShrd.getLayoutParams().width=0;
//                ((RecieveViewHolder) holder).cardImg.removeAllViews();
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
        public ImageView voice,imgShrd;
        public LinearLayout llc;
        public SendViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d("bugg","Viewholder_send");
            TxtSent=itemView.findViewById(R.id.txtsent);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
            llc=itemView.findViewById(R.id.llDay);
            imgShrd=itemView.findViewById(R.id.imgShrd);
        }
    }
    class RecieveViewHolder extends RecyclerView.ViewHolder{
        public TextView TxtReciv,Txttime,isEncrypted;
        public ImageView voice,imgShrd;
        public LinearLayout llc;
        public CardView cardImg;
        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d("bugg","Viewholder_recieve");
            TxtReciv=itemView.findViewById(R.id.txtrecieve);
            voice=itemView.findViewById(R.id.voice);
            Txttime=itemView.findViewById(R.id.timex);
            isEncrypted=itemView.findViewById(R.id.isEncrypted);
            llc=itemView.findViewById(R.id.llDay);
            imgShrd=itemView.findViewById(R.id.imgShrd);
//            cardImg=itemView.findViewById(R.id.cardImg);
        }
    }
}
