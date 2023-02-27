package com.food.delivery.db.encryption;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.jetbrains.annotations.NotNull;

import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.KeySpec;
import android.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public abstract class EncryptionJava {
    private static final char[] HEX_CHARS = "0123456789ABCDEF".toCharArray();
    private static byte[] rawByteKey;
    private static char[] dbCharKey = null;

    public static final byte[] generateRandomKey() {
        byte[] rawByteKey = new byte[32];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                SecureRandom.getInstanceStrong().nextBytes(rawByteKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            new SecureRandom().nextBytes(rawByteKey);
        }
        return rawByteKey;
    }

    public static final char[] toHex(@NotNull byte[] bytes) {
        StringBuilder res = new StringBuilder();
        byte[] var5 = bytes;
        int var6 = 0;
        int var7 = bytes.length;
        while(var6 < var7) {
            byte element$iv = var5[var6];
            ++var6;
            int firstIndex = (element$iv & 240) >>> 4;
            int secondIndex = element$iv & 15;
            res.append(HEX_CHARS[firstIndex]);
            res.append(HEX_CHARS[secondIndex]);
        }
        return res.toString().toCharArray();
    }

    public static void createNewKey() {
        // This is the raw key that we'll be encrypting + storing
        rawByteKey = generateRandomKey();
        // This is the key that will be used by Room
        dbCharKey = toHex(rawByteKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void persistRawKey(Context context, char[] userPasscode) {
        Storable storable =  toStorable(rawByteKey, userPasscode);
        // Implementation explained in next step
        saveToPrefs(context, storable);
    }

    private static void saveToPrefs(Context context, Storable storable) {
        String gson = new Gson().toJson(storable);
        SharedPreferences preferences = context.getSharedPreferences(
                "database",
                Context.MODE_PRIVATE
        );
        preferences.edit().putString("key", gson).apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static Storable toStorable(byte[] rawDbKey, char[] userPasscode) {
        byte[] salt = new byte[32];
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                SecureRandom.getInstanceStrong().nextBytes(rawByteKey);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        } else {
            new SecureRandom().nextBytes(rawByteKey);
        }

        SecretKey secretKey = generateSecretKey(userPasscode, salt);
        byte[] iv = new byte[0], cipherText = new byte[0];
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            AlgorithmParameters parameters = cipher.getParameters();
            iv = parameters.getParameterSpec(IvParameterSpec.class).getIV();
            cipherText = cipher.doFinal(rawDbKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidParameterSpecException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }

        // return the IV and CipheText which can be stored to disk
        return new Storable(
                Base64.encodeToString(iv, Base64.DEFAULT),
                Base64.encodeToString(cipherText, Base64.DEFAULT),
                Base64.encodeToString(salt, Base64.DEFAULT)
        );
    }

    private static SecretKey generateSecretKey(char[] userPasscode, byte[] salt) {
        // Initialize PBE with password
        SecretKey tmp = null;
        try {
            SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec keySpec = new PBEKeySpec(userPasscode, salt, 65536, 256);
            tmp = secretKeyFactory.generateSecret(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        return new SecretKeySpec(tmp.getEncoded(), "AES");
    }

    // Decryption
    private static Storable getStorable(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "database",
                Context.MODE_PRIVATE
        );
        String serialized = sharedPreferences.getString("key", null);
        if (serialized.isEmpty()) {
            return null;
        }

         try {
           return new Gson().fromJson(serialized, Storable.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static byte[] getRawByteKey(char[] passcode, Storable storable) throws IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = null;
        byte[] aesWrappedKey = new byte[0];
        try {
            aesWrappedKey = Base64.decode(storable.getKey(), Base64.DEFAULT);
            byte[] iv = Base64.decode(storable.getIv(), Base64.DEFAULT);
            byte[] salt = Base64.decode(storable.getSalt(), Base64.DEFAULT);
            SecretKey secretKey = generateSecretKey(passcode, salt);
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return cipher.doFinal(aesWrappedKey);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static char[] getCharKey(char[] passcode, Context context) throws IllegalBlockSizeException, BadPaddingException {
        if (dbCharKey == null) {
            initKey(passcode, context);
        }
        return dbCharKey;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void initKey(char[] passcode, Context context) throws IllegalBlockSizeException, BadPaddingException {
        Storable storable = getStorable(context);
        if (storable == null) {
            createNewKey();
            persistRawKey(context, passcode);
        } else {
            rawByteKey = getRawByteKey(passcode, storable);
            dbCharKey = toHex(rawByteKey);
        }
    }
}
