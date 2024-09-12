package com.example.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class SharedPreferenceClass {
    private static final String USER_PREF = "secure_user_prefs";
    private SharedPreferences appShared;
    private SharedPreferences.Editor prefsEditor;
    public SharedPreferenceClass(Context context) {
        try {
            // Tạo hoặc lấy masterKeyAlias cho việc mã hóa
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Sử dụng EncryptedSharedPreferences
            appShared = EncryptedSharedPreferences.create(
                    USER_PREF,         // Tên file SharedPreferences
                    masterKeyAlias,    // Key mã hóa
                    context,           // Context của ứng dụng
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            this.prefsEditor = appShared.edit();

        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    // int
    public int getValue_int(String key) {
        return appShared.getInt(key, 0);
    }

    public void setValue_int(String key, int value) {
        prefsEditor.putInt(key, value).commit();
    }

    // string
    public String getValue_string(String key) {
        return appShared.getString(key, "");
    }

    public void setValue_string(String key, String value) {
        prefsEditor.putString(key, value).commit();
    }


    // boolean
    public boolean getValue_boolean(String key) {
        return appShared.getBoolean(key, false);
    }

    public void setValue_boolean(String key, boolean value) {
        prefsEditor.putBoolean(key, value).commit();
    }

    public void clear() {
        prefsEditor.clear().commit();
    }
}
