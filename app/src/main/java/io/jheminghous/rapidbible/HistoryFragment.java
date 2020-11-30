package io.jheminghous.rapidbible;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment
{
    static final int MAX_HISTORY_ENTRIES = 100;
    private static final String TAG = VersionLoader.class.getSimpleName();

    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _linearLayoutManager;
    private HistoryAdapter _adapter;

    private List<BibleItem> _items = new ArrayList<>();

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

        _linearLayoutManager = new LinearLayoutManager(getContext());
        _recyclerView.setLayoutManager(_linearLayoutManager);

        _adapter = new HistoryAdapter(_provider, _items);
        _recyclerView.setAdapter(_adapter);

        return _recyclerView;
    }

    @Override
    public void onResume()
    {
        final String KEY = getString(R.string.history_key);

        super.onResume();

        _provider.setTitle(getString(R.string.history_title));

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        String history = preferences.getString(KEY, "");
        assert history != null;

        List<BibleItem> allItems = _provider.getVersion().getItems();
        for (String entry : history.split(","))
        {
            try
            {
                int index = Integer.parseInt(entry);
                if (index < 0 || index >= allItems.size())
                {
                    Log.w(TAG, "Found out of range history entry: " + entry);
                    continue;
                }
                _items.add(allItems.get(index));
            }
            catch (NumberFormatException ex)
            {
                Log.w(TAG, "Found non-numeric history entry: " + entry);
            }
        }

        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onPause()
    {
        _items.clear();

        super.onPause();
    }

    @Override
    public void onDestroyView()
    {
        _recyclerView = null;

        _adapter = null;

        _linearLayoutManager = null;

        _provider = null;

        super.onDestroyView();
    }
}
