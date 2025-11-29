package com.pixelsmith.pixelview;
import android.graphics.drawable.Drawable;
public class AppItem {
    public String label;
    public Drawable icon;
    public String packageName;
    public boolean isBanner; // <--- вот это добавь

    public AppItem(String label, Drawable icon, String packageName, boolean isBanner) {
        this.label = label;
        this.icon = icon;
        this.packageName = packageName;
        this.isBanner = isBanner;
    }
}
