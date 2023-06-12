package com.codezilla.chatapp.ProfilePicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;

import com.codezilla.chatapp.ImageCompression.StringToBitmapViseVersa;
import com.codezilla.chatapp.MainActivity;
import com.codezilla.chatapp.UserAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SNIHostName;

public class ProfilePictureOperations {
    private static final String TAG = "ProfilePictureOperation";
       DatabaseReference mDbRef;
       FirebaseAuth mAuth;
       StorageReference mStoreRef;

       public static Map picMap= new HashMap();
       public String filePath;
       Handler handler;
       UserAdapter adpter;
       public File friendsPicFile;
       public MainActivity ref;
       public final String picFolderName="ProfilePic";
       public final String currUserFile="CurrUserPic";
       public final String Extension=".jpg";

       public ProfilePictureOperations(Handler handler, UserAdapter adp, Context con,MainActivity ref){
       this.handler=handler;
       this.adpter=adp;
       this.ref=ref;
       mAuth = FirebaseAuth.getInstance();
       mDbRef=FirebaseDatabase.getInstance().getReference();
       mStoreRef= FirebaseStorage.getInstance().getReference("uploads");
       friendsPicFile=new File(con.getExternalFilesDir(null),picFolderName);
       if(!friendsPicFile.exists()){
               friendsPicFile.mkdir();
       };
       filePath=friendsPicFile.getPath();
       }

