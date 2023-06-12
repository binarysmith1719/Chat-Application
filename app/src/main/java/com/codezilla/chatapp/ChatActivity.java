package com.codezilla.chatapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RenderNode;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.CaseMap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.codezilla.chatapp.ImageCompression.ImageCompress;

import com.codezilla.chatapp.ImageCompression.ImageCompressionCompletionListener;
import com.codezilla.chatapp.Notification.APIService;
import com.codezilla.chatapp.Notification.Client;
import com.codezilla.chatapp.Notification.Data;
import com.codezilla.chatapp.Notification.MyResponse;
import com.codezilla.chatapp.Notification.NotificationSender;
import com.codezilla.chatapp.Notification.Token;
import com.codezilla.chatapp.Notification.UserPreferences;
import com.codezilla.chatapp.ProfilePicture.ProfilePictureOperations;
import com.codezilla.chatapp.RsaEncryption.MyKeyPair;
import com.codezilla.chatapp.RsaEncryption.RsaEncryptionHandler;
import com.codezilla.chatapp.ViewModel.TaskViewModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Bidi;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity  implements ImageCompressionCompletionListener {
    private static final String TAG2 = "BIG_BUG";
    private static final String TAG = "ChatActivity";
    private Toolbar tlbr;
    private RelativeLayout relativeLayout;
    private RecyclerView rview;
    private EditText edtx;
    private ImageView imgv,imgShr;
    public  ProgressBar pgbr;
    public  ProgressBar pgbrChat;
    private ChatAdapter msgAdapter;
    private ArrayList<Message> chatList;
    private String senderroom;
    private String recieverroom;
    private String recieverNodeKey;
    private TaskViewModel taskViewModel;
    private DatabaseReference mDbRef;

    //USED IN IMAGE PROCESSING ---------------------------->>
    public static Map map=new HashMap();
    public static boolean isStoringImage;
    public StoreImagesToStorage StoreImgRef;
    public StorageReference mStoreRef;
    Uri imageUri;
    String imageUrl;
    StorageTask mStoragetask;  //USED TO KEEP CHECK ON A UPLOADING TASK. CHECKS IF AN UPLOADING TASK IS COMPLETE OR NOT
    public Boolean selectedImage=false; //CHECKS IF IMAGE HAS BEEN SELECTED
    //----------------------------------------------------->>
    public static  onBackPressListener ref=null;
    private APIService apiService;
    public String recieverPublicKey="";

    public static boolean backPressed= false; //TO CHECK IF BACK BUTTON IS PRESSED

    Handler mainHandler = new Handler();
    public static int flag=0; //ALLOWS TO SCROLL RECYCLER VIEW TO LAST POSITION ONLY RECEIVER REPLIES OR USER ENDS NEW MESSAGES
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //ACTION BAR SETUP
        tlbr=findViewById(R.id.actionbar);
        setSupportActionBar(tlbr);
        //Color.parseColor("#FF0000")
        ColorDrawable cd= new ColorDrawable(getResources().getColor(R.color.black));
//        getSupportActionBar().setTitle(reciever);
        getSupportActionBar().setBackgroundDrawable(cd);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView title= findViewById(R.id.name);
//        Log.d(TAG2,"backpressed ->"+backPressed);
        backPressed=false;
        flag=0;
        UserActive.isActive=true;
        isStoringImage=false;
        apiService= Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();
        String reciever = intent.getStringExtra("NAME_KEY");
        String Recieverid = intent.getStringExtra("UID_KEY");
        recieverNodeKey=intent.getStringExtra("NODEKEY");
        UserActive.nodeDeletionKey=recieverNodeKey;

        //Changing status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));
        }
        title.setText(reciever);
