package com.shadark.android.react.amaps;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

public class RegionChangeEvent extends Event<RegionChangeEvent> {

    private final LatLngBounds bounds;
    private final LatLng center;
    private final boolean continuous;

    public RegionChangeEvent(int id, LatLngBounds bounds, LatLng center, boolean continuous) {
        super(id);
        this.bounds = bounds;
        this.center = center;
        this.continuous = continuous;
    }

    @Override
    public String getEventName() {
        return "topChange";
    }

    @Override
    public boolean canCoalesce() {
        return false;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        WritableMap event = Arguments.createMap();
        event.putBoolean("continuous", continuous);

        WritableMap region = Arguments.createMap();
        region.putDouble("latitude", center.latitude);
        region.putDouble("longitude", center.longitude);
        region.putDouble("latitudeDelta", bounds.northeast.latitude - bounds.southwest.latitude);
        region.putDouble("longitudeDelta", bounds.northeast.longitude - bounds.southwest.longitude);
        event.putMap("region", region);

        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), event);
    }
}
