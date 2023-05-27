package com.codezilla.chatapp.RsaEncryption;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class RsaEncryptionHandler {

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptMessageForReciever(String data,String recieverPublicKey){
        String result=null;

        PublicKey pubKey = null;
        try {
            byte[] publicBytes = Base64.getDecoder().decode(recieverPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pubKey = keyFactory.generatePublic(keySpec);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            result=RsaAlgo.getInstance().enrypt(data,pubKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encryptMessageForSender(String data){
        String result=null;

        try {
            result=RsaAlgo.getInstance().enrypt(data,MyKeyPair.publicKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decryptMessage(String data){
        String result=null;
        try {
            result=RsaAlgo.getInstance().decrypt(data,MyKeyPair.privateKey);
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