//        title.setText("subham kumar nandy developer");
        if(ProfilePictureOperations.picMap.containsKey(Recieverid));
        {
            ImageView imageView=findViewById(R.id.profilePic);
            if((Bitmap)ProfilePictureOperations.picMap.get(Recieverid)!=null)
            imageView.setImageBitmap((Bitmap) ProfilePictureOperations.picMap.get(Recieverid));
        }
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mStoreRef= FirebaseStorage.getInstance().getReference("uploads");
        //GETTING RECEIVERS PUBLIC KEY
        getRecieverPublicKey(Recieverid);

        //GETTING SENDERROOM AND RECIEVERROOM
        String Senderid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        senderroom = Senderid + Recieverid;
        recieverroom = Recieverid + Senderid;
        //INITIALIZING IMAGE TO STORAGE CLASS
        StoreImgRef = new StoreImagesToStorage(this,senderroom);

        relativeLayout=findViewById(R.id.mainLayout);
        edtx= findViewById(R.id.edt_write);
        pgbr=findViewById(R.id.pgbr);
        pgbrChat=findViewById(R.id.pgbrChat);
        pgbrChat.setVisibility(View.VISIBLE);
        imgv= findViewById(R.id.img_send);
        rview= findViewById(R.id.rvchat);
        rview.setHasFixedSize(true);
        LinearLayoutManager llm=new LinearLayoutManager(this);
//        llm.setStackFromEnd(true);
//        llm.setReverseLayout(true);
        rview.setLayoutManager(llm);
        chatList = new ArrayList<Message>();
        Log.d("bug","here1");
        msgAdapter=new ChatAdapter(this,getExternalFilesDir(null)+"/"+senderroom);
//        msgAdapter.submitList(chatList);
//        msgAdapter = new MessageAdapter(this,chatList);
        rview.setAdapter(msgAdapter);
//        rview.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(ChatActivity.this, "Touched the recyclerview"+(count), Toast.LENGTH_SHORT).show();
//                count++;
//                return true;
//            }
//        });

        //****************** Populating RecyclerView ***************
        taskViewModel= new ViewModelProvider(this).get(TaskViewModel.class);      //NO NEW OBJECT FOR VIEWMODEL IS CREATED WHEN LIFECYCLE CHANGES
        taskViewModel.probeRepository(senderroom);
        taskViewModel.messageData.observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messageList) {
//                Toast.makeText(ChatActivity.this, "on chatActivity", Toast.LENGTH_SHORT).show();
//                Log.d("Repository",messageList.size()+" observed list size");
//                maxMsgId=Integer.MAX_VALUE;
//                card.insertLayout(messageList.size());
//                Log.d("Repository","messageList.Size ->"+messageList.size()+"    adaptersize"+msgAdapter.getItemCount());
                if (messageList.size()>msgAdapter.getItemCount()) {
                    chatList = new ArrayList<>(messageList);
                    storingImages(chatList);
//                    for (int i = 0; i < chatList.size(); i++) {
//                        Log.d(TAG, "chatList[" + i + "] = " + chatList.get(i));
//                    }
                    msgAdapter.submitList(chatList, new Runnable() {   //THIS CALLBACK IS CALLED WHEN THE
                        @Override
                        public void run() {
                            if (flag == 0) {
                                rview.scrollToPosition(msgAdapter.getItemCount() - 1);
                                flag = 1;
                            }
                        }
                    });
//                    if (flag == 0) {
//                        rview.scrollToPosition(msgAdapter.getItemCount());
////                    flag=1;
//                    }
                }
                pgbrChat.setVisibility(View.INVISIBLE);
            }
        });
        //**********************************************************

        //*****************ADDING SWIPE TO THE TEXTS****************

//        MessageSwipeController mgc=new MessageSwipeController(this, new MessageSwipeController.SwipeControllerActions() {
//            @Override
//            public void showReplyUI(int var1) {
//                Toast.makeText(ChatActivity.this, "YAAY SWIPED", Toast.LENGTH_LONG).show();
//            }
//        });
//        new ItemTouchHelper(mgc).attachToRecyclerView(rview);


