package io.jheminghous.rapidbible;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat
{
    static final String CHANGING_THEME = "changing-theme";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        try
        {
            BibleProvider provider = (BibleProvider) getActivity();
            if (provider != null)
            {
                provider.setTitle(getString(R.string.settings));
            }
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Activity must implement" +
                                               BibleProvider.class.getSimpleName());
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey)
    {
        setPreferencesFromResource(R.xml.preferences, rootKey);

        final PreferenceManager manager = getPreferenceManager();
        Preference themePref = manager.findPreference(getString(R.string.theme_key));
        if (themePref != null)
        {
            themePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    FragmentActivity activity = getActivity();
                    if (activity != null)
                    {
                        Intent intent = activity.getIntent();
                        intent.putExtra(CHANGING_THEME, true);
                    }

                    setTheme(newValue.toString(), getResources());

                    return true;
                }
            });
        }
    }

    static void setTheme(@NonNull String theme, @NonNull Resources resources)
    {
        if (theme.equals(resources.getString(R.string.theme_entry_light)))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if (theme.equals(resources.getString(R.string.theme_entry_dark)))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P)
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
            }
            else
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }
    }
}
