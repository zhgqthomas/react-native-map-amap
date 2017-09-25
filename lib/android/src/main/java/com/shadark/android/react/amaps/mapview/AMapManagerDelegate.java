package com.shadark.android.react.amaps.mapview;

import android.util.Log;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.MyTrafficStyle;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.shadark.android.react.amaps.SizeReportingShadowNode;

import java.util.Map;

import javax.annotation.Nullable;

public class AMapManagerDelegate {

    private static final String TAG = AMapManagerDelegate.class.getName();
    private static final int ANIMATE_TO_REGION = 1;
    private static final int ANIMATE_TO_COORDINATE = 2;
    private static final int FIT_TO_ELEMENTS = 3;
    private static final int FIT_TO_SUPPLIED_MARKERS = 4;
    private static final int FIT_TO_COORDINATES = 5;

    private final Map<String, Integer> MAP_TYPES = MapBuilder.of(
            "standard", AMap.MAP_TYPE_NORMAL,
            "satellite", AMap.MAP_TYPE_SATELLITE,
            "night", AMap.MAP_TYPE_NIGHT,
            "navi", AMap.MAP_TYPE_NAVI,
            "none", AMap.MAP_TYPE_BUS
    );

    public void setMapType(IMapView mapView, @Nullable String mapType) {
        int typeId = MAP_TYPES.get(mapType);
        mapView.getAMap().setMapType(typeId);
    }

    public void setScaleControlEnabled(IMapView mapView, boolean showScaleControl) {
        mapView.getAMap().getUiSettings().setScaleControlsEnabled(showScaleControl);
    }

    public void setShowZoomControl(IMapView mapView, boolean showZoomControl) {
        Log.d(TAG, "zoom control: " + showZoomControl);
        mapView.getAMap().getUiSettings().setZoomControlsEnabled(showZoomControl);
    }

    public void setShowCompass(IMapView mapView, boolean showCompass) {
        mapView.getAMap().getUiSettings().setCompassEnabled(showCompass);
    }

    public void setShowMyLocationButton(IMapView mapView, boolean showMyLocationButton) {
        mapView.getAMap().getUiSettings().setMyLocationButtonEnabled(showMyLocationButton);
    }

    public void setShowMapText(IMapView mapView, boolean showMapText) {
        mapView.getAMap().showMapText(showMapText);
    }

    public void setShowBuildings(IMapView mapView, boolean showBuildings) {
        mapView.getAMap().showBuildings(showBuildings);
    }

    public void setShowTraffic(IMapView mapView, boolean showTraffic) {
        mapView.getAMap().setTrafficEnabled(showTraffic);
        if (showTraffic) {
            MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
            myTrafficStyle.setSeriousCongestedColor(0xff92000a);
            myTrafficStyle.setCongestedColor(0xffea0312);
            myTrafficStyle.setSlowColor(0xffff7508);
            myTrafficStyle.setSmoothColor(0xff00a209);
            mapView.getAMap().setMyTrafficStyle(myTrafficStyle);
        }
    }

    public void setScrollEnabled(IMapView mapView, boolean scrollEnabled) {
        mapView.getAMap().getUiSettings().setScrollGesturesEnabled(scrollEnabled);
    }

    public void setZoomEnabled(IMapView mapView, boolean zoomEnabled) {
        mapView.getAMap().getUiSettings().setZoomGesturesEnabled(zoomEnabled);
    }

    public void setRotateEnabled(IMapView mapView, boolean rotateEnabled) {
        mapView.getAMap().getUiSettings().setRotateGesturesEnabled(rotateEnabled);
    }

    public void setMoveOnMarkerPress(IViewDelegate viewDelegate, boolean moveOnPress) {
        viewDelegate.setMoveOnMarkerPress(moveOnPress);
    }

    public void setShowsUserLocation(IViewDelegate viewDelegate, boolean showUserLocation) {
        viewDelegate.setShowUserLocation(showUserLocation);
    }

    public void setInitialRegion(IViewDelegate viewDelegate, ReadableMap initialRegion) {
        viewDelegate.setInitialRegion(initialRegion);
    }

    public void setRegion(IViewDelegate viewDelegate, ReadableMap region) {
        viewDelegate.setRegion(region);
    }

