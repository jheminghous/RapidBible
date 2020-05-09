package io.jheminghous.rapidbible;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class BibleApplication extends Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String theme = preferences.getString(getString(R.string.theme_key),
                                             getString(R.string.theme_entry_default));
        if (theme != null)
        {
            SettingsFragment.setTheme(theme, getResources());
        }
    }
}
