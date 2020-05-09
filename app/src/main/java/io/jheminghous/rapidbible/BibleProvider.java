package io.jheminghous.rapidbible;

import androidx.fragment.app.Fragment;

interface BibleProvider
{
    void setTitle(String title);
    void showFragment(Fragment fragment, boolean addToBackStack);

    BibleVersion getVersion();

    BibleItem getCurrentItem();
    void setCurrentItem(BibleItem item);
}
