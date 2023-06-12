package com.codezilla.chatapp.ImageCompression;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageCompress {
    private static final String TAG = "ImageCompression";
    private static final float maxHeight = 1280.0f;
    private static final float maxWidth = 1280.0f;
    public Context context;
    ImageCompressionCompletionListener ref;
    public void getCompressedImage(Context con, InputStream is, ImageCompressionCompletionListener ref ){
        context=con;
        this.ref=ref;
//        Activity activity=act;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG,"run () ");
                String path= null;
                try {
                    Log.d(TAG,"trying in run ");

                    path = getFilePath(is);
                    Log.d(TAG,"got file in run ");

                    String compPath=compressImage(path);
                    ref.uploadOnCompression();
                    ref.getProgress(30);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d(TAG,"got File Not found exception in run");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private String getFilePath(InputStream is)throws FileNotFoundException ,IOException
    {
        InputStream inputStream=is;
        Bitmap bitmap= BitmapFactory.decodeStream(is);

        File filex=new File(context.getExternalFilesDir(null),"compressed");
        if(!filex.exists())
        { filex.mkdir();}

        File file=new File(filex,"compimage.jpg");
        FileOutputStream fileOutputStream= new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
        fileOutputStream.close();
        ref.getProgress(15);
        return "/storage/emulated/0/Android/data/com.codezilla.chatapp/files/compressed/compimage.jpg";
    }
    private String compressImage(String imagePath) {
        Log.d(TAG,"Compressing image   external dir ");
        Log.d(TAG,"imagePATH "+imagePath);


        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath, options);
        Log.d(TAG,"BitmapFactory.decodeFile");

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / (float) actualHeight;
        float maxRatio = maxWidth / maxHeight;

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }
        Log.d(TAG," calculateInSampleSize");

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            bmp = BitmapFactory.decodeFile(imagePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        Log.d(TAG,"Bitmap.createBitmap");

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.RGB_565);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        Log.d(TAG,"Matrix");

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        if (bmp != null) {
            bmp.recycle();
        }
        Log.d(TAG,"exit interface");

        ExifInterface exif;
        try {
            exif = new ExifInterface(imagePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        Log.d(TAG,"getting file path  ");

        String filepath = getFilename();
        try {
            out = new FileOutputStream(filepath);

            //write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return filepath;
    }

   private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }
        return inSampleSize;
    }

    private String getFilename() {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + "com.codezilla.chatapp"
                + "/Files/Compressed");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            mediaStorageDir.mkdirs();
        }

        String mImageName="newcompimage.jpg";
        String uriString = (mediaStorageDir.getAbsolutePath() + "/"+ mImageName);
        ref.getProgress(20);
        return uriString;

    }
}
