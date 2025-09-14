package com.adalbero.app.lebenindeutschland.ui.settings;

import android.os.Bundle;
import android.os.PersistableBundle;

import com.adalbero.app.lebenindeutschland.R;
import com.adalbero.app.lebenindeutschland.ui.common.AppBaseActivity;

public class SettingsActivity extends AppBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        this.setTitle("Settings");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);

        getFragmentManager().beginTransaction().replace(R.id.fragment, new SettingsFragment()).commit();
    }

}

