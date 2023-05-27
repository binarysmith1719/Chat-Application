package com.codezilla.chatapp.ViewModel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.codezilla.chatapp.Message;
import com.codezilla.chatapp.Repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class TaskViewModel extends AndroidViewModel implements Repository.onFirestoreTaskComplete {
    public MutableLiveData<ArrayList<Message>> messageData= new MutableLiveData<ArrayList<Message>>();
    public Repository respository=new Repository(this);
    public boolean hasBeenCalledOnce=false;    // MARK THE CALL OF getModelData() FUNCTION
    public TaskViewModel(@NonNull Application application) {
        super(application);
        Log.d("tag"," TaskModel Initializing ");
    }

    public void probeRepository(String senderroom){
        if(!hasBeenCalledOnce) {
//            Log.d("OnCancel", " Probe Repository ");
            respository.getModelData(senderroom);     // THIS METHOD WILL BE CALLED ONCE FOR EACH CHAT ACTIVITY
//            Log.d("tagon", "returning the data form TaskViewModel");
            hasBeenCalledOnce=true;
        }
    }

    @Override
    public void TaskDataLoaded(ArrayList<Message> messageList) {
//        Log.d("tag",messageList.size()+" Got list_data through Interface");
        messageData.setValue((ArrayList<Message>)messageList);
    }

//    @Override
//    public void onError(Exception e) {}
}
