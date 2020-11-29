package io.jheminghous.rapidbible;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        new VersionLoader("KJV",
                          getResources().openRawResource(R.raw.kjv),
                          new VersionLoader.Listener()
                          {
                              @Override
                              public void onLoaded(BibleVersion version)
                              {
                                  if (isFinishing()) return;

                                  BibleApplication app = (BibleApplication) getApplication();
                                  app.setVersion(version);

                                  startActivity(new Intent(SplashActivity.this,
                                                           BibleActivity.class));
                                  overridePendingTransition(0, 0);

                                  finish();
                              }
                          });
    }
}
