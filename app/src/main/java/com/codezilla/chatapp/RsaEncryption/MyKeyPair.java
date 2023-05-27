package com.codezilla.chatapp.RsaEncryption;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.security.KeyPair;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.util.Base64;

import javax.crypto.NoSuchPaddingException;

@RequiresApi(api = Build.VERSION_CODES.O)
public class MyKeyPair {
    public static KeyPair keyPair=null;
    public static PublicKey publicKey=null;
    public static PrivateKey privateKey=null;
    public static String StrPublickey=null;

    public static void initializeKeyPair(){
         RsaAlgo rsa=RsaAlgo.getInstance();
        try {
            Log.d("tag","key getAsymmetric");
            keyPair = rsa.getAsymmetricKeyPair();
            publicKey=keyPair.getPublic();
            privateKey=keyPair.getPrivate();
            StrPublickey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }
}
