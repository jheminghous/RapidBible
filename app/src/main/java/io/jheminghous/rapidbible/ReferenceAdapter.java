package io.jheminghous.rapidbible;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ReferenceAdapter extends RecyclerView.Adapter<ReferenceAdapter.ViewHolder>
{
    private BibleProvider _provider;

    private RecyclerView _recyclerView;
    private GridLayoutManager _gridLayoutManager;

    private ReferenceAdapter _adapter;

    private BibleItem _root;

    private ExpandableViewHolder _expanded;

    ReferenceAdapter(Context context, @NonNull BibleProvider provider)
    {
        _provider = provider;

        if (context != null)
        {
            _recyclerView = new RecyclerView(context);

            _gridLayoutManager = new GridLayoutManager(context, 4);
            _recyclerView.setLayoutManager(_gridLayoutManager);

            _adapter = new ReferenceAdapter(null, provider);
            _recyclerView.setAdapter(_adapter);
        }
    }

    BibleItem getRoot()
    {
        return _root;
    }

    void setRoot(BibleItem root)
    {
        _root = root;
        notifyDataSetChanged();
    }

    void select(BibleItem item)
    {
        if (_adapter != null)
        {
            _adapter.setRoot(item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (_recyclerView == null)
        {
            return new SimpleViewHolder(inflater.inflate(R.layout.selectable_list_item,
                                                         parent,
                                                         false));
        }

        return new ExpandableViewHolder(inflater.inflate(R.layout.expandable_list_item,
                                                         parent,
                                                         false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.apply(_root.getChildren().get(position));
    }

    @Override
    public int getItemCount()
    {
        return _root != null ? _root.getChildren().size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        protected TextView _textView;

        protected BibleItem _item;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
        }

        void apply(BibleItem item)
        {
            _item = item;

            switch (_item.getType())
            {
                case BOOK:
                    _textView.setText(_item.getText());
                    _textView.setGravity(Gravity.START);
                    break;
                case CHAPTER:
                case VERSE:
                    _textView.setText(String.valueOf(_item.getNumber()));
                    _textView.setGravity(Gravity.CENTER);
                    break;
            }
        }
    }

    class SimpleViewHolder extends ViewHolder
    {
        SimpleViewHolder(@NonNull View itemView)
        {
            super(itemView);

            _textView = (TextView) itemView;

            _textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    _provider.setCurrentItem(_item);
                }
            });
        }
    }

    class ExpandableViewHolder extends ViewHolder
    {
        protected ViewGroup _expandableLayout;

        ExpandableViewHolder(@NonNull View itemView)
        {
            super(itemView);

            _textView = itemView.findViewById(R.id.text);
            _expandableLayout = itemView.findViewById(R.id.expandable_layout);

            _textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (_expandableLayout.getVisibility() != View.VISIBLE)
                    {
                        if (_item.getChildren().size() == 1)
                        {
                            _provider.setCurrentItem(_item);
                            return;
                        }

                        _adapter.setRoot(_item);
                        expand();
                    }
                    else
                    {
                        collapse();
                        _adapter.setRoot(null);
                    }
                }
            });
            _textView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    _provider.setCurrentItem(_item);
                    return true;
                }
            });

            _expandableLayout.setVisibility(View.GONE);
        }

        @Override
        void apply(BibleItem item)
        {
            super.apply(item);

            if (_item == _adapter.getRoot() && _item.getChildren().size() > 1)
            {
                expand();
            }
            else
            {
                collapse();
            }
        }

        void expand()
        {
            if (_expanded == this) return;

            if (_expanded != null)
            {
                _expanded.collapse();
            }

            _expanded = this;
            _expandableLayout.addView(_recyclerView);
            _expandableLayout.setVisibility(View.VISIBLE);
        }

        void collapse()
        {
            if (_expanded != this) return;

            _expandableLayout.setVisibility(View.GONE);
            _expandableLayout.removeView(_recyclerView);
            _expanded = null;
        }
    }
}