       public static ArrayList<Integer> EchFrndCall=new ArrayList<Integer>(); //NO OF CALL FOR EACH FRIEND
       public final int blockOfPic=20;
       public void getFriendsPic(String fUid,int count,long total) //FOR UPDATING RECYCLER VIEW AND SAVING THE NEW PIC FILE
       {
//           Log.d("chk","counter for count [ "+count+" ]   is ---> "+ cntr[0]);
           mDbRef.child(picFolderName).child(fUid).addValueEventListener(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot snapshot) {

                   String picURL=snapshot.getValue(String.class);
                   if(picURL==null || picURL.equals("")) //PIC NOT AVAILABLE
                       return;
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           try {
                               Bitmap bm= BitmapFactory.decodeStream((InputStream) new URL(picURL).getContent());
                               int Flag=0; //PIC WAS PRE AVAILABLE OR NOT
                               Bitmap oldBitmap=null;
                               if(picMap.containsKey(fUid)) {
//                                   Log.d("MAN","Flag=1 count--> "+count +"  indx= "+(count-1));
//                                   Log.d("MAN","Flag=1 count--> "+count +"  old == new ");
                                   Flag = 1;
                                   oldBitmap= (Bitmap) picMap.get(fUid);
                               }
                               picMap.put(fUid,bm);
//                                   Log.d("MAN","       count--> "+count +"  indx= "+(count-1)+"   total---> "+total +"  calls "+EchFrndCall.get(count-1));
                               if(Flag==0 || EchFrndCall.get(count-1)==1 )
                               {
                                   handler.post(new Runnable() {
                                       @Override
                                       public void run() {
                                           ref.updateAdapter(count-1);
                                       }
                                   });
                               }
                               File file=new File(friendsPicFile,fUid+Extension);
                               FileOutputStream fileOutputStream= new FileOutputStream(file);
                               bm.compress(Bitmap.CompressFormat.JPEG,40,fileOutputStream);
                               fileOutputStream.close();
                               if(Flag==1 && EchFrndCall.get(count-1)==0 )
                               {
//                                   Log.d("MAN","Flag==1 && EchFrndCall.get(count-1)==0   Flag=1 count--> "+count +"  old == new ");
                                   Bitmap bmx= BitmapFactory.decodeFile(file.getPath());
                                   if(!bmx.sameAs(oldBitmap)){
//                                   Log.d("MAN","!bmx.sameAs(oldBitmap)bm NOT EQUAL              count--> "+count +"  old == new ");
                                       handler.post(new Runnable() {
                                           @Override
                                           public void run() {
                                               ref.updateAdapter(count-1);
                                           }
                                       });
                                   }
                               }
                               EchFrndCall.set(count-1,1);
//                               Log.d("chk","count val ---> "+count +"  indx= "+(count-1)+"   total---> "+total +"  calls "+EchFrndCall.get(count-1));
//                               if(EchFrndCall.get(count-1)==0 && (count % blockOfPic == 0 || count == total )) {
////                                   Log.d("chk","Handler first Call count val ---> "+count +"  indx= "+(count-1)+"  calls "+EchFrndCall.get(count-1));
//                                   handler.post(new Runnable() {
//                                       @Override
//                                       public void run() {
//                                           ref.updateAdapter(0);
////                                           adpter.notifyDataSetChanged();
//                                       }
//                                   });
//                               }
//
//                               boolean AUPIA = false; //ALREADY UPDATED PIC IN ADAPTER
//                               if(EchFrndCall.get(count-1)!=0 || Flag==0)
//                               {
////                                   Log.d("chk","Handler NEXT CALL count val ---> "+count +"  indx= "+(count-1));
//                                   handler.post(new Runnable() {
//                                       @Override
//                                       public void run() {
//                                           ref.updateAdapter(count-1);
////                                           adpter.notifyDataSetChanged();
//                                       }
//                                   });
//                                   AUPIA=true;
//                               }
//                               EchFrndCall.set(count-1,1);
//
//
//                               File file=new File(friendsPicFile,fUid+Extension);
//                               FileOutputStream fileOutputStream= new FileOutputStream(file);
//                               bm.compress(Bitmap.CompressFormat.JPEG,40,fileOutputStream);
//                               fileOutputStream.close();
////                               int finalFlag = Flag;
////                               Bitmap finalOldBitmap = oldBitmap;
////                               new Thread(new Runnable() {
////                                   @Override
////                                   public void run() {
////                               Log.d("chk","CHECKING IF FLAG==1 count--> "+count +"  FLag="+Flag);
//                               //THIS BELOW IS EXECUTED ONLY DURING THE FIRST CALL (IF FLAG==1 AND OLD!=NEW)
//                               if(Flag==1 && !AUPIA)
//                               {
////                                   Log.d("chk","Flag=1 count--> "+count +"  old == new ");
//                                   Bitmap bmx= BitmapFactory.decodeFile(file.getPath());
//                                   if(!bmx.sameAs(oldBitmap)){
////                                       Log.d("chk","bm NOT EQUAL  count--> "+count +"  old == new ");
//                                       handler.post(new Runnable() {
//                                           @Override
//                                           public void run() {
//                                               ref.updateAdapter(count-1);
//                                           }
//                                       });
//                                   }
//                               }
//                                   }
//                               }).start();
                           } catch (IOException e) {
                               e.printStackTrace();
                           }
                       }
                   }).start();

               }
               @Override
               public void onCancelled(@NonNull DatabaseError error) {}
           });
       }

       //GET FRIENDS IN THE VERY BEGINNING OF MAIN ACTIVITY
