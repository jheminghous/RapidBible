package io.jheminghous.rapidbible;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.ViewHolder>
{
    private BibleVersion _version;

    BibleAdapter(BibleVersion version)
    {
        _version = version;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == BibleItem.Type.BOOK.ordinal())
        {
            return new ViewHolder(inflater.inflate(R.layout.book, parent, false));
        }
        else if (viewType == BibleItem.Type.CHAPTER.ordinal())
        {
            return new ViewHolder(inflater.inflate(R.layout.chapter, parent, false));
        }
        else if (viewType == BibleItem.Type.VERSE.ordinal())
        {
            return new ViewHolder(inflater.inflate(R.layout.verse, parent, false));
        }

        throw new IllegalArgumentException();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        holder.apply(_version.getItems().get(position));
    }

    @Override
    public int getItemViewType(int position)
    {
        return _version.getItems().get(position).getType().ordinal();
    }

    @Override
    public int getItemCount()
    {
        return _version.getItems().size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        private TextView _textView;

        ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            _textView = (TextView) itemView;
        }

        void apply(BibleItem item)
        {
            switch (item.getType())
            {
                case BOOK:
                    _textView.setText(item.getText());
                    break;
                case CHAPTER:
                    _textView.setText(String.format(Locale.getDefault(),
                                                    "Chapter %d",
                                                    item.getNumber()));
                    break;
                case VERSE:
                    _textView.setText(String.format(Locale.getDefault(),
                                                    "%d %s",
                                                    item.getNumber(),
                                                    item.getText()));
                    break;
            }
        }
    }
}
