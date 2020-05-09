package io.jheminghous.rapidbible;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class TitleUpdater
{
    private static final String TAG = TitleUpdater.class.getSimpleName();

    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _layoutManager;

    TitleUpdater(BibleProvider provider, RecyclerView recyclerView)
    {
        _provider = provider;

        _recyclerView = recyclerView;
        if (_recyclerView.getLayoutManager() instanceof LinearLayoutManager)
        {
            _layoutManager = (LinearLayoutManager) _recyclerView.getLayoutManager();
        }
    }

    void start()
    {
        if (_provider == null)
        {
            Log.e(TAG, "Could not start because the provider is null");
            return;
        }

        if (_layoutManager == null)
        {
            Log.e(TAG, "Could not start because the a linear layout manager was not provided");
            return;
        }

        _recyclerView.addOnScrollListener(_scrollListener);
    }

    void stop()
    {
        _recyclerView.removeOnScrollListener(_scrollListener);
    }

    private RecyclerView.OnScrollListener _scrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
        {
            BibleItem book;
            BibleItem chapter = null;

            int position = _layoutManager.findFirstVisibleItemPosition();
            BibleItem item = _provider.getVersion().getItems().get(position);
            switch (item.getType())
            {
                case VERSE:
                    item = item.getParent();
                    // Intentional fall-through
                case CHAPTER:
                    chapter = item;
                    item = item.getParent();
                    // Intentional fall-through
                case BOOK:
                default:
                    book = item;
            }

            String title = book.getText();
            if (chapter != null)
            {
                title += " " + chapter.getNumber();
            }
            _provider.setTitle(title);
        }
    };
}
