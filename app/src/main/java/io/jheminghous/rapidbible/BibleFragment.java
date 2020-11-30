package io.jheminghous.rapidbible;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BibleFragment extends Fragment
{
    private static final String TAG = BibleFragment.class.getSimpleName();

    private static final long SAVE_INTERVAL_MILLIS = 1000;

    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _layoutManager;

    private TitleUpdater _titleUpdater;

    private SettingsFragment _settingsFragment;

    private Handler _handler = new Handler();
    private long _nextSaveTime = SystemClock.uptimeMillis();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        try
        {
            _provider = (BibleProvider) getActivity();
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Activity must implement" +
                                               BibleProvider.class.getSimpleName());
        }

        _recyclerView = (RecyclerView) inflater.inflate(R.layout.list, container, false);
        _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);
        _recyclerView.setAdapter(new BibleAdapter(_provider.getVersion()));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        _layoutManager.scrollToPosition(preferences.getInt(getString(R.string.position_key), 0));

        _recyclerView.addOnScrollListener(_savePositionScrollListener);

        _titleUpdater = new TitleUpdater(_provider, _recyclerView);

        _settingsFragment = new SettingsFragment();

        setHasOptionsMenu(true);

        return _recyclerView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.settings_item)
        {
            showSettings();
        }
        return false;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        _titleUpdater.start();

        FragmentActivity activity = getActivity();
        Intent intent = activity != null ? activity.getIntent() : null;
        if (intent != null && intent.getBooleanExtra(SettingsFragment.CHANGING_THEME, false))
        {
            intent.removeExtra(SettingsFragment.CHANGING_THEME);
            showSettings();
        }
    }

    @Override
    public void onStop()
    {
        _titleUpdater.stop();

        super.onStop();
    }

    @Override
    public void onDestroyView()
    {
        _settingsFragment = null;

        _titleUpdater = null;

        _recyclerView.removeOnScrollListener(_savePositionScrollListener);

        _recyclerView = null;
        _layoutManager = null;

        _provider = null;

        super.onDestroyView();
    }

    BibleItem getCurrentItem()
    {
        if (_provider == null)
        {
            throw new IllegalStateException("The fragment must be active to get the current item");
        }

        int position = _layoutManager.findFirstVisibleItemPosition();
        return _provider.getVersion().getItems().get(position);
    }

    void scrollToItem(BibleItem item)
    {
        if (_provider == null)
        {
            throw new IllegalStateException("The fragment must be active to scroll to an item");
        }

        if (item == null)
        {
            return;
        }

        int position = _provider.getVersion().getItems().indexOf(item);
        if (position >= 0)
        {
            _recyclerView.scrollToPosition(position);
        }
        else
        {
            Log.w(TAG, String.format("Item %s %d not found",
                                     item.getType().name(),
                                     item.getNumber()));
        }
    }

    private void showSettings()
    {
        _provider.showFragment(_settingsFragment, true);
    }

    private RecyclerView.OnScrollListener _savePositionScrollListener =
            new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
        {
            long now = SystemClock.uptimeMillis();
            if (now > _nextSaveTime)
            {
                _nextSaveTime = now + SAVE_INTERVAL_MILLIS;
                _handler.postAtTime(_savePositionRunnable, _nextSaveTime);
            }
        }
    };

    private Runnable _savePositionRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (_layoutManager == null)
            {
                return;
            }

            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(getContext());
            preferences.edit()
                       .putInt(getString(R.string.position_key),
                               _layoutManager.findFirstVisibleItemPosition())
                       .apply();
        }
    };
}
