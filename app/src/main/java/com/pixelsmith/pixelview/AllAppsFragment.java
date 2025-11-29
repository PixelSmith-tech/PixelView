package com.pixelsmith.pixelview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import java.util.*;

public class AllAppsFragment extends Fragment {

    RecyclerView allAppsView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_all_apps, container, false);
        AccentBackgroundView bg = requireActivity().findViewById(R.id.accentBg);
        SharedPreferences prefs = requireContext().getSharedPreferences("pixelview_prefs", Context.MODE_PRIVATE);
        prefs.edit().putString("accent_color", "#FF4081").apply();

// обновляем вручную

        allAppsView = view.findViewById(R.id.allApps);
        loadAllApps();
        return view;
    }

    private void loadAllApps() {
        PackageManager pm = requireContext().getPackageManager();
        List<ApplicationInfo> installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppItem> appList = new ArrayList<>();

        for (ApplicationInfo appInfo : installedApps) {
            Log.d("ALL_APPS", appInfo.packageName);

            Intent launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName);
            if (launchIntent != null) {
                Drawable iconOrBanner = getBannerOrIcon(pm, appInfo);
                String label = appInfo.loadLabel(pm).toString();
                boolean isBanner = isBannerDrawable(iconOrBanner);

                appList.add(new AppItem(label, iconOrBanner, appInfo.packageName, isBanner));
            }
        }

        appList.sort((a, b) -> a.label.compareToIgnoreCase(b.label));

        AppAdapter adapter = new AppAdapter(requireContext(), appList, false, -1);

        allAppsView.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        allAppsView.setAdapter(adapter);
    }

    private Drawable getBannerOrIcon(PackageManager pm, ApplicationInfo appInfo) {
        try {
            Drawable banner = pm.getApplicationBanner(appInfo.packageName);
            if (banner != null) {
                return banner;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return appInfo.loadIcon(pm);
    }

    private boolean isBannerDrawable(Drawable drawable) {
        return drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight();
    }
}
