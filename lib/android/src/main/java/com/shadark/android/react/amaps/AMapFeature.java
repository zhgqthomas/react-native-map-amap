package com.shadark.android.react.amaps;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.facebook.react.views.view.ReactViewGroup;

public abstract class AMapFeature extends ReactViewGroup {

    public AMapFeature(Context context) {
        super(context);
    }

    public abstract void addToMap(AMap aMap);

    public abstract void removeFromMap(AMap aMap);

    public abstract Object getFeature();
}
