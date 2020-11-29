package io.jheminghous.rapidbible;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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