    public LayoutShadowNode createShadowNodeInstance() {
        return new SizeReportingShadowNode();
    }

    public void receiveCommand(IViewDelegate viewDelegate, int commandId, @Nullable ReadableArray args) {
        if (args == null) {
            return;
        }

        Integer duration;
        Double lat;
        Double lng;
        Double latDelta;
        Double lngDelta;
        ReadableMap region;

        switch (commandId) {
            case ANIMATE_TO_REGION:
                region = args.getMap(0);
                duration = args.getInt(1);
                lng = region.getDouble("longitude");
                lat = region.getDouble("latitude");
                lngDelta = region.getDouble("longitudeDelta");
                latDelta = region.getDouble("latitudeDelta");

                LatLngBounds bounds = new LatLngBounds(
                        new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                        new LatLng(lat + latDelta / 2, lng + lngDelta / 2) // northeast
                );

                viewDelegate.animateToRegion(bounds, duration);
                break;

            case ANIMATE_TO_COORDINATE:
                region = args.getMap(0);
                duration = args.getInt(1);
                lng = region.getDouble("longitude");
                lat = region.getDouble("latitude");

                viewDelegate.animateToCoordinate(new LatLng(lat, lng), duration);
                break;

            case FIT_TO_ELEMENTS:
                viewDelegate.fitToElements(args.getBoolean(0));
                break;

            case FIT_TO_SUPPLIED_MARKERS:
                viewDelegate.fitToSuppliedMarkers(args.getArray(0), args.getBoolean(1));
                break;

            case FIT_TO_COORDINATES:
                viewDelegate.fitToCoordinates(args.getArray(0), args.getMap(1), args.getBoolean(2));
                break;
        }
    }

    public Map getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> map = MapBuilder.of(
                "onMapReady", MapBuilder.of("registrationName", "onMapReady"),
                "onPress", MapBuilder.of("registrationName", "onPress"),
                "onLongPress", MapBuilder.of("registrationName", "onLongPress"),
                "onMarkerPress", MapBuilder.of("registrationName", "onMarkerPress"),
                "onMarkerSelect", MapBuilder.of("registrationName", "onMarkerSelect"),
                "onMarkerDeselect", MapBuilder.of("registrationName", "onMarkerDeselect"),
                "onCalloutPress", MapBuilder.of("registrationName", "onCalloutPress")
        );

        map.putAll(MapBuilder.of(
                "onMarkerDragStart", MapBuilder.of("registrationName", "onMarkerDragStart"),
                "onMarkerDrag", MapBuilder.of("registrationName", "onMarkerDrag"),
                "onMarkerDragEnd", MapBuilder.of("registrationName", "onMarkerDragEnd"),
                "onPanDrag", MapBuilder.of("registrationName", "onPanDrag")
        ));

        return map;
    }

    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "animateToRegion", ANIMATE_TO_REGION,
                "animateToCoordinate", ANIMATE_TO_COORDINATE,
                "fitToElements", FIT_TO_ELEMENTS,
                "fitToSuppliedMarkers", FIT_TO_SUPPLIED_MARKERS,
                "fitToCoordinates", FIT_TO_COORDINATES
        );
    }

    public void addView(IViewDelegate viewDelegate, View child, int index) {
        viewDelegate.addFeature(child, index);
    }

    public int getChildCount(IViewDelegate viewDelegate) {
        return viewDelegate.getFeatureCount();
    }

    public View getChildAt(IViewDelegate viewDelegate, int index) {
        return viewDelegate.getFeatureAt(index);
    }

    public void removeViewAt(IViewDelegate viewDelegate, int index) {
        viewDelegate.removeFeatureAt(index);
    }

    public void updateExtraData(IViewDelegate viewDelegate, Object extraData) {
        viewDelegate.updateExtraData(extraData);
    }

    public void onDropViewInstance(IViewDelegate viewDelegate) {
        viewDelegate.doDestroy();
    }

    public void pushEvent(ThemedReactContext context, View view, String name, WritableMap data) {
        context.getJSModule(RCTEventEmitter.class)
                .receiveEvent(view.getId(), name, data);
    }
}
