package io.jheminghous.rapidbible;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class VersionLoader
{
    private static final String TAG = VersionLoader.class.getSimpleName();

    private Handler _handler = new Handler();

    interface Listener
    {
        void onLoaded(BibleVersion version);
    }

    VersionLoader(final String name, final InputStream inputStream, final Listener listener)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                // On loader thread
                List<BibleItem> items = new ArrayList<>();
                final BibleVersion version = new BibleVersion(name, items);

                BibleItem book = null;
                BibleItem chapter = null;

                long startTime = SystemClock.uptimeMillis();

                try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream)))
                {
                    while (br.ready())
                    {
                        String line = br.readLine();

                        String[] parts = line.split("\t");
                        if (parts.length != 2)
                        {
                            Log.w(TAG, String.format("Unexpected format on line: %s", line));
                            continue;
                        }
                        String verseText = parts[1];

                        int lastSpace = parts[0].lastIndexOf(' ');
                        if (lastSpace <= 0 || lastSpace > parts[0].length() - 4)
                        {
                            Log.w(TAG, String.format("Unexpected format on line: %s", line));
                            continue;
                        }
                        String bookName = parts[0].substring(0, lastSpace);

                        parts = parts[0].substring(lastSpace + 1).split(":");
                        if (parts.length != 2)
                        {
                            Log.w(TAG, String.format("Unexpected format on line: %s", line));
                            continue;
                        }
                        int chapterNumber = Integer.parseInt(parts[0]);
                        int verseNumber = Integer.parseInt(parts[1]);

                        if (book == null || !book.getText().equals(bookName))
                        {
                            book = new BibleItem(BibleItem.Type.BOOK,
                                                 version.getChildren().size() + 1,
                                                 bookName,
                                                 version);
                            items.add(book);
                            chapter = null;
                        }

                        if (chapter == null || chapter.getNumber() != chapterNumber)
                        {
                            chapter = new BibleItem(BibleItem.Type.CHAPTER,
                                                    chapterNumber,
                                                    null,
                                                    book);
                            items.add(chapter);
                        }

                        BibleItem verse = new BibleItem(BibleItem.Type.VERSE,
                                                        verseNumber,
                                                        verseText,
                                                        chapter);
                        items.add(verse);
                    }
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                long endTime = SystemClock.uptimeMillis();
                Log.i(TAG, String.format("Loaded %s in %d milliseconds",
                                         name,
                                         endTime - startTime));

                _handler.post(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // On UI thread
                        listener.onLoaded(version);
                    }
                });
            }
        }).start();
    }
}
