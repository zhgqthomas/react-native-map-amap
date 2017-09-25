package com.shadark.android.react.amaps.mapview;


import android.view.View;

import com.amap.api.maps.AMap;

public interface IMapView {

    void onMapResume();

    void onMapPause();

    void onMapDestroy();

    AMap getAMap();

    View getView();
}
