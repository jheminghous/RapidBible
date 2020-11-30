package io.jheminghous.rapidbible;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder>
{
    private BibleProvider _provider;
    private List<BibleItem> _items;

    HistoryAdapter(@NonNull BibleProvider provider, @NonNull List<BibleItem> items)
    {
        _provider = provider;
        _items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        return new ViewHolder(inflater.inflate(R.layout.selectable_list_item,
                                               parent,
                                               false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.apply(_items.get(position));
    }

    @Override
    public int getItemCount()
    {
        return _items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView _textView;

        private BibleItem _item;

        public ViewHolder(@NonNull View itemView)
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

        void apply(BibleItem item)
        {
            _item = item;

            switch (_item.getType())
            {
                case BOOK:
                    _textView.setText(_item.getText());
                    break;
                case CHAPTER:
                    _textView.setText(String.format(Locale.getDefault(),
                                                    "%s %d",
                                                    _item.getParent().getText(),
                                                    _item.getNumber()));
                    break;
                case VERSE:
                    _textView.setText(String.format(Locale.getDefault(),
                                                    "%s %d:%d",
                                                    _item.getParent().getParent().getText(),
                                                    _item.getParent().getNumber(),
                                                    _item.getNumber()));
                    break;
            }
        }
    }
}
