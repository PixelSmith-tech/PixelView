package com.pixelsmith.pixelview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LocationSettingsFragment extends Fragment {

    private static final String PREFS_NAME = "location_prefs";
    private static final String KEY_SELECTED_CITY = "selected_city";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_location_settings, container, false);

        EditText cityEditText = view.findViewById(R.id.cityEditText);
        Button saveButton = view.findViewById(R.id.saveCityButton);

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String savedCity = prefs.getString(KEY_SELECTED_CITY, "");
        cityEditText.setText(savedCity);

        saveButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();
            if (!city.isEmpty()) {
                prefs.edit().putString(KEY_SELECTED_CITY, city).apply();
                Toast.makeText(requireContext(), "Город сохранён: " + city, Toast.LENGTH_SHORT).show();

       
                Intent intent = new Intent("com.pixelsmith.pixelview.UPDATE_WEATHER");
                intent.putExtra("city", city);
                requireContext().sendBroadcast(intent);

            } else {
                Toast.makeText(requireContext(), "Введите название города", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
