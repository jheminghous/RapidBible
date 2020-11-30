package io.jheminghous.rapidbible;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment
{
    private BibleProvider _provider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        try
        {
            _provider = (BibleProvider) getActivity();
        }
        catch (ClassCastException e)
        {
            throw new IllegalArgumentException("Activity must implement" +
                                               BibleProvider.class.getSimpleName());
        }

        return inflater.inflate(R.layout.about, container, false);
    }

    @Override
    public void onResume()
    {
        super.onResume();

        _provider.setTitle(getString(R.string.about_title));

        // TODO: populate version with date/hash from last build
    }
}
