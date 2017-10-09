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

public class ATextureMapManager extends ViewGroupManager<ATextureMapView> implements IMapManager {

    private static final String REACT_CLASS = "ATextureMapView";
    private static final String TAG = ATextureMapManager.class.getName();

    private final ReactApplicationContext mAppContext;
    private final AMapManagerDelegate mManagerDelegate;

    public ATextureMapManager(ReactApplicationContext appContent) {
        mAppContext = appContent;
        mManagerDelegate = new AMapManagerDelegate();
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected ATextureMapView createViewInstance(ThemedReactContext reactContext) {
        AMapOptions options = new AMapOptions();
        return new ATextureMapView(reactContext, mAppContext, this, options);
    }

    @ReactProp(name = "mapType")
    public void setMapType(ATextureMapView mapView, @Nullable String mapType) {
        mManagerDelegate.setMapType(mapView, mapType);
    }

    @ReactProp(name = "showScaleControl", defaultBoolean = true)
    public void setShowScaleControl(ATextureMapView mapView, boolean showScaleControl) {
        mManagerDelegate.setScaleControlEnabled(mapView, showScaleControl);
    }

    @ReactProp(name = "showZoomControl", defaultBoolean = false)
    public void setShowZoomControl(ATextureMapView mapView, boolean showZoomControl) {
        mManagerDelegate.setShowZoomControl(mapView, showZoomControl);
    }

    @ReactProp(name = "showCompass", defaultBoolean = false)
    public void setShowCompass(ATextureMapView mapView, boolean showCompass) {
        mManagerDelegate.setShowCompass(mapView, showCompass);
    }

    @ReactProp(name = "showMyLocationButton", defaultBoolean = false)
    public void setShowMyLocationButton(ATextureMapView mapView, boolean showMyLocationButton) {
        mManagerDelegate.setShowMyLocationButton(mapView, showMyLocationButton);
    }

    @ReactProp(name = "showMapText", defaultBoolean = true)
    public void setShowMapText(ATextureMapView mapView, boolean showMapText) {
        mManagerDelegate.setShowMapText(mapView, showMapText);
    }

    @ReactProp(name = "showBuildings", defaultBoolean = false)
    public void setShowBuildings(ATextureMapView mapView, boolean showBuildings) {
        mManagerDelegate.setShowBuildings(mapView, showBuildings);
    }

    @ReactProp(name = "showTraffic", defaultBoolean = false)
    public void setShowTraffic(ATextureMapView mapView, boolean showTraffic) {
        mManagerDelegate.setShowTraffic(mapView, showTraffic);
    }

    @ReactProp(name = "scrollEnabled", defaultBoolean = false)
    public void setScrollEnabled(ATextureMapView mapView, boolean scrollEnabled) {
        mManagerDelegate.setScrollEnabled(mapView, scrollEnabled);
    }

    @ReactProp(name = "zoomEnabled", defaultBoolean = false)
    public void setZoomEnabled(ATextureMapView mapView, boolean zoomEnabled) {
        mManagerDelegate.setZoomEnabled(mapView, zoomEnabled);
    }

    @ReactProp(name = "rotateEnabled", defaultBoolean = false)
    public void setRotateEnabled(ATextureMapView mapView, boolean rotateEnabled) {
        mManagerDelegate.setRotateEnabled(mapView, rotateEnabled);
    }

    @ReactProp(name = "moveOnMarkerPress", defaultBoolean = true)
    public void setMoveOnMarkerPress(ATextureMapView viewDelegate, boolean moveOnPress) {
        mManagerDelegate.setMoveOnMarkerPress(viewDelegate, moveOnPress);
    }

    @ReactProp(name = "showsUserLocation", defaultBoolean = false)
    public void setShowsUserLocation(ATextureMapView viewDelegate, boolean showUserLocation) {
        mManagerDelegate.setShowsUserLocation(viewDelegate, showUserLocation);
    }

    @ReactProp(name = "region")
    public void setRegion(ATextureMapView viewDelegate, ReadableMap region) {
        mManagerDelegate.setRegion(viewDelegate, region);
    }

    @ReactProp(name = "initialRegion")
    public void setInitialRegion(ATextureMapView viewDelegate, ReadableMap initialRegion) {
        mManagerDelegate.setInitialRegion(viewDelegate, initialRegion);
    }

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        return mManagerDelegate.createShadowNodeInstance();
    }

    @Override
    public void receiveCommand(ATextureMapView root, int commandId, @Nullable ReadableArray args) {
        mManagerDelegate.receiveCommand(root, commandId, args);
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return mManagerDelegate.getExportedCustomDirectEventTypeConstants();
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return mManagerDelegate.getCommandsMap();
    }

    @Override
    public void addView(ATextureMapView parent, View child, int index) {
        mManagerDelegate.addView(parent, child, index);
    }

    @Override
    public int getChildCount(ATextureMapView parent) {
        return mManagerDelegate.getChildCount(parent);
    }

    @Override
    public View getChildAt(ATextureMapView parent, int index) {
        return mManagerDelegate.getChildAt(parent, index);
    }

    @Override
    public void removeViewAt(ATextureMapView parent, int index) {
        mManagerDelegate.removeViewAt(parent, index);
    }

    @Override
    public void updateExtraData(ATextureMapView root, Object extraData) {
        mManagerDelegate.updateExtraData(root, extraData);
    }

    @Override
    public void onDropViewInstance(ATextureMapView view) {
        mManagerDelegate.onDropViewInstance(view);
        super.onDropViewInstance(view);
    }

    @Override
    public void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
        mManagerDelegate.pushEvent(context, view, name, data);
    }
}