//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//                return false;
//
//            @Override
//            public RecyclerView.ViewHolder chooseDropTarget(@NonNull RecyclerView.ViewHolder selected, @NonNull List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
//                super.chooseDropTarget(selected, dropTargets, curX, curY);
//                return  selected;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            }
//        }).attachToRecyclerView(rview);
        //**********************************************************

        //****************** Getting thus application build date***********
        String startingdate="2023-05-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");    //sdf is SimpleDateFormat Object
        Date date = null;
        try {date = sdf.parse(startingdate);}                                //date from string to --> Date Format}
        catch (Exception e)
        { e.printStackTrace();}
        long starttime = date.getTime();                                     //total time till this date from 01/01/1970 [in ms].
        //*****************************************************************
        //******************* Updating Chatroom ***************************
          imgv.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {

//                  Log.d("bug","here2");
//                  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                  imm.hideSoftInputFromWindow(relativeLayout.getWindowToken(), 0);
                  String msg = edtx.getText().toString();
                                              //temp
//                  new Thread(new Runnable() {             //temp
//                      @Override                           //temp
//                      public void run() {                 //temp
//                          int cntr=1001;
//                      for(int i=0;i<1000;i++) {

//                          msg = "" + (++cntr);
                  if(msg.equals(""))
                    return;
                  flag=0;

                          Calendar c = Calendar.getInstance();
                          int hr = c.get(Calendar.HOUR_OF_DAY);
                          int min = c.get(Calendar.MINUTE);
                          String sendingtime = hr + ":" + min + "";
                          long crnttime = System.currentTimeMillis();
                          Date currentDate = new Date(crnttime);
                          crnttime = crnttime - starttime;

                          //CONVERTING CURRENT DATE TO STRING FORMAT
                          SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yy");    //sdf is SimpleDateFormat Object
                          String stringCurrDate = sdf2.format(currentDate);
                          String id = "" + crnttime;
//                  Toast.makeText(ChatActivity.this, stringCurrDate, Toast.LENGTH_SHORT).show();
                         if(imageUrl!=null && !imageUrl.equals(""))
                         storeOutgoingImg(id,imageUri);


                  Message msgObject = new Message(msg, Senderid, sendingtime, id);
                          msgObject.chatDay = stringCurrDate;
                          if (UserPreferences.getInstance(ChatActivity.this).isEncryptionEnabled()) {//WHEN ENCRYPTION IS ON
                              if (recieverPublicKey == null) {
                                  Toast.makeText(ChatActivity.this, "Cannot encrypt as reciever is not updated", Toast.LENGTH_LONG).show();
                                  return;
                              }
                              Handler handler = new Handler(getMainLooper());
                              handler.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      Message msgObject2 = new Message(msg, Senderid, sendingtime, id); //This will be stored in SenderRoom

                                      String m = msg;
                                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                          //for reciever
                                          m = RsaEncryptionHandler.encryptMessageForReciever(msg, recieverPublicKey);
                                          if (m == null) {
                                              Toast.makeText(ChatActivity.this, "Too BIG to ENCRYPT", Toast.LENGTH_SHORT).show();
                                              return;
                                          }
                                          msgObject.setMessage(m);
                                          msgObject.setPublickey("1");
                                          msgObject.chatDay = stringCurrDate;

                                          //for  sender
                                          m = RsaEncryptionHandler.encryptMessageForSender(msg);
                                          msgObject2.setMessage(m);
                                          msgObject2.setPublickey("1");
                                          msgObject2.chatDay = stringCurrDate;
                                          if(imageUrl!=null && !imageUrl.equals("")){
                                              msgObject.imageURL=imageUrl;
                                              msgObject2.imageURL=imageUrl;
                                              imageUrl="";
                                          }

                                      }
                                      edtx.setText("");
                                      mDbRef.child("chats").child(senderroom).child("messages").push().setValue(msgObject2).
                                              addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void unused) {
                                                      Log.d("bug", "here3");
                                                      mDbRef.child("chats").child(recieverroom).child("messages").push().setValue(msgObject);
                                                  }
                                              });
                                  }
                              });

                          } else { // WHEN ENCRYPTION IS OFF
                              if(imageUrl!=null && !imageUrl.equals("")){
                                  msgObject.imageURL=imageUrl;
                                  imageUrl="";
                              }
                              new Thread(new Runnable() {
                                  @Override
                                  public void run() {
                                      mDbRef.child("chats").child(senderroom).child("messages").push().setValue(msgObject).
                                              addOnSuccessListener(new OnSuccessListener<Void>() {
                                                  @Override
                                                  public void onSuccess(Void unused) {
                                                      Log.d("bug", "here3");
                                                      mDbRef.child("chats").child(recieverroom).child("messages").push().setValue(msgObject);
                                                  }
                                              });
                                  }
                              }).start();
                              edtx.setText("");
                          }
