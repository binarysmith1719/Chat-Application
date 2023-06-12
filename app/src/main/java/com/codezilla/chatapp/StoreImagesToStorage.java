package com.codezilla.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;

public class StoreImagesToStorage {
    Context context;
    String chatRoom;
    File filex;
    public boolean canLoad=true;
    public StoreImagesToStorage(Context context,String chatRoom){
        this.context=context;
        this.chatRoom=chatRoom;
        filex=new File(context.getExternalFilesDir(null),chatRoom);
        if(!filex.exists()){
            filex.mkdir();
        }
    }
//    BitmapDrawable drawable=null;
    public void saveImage(String URL,String fname){
//        Log.d("images","-------------------------SAVING IMAGE --------------------------");
//        Log.d("SBS","-------------------------SAVING IMAGE --------------------------");
        File imgFile=new File(filex,fname+".jpg");
        if(!imgFile.exists()) {
//            Log.d("images","storing images , file do not exits");
//            Log.d("SBS","1   storing images , file do not exits");
//            ImageView imageView = new ImageView(context);
//            Picasso.get().load(URL).into(imageView, new Callback() {
//                @Override
//                public void onSuccess() {
//                    drawable = (BitmapDrawable) imageView.getDrawable();
//                    Bitmap bitmap = drawable.getBitmap();
//                    FileOutputStream outputStream=null;
//                    try {outputStream=new FileOutputStream(imgFile);
//                    } catch (FileNotFoundException e) {}
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
//
//                    try {
//                        outputStream.flush();
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onError(Exception e) {
//                    canLoad = false;
//                }
//            });
//            ##################################################################################
//            Picasso.get().load(URL).into(new Target() {
//                @Override
//                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                    FileOutputStream outputStream=null;
//                    try {outputStream=new FileOutputStream(imgFile);
//                    } catch (FileNotFoundException e) {}
//
//                    bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
//
//                    try {
//                        outputStream.flush();
//                        outputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                @Override
//                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                }
//
//                @Override
//                public void onPrepareLoad(Drawable placeHolderDrawable) {
//                }
//            });
            pushBitmap_Into_OutputStream(imgFile,URL,fname);
        }
        else {
//            Log.d("images", " FILE EXITS IN THE STORAGE");
//            Log.d("SBS"," FILE EXITS IN THE STORAGE");

            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            if (bitmap == null) {
//                Log.d("SBS", "File exists but is NULL      fname ==>" + fname);
//                Log.d("imagesy", "File exists but is NULL     url=" + URL);
//                try {
//                    ChatActivity.isStoringImage = true;//--------------------------------
//                    FileOutputStream outputStream = null;
//                    try {
//                        outputStream = new FileOutputStream(imgFile);
//                    } catch (FileNotFoundException e) {
//                    }
//                    Log.d("imagesy", "File Stream Created----------------==-=-=-=-=-=-=-=-=-=-");
//                    bitmap = BitmapFactory.decodeStream((InputStream) new URL(URL).getContent());
//                    Log.d("imagesy", "bitmap Created Created---------------=-=-=-=-=-=-=-=-=-=-=");
//                    ChatActivity.map.put(fname, bitmap);
//                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                    Log.d("imagesy", "bitmap compressed-=-=----------==-=-=-=-=--==-=--=-");
//                    outputStream.flush();
//                    Log.d("imagesy", "File Stream Flushed-=-=-=-=-=----------=-=-=-=-===");
//                    outputStream.close();
//                    ChatActivity.isStoringImage = false;//-------------------------------
//                    Log.d("imagesy", "File Stream Closed");
//
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                pushBitmap_Into_OutputStream(imgFile,URL,fname);
                return;
            }
            ChatActivity.map.put(fname, bitmap);
        }
    }

    public void pushBitmap_Into_OutputStream(File imgFile,String Url,String fname)
    {
        try {
//            ChatActivity.isStoringImage=true;//--------------------------------
//            Log.d("SBS","File output Stream Created  fname--> "+fname);
            Bitmap bitmap=BitmapFactory.decodeStream((InputStream) new URL(Url).getContent());
//            Log.d("SBS","got the bitmap  fname--> "+fname);
            ChatActivity.map.put(fname,bitmap);
            //FILE OUTPUT STREAM IS PUT BELOW BECAUSE THE BITMAP CAN BE NULL AND IN THAT CASE
            //WE DON'T WANT TO CREATE THE imgFile FILE .  IF THE BITMAP IS NULL IT WILL THROW IO EXCEPTION
            //WHICH WILL BE CATCH AND THIS output stream WILL NOT BE CREATED
            FileOutputStream outputStream=null;
            try {outputStream=new FileOutputStream(imgFile);
            } catch (FileNotFoundException e) {
//                Log.d("SBS"," 2 File not found  fname--> "+fname);
            }
//            if(bitmap.)
//            if(bitmap==null){
//                Log.d("SBS"," 3 bitmap is null returning   fname->"+fname);
//                outputStream.flush();
//                outputStream.close();
//                return;
//            }
//            Log.d("SBS","4 bitmap Created & put in NULL    fname->"+fname);
            bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
//            Log.d("imagesx","bitmap compressed");
            outputStream.flush();
            outputStream.close();
//            ChatActivity.isStoringImage=false;//-------------------------------
        } catch (IOException e) {
//            Log.d("SBS","got the bitmap  then IOException fname--> "+fname);
            e.printStackTrace();

        }
    }
    //STORING NEW OUTGOING IMAGES
    public void storeNewImage(String fname,Uri uri) throws FileNotFoundException,IOException
    {
        InputStream is= context.getContentResolver().openInputStream(uri);
//        Log.d("SBS","storing new image");
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        ChatActivity.map.put(fname,bitmap);
//        Log.d("SBS","inserted new image in the MAP");

        //STORING THE FILE IN FILES DIRECTORY
        File imgFile=new File(filex,fname+".jpg");
        FileOutputStream outputStream = new FileOutputStream(imgFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG,80,outputStream);
        outputStream.flush();
        outputStream.close();
    }


}
