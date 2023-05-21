package com.codezilla.chatapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
//    private String Name;
    private Context context;
    private ArrayList<AppUser> userList ;
    public UserAdapter(Context context,ArrayList<AppUser> userList)
    {
        Log.d("bug","got_response21");
        this.context=context;
        this.userList=userList;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("bug","got_response2");
       View view = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        Log.d("bug","got_response4");
      AppUser user = userList.get(position);
        holder.nametxt.setText(user.getName().toUpperCase());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserActive.chattingWith=user.getName();
                Intent intent = new Intent(context,ChatActivity.class);
                intent.putExtra("NAME_KEY",user.getName());
                intent.putExtra("UID_KEY", user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        private TextView nametxt ;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("bug","got_response3");
            nametxt= itemView.findViewById(R.id.nametxt);

        }
    }
}