//                          try {
//                              Thread.sleep(1000);
//                          } catch (InterruptedException e) {
//                              e.printStackTrace();
//                          }
//                      }
//                      }                     //temp
//                  }).start();               //temp
               //***************  Sending the Notification ******************************************
               new Thread(new Runnable() {
                   @Override
                   public void run() {
                       mDbRef.child("Token").child(Recieverid).addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               String reciever_token = snapshot.getValue(String.class);
                               Sending_Notification(reciever_token, UserDetails.uname, msg);
                           }
                           @Override
                           public void onCancelled(@NonNull DatabaseError error) {
                           }
                       });
                   }
               }).start();
               //*************XXXXXX Sending Notifiacation XXXXXX************************************
              }
          });
        //*****************************************************************

        //************************CHOOSING FILE****************************
        imgShr=findViewById(R.id.img_shr);
        imgShr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mStoragetask!=null &&  mStoragetask.isInProgress() || selectedImage==true){
//                    Toast.makeText(ChatActivity.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }
                else{
//                    Toast.makeText(ChatActivity.this, "open file chooser", Toast.LENGTH_SHORT).show();
                    openFileChooser();
                   }
            }
        });
    }
    public void ScrollingToEnd()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("SKS","thread id of scrollingToEnd "+Thread.currentThread().getId());
            }
        });
    }

    public void Sending_Notification(String usertoken, String title, String message)
    {
           Data data = new Data(title, message);
           NotificationSender sender = new NotificationSender(data, usertoken);
           apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    if (response.body().success != 1) {
//                        Toast.makeText(SendNotif.this, "Failed ", Toast.LENGTH_LONG);
                    }
                }
            }
            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.chatmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        rview.scrollToPosition(msgAdapter.getItemCount()-1);

        if(item.getItemId()==R.id.delfriend)
        {
            mDbRef.child("Friends").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(UserActive.nodeDeletionKey).removeValue();
            finish();
        }
        else {
            if(!UserPreferences.getInstance(this).isEncryptionEnabled()) {

                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setTitle("Encryption");
                dialog1.setMessage("Encryption will securely encrypt the messages with a protected key.                  Note : Encrypted messages will be lost if you uninstall the app");
                dialog1.setIcon(R.drawable.encrypticon2);
                dialog1.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                             UserPreferences.getInstance(ChatActivity.this).enableEncryption();
                    }
                });
                dialog1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog1.show();
            }
            else
            {
                AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                dialog1.setTitle("Encryption");
                dialog1.setMessage("Are you sure you want disable the encryption ?");
                dialog1.setIcon(R.drawable.encrypticon2);
                dialog1.setPositiveButton("disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                           UserPreferences.getInstance(ChatActivity.this).disableEncryption();
                    }
                });
                dialog1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog1.show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
    void getRecieverPublicKey(String RecieverId)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDbRef.child("PublicKeys").child(RecieverId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String recieverPubKey = snapshot.getValue(String.class);
//                        Toast.makeText(ChatActivity.this, ""+recieverPubKey, Toast.LENGTH_LONG).show();
                        recieverPublicKey=recieverPubKey;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }).start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        UserActive.isActive=false;
    }
    @Override
    protected void onResume() {
        super.onResume();
        UserActive.isActive=true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ChatActivity.map.clear();
        backPressed=true;
        if(ref != null){
            ref.onBackPressByUser();// CANCELING THE BACKGROUND TASK
        }
    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        ChatActivity.map.clear();
//
//        if(ref != null){
//            ref.onBackPressByUser();// CANCELING THE BACKGROUND TASK
//        }
//    }

    //THIS LISTENER WILL STOP THE BACKGROUND WORK ON BACKPRESS
    public interface onBackPressListener{
        public void onBackPressByUser();
    }


    //IMAGE SHARING
    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            selectedImage=true;
            pgbr.setVisibility(View.VISIBLE);
            pgbr.setProgress(5);
            imageUri=data.getData();
            try {
                new ImageCompress().getCompressedImage(this,getContentResolver().openInputStream(imageUri),this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            Toast.makeText(ChatActivity.this, "Got data", Toast.LENGTH_SHORT).show();
//            try {
//                Toast.makeText(this, "Img String", Toast.LENGTH_SHORT).show();
//                Log.d("SBS","converting to string");
//                InputStream is= getContentResolver().openInputStream(imageUri);
//                Log.d("SBS","got stream");
//                Bitmap btmp = BitmapFactory.decodeStream(is);
//                Log.d("SBS","decoding stream for bitmap");
//                String sImage=ByteToString(btmp);
//                Log.d("SBS","sImage-->"+sImage);
////                Toast.makeText(this, "Img String :"+sImage, Toast.LENGTH_SHORT).show();
//            } catch (FileNotFoundException e) {
////                e.printStackTrace();
//                Toast.makeText(this, "Cannot put in inputstream", Toast.LENGTH_SHORT).show();
//            }
//            UploadFile();
//            Picasso.get().load(imageUri).into(img);
        }
    }
    public String getFileExtension(Uri uri)
    {
        ContentResolver cr= getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    // UPLOADING IMAGES TO FIREBASE STORAGE
    private void UploadFile() {
        if(imageUri!=null)
        {
//            imageUri.toString();
//            String string=imageUri.toString();
//            Log.d("ImageCompression",""+string);
//
//            File file = new File(imageUri.getPath());//create path from uri
//            File filex=new File(getExternalFilesDir(null),"compressed.jpg");
//            try {
//                InputStream is= getContentResolver().openInputStream(imageUri);
////                Log.d("SBS","storing new image");
//                Bitmap bitmap = BitmapFactory.decodeStream(is);
////                ChatActivity.map.put(fname,bitmap);
////                Log.d("SBS","inserted new image in the MAP");
////            ChatActivity.isStoringImage=true;//--------------------------------
//                FileOutputStream outputStream=null;
//                try {outputStream=new FileOutputStream(filex);
//                } catch (FileNotFoundException e) {}
//                Log.d("images","File Stream Created");
////                Bitmap bitmap2=BitmapFactory.decodeStream((InputStream) new URL(Url).getContent());
////            Log.d("imagesx","bitmap Created Created");
////                ChatActivity.map.put(fname,bitmap);
//                bitmap.compress(Bitmap.CompressFormat.JPEG,20,outputStream);
////            Log.d("imagesx","bitmap compressed");
//                outputStream.flush();
//                outputStream.close();
////            ChatActivity.isStoringImage=false;//-------------------------------
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            final String[] split = file.getPath().split(":");//split the path.
//            String path= split[1];
//            Log.d("ImageCompression",""+path);
//
//            File file2 = new File(imageUri.getPath());//create path from uri
//            Log.d("ImageCompression","+++++++++++++"+imageUri.getPath());
////
//            String x="/storage/emulated/0/Android/data/com.codezilla.chatapp/files/x3v6WXiQk5NdT4jvtSP6aEQdHMk1LkzORVcU1INqpQpyTPp9Kq1nO2k2/3259386947.jpg";
////            Log.d("ImageCompression",""+getExternalFilesDir(null));
//            String x2="image/12342";
//            new ImageCompression(this,ChatActivity.this).execute(x2);
            StorageReference fileRef=mStoreRef.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
//            Toast.makeText(ChatActivity.this, "image != null", Toast.LENGTH_SHORT).show();
            mStoragetask=fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            Handler handler=new Handler();
//                            handler.postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//                                    pgbr.setProgress(0);
//                                }
//                            },500);
                            Toast.makeText(ChatActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                            pgbr.setVisibility(View.VISIBLE);
                            pgbr.setProgress(80);
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
//                                    Toast.makeText(ChatActivity.this, "image != null", Toast.LENGTH_SHORT).show();
//                                    image img= new image(fname.getText().toString().trim(), uri.toString());
//                                    mDbRef.push().setValue(img)
                                      imageUrl=uri.toString();
                                      selectedImage=false;
                                      pgbr.setProgress(100);
                                      pgbr.setVisibility(View.INVISIBLE);
                                }
                            });

//                            Toast.makeText(ChatActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChatActivity.this, "Failed "+e, Toast.LENGTH_LONG).show();
                            selectedImage=false;
                            pgbr.setProgress(100);
                            pgbr.setVisibility(View.INVISIBLE);
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
//                            double progress=(100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
//                            pgbr.setProgress((int)progress);
                        }
                    });

        }
        else{
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    int noOfImagesHandled=0;
    int count=0;
    //STORING IMAGES TO FILES DIRECTORY
    public void storingImages(ArrayList<Message> arrayList)
    {
          if(arrayList.size()-noOfImagesHandled!=1) {
//        Handler handler=new Handler();
              new Thread(new Runnable() {
                  @Override
                  public void run() {
                      ArrayList<Message> list = new ArrayList<>(arrayList);
                      int x = noOfImagesHandled;
                      noOfImagesHandled = list.size();
                      for (int i = list.size() - x - 1; i >= 0; i--) {
                          Message msg = list.get(i);
                          if (msg.imageURL != null && !msg.imageURL.equals("")) {
                              StoreImgRef.saveImage(msg.imageURL, msg.id);
                          }
                      }
                  }
              }).start();
          }
          else
          {
              Log.d(TAG2,"New Message is being storaged");
              //FOR HANDING NEW INCOMING MESSAGES-----------
              new Thread(new Runnable() {
                  @Override public void run() {
                      Message mObj=arrayList.get(arrayList.size()-1);
                      if(mObj.imageURL!=null && !mObj.imageURL.equals("")) {
                          StoreImgRef.saveImage(mObj.imageURL, mObj.id);
                      }
                  }
              }).start();
              //--------------------------------------------
          }
    }
//    public String ByteToString(Bitmap bitmap){
//        ByteArrayOutputStream baos= new ByteArrayOutputStream();
////        Log.d("SBS","Byte Array Output Stream");
//        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
////        Log.d("SBS","compressing ");
//        byte[] b=baos.toByteArray();
////        Log.d("SBS","Got Array");
//        String temp= Base64.encodeToString(b,Base64.DEFAULT);
////        Log.d("SBS","got string ----> "+temp);
//        return temp;
//    }

    public void storeOutgoingImg(String fid,Uri uri) {   //WORKS FASTER WHEN NOT USING THREAD
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                try {
//                    String fname=fid;
//                    Uri imageURI=uri;
                    StoreImgRef.storeNewImage(fid,uri);
                } catch (IOException e) {
                }
//            }
//        }).start();
    }

    @Override
    public void uploadOnCompression() {
     imageUri=Uri.fromFile(new File("/storage/emulated/0/Android/data/com.codezilla.chatapp/files/compressed/newcompimage.jpg"));
     mainHandler.post(new Runnable() {
         @Override
         public void run() {
             UploadFile();
         }
     });
    }

    @Override
    public void getProgress(int p) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                pgbr.setVisibility(View.VISIBLE);
                pgbr.setProgress(p);
            }
        });
    }

//    @Override
//    public void onConfigurationChanged(@NonNull Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        rview.scrollToPosition(msgAdapter.getItemCount()-1);
//    }
}