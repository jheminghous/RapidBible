package io.jheminghous.rapidbible;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class BibleApplication extends Application
{
    private BibleVersion _version;

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

    public BibleVersion getVersion()
    {
        return _version;
    }

    public void setVersion(BibleVersion version)
    {
        _version = version;
    }
}
