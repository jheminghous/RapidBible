package io.jheminghous.rapidbible;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

interface BibleProvider
{
    Toolbar getToolbar();
    void showFragment(Fragment fragment, boolean addToBackStack);

    BibleVersion getVersion();

    BibleItem getCurrentItem();
    void setCurrentItem(BibleItem item);
}
