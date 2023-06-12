package com.codezilla.chatapp.ImageCompression;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class StringToBitmapViseVersa {
    private static final String TAG = "MainActivity";

    public static Bitmap StringToBitmap(String s){
     byte[] bytes= Base64.decode(s,Base64.DEFAULT);
     Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
     return bitmap;
    }

    public static String BitmapToString(Bitmap bitmap){
        ByteArrayOutputStream baos= new ByteArrayOutputStream();
//        Log.d("SBS","Byte Array Output Stream");
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
//        Log.d("SBS","compressing ");
        byte[] b=baos.toByteArray();
//        Log.d("SBS","Got Array");
        String temp= Base64.encodeToString(b,Base64.DEFAULT);
//        Log.d("SBS","got string ----> "+temp);
        return temp;
    }
}
