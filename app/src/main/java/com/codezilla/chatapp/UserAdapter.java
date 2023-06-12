package com.codezilla.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.codezilla.chatapp.ProfilePicture.ProfilePictureOperations;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class UserAdapter extends ListAdapter<AppUser,UserAdapter.ViewHolder> {
//    private String Name;
    private Context context;
//    private ArrayList<AppUser> userList ;
    private DatabaseReference mDbRef;
    Bitmap removed ;
    public UserAdapter(Context context)
    {
        super(DIFF_CALLBACK);
        Log.d("bug","got_response21");
        this.context=context;
        mDbRef= FirebaseDatabase.getInstance().getReference();
        removed=BitmapFactory.decodeResource(context.getResources(), R.drawable.userabc);
    }
    private static final DiffUtil.ItemCallback<AppUser> DIFF_CALLBACK = new DiffUtil.ItemCallback<AppUser>() {
        @Override
        public boolean areItemsTheSame(@NonNull AppUser oldItem, @NonNull AppUser newItem) {
            return oldItem.uid==newItem.uid;
        }

        @Override
        public boolean areContentsTheSame(@NonNull AppUser oldItem, @NonNull AppUser newItem) {
          return true;
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("bugg","here onCreate--------------------------------------------------------------------"+getItemCount());
        View view = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);
        return new ViewHolder(view);
    }

//    @NonNull
//    @Override
//    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d("bug","got_response2");
////       View view = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);
////
////        return new ViewHolder(view);
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("bug","got_response4");
        AppUser user = getItem(position);
        holder.nametxt.setText(user.getName().toUpperCase());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActive.chattingWith=user.getName();
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("NAME_KEY",user.getName());
                intent.putExtra("UID_KEY", user.getUid());
                intent.putExtra("NODEKEY",user.NodeKeyForDeletion);
                context.startActivity(intent);
            }
        });
        if(ProfilePictureOperations.picMap.containsKey(user.getUid())){
            holder.imgView.setImageBitmap((Bitmap) ProfilePictureOperations.picMap.get(user.getUid()));
        }else {
            holder.imgView.setImageBitmap(removed);
        }
//            mDbRef.child("ProfilePic").child(user.getUid()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    String userId=snapshot.getKey();
////                    if(userId!=user.uid)
////                        return;
//                    Log.d("Adap","user who called this ------>"+userId);
//                    Log.d("Adap","user holding the holder---->"+user.getUid()+"     name--->"+user.name);
//
//                    String picURL = snapshot.getValue(String.class);
//                    if (picURL == null || picURL.equals("")) //PIC NOT AVAILABLE
//                    {
//                        holder.imgView.setImageBitmap(removed);
//                        return;
//                    }
//                    Log.d("Adap","using glide by user-------->"+user.getUid()+"     name--->"+user.name);
//                    Glide.with(context).load(picURL).into(holder.imgView);
//                }
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {}
//            });
//        }
    }
//
//    @Override
//    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
////        Log.d("bug","got_response4");
////      AppUser user = userList.get(position);
////        holder.nametxt.setText(user.getName().toUpperCase());
////        holder.itemView.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                UserActive.chattingWith=user.getName();
////                Intent intent = new Intent(context,ChatActivity.class);
////                intent.putExtra("NAME_KEY",user.getName());
////                intent.putExtra("UID_KEY", user.getUid());
////                intent.putExtra("NODEKEY",user.NodeKeyForDeletion);
////                context.startActivity(intent);
////            }
////        });
////        if(ProfilePictureOperations.picMap.containsKey(user.getUid())){
////        holder.imgView.setImageBitmap((Bitmap) ProfilePictureOperations.picMap.get(user.getUid()));
////        }else
////            holder.imgView.setImageResource(R.drawable.userabc);
//    }

//    @Override
//    public int getItemCount() {
////        return userList.size();
//    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView nametxt ;
        private ImageView imgView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","got_response3");
            nametxt= itemView.findViewById(R.id.nametxt);
            imgView=itemView.findViewById(R.id.userPic);
        }
    }
}
