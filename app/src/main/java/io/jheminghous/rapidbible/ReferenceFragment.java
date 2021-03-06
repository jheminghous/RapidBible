package io.jheminghous.rapidbible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReferenceFragment extends Fragment
{
    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _linearLayoutManager;
    private ReferenceAdapter _adapter;

    private HistoryFragment _historyFragment;

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

        _adapter = new ReferenceAdapter(getContext(), _provider);
        _recyclerView.setAdapter(_adapter);

        _historyFragment = new HistoryFragment();

        setHasOptionsMenu(true);

        return _recyclerView;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater)
    {
        inflater.inflate(R.menu.reference, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.history_item)
        {
            _provider.showFragment(_historyFragment, true);
            return true;
        }
        return false;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        _provider.setTitle(getString(R.string.reference_title));

        BibleItem book;

        BibleItem currentItem = _provider.getCurrentItem();
        switch (currentItem.getType())
        {
            default:
            case BOOK:
                book = currentItem;
                break;
            case CHAPTER:
                book = currentItem.getParent();
                break;
            case VERSE:
                book = currentItem.getParent().getParent();
                break;
        }

        _adapter.setRoot(book.getParent());
        _adapter.select(book);
        _linearLayoutManager.scrollToPosition(book.getNumber() - 1);
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
