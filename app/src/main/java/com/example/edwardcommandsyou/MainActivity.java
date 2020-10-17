package com.example.edwardcommandsyou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // don't know if we want last argument to be false or true
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferencesChangedListener);

    }

    private OnSharedPreferenceChangeListener preferencesChangedListener = new OnSharedPreferenceChangeListener() {
        // called when user changes preferences
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            MainActivityFragment gameFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.gameFragment);
            if (s.equals("command_lengths")) {
                gameFragment.updateCommandLength(sharedPreferences);
            }
            else if (s.equals("number_of_rounds")) {
                gameFragment.updateRoundLength(sharedPreferences);
            }
            else if (s.equals("in_reverse")) {
                gameFragment.updateInReverse(sharedPreferences);
            }
        }
    };
}