package io.jheminghous.rapidbible;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements BibleProvider
{
    private BibleVersion _version;

    private Toolbar _toolbar;

    private SplashFragment _splashFragment;
    private BibleFragment _bibleFragment;
    private ReferenceFragment _referenceFragment;

    private BibleItem _currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        _splashFragment = new SplashFragment();
        _bibleFragment = new BibleFragment();
        _referenceFragment = new ReferenceFragment();

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(_backStackListener);

        fragmentManager.beginTransaction()
                       .add(R.id.content, _splashFragment)
                       .commit();

        _toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(_toolbar);

        _toolbar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!_bibleFragment.isVisible()) return;

                _currentItem = _bibleFragment.getCurrentItem();

                fragmentManager.beginTransaction()
                               .replace(R.id.content, _referenceFragment)
                               .addToBackStack(null)
                               .commit();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        new VersionLoader("KJV",
                          getResources().openRawResource(R.raw.kjv),
                          new VersionLoader.Listener()
                          {
                              @Override
                              public void onLoaded(BibleVersion version)
                              {
                                  _version = version;

                                  getSupportFragmentManager().beginTransaction()
                                                             .replace(R.id.content, _bibleFragment)
                                                             .commit();

                                  _toolbar.setVisibility(View.VISIBLE);
                              }
                          });
    }

    @Override
    protected void onDestroy()
    {
        getSupportFragmentManager().removeOnBackStackChangedListener(_backStackListener);

        _referenceFragment = null;
        _bibleFragment = null;
        _splashFragment = null;

        _toolbar = null;

        _version = null;

        super.onDestroy();
    }

    @Override
    public BibleVersion getVersion()
    {
        return _version;
    }

    @Override
    public Toolbar getToolbar()
    {
        return _toolbar;
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
        _currentItem = item;

        if (_referenceFragment.isVisible())
        {
            getSupportFragmentManager().popBackStack();
        }
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
