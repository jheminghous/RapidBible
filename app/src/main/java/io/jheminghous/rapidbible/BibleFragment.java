package io.jheminghous.rapidbible;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BibleFragment extends Fragment
{
    private static final String TAG = BibleFragment.class.getSimpleName();

    private static final String POSITION = "position";
    private static final long SAVE_INTERVAL_MILLIS = 1000;

    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _layoutManager;

    private TitleUpdater _titleUpdater;

    private Handler _handler = new Handler();
    private long _nextSaveTime = SystemClock.uptimeMillis();

    @Override
    public void onAttach(@NonNull Context context)
    {
        super.onAttach(context);

        try
        {
            _provider = (BibleProvider) context;
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException(context.toString() + " must implement" +
                                               BibleProvider.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        _recyclerView = (RecyclerView) inflater.inflate(R.layout.list, container, false);
        _layoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_layoutManager);
        _recyclerView.setAdapter(new BibleAdapter(_provider.getVersion()));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        _layoutManager.scrollToPosition(preferences.getInt(POSITION, 0));

        _recyclerView.addOnScrollListener(_savePositionScrollListener);

        _titleUpdater = new TitleUpdater(_provider.getToolbar(),
                                         _recyclerView,
                                         _provider.getVersion());

        return _recyclerView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        _titleUpdater.start();
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
        _titleUpdater = null;

        _recyclerView.removeOnScrollListener(_savePositionScrollListener);

        _recyclerView = null;
        _layoutManager = null;

        super.onDestroyView();
    }

    @Override
    public void onDetach()
    {
        _provider = null;

        super.onDetach();
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
                       .putInt(POSITION, _layoutManager.findFirstVisibleItemPosition())
                       .apply();
        }
    };
}
