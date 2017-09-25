package com.shadark.android.react.amaps.mapview;

import android.view.MotionEvent;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.shadark.android.react.amaps.ContextHelper;


public final class ATextureMapView extends TextureMapView implements IMapView, IViewDelegate {

    private AMapViewViewDelegate mViewDelegate;

    public ATextureMapView(ThemedReactContext reactContext, ReactApplicationContext appContext,
                           IMapManager manager, AMapOptions aMapOptions) {
        super(ContextHelper.getNonBuggyContext(reactContext, appContext), aMapOptions);

        super.onCreate(null);
        mViewDelegate = new AMapViewViewDelegate(reactContext, appContext, manager, this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mViewDelegate.mapViewDispatchTouchEvent(ev);
        super.dispatchTouchEvent(ev);
        return true;
    }

    @Override
    public void onMapResume() {
        ATextureMapView.this.onResume();
    }

    @Override
    public void onMapPause() {
        ATextureMapView.this.onPause();
    }

    @Override
    public void onMapDestroy() {
        ATextureMapView.this.onDestroy();
    }

    @Override
    public AMap getAMap() {
        return ATextureMapView.this.getMap();
    }

    @Override
    public View getView() {
        return ATextureMapView.this;
    }

    @Override
    public void setMoveOnMarkerPress(boolean moveOnMarkerPress) {
        mViewDelegate.setMoveOnMarkerPress(moveOnMarkerPress);
    }

    @Override
    public void setShowUserLocation(boolean showUserLocation) {
        mViewDelegate.setShowUserLocation(showUserLocation);
    }

    @Override
    public void setInitialRegion(ReadableMap initialRegion) {
        mViewDelegate.setInitialRegion(initialRegion);
    }

    @Override
    public void setRegion(ReadableMap region) {
        mViewDelegate.setRegion(region);
    }

    @Override
    public void setHandlePanDrag(boolean handlePanDrag) {
        mViewDelegate.setHandlePanDrag(handlePanDrag);
    }

    @Override
    public void animateToRegion(LatLngBounds bounds, int duration) {
        mViewDelegate.animateToRegion(bounds, duration);
    }

    @Override
    public void animateToCoordinate(LatLng coordinate, int duration) {
        mViewDelegate.animateToCoordinate(coordinate, duration);
    }

    @Override
    public void fitToElements(boolean animated) {
        mViewDelegate.fitToElements(animated);
    }

    @Override
    public void fitToSuppliedMarkers(ReadableArray markerIDsArray, boolean animated) {
        mViewDelegate.fitToSuppliedMarkers(markerIDsArray, animated);
    }

    @Override
    public void fitToCoordinates(ReadableArray coordinatesArray, ReadableMap edgePadding, boolean animated) {
        mViewDelegate.fitToCoordinates(coordinatesArray, edgePadding, animated);
    }

    @Override
    public void addFeature(View child, int index) {
        mViewDelegate.addFeature(child, index);
    }

    @Override
    public int getFeatureCount() {
        return mViewDelegate.getFeatureCount();
    }

    @Override
    public View getFeatureAt(int index) {
        return mViewDelegate.getFeatureAt(index);
    }

    @Override
    public void removeFeatureAt(int index) {
        mViewDelegate.removeFeatureAt(index);
    }

    @Override
    public void updateExtraData(Object extraData) {
        mViewDelegate.updateExtraData(extraData);
    }

    @Override
    public void doDestroy() {
        mViewDelegate.doDestroy();
    }
}
