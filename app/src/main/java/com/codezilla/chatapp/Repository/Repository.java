package com.codezilla.chatapp.Repository;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codezilla.chatapp.ChatActivity;
import com.codezilla.chatapp.MainActivity;
import com.codezilla.chatapp.Message;
import com.codezilla.chatapp.RsaEncryption.RsaEncryptionHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class Repository implements ChatActivity.onBackPressListener {
    private static final String TAG = "Repository";
    private onFirestoreTaskComplete onFirestoreTaskCompleteRef;
    private DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Message> chatListx;
    public DbAsyncTask asyncTask=null;
    public boolean isListAvailable=true;  //SEMAPHORE  ---> SYNCHRONIZING [childAdditionListener,backgroundTask,stimulating the ViewModel through interface]
    int onlyonce=0;                       //MAKES SURE THAT THE childAdditionListener DO NOT +ADD ANY DATA TO THE LIST ON INITIALIZATION
    public Repository(onFirestoreTaskComplete onFirestoreTaskCompleteRef) {
//        Log.d("tag"," Repository Initializing ");
        this.onFirestoreTaskCompleteRef = onFirestoreTaskCompleteRef;
        chatListx=new ArrayList<>();
        ChatActivity.ref=this;
    }
    public void getModelData(String senderroom) // THIS METHOD WILL BE CALLED ONCE FOR EACH CHAT ACTIVITY
    {
        if(asyncTask!=null) {
            asyncTask.cancel(true);
            chatListx.clear();
        }
        onlyonce=0;
//        Log.d("OnCancel","BackPress chkr chatList size => "+chatListx.size());
        mDbRef.child("chats").child(senderroom).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d("tag"," onDateChangeId -> "+Thread.currentThread().getId());
                    if (chatListx.size() < snapshot.getChildrenCount()) {
                       asyncTask = (DbAsyncTask) new DbAsyncTask().execute(snapshot);
                    }
                    else
                        onlyonce=1;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        onChildAddition(senderroom);
    }

    public void onChildAddition(String senderroom)
    {
        Log.d(TAG,"onChildAddition ^^^^^^^^^ called ");

        mDbRef.child("chats").child(senderroom).child("messages").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(onlyonce==1) {
//                    Log.d(TAG,"%%%%%%%%%%%%%-%% ON THREAD %%%%%% {"+(count++)+" } %%%%%%%---%%% ");
                            while(!isListAvailable);
                            isListAvailable=false;
                            //EXCLUSIVE LOCK ACQUIRED
//                            Log.d("tagon","ON DATA ADDED aquired the SEMAPHORE Background-> "+Thread.currentThread().getId());
                                Message msgobj = snapshot.getValue(Message.class);
                                if (msgobj.publickey.equals("1")) {
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                      String decryptedMessage = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
                                      msgobj.setMessage(decryptedMessage);
                                   }
                                }
                                chatListx.add(msgobj);
                            //EXCLUSIVE LOCK ACQUIRED
                            isListAvailable=true;
                            ChatActivity.flag=0;// TO ALLOW THE RECYCLER VIEW TO SCROLL TO THE END
                    callingInterface();
                }
                else{
//                    Log.d("tagon","&&&&&&&&&&-&& ONLY ONCE &&&& {"+(count++)+" } &&&&&&&&---&&& ");
                    onlyonce=1;}
//                Log.d("tag"," Child Added ");
//                if (semaphore == 0) {
//                    Message msg = snapshot.getValue(Message.class);
//                    if (msg.publickey.equals("1")) {
//                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                            String msgx = RsaEncryptionHandler.decryptMessage(msg.getMessage());
//                            msg.setMessage(msgx);
//                        }
//                    }
//                    chatListx.add(msg);
//                    callingInterface();
//                onlyonce=1;
//                }
//                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
    int cnt=0;
    public void callingInterface()
    {
        while(!isListAvailable);
        isListAvailable=false;
//        //EXCLUSIVE LOCK ACQUIRED
//        for(int i=chatListx.size()-1;i>=0;i--) {
//            Log.d(TAG,"On calling interface ^^-^-^-^-^-^^^^^^^^^^^^^^"+chatListx.get(i).getMessage()+" || i->"+i+" ||cnt-> "+cnt++);
//        }
//        Log.d(TAG,"Inteface to ViewModel called ");
        onFirestoreTaskCompleteRef.TaskDataLoaded(chatListx);
        //EXCLUSIVE LOCK RELEASED
        isListAvailable=true;
    }


    @Override
    public void onBackPressByUser() {
        if(asyncTask!=null)
        {
//            Log.d("OnCancel"," chatList size => "+chatListx.size());
            asyncTask.cancel(true);
//            chatListx.clear();
        }
    }



    public  class DbAsyncTask extends AsyncTask<DataSnapshot,Void,ArrayList<Message>>
    {
//        Repository reference;
//        DatabaseReference mDbRef;
//        ArrayList<Message> chatlistOfAsyncTask;
//        String room;
        private DbAsyncTask(){ Log.d("tag"," DbAsyncTask Initializing  and its id -> "+Thread.currentThread().getId());}

        @Override
        protected ArrayList<Message> doInBackground(DataSnapshot... snapshots) {

            Log.d(TAG," doInBackground Async_task_thread id -> "+Thread.currentThread().getId());
            Log.d(TAG," doInBackground Async_task_thread id -> "+Thread.currentThread().getId()+" list size =>"+chatListx.size());
            Log.d(TAG," doInBackground Async_task_thread id -> "+Thread.currentThread().getId()+" dataShot size =>"+snapshots[0].getChildrenCount());

            ArrayList<Message> chatlistBg = new ArrayList<>();

            ArrayList<Message> unDecList = new ArrayList<>();
            for(DataSnapshot snap:snapshots[0].getChildren()) {
                Message msgobj = snap.getValue(Message.class);
                unDecList.add(msgobj);
            }


            int iniLoadAmount=20;   //LOADING RECENT 10 MESSAGES   ( initial load amount )
            int unDecMessageSize=unDecList.size();
            int count=unDecMessageSize;
            while(unDecMessageSize>0)
            {
                int initial=unDecMessageSize-iniLoadAmount;

                if(initial<0)
                initial=0;

                for(int i=initial;i<unDecMessageSize;i++)
                {

//                    Log.d(TAG," for loop i (val) -> "+i);
                    Message msgobj = unDecList.get(i);
                    String msgx = "";
                    if (msgobj.publickey.equals("1")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                          msgx = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
                          msgobj.setMessage(msgx);
                        }
                    }
                    chatlistBg.add(msgobj);
                    count--;
//                    Log.d(TAG," (inthe loop in doinBack) chatlist Size -> "+chatlistBg.size());
//                    Log.d(TAG," (inthe loop in doinBack) remaining unencrypted -> "+count);
                }
                while(!isListAvailable);
                isListAvailable=false;
                //EXCLUSIVE LOCK ACQUIRED
//                      int availableSize=chatListx.size();
//                      for(int i=0;i<availableSize;i++) {
//                         chatlistBg.add(chatListx.get(i));
//                      }
//                      chatListx.clear();
                      for(int i=chatlistBg.size()-1;i>=0;i--) {
////                      for(int i=0;i<chatlistBg.size();i++){
//                          Log.d(TAG,"^ Inserting in chatListx ^^^^^^^^^^^^^^^^^^^^^^^^^^^"+chatlistBg.get(i).getMessage()+"  i= "+i);
                         chatListx.add(0,chatlistBg.get(i));
                      }
                //EXCLUSIVE LOCK RELEASED
                isListAvailable=true;
                publishProgress();
                unDecMessageSize=initial;
                chatlistBg.clear();
//                break;
            }
//            publishProgress();
            return chatlistBg;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            callingInterface();
//            Log.d(TAG,"from ON Progress ********* chatlist Size ******** -> "+chatListx.size());
//            semaphore=0;
//            if(onlyonce==0) {
//                onChildAddition();
//                onlyonce=1;
//            }
        }
//        @Override
//        protected void onProgressUpdate(ArrayList<Message>... values) {
//            super.onProgressUpdate(values);
//            ArrayList<Message> list=new ArrayList<>();
//            for(int i=0;i<values[0].size();i++)
//                list.add(values[0].get(i));
//
//            Log.d("tag"," chatlist Size ******** -> "+list.size());
//            if(list.size()>0) {
//                for (int i = 0; i < chatListx.size(); i++) {
//                    Message msg = chatListx.get(i);
//                    list.add(msg);
//                }
//                chatListx = list;
//                callingInterface();
//            }
//        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(ArrayList<Message> messages) {
            super.onPostExecute(messages);
//            Log.d(TAG,"onPostExecute Async_task_thread id -> "+Thread.currentThread().getId());
//            chatListx=messages;
//            callingInterface();
        }

    }
    public interface onFirestoreTaskComplete {
        void TaskDataLoaded(ArrayList<Message> messageList);
    }
}
