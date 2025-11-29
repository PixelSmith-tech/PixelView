package com.pixelsmith.pixelview;

import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import android.graphics.Color;
import java.util.*;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RecentAppsFragment extends Fragment {

    private RecyclerView recentAppsView;
    private static final String API_KEY = "API_KEY";
    private AppAdapter adapterRecent;

    private final List<AppItem> appList = new ArrayList<>();
    private static final String TAG = "RecentAppsFragment";

    private TextView timeTextView;
    private TextView weatherTextView;
    private final Handler handler = new Handler();

    private final Runnable timeUpdater = new Runnable() {
        @Override
        public void run() {
            java.text.DateFormat timeFormat = DateFormat.getTimeFormat(requireContext());
            String currentTime = timeFormat.format(new Date());

            if (timeTextView != null) {
                timeTextView.setText(currentTime);
            }

            handler.postDelayed(this, 1000);
        }
    };

    private final BroadcastReceiver weatherUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("com.pixelsmith.pixelview.UPDATE_WEATHER".equals(intent.getAction())) {
                String city = intent.getStringExtra("city");
                if (city != null && !city.isEmpty()) {
                    fetchWeather(city, weatherTextView);
                }
            }
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent_apps, container, false);

        recentAppsView = view.findViewById(R.id.recentApps);

        adapterRecent = new AppAdapter(requireContext(), appList, true, 4);
        recentAppsView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recentAppsView.setAdapter(adapterRecent);

        loadRecentApps();

        ImageButton openSettingsButton = view.findViewById(R.id.settingsButton);
        openSettingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SettingsActivity.class);
            startActivity(intent);
        });

        timeTextView = view.findViewById(R.id.time);
        handler.post(timeUpdater);

        weatherTextView = view.findViewById(R.id.weather);
        weatherTextView.setText("Загрузка погоды...");

        SharedPreferences prefs = requireContext().getSharedPreferences("location_prefs", Context.MODE_PRIVATE);
        String city = prefs.getString("selected_city", "Москва");

        fetchWeather(city, weatherTextView);

        IntentFilter filter = new IntentFilter("com.pixelsmith.pixelview.UPDATE_WEATHER");
        requireContext().registerReceiver(weatherUpdateReceiver, filter, Context.RECEIVER_NOT_EXPORTED);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AccentBackgroundView bg = requireActivity().findViewById(R.id.accentBg);
        if (bg != null) {
            SharedPreferences prefs2 = requireContext().getSharedPreferences("pixelview_prefs", Context.MODE_PRIVATE);
            prefs2.edit().putString("accent_color", "#FF4081").apply();
           
        } else {
            Log.w("AccentDebug", "AccentBackgroundView is null в onViewCreated");
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(timeUpdater);
        requireContext().unregisterReceiver(weatherUpdateReceiver);
    }

    private void fetchWeather(String city, TextView weatherTextView) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://api.openweathermap.org/data/2.5/weather?q=" +
                city + "&appid=" + API_KEY + "&units=metric&lang=ru";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e(TAG, "Ошибка загрузки погоды", e);
                requireActivity().runOnUiThread(() ->
                        weatherTextView.setText("Ошибка загрузки погоды"));
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    try {
                        JSONObject json = new JSONObject(responseData);
                        String description = json.getJSONArray("weather")
                                .getJSONObject(0).getString("description");

                        double temp = json.getJSONObject("main").getDouble("temp");

                        String emoji = getWeatherEmoji(description);
                        String result = String.format(Locale.getDefault(), "%s %.1f°C, %s", emoji, temp, description);

                        requireActivity().runOnUiThread(() ->
                                weatherTextView.setText(city + ": " + result));
                    } catch (JSONException e) {
                        Log.e(TAG, "Ошибка парсинга JSON", e);
                        requireActivity().runOnUiThread(() ->
                                weatherTextView.setText("Ошибка обработки погоды"));
                    }
                } else {
                    Log.w(TAG, "Неверный ответ: " + response.code());
                    requireActivity().runOnUiThread(() ->
                            weatherTextView.setText("Погода не найдена"));
                }
            }
        });
    }

    private void loadRecentApps() {
        SharedPreferences prefs = requireContext().getSharedPreferences("recent_apps", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = prefs.getAll();

        List<Map.Entry<String, ?>> sortedEntries = new ArrayList<>(allEntries.entrySet());

        sortedEntries.sort((e1, e2) -> {
            long val1 = parseLongSafe(e1.getValue());
            long val2 = parseLongSafe(e2.getValue());
            return Long.compare(val2, val1);
        });

        appList.clear();

        PackageManager pm = requireContext().getPackageManager();

        for (Map.Entry<String, ?> entry : sortedEntries) {
            String packageName = entry.getKey();
            try {
                Drawable banner = pm.getApplicationBanner(packageName);
                Drawable iconToUse = banner != null ? banner : pm.getApplicationIcon(packageName);
                boolean isBanner = banner != null;

                CharSequence label = pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0));

                appList.add(new AppItem(label.toString(), iconToUse, packageName, isBanner));
            } catch (Exception e) {
                Log.e(TAG, "Error loading app info for package: " + packageName, e);
            }
        }

        adapterRecent.notifyDataSetChanged();
    }

    private long parseLongSafe(Object value) {
        if (value == null) return 0L;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Failed to parse String to long: " + value);
                return 0L;
            }
        }
        return 0L;
    }
    private String getWeatherEmoji(String description) {
        description = description.toLowerCase(Locale.ROOT);

        if (description.contains("ясно")) return "☀️";
        if (description.contains("облачно") || description.contains("пасмурно")) return "☁️";
        if (description.contains("дожд")) return "🌧️";
        if (description.contains("гроза")) return "⛈️";
        if (description.contains("снег")) return "❄️";
        if (description.contains("туман")) return "🌫️";
        if (description.contains("ветер")) return "🌬️";

        return "🌡️"; 
    }

}
