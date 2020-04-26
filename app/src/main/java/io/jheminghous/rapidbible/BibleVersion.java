package io.jheminghous.rapidbible;

import java.util.Collections;
import java.util.List;

class BibleVersion extends BibleItem
{
    private List<BibleItem> _items;

    BibleVersion(String name, List<BibleItem> items)
    {
        super(Type.VERSION, 0, name, null);

        _items = items;
    }

    List<BibleItem> getItems()
    {
        return Collections.unmodifiableList(_items);
    }
}
