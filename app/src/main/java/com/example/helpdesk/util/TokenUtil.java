package com.example.helpdesk.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class TokenUtil {
    private Activity activity;

    public TokenUtil(Activity activity) {
        this.activity = activity;
    }

    public String getAcessToken() {
        SharedPreferences preferences = activity.getSharedPreferences("HELPDESK", Context.MODE_PRIVATE);
        String token = preferences.getString("TOKEN", null);
        return token;
    }
}
