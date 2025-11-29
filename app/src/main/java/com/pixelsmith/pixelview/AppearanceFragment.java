package com.pixelsmith.pixelview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.materialswitch.MaterialSwitch;

public class AppearanceFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_appearance, container, false);

        MaterialSwitch systemSwitch = view.findViewById(R.id.systemSwitch);
        MaterialButtonToggleGroup toggleGroup = view.findViewById(R.id.themeToggle);
        MaterialButton lightButton = view.findViewById(R.id.lightButton);
        MaterialButton darkButton = view.findViewById(R.id.darkButton);

        int savedTheme = ThemeHelper.getSavedTheme(requireContext());

        if (savedTheme == ThemeHelper.THEME_SYSTEM) {
            systemSwitch.setChecked(true);
            toggleGroup.clearChecked();
            toggleGroup.setEnabled(false);
        } else {
            systemSwitch.setChecked(false);
            toggleGroup.setEnabled(true);
            toggleGroup.check(savedTheme == ThemeHelper.THEME_LIGHT
                    ? R.id.lightButton
                    : R.id.darkButton);
        }

        systemSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ThemeHelper.saveTheme(requireContext(), ThemeHelper.THEME_SYSTEM);
                requireActivity().recreate();
            } else {
                toggleGroup.setEnabled(true);
            }
        });

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked || systemSwitch.isChecked()) return;

            int selectedTheme = (checkedId == R.id.darkButton)
                    ? ThemeHelper.THEME_DARK
                    : ThemeHelper.THEME_LIGHT;

            ThemeHelper.saveTheme(requireContext(), selectedTheme);
            requireActivity().recreate();
        });

        return view;
    }
}
