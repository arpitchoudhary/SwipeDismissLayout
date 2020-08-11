package com.viewgroup.sample;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class Helper {
    public static final String PREFERENCES_PARENT_LANG ="LANG";
    public static final String PREFERENCES_NAME_LANG_CODE ="lang_code";

    public static String loadPreference(Context context, String parentName, String name, String defValue)
    {
        SharedPreferences pref = context.getSharedPreferences(parentName, MODE_PRIVATE);
        return pref.getString(name, defValue);
    }
    public static void savePreference(Context context, String parentName, String name, String newValue)
    {
        SharedPreferences pref = context.getSharedPreferences(parentName, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(name, newValue);
        editor.apply();
    }
    public static Context SetLanguage(Context context) {
        Locale locale = new Locale(loadPreference(context, PREFERENCES_PARENT_LANG, PREFERENCES_NAME_LANG_CODE, "en"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, locale);
        } else {
            return updateResourcesLegacy(context, locale);
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        return context.createConfigurationContext(configuration);
    }

    private static Context updateResourcesLegacy(Context context, Locale locale) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }
}
