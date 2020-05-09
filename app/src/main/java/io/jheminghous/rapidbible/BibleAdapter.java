package io.jheminghous.rapidbible;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Locale;

public class BibleAdapter extends RecyclerView.Adapter<BibleAdapter.ViewHolder>
{
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final int BOOK_SIZE_OFFSET = 4;
    private static final int CHAPTER_SIZE_OFFSET = 2;

    private BibleVersion _version;

    BibleAdapter(BibleVersion version)
    {
        _version = version;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int baseFontSize = DEFAULT_FONT_SIZE;
        try
        {
            String fontSize = preferences.getString(context.getString(R.string.font_size_key),
                                                    String.valueOf(DEFAULT_FONT_SIZE));
            if (fontSize != null)
            {
                baseFontSize = Integer.parseInt(fontSize);
            }
        }
        catch (Exception e)
        {
            // Do nothing
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        TextView view;
        if (viewType == BibleItem.Type.BOOK.ordinal())
        {
            view = (TextView) inflater.inflate(R.layout.book, parent, false);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, baseFontSize + BOOK_SIZE_OFFSET);
        }
        else if (viewType == BibleItem.Type.CHAPTER.ordinal())
        {
            view = (TextView) inflater.inflate(R.layout.chapter, parent, false);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, baseFontSize + CHAPTER_SIZE_OFFSET);
        }
        else if (viewType == BibleItem.Type.VERSE.ordinal())
        {
            view = (TextView) inflater.inflate(R.layout.verse, parent, false);
            view.setTextSize(TypedValue.COMPLEX_UNIT_SP, baseFontSize);
        }
        else
        {
            throw new IllegalArgumentException();
        }

        return new ViewHolder(view);
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

    static class ViewHolder extends RecyclerView.ViewHolder
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