//       public static void fetchAvailablePics(String fPath)
       public  void fetchAvailablePics()
       {
           File file= new File(filePath);
          new Thread(new Runnable() {
              @Override
              public void run() {
                  File[] fileArray= file.listFiles();
                  if(fileArray!=null)
                  {
                      Bitmap bm=null;
                      for(File f:fileArray)
                      {
                          bm=BitmapFactory.decodeFile(f.getPath());
                          if(bm!=null)
                          {
                              String fileName= f.getName();
                              String[] srr=fileName.split("\\.");
                              picMap.put(srr[0],bm);
                              Log.d(TAG,"inserted in map DIRECTLY from file");
                          }
                      }
                  }
              }
          }).start();

       }

    //UPLOAD TO CLOUD STORAGE
    Bitmap bm=null; //CURRENT USERS BITMAP , ACCESSED ONLY IF UPLOAD IS SUCCESSFUL
    public void uploadProfilePic(Uri imageUri) {
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
            StorageReference fileRef=mStoreRef.child(System.currentTimeMillis()+".jpg");
//            Toast.makeText(ChatActivity.this, "image != null", Toast.LENGTH_SHORT).show();
//            mStoragetask=
            fileRef.putFile(imageUri)
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
//                            pgbr.setVisibility(View.VISIBLE);
//                            pgbr.setProgress(80);
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String currUserId=mAuth.getCurrentUser().getUid();
//                                    Toast.makeText(ChatActivity.this, "image != null", Toast.LENGTH_SHORT).show();
//                                    image img= new image(fname.getText().toString().trim(), uri.toString());
//                                    mDbRef.push().setValue(img)
                                    //INSERT THE BITMAP INTO MAP
                                    try {
                                        bm= BitmapFactory.decodeStream(ref.getContentResolver().openInputStream(imageUri));
//                                        picMap.put(currUserFile,bm);
                                    } catch (FileNotFoundException e) {e.printStackTrace();}

                                    //GIVING RESULT TO THE MAIN ACTIVITY
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ref.picUploadResult(1,bm);
                                        }
                                    });
                                    String imageURL=uri.toString();

                                    //UPDATING URL IN REAL_TIME DATABASE
                                    mDbRef.child("ProfilePic").child(currUserId).setValue(imageURL);

                                    //INSERTING THE NEW PIC IN THE CURR USERS PIC_FILE
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            File file = new File(ref.getExternalFilesDir(null),currUserFile+Extension);
                                            try {
//                                                Log.d("picIn","creating out");
                                                FileOutputStream outputStream= new FileOutputStream(file);
                                                bm.compress(Bitmap.CompressFormat.JPEG,40,outputStream);
//                                                Log.d("picIn","bm compress() and saved");
                                                outputStream.close();
                                            } catch (FileNotFoundException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
//                                    selectedImage=false;
//                                    pgbr.setProgress(100);
//                                    pgbr.setVisibility(View.INVISIBLE);
                                }
                            });

//                            Toast.makeText(ChatActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            selectedImage=false;
//                            pgbr.setProgress(100);
//                            pgbr.setVisibility(View.INVISIBLE);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ref.picUploadResult(0,null);
                                }
                            });
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
    }

    //MANAGING CURRENT USERS PROFILE PIC
    public void manageCurrUserPic()
    {
        Log.d("TAGX","point manageUserPic   Thread id --> "+Thread.currentThread().getId());
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("TAGX","point run   Thread id --> "+Thread.currentThread().getId());
                File file = new File(ref.getExternalFilesDir(null),currUserFile+Extension);
                if(file.exists())
                {
                    bm=BitmapFactory.decodeFile(file.getPath());
                    ref.fetchSuccessful(bm);
                }
                mDbRef.child("ProfilePic").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Log.d("TAGX","point addValueEventListener   Thread id --> "+Thread.currentThread().getId());
                        String picUrl=snapshot.getValue(String.class);
                        if(picUrl!=null&& !picUrl.equals("")) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d("TAGX","point bitmap<-Url   Thread id --> "+Thread.currentThread().getId());
                                    try {
                                        bm = BitmapFactory.decodeStream((InputStream) new URL(picUrl).getContent());
                                        ref.fetchSuccessful(bm);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }).start();
    }

 public interface picFetchSuccessListener{
        public void fetchSuccessful(Bitmap bitmap);
 }

//    public void getFriendsProfilePic(String fUid)
//    {
//        mDbRef.child("ProfilePic").child(fUid).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                Log.d(TAG,"friends uid "+fUid+"  ON success");
//
//                String picString=snapshot.getValue(String.class);
//                if(picString==null || picString.equals(""))
//                    return;
////                Log.d(TAG,"friends uid "+fUid+"    picstring"+picString);
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String frUid=fUid;
//                        String s=picString;
//                        Bitmap bm= StringToBitmapViseVersa.StringToBitmap(s);
//                        if(bm!=null)
//                        {
//                            picMap.put(frUid,bm);
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    userAdapter.notifyDataSetChanged();
//                                }
//                            });
//
//                            File pic=new File(friendsPicFile,frUid+".jpg");
//                            try {
//                                FileOutputStream fileOutputStream= new FileOutputStream(pic);
//                                bm.compress(Bitmap.CompressFormat.PNG,50,fileOutputStream);
//                                fileOutputStream.close();
//                            } catch (FileNotFoundException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {}
//        });
//
//    }
}
