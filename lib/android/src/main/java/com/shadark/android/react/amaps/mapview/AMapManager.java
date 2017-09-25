package com.shadark.android.react.amaps.mapview;

import android.view.View;

import com.amap.api.maps.AMapOptions;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nullable;


public final class AMapManager extends ViewGroupManager<AMapView> implements IMapManager {

    private static final String REACT_CLASS = "AMapView";
    private static final String TAG = AMapManager.class.getName();

    private final ReactApplicationContext mAppContext;
    private final AMapManagerDelegate mManagerDelegate;

    public AMapManager(ReactApplicationContext appContext) {
        mAppContext = appContext;
        mManagerDelegate = new AMapManagerDelegate();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AMapView createViewInstance(ThemedReactContext reactContext) {
        AMapOptions options = new AMapOptions();
        options.zoomControlsEnabled(false);
        return new AMapView(reactContext, mAppContext, this, options);
    }

    @ReactProp(name = "mapType")
    public void setMapType(AMapView mapView, @Nullable String mapType) {
        mManagerDelegate.setMapType(mapView, mapType);
    }

    @ReactProp(name = "showScaleControl", defaultBoolean = true)
    public void setShowScaleControl(AMapView mapView, boolean showScaleControl) {
        mManagerDelegate.setScaleControlEnabled(mapView, showScaleControl);
    }

    @ReactProp(name = "showZoomControl", defaultBoolean = false)
    public void setShowZoomControl(AMapView mapView, boolean showZoomControl) {
        mManagerDelegate.setShowZoomControl(mapView, showZoomControl);
    }

    @ReactProp(name = "showCompass", defaultBoolean = false)
    public void setShowCompass(AMapView mapView, boolean showCompass) {
        mManagerDelegate.setShowCompass(mapView, showCompass);
    }

    @ReactProp(name = "showMyLocationButton", defaultBoolean = false)
    public void setShowMyLocationButton(AMapView mapView, boolean showMyLocationButton) {
        mManagerDelegate.setShowMyLocationButton(mapView, showMyLocationButton);
    }

    @ReactProp(name = "showMapText", defaultBoolean = true)
    public void setShowMapText(AMapView mapView, boolean showMapText) {
        mManagerDelegate.setShowMapText(mapView, showMapText);
    }

    @ReactProp(name = "showBuildings", defaultBoolean = false)
    public void setShowBuildings(AMapView mapView, boolean showBuildings) {
        mManagerDelegate.setShowBuildings(mapView, showBuildings);
    }

    @ReactProp(name = "showTraffic", defaultBoolean = false)
    public void setShowTraffic(AMapView mapView, boolean showTraffic) {
        mManagerDelegate.setShowTraffic(mapView, showTraffic);
    }

    @ReactProp(name = "scrollEnabled", defaultBoolean = false)
    public void setScrollEnabled(AMapView mapView, boolean scrollEnabled) {
        mManagerDelegate.setScrollEnabled(mapView, scrollEnabled);
    }

    @ReactProp(name = "zoomEnabled", defaultBoolean = false)
    public void setZoomEnabled(AMapView mapView, boolean zoomEnabled) {
        mManagerDelegate.setZoomEnabled(mapView, zoomEnabled);
    }

    @ReactProp(name = "rotateEnabled", defaultBoolean = false)
    public void setRotateEnabled(AMapView mapView, boolean rotateEnabled) {
        mManagerDelegate.setRotateEnabled(mapView, rotateEnabled);
    }

    @ReactProp(name = "moveOnMarkerPress", defaultBoolean = true)
    public void setMoveOnMarkerPress(AMapView viewDelegate, boolean moveOnPress) {
        mManagerDelegate.setMoveOnMarkerPress(viewDelegate, moveOnPress);
    }

    @ReactProp(name = "showsUserLocation", defaultBoolean = false)
    public void setShowsUserLocation(AMapView viewDelegate, boolean showUserLocation) {
        mManagerDelegate.setShowsUserLocation(viewDelegate, showUserLocation);
    }

    @ReactProp(name = "region")
    public void setRegion(AMapView viewDelegate, ReadableMap region) {
        mManagerDelegate.setRegion(viewDelegate, region);
    }

    @ReactProp(name = "initialRegion")
    public void setInitialRegion(AMapView viewDelegate, ReadableMap initialRegion) {
        mManagerDelegate.setInitialRegion(viewDelegate, initialRegion);
    }

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        return mManagerDelegate.createShadowNodeInstance();
    }

    @Override
    public void receiveCommand(AMapView root, int commandId, @Nullable ReadableArray args) {
        mManagerDelegate.receiveCommand(root, commandId, args);
    }

    @Nullable
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return mManagerDelegate.getExportedCustomDirectEventTypeConstants();
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return mManagerDelegate.getCommandsMap();
    }

    @Override
    public void addView(AMapView parent, View child, int index) {
        mManagerDelegate.addView(parent, child, index);
    }

    @Override
    public int getChildCount(AMapView parent) {
        return mManagerDelegate.getChildCount(parent);
    }

    @Override
    public View getChildAt(AMapView parent, int index) {
        return mManagerDelegate.getChildAt(parent, index);
    }

    @Override
    public void removeViewAt(AMapView parent, int index) {
        mManagerDelegate.removeViewAt(parent, index);
    }

    @Override
    public void updateExtraData(AMapView root, Object extraData) {
        mManagerDelegate.updateExtraData(root, extraData);
    }

    @Override
    public void onDropViewInstance(AMapView view) {
        mManagerDelegate.onDropViewInstance(view);
        super.onDropViewInstance(view);
    }

    @Override
    public void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
        mManagerDelegate.pushEvent(context, view, name, data);
    }
}
