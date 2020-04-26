package io.jheminghous.rapidbible;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class TitleUpdater
{
    private static final String TAG = TitleUpdater.class.getSimpleName();

    private Toolbar _toolBar;

    private RecyclerView _recylerView;
    private LinearLayoutManager _layoutManager;

    private BibleVersion _version;

    TitleUpdater(Toolbar toolbar, RecyclerView recyclerView, BibleVersion version)
    {
        _toolBar = toolbar;

        _recylerView = recyclerView;
        if (_recylerView.getLayoutManager() instanceof LinearLayoutManager)
        {
            _layoutManager = (LinearLayoutManager) _recylerView.getLayoutManager();
        }

        _version = version;
    }

    void start()
    {
        if (_toolBar == null)
        {
            Log.e(TAG, "Could not start because the tool bar is null");
            return;
        }

        if (_layoutManager == null)
        {
            Log.e(TAG, "Could not start because the a linear layout manager was not provided");
            return;
        }

        _recylerView.addOnScrollListener(_scrollListener);
    }

    void stop()
    {
        _recylerView.removeOnScrollListener(_scrollListener);
    }

    private RecyclerView.OnScrollListener _scrollListener = new RecyclerView.OnScrollListener()
    {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy)
        {
            BibleItem book;
            BibleItem chapter = null;

            int position = _layoutManager.findFirstVisibleItemPosition();
            BibleItem item = _version.getItems().get(position);
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
            _toolBar.setTitle(title);
        }
    };
}
