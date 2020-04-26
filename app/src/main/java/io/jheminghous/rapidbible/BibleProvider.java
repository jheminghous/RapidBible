package io.jheminghous.rapidbible;

import androidx.appcompat.widget.Toolbar;

public interface BibleProvider
{
    BibleVersion getVersion();
    Toolbar getToolbar();

    BibleItem getCurrentItem();
    void setCurrentItem(BibleItem item);
}
