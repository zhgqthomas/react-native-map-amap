package com.shadark.android.react.amaps.marker;

import android.graphics.Color;
import android.view.View;

import com.amap.api.maps.model.Marker;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.shadark.android.react.amaps.SizeReportingShadowNode;
import com.shadark.android.react.amaps.callout.AMapCallout;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class AMarkerManager extends ViewGroupManager<AMapMarker> {

    private static final String REACT_CLASS = "AMapMarker";
    private static final int SHOW_INFO_WINDOW = 1;
    private static final int HIDE_INFO_WINDOW = 2;

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    protected AMapMarker createViewInstance(ThemedReactContext reactContext) {
        return new AMapMarker(reactContext);
    }

    @ReactProp(name = "coordinate")
    public void setCoordinate(AMapMarker view, ReadableMap map) {
        view.setCoordinate(map);
    }

    @ReactProp(name = "title")
    public void setTitle(AMapMarker view, String title) {
        view.setTitle(title);
    }

    @ReactProp(name = "identifier")
    public void setIdentifier(AMapMarker view, String identifier) {
        view.setIdentifier(identifier);
    }

    @ReactProp(name = "image")
    public void setImage(AMapMarker view, @Nullable String source) {
        view.setImage(source);
    }

    @ReactProp(name = "description")
    public void setDescription(AMapMarker view, String description) {
        view.setSnippet(description);
    }

    @ReactProp(name = "anchor")
    public void setAnchor(AMapMarker view, ReadableMap map) {
        // should default to (0.5, 1) (bottom middle)
        double x = map != null && map.hasKey("x") ? map.getDouble("x") : 0.5;
        double y = map != null && map.hasKey("y") ? map.getDouble("y") : 1.0;
        view.setAnchor(x, y);
    }

    @ReactProp(name = "rotation", defaultFloat = 0.0f)
    public void setMarkerRotation(AMapMarker view, float rotation) {
        view.setRotation(rotation);
    }

    @ReactProp(name = "flat", defaultBoolean = false)
    public void setFlat(AMapMarker view, boolean flat) {
        view.setFlat(flat);
    }

    @ReactProp(name = "draggable", defaultBoolean = false)
    public void setDraggable(AMapMarker view, boolean draggable) {
        view.setDraggable(draggable);
    }

    @ReactProp(name = "pinColor", defaultInt = Color.RED, customType = "Color")
    public void setPinColor(AMapMarker view, int pinColor) {
        float[] hsv = new float[3];
        Color.colorToHSV(pinColor, hsv);
        // NOTE: android only supports a hue
        view.setMarkerHue(hsv[0]);
    }

    @Override
    @ReactProp(name = "zIndex", defaultFloat = 0.0f)
    public void setZIndex(AMapMarker view, float zIndex) {
        super.setZIndex(view, zIndex);
        int integerZIndex = Math.round(zIndex);
        view.setZIndex(integerZIndex);
    }

    @Override
    @ReactProp(name = "opacity", defaultFloat = 1.0f)
    public void setOpacity(AMapMarker view, float opacity) {
        super.setOpacity(view, opacity);
        view.setOpacity(opacity);
    }

    @Override
    public void addView(AMapMarker parent, View child, int index) {
        if (child instanceof AMapCallout) {
            parent.setCalloutView((AMapCallout) child);
        } else {
            super.addView(parent, child, index);
            parent.update();
        }
    }

    @Override
    public void removeViewAt(AMapMarker parent, int index) {
        super.removeViewAt(parent, index);
        parent.update();
    }

    @Override
    @Nullable
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "showCallout", SHOW_INFO_WINDOW,
                "hideCallout", HIDE_INFO_WINDOW
        );
    }

    @Override
    public void receiveCommand(AMapMarker view, int commandId, @Nullable ReadableArray args) {
        switch (commandId) {
            case SHOW_INFO_WINDOW:
                ((Marker) view.getFeature()).showInfoWindow();
                break;

            case HIDE_INFO_WINDOW:
                ((Marker) view.getFeature()).hideInfoWindow();
                break;
        }
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        Map<String, Map<String, String>> map = MapBuilder.of(
                "onPress", MapBuilder.of("registrationName", "onPress"),
                "onCalloutPress", MapBuilder.of("registrationName", "onCalloutPress"),
                "onDragStart", MapBuilder.of("registrationName", "onDragStart"),
                "onDrag", MapBuilder.of("registrationName", "onDrag"),
                "onDragEnd", MapBuilder.of("registrationName", "onDragEnd")
        );

        map.putAll(MapBuilder.of(
                "onDragStart", MapBuilder.of("registrationName", "onDragStart"),
                "onDrag", MapBuilder.of("registrationName", "onDrag"),
                "onDragEnd", MapBuilder.of("registrationName", "onDragEnd")
        ));

        return map;
    }

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        // we use a custom shadow node that emits the width/height of the view
        // after layout with the updateExtraData method. Without this, we can't generate
        // a bitmap of the appropriate width/height of the rendered view.
        return new SizeReportingShadowNode();
    }

    @Override
    public void updateExtraData(AMapMarker view, Object extraData) {
        HashMap<String, Float> data = (HashMap<String, Float>) extraData;
        float width = data.get("width");
        float height = data.get("height");
        view.update((int) width, (int) height);
    }
}
