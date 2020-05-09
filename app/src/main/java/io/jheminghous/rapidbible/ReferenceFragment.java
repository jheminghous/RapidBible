package io.jheminghous.rapidbible;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

public class ReferenceFragment extends Fragment
{
    private static final String TAG = ReferenceFragment.class.getSimpleName();

    private BibleProvider _provider;

    private TabLayout _tabLayout;

    private RecyclerView _recyclerView;
    private LinearLayoutManager _linearLayoutManager;
    private GridLayoutManager _gridLayoutManager;

    private ReferenceAdapter _adapter;

    private BibleItem _currentItem;

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

        View view = inflater.inflate(R.layout.reference, container, false);

        _tabLayout = view.findViewById(R.id.tab_layout);
        _tabLayout.addOnTabSelectedListener(_tabListener);

        _recyclerView = view.findViewById(R.id.list);
        _linearLayoutManager = new LinearLayoutManager(getContext());
        _gridLayoutManager = new GridLayoutManager(getContext(), 4);
        _recyclerView.setLayoutManager(_linearLayoutManager);

        _adapter = new ReferenceAdapter(_referenceListener);
        _recyclerView.setAdapter(_adapter);

        _currentItem = _provider.getCurrentItem();

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        _tabListener.onTabSelected(_tabLayout.getTabAt(0));
    }

    @Override
    public void onDestroyView()
    {
        _tabLayout.removeOnTabSelectedListener(_tabListener);
        _tabLayout = null;

        _recyclerView = null;

        _adapter = null;

        _linearLayoutManager = null;
        _gridLayoutManager = null;

        _provider = null;

        super.onDestroyView();
    }

    private TabLayout.OnTabSelectedListener _tabListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            int position = tab.getPosition();

            BibleItem root = null;
            int scrollPosition = 0;

            if (position == 0)
            {
                _recyclerView.setLayoutManager(_linearLayoutManager);

                switch (_currentItem.getType())
                {
                    case BOOK:
                        root = _currentItem.getParent();
                        scrollPosition = _currentItem.getNumber() - 1;
                        break;
                    case CHAPTER:
                        root = _currentItem.getParent().getParent();
                        scrollPosition = _currentItem.getParent().getNumber() - 1;
                        break;
                    case VERSE:
                        root = _currentItem.getParent().getParent().getParent();
                        scrollPosition = _currentItem.getParent().getParent().getNumber() - 1;
                        break;
                }

                _adapter.setRoot(root);
                _linearLayoutManager.scrollToPosition(scrollPosition);

                _provider.setTitle("Reference");
            }
            else if (position == 1)
            {
                _recyclerView.setLayoutManager(_gridLayoutManager);

                switch (_currentItem.getType())
                {
                    case BOOK:
                        root = _currentItem;
                        scrollPosition = 0;
                        break;
                    case CHAPTER:
                        root = _currentItem.getParent();
                        scrollPosition = _currentItem.getNumber() - 1;
                        break;
                    case VERSE:
                        root = _currentItem.getParent().getParent();
                        scrollPosition = _currentItem.getParent().getNumber() - 1;
                        break;
                }

                _adapter.setRoot(root);
                _gridLayoutManager.scrollToPosition(scrollPosition);

                _provider.setTitle("Reference - " + _adapter.getRoot().getText());
            }
            else if (position == 2)
            {
                _recyclerView.setLayoutManager(_gridLayoutManager);

                switch (_currentItem.getType())
                {
                    case BOOK:
                        root = _currentItem.getChildren().get(0);
                        scrollPosition = 0;
                        break;
                    case CHAPTER:
                        root = _currentItem;
                        scrollPosition = 0;
                        break;
                    case VERSE:
                        root = _currentItem.getParent();
                        scrollPosition = _currentItem.getNumber() - 1;
                        break;
                }

                if (root == null)
                {
                    Log.wtf(TAG, "The root item is null");
                    return;
                }

                _adapter.setRoot(root);
                _gridLayoutManager.scrollToPosition(scrollPosition);

                _provider.setTitle("Reference - " + root.getParent().getText() + " " +
                                   root.getNumber());
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab)
        {
            // Do nothing
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab)
        {
            // Do nothing
        }
    };

    private ReferenceAdapter.Listener _referenceListener = new ReferenceAdapter.Listener()
    {
        @Override
        public void onItemSelected(BibleItem item, boolean hardSelect)
        {
            _currentItem = item;

            switch (_currentItem.getType())
            {
                case BOOK:
                    if (!hardSelect)
                    {
                        TabLayout.Tab chapterTab = _tabLayout.getTabAt(1);
                        if (chapterTab == null)
                        {
                            Log.wtf(TAG, "The chapter tab is null");
                            return;
                        }
                        chapterTab.select();
                        break;
                    }
                    // Intentional fallthrough
                case CHAPTER:
                    if (!hardSelect)
                    {
                        TabLayout.Tab verseTab = _tabLayout.getTabAt(2);
                        if (verseTab == null)
                        {
                            Log.wtf(TAG, "The verse tab is null");
                            return;
                        }
                        verseTab.select();
                        break;
                    }
                // Intentional fallthrough
                case VERSE:
                    _provider.setCurrentItem(_currentItem);
                    break;
            }
        }
    };
}
