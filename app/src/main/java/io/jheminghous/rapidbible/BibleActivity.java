package io.jheminghous.rapidbible;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class BibleActivity extends AppCompatActivity implements BibleProvider
{
    private Toolbar _toolbar;
    private TextView _toolbarTitle;

    private BibleFragment _bibleFragment;
    private ReferenceFragment _referenceFragment;

    private BibleItem _currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        _bibleFragment = new BibleFragment();
        _referenceFragment = new ReferenceFragment();

        getSupportFragmentManager().addOnBackStackChangedListener(_backStackListener);

        _toolbar = findViewById(R.id.toolbar);
        _toolbar.setTitle("");
        _toolbarTitle = _toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(_toolbar);

        _toolbarTitle.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                _currentItem = _bibleFragment.getCurrentItem();
                showFragment(_referenceFragment, true);
            }
        });

        showFragment(_bibleFragment, false);
    }

    @Override
    protected void onDestroy()
    {
        getSupportFragmentManager().removeOnBackStackChangedListener(_backStackListener);

        _referenceFragment = null;
        _bibleFragment = null;

        _toolbar = null;

        super.onDestroy();
    }

    @Override
    public void setTitle(String title)
    {
        if (_bibleFragment.isVisible())
        {
            _toolbar.setTitle("");
            _toolbarTitle.setVisibility(View.VISIBLE);
            _toolbarTitle.setText(title);
        }
        else
        {
            _toolbarTitle.setVisibility(View.GONE);
            _toolbar.setTitle(title);
        }
    }

    @Override
    public void showFragment(Fragment fragment, boolean addToBackStack)
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction()
                                                            .replace(R.id.content, fragment);
        if (addToBackStack)
        {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public BibleVersion getVersion()
    {
        BibleApplication app = (BibleApplication) getApplication();
        return app.getVersion();
    }

    @Override
    public BibleItem getCurrentItem()
    {
        return _currentItem;
    }

    @Override
    public boolean onSupportNavigateUp()
    {
        onBackPressed();
        return true;
    }

    @Override
    public void setCurrentItem(BibleItem item)
    {
        saveHistory();

        _currentItem = item;

        saveHistory();

        while (!_bibleFragment.isVisible())
        {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }


    private void saveHistory()
    {
        final String KEY = getString(R.string.history_key);

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        String history = preferences.getString(KEY, "");
        assert history != null;

        List<String> entries = new LinkedList<>(Arrays.asList(history.split(",")));
        String newEntry = String.valueOf(getVersion().getItems().indexOf(_currentItem));
        int currentIndex = entries.indexOf(newEntry);
        if (currentIndex >= 0)
        {
            entries.remove(currentIndex);
        }
        entries.add(0, newEntry);
        if (entries.size() > HistoryFragment.MAX_HISTORY_ENTRIES)
        {
            entries.remove(entries.size() - 1);
        }

        StringBuilder builder = new StringBuilder(entries.size() * 2 - 1);
        builder.append(entries.get(0));
        for (int i = 1; i < entries.size(); ++i)
        {
            builder.append(",")
                   .append(entries.get(i));
        }

        preferences.edit()
                   .putString(KEY, builder.toString())
                   .apply();
    }

    private FragmentManager.OnBackStackChangedListener _backStackListener =
            new FragmentManager.OnBackStackChangedListener()
    {
        @Override
        public void onBackStackChanged()
        {
            ActionBar actionBar = getSupportActionBar();

            if (_bibleFragment.isVisible())
            {
                _bibleFragment.scrollToItem(_currentItem);

                if (actionBar != null)
                {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                }
            }
            else
            {
                if (actionBar != null)
                {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    };
}
