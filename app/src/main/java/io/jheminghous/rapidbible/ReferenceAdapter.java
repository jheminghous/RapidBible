package io.jheminghous.rapidbible;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReferenceAdapter extends RecyclerView.Adapter<ReferenceAdapter.ViewHolder>
{
    private BibleItem _root;

    private Listener _listener;

    interface Listener
    {
        void onItemSelected(BibleItem item, boolean hardSelect);
    }

    ReferenceAdapter(Listener listener)
    {
        _listener = listener;
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.selectable_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.apply(_root.getChildren().get(position));
    }

    @Override
    public int getItemViewType(int position)
    {
        return _root.getChildren().get(position).getType().ordinal();
    }

    @Override
    public int getItemCount()
    {
        return _root != null ? _root.getChildren().size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView _textView;

        private BibleItem _item;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            _textView = (TextView) itemView;
            _textView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (_listener != null)
                    {
                        _listener.onItemSelected(_item, false);
                    }
                }
            });
            _textView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    if (_listener != null)
                    {
                        _listener.onItemSelected(_item, true);
                    }

                    return true;
                }
            });
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
}
