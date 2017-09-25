package com.shadark.android.react.amaps.mapview;

import android.view.View;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;

public interface IMapManager {

    void pushEvent(ThemedReactContext context, View view, String name, WritableMap data);
}
