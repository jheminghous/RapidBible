package io.jheminghous.rapidbible;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class BibleItem
{
    enum Type
    {
        VERSION,
        BOOK,
        CHAPTER,
        VERSE,
    }

    private Type _type;
    private int _number;
    private String _text;
    private BibleItem _parent;
    private List<BibleItem> _children = new ArrayList<>();

    BibleItem(Type type, int index, String text, BibleItem parent)
    {
        _type = type;
        _number = index;
        _text = text;
        _parent = parent;

        if (_parent != null)
        {
            _parent._children.add(this);
        }
    }

    Type getType()
    {
        return _type;
    }

    int getNumber()
    {
        return _number;
    }

    String getText()
    {
        return _text;
    }

    BibleItem getParent()
    {
        return _parent;
    }

    List<BibleItem> getChildren()
    {
        return Collections.unmodifiableList(_children);
    }
}
