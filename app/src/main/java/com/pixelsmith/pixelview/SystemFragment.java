package com.pixelsmith.pixelview;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class SystemFragment extends Fragment {

    private int resetStep = 0;
    private Handler handler = new Handler();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Context context = requireContext();

        View view = inflater.inflate(R.layout.fragment_system, container, false);

        MaterialButton restartButton = view.findViewById(R.id.buttonRestart);
        MaterialButton resetButton = view.findViewById(R.id.buttonReset);
        ImageButton buttonGitHub = view.findViewById(R.id.githubButton);
        buttonGitHub.setOnClickListener(v -> {
            String url = "https://github.com/PixelSmith-tech/PixelView"; 
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        });
        restartButton.setOnClickListener(v -> restartLauncher());
        resetButton.setOnClickListener(v -> showResetConfirmation(context));

        return view;
    }

    private void restartLauncher() {
        Intent intent = requireActivity().getPackageManager()
                .getLaunchIntentForPackage(requireActivity().getPackageName());
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            requireActivity().finish();
        }
    }


    private void showResetConfirmation(Context context) {
        if (resetStep >= 3) {
            performReset();
            return;
        }

        int currentStep = resetStep + 1;

        new MaterialAlertDialogBuilder(context)
                .setTitle("Сброс настроек")
                .setMessage("Вы точно хотите сбросить настройки? (" + currentStep + "/3)")
                .setCancelable(false)
                .setPositiveButton("Да", (dialog, which) -> {
                    resetStep++;
                    handler.postDelayed(() -> showResetConfirmation(context), 5000);
                })
                .setNegativeButton("Отмена", (dialog, which) -> resetStep = 0)
                .show();
    }

    private void performReset() {
        SharedPreferences prefs = requireContext().getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

       
        resetStep = 0;

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Сброс выполнен")
                .setMessage("Настройки сброшены. Приложение будет перезапущено.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> restartLauncher())
                .show();
    }
}
