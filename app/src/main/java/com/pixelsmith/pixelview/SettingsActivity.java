package com.pixelsmith.pixelview;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsActivity extends AppCompatActivity {

    private ListView settingsTabs;
    private FrameLayout settingsContent;

    private final String[] tabTitles = {"\uD83C\uDFA8 Оформление", "⚙\uFE0F Система", "\uD83C\uDF0D Геолокация"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Применяем тему ДО super.onCreate()
        ThemeHelper.applyTheme(this);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 2. Всё как раньше
        settingsTabs = findViewById(R.id.settingsTabs);
        settingsContent = findViewById(R.id.settingsContent);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1, tabTitles);
        settingsTabs.setAdapter(adapter);
        settingsTabs.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        settingsTabs.setItemChecked(0, true);
        loadFragment(new AppearanceFragment());

        settingsTabs.setOnItemClickListener((parent, view, position, id) -> {
            Fragment selectedFragment;
            switch (position) {
                case 0:
                    selectedFragment = new AppearanceFragment();
                    break;
                case 1:
                    selectedFragment = new SystemFragment();
                    break;
                case 2:
                    selectedFragment = new LocationSettingsFragment();
                    break;
                default:
                    return;
            }
            loadFragment(selectedFragment);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settingsContent, fragment)
                .commit();
    }
}
