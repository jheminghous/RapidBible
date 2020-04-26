package io.jheminghous.rapidbible;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class BibleVersion extends BibleItem
{
    private static final String TAG = BibleVersion.class.getSimpleName();

    private List<BibleItem> _items = new ArrayList<>();

    BibleVersion(String name, InputStream inputStream)
    {
        super(Type.VERSION, 0, name, null);

        Pattern pattern = Pattern.compile("(.+) (\\d+):(\\d+)\t(.+)");
        BibleItem book = null;
        BibleItem chapter = null;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
        {
            while (br.ready())
            {
                String line = br.readLine();
                Matcher matcher = pattern.matcher(line);
                if (!matcher.matches())
                {
                    Log.w(TAG, String.format("Unexpected format on line: %s", line));
                    continue;
                }

                String bookName = matcher.group(1);
                int chapterNumber = Integer.parseInt(matcher.group(2));
                int verseNumber = Integer.parseInt(matcher.group(3));
                String verseText = matcher.group(4);

                if (book == null || !book.getText().equals(bookName))
                {
                    if (book != null)
                    {
                        Log.d(TAG, String.format("Added %d chapters", book.getChildren().size()));
                    }
                    Log.d(TAG, String.format("Adding %s", bookName));
                    book = new BibleItem(BibleItem.Type.BOOK,
                                         getChildren().size() + 1,
                                         bookName,
                                         this);
                    _items.add(book);
                    chapter = null;
                }

                if (chapter == null || chapter.getNumber() != chapterNumber)
                {
                    chapter = new BibleItem(BibleItem.Type.CHAPTER, chapterNumber, null, book);
                    _items.add(chapter);
                }

                BibleItem verse = new BibleItem(Type.VERSE, verseNumber, verseText, chapter);
                _items.add(verse);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    List<BibleItem> getItems()
    {
        return Collections.unmodifiableList(_items);
    }
}
