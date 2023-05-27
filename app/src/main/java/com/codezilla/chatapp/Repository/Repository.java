package com.codezilla.chatapp.Repository;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.codezilla.chatapp.Message;
import com.codezilla.chatapp.RsaEncryption.RsaEncryptionHandler;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class Repository {
    private onFirestoreTaskComplete onFirestoreTaskCompleteRef;
    private DatabaseReference mDbRef = FirebaseDatabase.getInstance().getReference();
    private ArrayList<Message> chatListx;
    public int messageCounter=0;
    public boolean isListAvailable=true;   //SEMAPHORE
    public String senderrm;
    int count=0;
    public Repository(onFirestoreTaskComplete onFirestoreTaskCompleteRef) {
        Log.d("tag"," Repository Initializing ");
        this.onFirestoreTaskCompleteRef = onFirestoreTaskCompleteRef;
        chatListx=new ArrayList<>();
    }
    public void getModelData(String senderroom)
    {
//        senderrm=senderroom;
        mDbRef.child("chats").child(senderroom).child("messages").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("tag"," onDateChangeId -> "+Thread.currentThread().getId());
                    if (chatListx.size() != snapshot.getChildrenCount()) {
                        new DbAsyncTask().execute(snapshot);
                    }
//              int counter=0;
//              chatlist.clear();
//              for(DataSnapshot snap:snapshot.getChildren()) {
//              if(counter>=messageCounter){
//              Message msgobj = snap.getValue(Message.class);
//              String msgx = "";
//              if (msgobj.publickey.equals("1")) {
//              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//              msgx = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
//              msgobj.setMessage(msgx);
//              }
//              }
//              chatlist.add(msgobj);
//              }
//              counter++;
//              }
//              messageCounter=counter;
//              onFirestoreTaskCompleteRef.TaskDataLoaded(chatList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        onChildAddition(senderroom);
    }
    int onlyonce=0;
    String chld="messages/-NWIqWDceZa7khN22HZU";
    int flag=0;
    public void onChildAddition(String senderroom)
    {
        Log.d("tag","onChildAddition ^^^^^^^^^ called ");

        mDbRef.child("chats").child(senderroom).child("messages").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(onlyonce==1) {
                    Log.d("tagon","%%%%%%%%%%%%%-%% ON THREAD %%%%%% {"+(count++)+" } %%%%%%%---%%% ");
//                    new DbOnChildAddition().execute(snapshot);
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                            while(!isListAvailable);
                            isListAvailable=false;
                            //EXCLUSIVE LOCK ACQUIRED
                            Log.d("tagon","ON DATA ADDED aquired the SEMAPHORE Background-> "+Thread.currentThread().getId());
                                Message msgobj = snapshot.getValue(Message.class);
                                if (msgobj.publickey.equals("1")) {
                                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                      String decryptedMessage = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
                                      msgobj.setMessage(decryptedMessage);
                                   }
                                }
                                chatListx.add(msgobj);
                                callingInterface();
                            //EXCLUSIVE LOCK ACQUIRED
                            isListAvailable=true;
//                        }
//                    }).start();

                }
                else{                Log.d("tagon","&&&&&&&&&&-&& ONLY ONCE &&&& {"+(count++)+" } &&&&&&&&---&&& ");
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
        for(int i=chatListx.size()-1;i>=0;i--) {
            Log.d("bugg doInBackground","On calling interface ^^-^-^-^-^-^^^^^^^^^^^^^^"+chatListx.get(i).getMessage()+" || i->"+i+" ||cnt-> "+cnt++);
        }
        Log.d("tag","Inteface to ViewModel called ");
        onFirestoreTaskCompleteRef.TaskDataLoaded(chatListx);
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

            Log.d("tag"," doInBackground Async_task_thread id -> "+Thread.currentThread().getId());
            Log.d("tag"," doInBackground Async_task_thread id -> "+Thread.currentThread().getId()+" list size =>"+chatListx.size());
            Log.d("tag"," doInBackground Async_task_thread id -> "+Thread.currentThread().getId()+" dataShot size =>"+snapshots[0].getChildrenCount());

//            ArrayList<Message> chatlistBg = chatListx;
            ArrayList<Message> chatlistBg = new ArrayList<>();

//            for(int i=0;i<10;i++)
//            {
//                Log.d("tag","In for loop count = "+i+"    Thread id => "+Thread.currentThread().getId());
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }

//            int counter=0;
//            for(DataSnapshot snap:snapshots[0].getChildren()) {
//                if(counter>=messageCounter){
//                Message msgobj = snap.getValue(Message.class);
//                String msgx = "";
//                if (msgobj.publickey.equals("1")) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        msgx = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
//                        msgobj.setMessage(msgx);
//                    }
//                }
//                chatlistBg.add(msgobj);
//                }
//                counter++;
//            }
//            messageCounter=counter;
//            chatListx=chatlistBg;

            //point 2
            ArrayList<Message> unDecList = new ArrayList<>();
            for(DataSnapshot snap:snapshots[0].getChildren()) {
                Message msgobj = snap.getValue(Message.class);
                Log.d("tag"," chatlist Size -> "+chatlistBg.size());
                unDecList.add(msgobj);
            }

//          long unDecMessageSize=snapshots[0].getChildrenCount();

            int iniLoadAmount=15;   //LOADING RECENT 10 MESSAGES   ( initial load amount )
            int unDecMessageSize=unDecList.size();
            int count=unDecMessageSize;
            while(unDecMessageSize>0)
            {
                int initial=unDecMessageSize-iniLoadAmount;

                if(initial<0)
                initial=0;

                for(int i=initial;i<unDecMessageSize;i++)
                {

                    Log.d("tag"," for loop i (val) -> "+i);
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
                    Log.d("bugg"," in the loop in doinBack chatlist Size -> "+chatlistBg.size());
                    Log.d("bugg"," in the loop in doinBack remaining unencrypted -> "+count);
                }
                while(!isListAvailable);
                isListAvailable=false;
                //EXCLUSIVE LOCK ACQUIRED
                      int availableSize=chatListx.size();
                      for(int i=0;i<availableSize;i++) {
                         chatlistBg.add(chatListx.get(i));
                      }
                      chatListx.clear();
//                      for(int i=chatlistBg.size()-1;i>=0;i--) {
                      for(int i=0;i<chatlistBg.size();i++){
                          Log.d("bugg doInBackground","^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"+chatlistBg.get(i).getMessage()+"");
                         chatListx.add(chatlistBg.get(i));
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
            Log.d("bugg","*********************************** chatlist Size ******** -> "+chatListx.size());
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
        protected void onPostExecute(ArrayList<Message> messages) {
            super.onPostExecute(messages);
            Log.d("tag","onPostExecute Async_task_thread id -> "+Thread.currentThread().getId());
//            chatListx=messages;
//            callingInterface();
        }

    }
    public interface onFirestoreTaskComplete {
        void TaskDataLoaded(ArrayList<Message> messageList);
    }

    public class DbOnChildAddition extends AsyncTask<DataSnapshot,Void,Void>{

        @Override
        protected Void doInBackground(DataSnapshot... snapshots) {
            Message msgobj = snapshots[0].getValue(Message.class);
            Log.d("tagon","ON DATA ADDED do_in_Background id -> "+Thread.currentThread().getId());
            while(!isListAvailable);
            isListAvailable=false;
            //EXCLUSIVE LOCK ACQUIRED
            Log.d("tagon","ON DATA ADDED aquired the SEMAPHORE Background-> "+Thread.currentThread().getId());
            if (msgobj.publickey.equals("1")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String decryptedMessage = RsaEncryptionHandler.decryptMessage(msgobj.getMessage());
                    msgobj.setMessage(decryptedMessage);
                }
            }
            chatListx.add(msgobj);
            //EXCLUSIVE LOCK ACQUIRED
            isListAvailable=true;
            Log.d("tagon","ON DATA ADDED released the SEMAPHORE Background-> "+Thread.currentThread().getId());

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            callingInterface();
        }
    }
}
