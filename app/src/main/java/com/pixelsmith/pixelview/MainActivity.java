package com.pixelsmith.pixelview;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;
import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    PageAdapter adapter;
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(1, true); // Переключаем на экран со всеми приложениями
                    return true;
                }
            } else if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (viewPager.getCurrentItem() == 1) {
                    viewPager.setCurrentItem(0, true); // Возвращаемся назад
                    return true;
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);

        List<androidx.fragment.app.Fragment> pages = new ArrayList<>();
        pages.add(new RecentAppsFragment());
        pages.add(new AllAppsFragment());

        adapter = new PageAdapter(this, pages);
        viewPager.setAdapter(adapter);
    }
}
