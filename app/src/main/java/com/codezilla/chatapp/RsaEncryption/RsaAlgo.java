package com.codezilla.chatapp.RsaEncryption;

import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RsaAlgo {

    final String ANDROID_KEYSTORE="AndroidKeyStore";
    final String KEY_ALIAS="key1";
    final String RSA_TRANS="RSA/ECB/PKCS1Padding";

    private static RsaAlgo instance=null;
    private RsaAlgo(){}
    public static RsaAlgo getInstance(){
        if(instance==null)
        {
            return new RsaAlgo();
        }
        return instance;
    }

    //CREATE A NEW KEY_PAIR
    public KeyPair createAsymmetricKeyPair(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, ANDROID_KEYSTORE);

            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            KEY_ALIAS,
                            KeyProperties.PURPOSE_DECRYPT|KeyProperties.PURPOSE_ENCRYPT)
                            .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                            .build());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            return keyPair;
        }
        catch(Exception e){Log.d("tag","Cant build");}
        return null;
    }

    //GETTING THE KEY STORE
    public KeyStore createKeyStore(){
        try {
            KeyStore ks = KeyStore.getInstance(ANDROID_KEYSTORE);
            ks.load(null);
            return ks;
        }
        catch (Exception e){}
   //     {Toast.makeText(context, "Cant create keystore", Toast.LENGTH_SHORT).show();}
        return  null;
    }

    public KeyPair getAsymmetricKeyPair() throws NoSuchPaddingException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException {
        KeyStore ks=createKeyStore();
        KeyPair keyPair;
        try {
            PrivateKey privateKey= (PrivateKey) ks.getKey(KEY_ALIAS, null);
            Log.d("tag","ks.getKey()");
            if(privateKey==null){
                Log.d("tag","privateKey == null");

                keyPair=this.createAsymmetricKeyPair();
                return keyPair;
            }
            else
            {
                Log.d("tag","privateKey =/= null"+privateKey.toString());
                PublicKey publicKey=(PublicKey) ks.getCertificate(KEY_ALIAS).getPublicKey();
                Log.d("tag","privateKey =/= null"+publicKey.toString());

                return new KeyPair(publicKey,privateKey);
            }
        }
        catch (UnrecoverableEntryException e){
            Log.d("tag","UnRecoverable");
        }
        return null;
    }

    public String enrypt(String data, PublicKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Log.d("tag","ecnryption started "+data+"  key= "+key);
        Cipher cipher = Cipher.getInstance(RSA_TRANS);
        cipher.init(Cipher.ENCRYPT_MODE,key);
        byte[] bytes=cipher.doFinal(data.getBytes());
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    public String decrypt(String data, PrivateKey key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException ,IllegalBlockSizeException, BadPaddingException{
        Log.d("tag","decryption started "+data);
        Cipher cipher = Cipher.getInstance(RSA_TRANS);
//        Log.d("tag","got cypher instance "+data);
        cipher.init(Cipher.DECRYPT_MODE,key);
//        byte [] encryptedData=Base64.decode(data,Base64.DEFAULT);
//        Log.d("tag","decrypting "+data);
//        byte [] decodedData=cipher.doFinal(data.getBytes());
        byte[] encryptedData = Base64.decode(data, Base64.DEFAULT);
        byte[] decodedData = new byte[0];
//        try {
            decodedData = cipher.doFinal(encryptedData);
//        }
//        catch (IllegalBlockSizeException e){        Log.d("tag","illegalblocksize 44");
//        }
//        catch (BadPaddingException e){        Log.d("tag","Badpadding");
//        }
//        Log.d("tag","cyphering");
        Log.d("tag"," decrypting cipher thread id -> "+Thread.currentThread().getId());

        return new String(decodedData);
//        cipher.init(Cipher.DECRYPT_MODE, Key);
//        byte[] cipherData = cipher.doFinal(Base64Utils.decodeFromString(data));
//        return new String(cipherData);
//        return Base64.encodeToString(decodedData,Base64.DEFAULT);
    }
    void  removeKeyStoreKey() {
        try {createKeyStore().deleteEntry(KEY_ALIAS);} catch (KeyStoreException e) {e.printStackTrace();}
    }


}
