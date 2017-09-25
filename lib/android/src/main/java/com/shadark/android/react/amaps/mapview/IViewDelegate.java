package com.shadark.android.react.amaps.mapview;

import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;

public interface IViewDelegate {

    void setMoveOnMarkerPress(boolean moveOnMarkerPress);

    void setShowUserLocation(boolean showUserLocation);

    void setInitialRegion(ReadableMap initialRegion);

    void setRegion(ReadableMap region);

    void setHandlePanDrag(boolean handlePanDrag);

    void animateToRegion(LatLngBounds bounds, int duration);

    void animateToCoordinate(LatLng coordinate, int duration);

    void fitToElements(boolean animated);

    void fitToSuppliedMarkers(ReadableArray markerIDsArray, boolean animated);

    void fitToCoordinates(ReadableArray coordinatesArray, ReadableMap edgePadding,
                          boolean animated);

    void addFeature(View child, int index);

    int getFeatureCount();

    View getFeatureAt(int index);

    void removeFeatureAt(int index);

    void updateExtraData(Object extraData);

    void doDestroy();
}
