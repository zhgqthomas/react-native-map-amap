package com.shadark.android.react.amaps.mapview;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Handler;
import android.support.v4.content.PermissionChecker;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.VisibleRegion;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.shadark.android.react.amaps.AMapFeature;
import com.shadark.android.react.amaps.ContextHelper;
import com.shadark.android.react.amaps.LatLngBoundsUtils;
import com.shadark.android.react.amaps.RegionChangeEvent;
import com.shadark.android.react.amaps.marker.AMapMarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final class AMapViewViewDelegate implements AMap.OnMapLoadedListener, IViewDelegate {

    private static final String[] PERMISSIONS = new String[]{
            "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};
    private static final int BASE_MAP_PADDING = 50;

    private boolean isDestroyed = false;
    private boolean isPaused = false;
    private final ThemedReactContext mReactContext;
    private final Context mContext;
    private final IMapView mMapView;
    private final IMapManager mMapManager;
    private LifecycleEventListener mlifecycleListener;
    private final EventDispatcher mEventDispatcher;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetectorCompat mGestureDetector;

    private boolean isMoveOnMarkerPress = true;
    private boolean isShowUserLocation = false;
    private boolean isTouchDown = false;
    private boolean isMonitoringRegion = false;
    private boolean isInitialRegionSet = false;
    private boolean isHandlePanDrag = false;
    private LatLngBounds mLastBoundsEmitted;
    private LatLngBounds mBounds2Move;

    private final Map<Marker, AMapMarker> mMarkerMap = new HashMap<>();
    private final List<AMapFeature> mMapFeatures = new ArrayList<>();

    private final Handler mTimerHandler = new Handler();

    AMapViewViewDelegate(ThemedReactContext reactContext,
                         ReactApplicationContext appContext,
                         IMapManager manager,
                         IMapView mapView) {
        mReactContext = reactContext;
        mContext = ContextHelper.getNonBuggyContext(reactContext, appContext);
        mMapManager = manager;
        mMapView = mapView;

        mapView.getAMap().setOnMapLoadedListener(this);

        setScaleGestureDetector();
        setGestureDetector();
        mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
    }

    @Override
    public void onMapLoaded() {
        mMapManager.pushEvent(mReactContext, mMapView.getView(), "onMapLoaded", Arguments.createMap());

        setMarkerClickListener();
        setPolylineClickListener();
        setInfoWindowClickListener();
        setCameraChangeListener();
        setMapClickListener();
        setMapLongClickListener();
        setMarkerClickListener();
        setLifecycleEventListener();
    }

    private void setGestureDetector() {
        mGestureDetector = new GestureDetectorCompat(mReactContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        startMonitoringRegion();
                        return false;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                        if (isHandlePanDrag) {
                            onPanDrag(e2);
                        }
                        startMonitoringRegion();
                        return false;
                    }
                });
    }

    private void setScaleGestureDetector() {
        mScaleDetector = new ScaleGestureDetector(mReactContext,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScaleBegin(ScaleGestureDetector detector) {
                        startMonitoringRegion();
                        return true; // stop recording this gesture. let mapview handle it.
                    }
                });
    }

    public void mapViewDispatchTouchEvent(MotionEvent motionEvent) {
        mScaleDetector.onTouchEvent(motionEvent);
        mGestureDetector.onTouchEvent(motionEvent);

        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMapView.getView().getParent().requestDisallowInterceptTouchEvent(
                        mMapView.getAMap() != null && mMapView.getAMap().getUiSettings().isScrollGesturesEnabled());
                isTouchDown = true;
                break;

            case MotionEvent.ACTION_MOVE:
                startMonitoringRegion();
                break;

            case MotionEvent.ACTION_UP:
                mMapView.getView().getParent().requestDisallowInterceptTouchEvent(false);
                isTouchDown = false;
                break;
        }
    }

    private void setMarkerClickListener() {
        mMapView.getAMap().setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                WritableMap event;
                AMapMarker aMapMarker = mMarkerMap.get(marker);

                event = makeClickEventData(marker.getPosition());
                event.putString("action", "marker-press");
                event.putString("id", aMapMarker.getIdentifier());

                mMapManager.pushEvent(mReactContext, mMapView.getView(), "onMarkerPress", event);

                mMapManager.pushEvent(mReactContext, aMapMarker, "onPress", event);

                if (isMoveOnMarkerPress) {
                    return false;
                } else {
                    marker.showInfoWindow();
                    return true;
                }
            }
        });
    }

    private void setPolylineClickListener() {
        mMapView.getAMap().setOnPolylineClickListener(new AMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                WritableMap event = makeClickEventData(polyline.getPoints().get(0));
                event.putString("action", "polyline-press");

                // TODO: push polyline event
            }
        });
    }

    private void setInfoWindowClickListener() {
        mMapView.getAMap().setOnInfoWindowClickListener(new AMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                WritableMap event;

                event = makeClickEventData(marker.getPosition());
                event.putString("action", "callout-press");
                mMapManager.pushEvent(mReactContext, mMapView.getView(), "onCalloutPress", event);

                AMapMarker aMapMarker = mMarkerMap.get(marker);
                mMapManager.pushEvent(mReactContext, aMapMarker, "onCalloutPress", event);

                // TODO: push infoWindow event
            }
        });
    }

    private void setMapClickListener() {
        mMapView.getAMap().setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                WritableMap event = makeClickEventData(latLng);
                event.putString("action", "press");
                mMapManager.pushEvent(mReactContext, mMapView.getView(), "onPress", event);
            }
        });
    }

    private void setMapLongClickListener() {
        mMapView.getAMap().setOnMapLongClickListener(new AMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                WritableMap event = makeClickEventData(latLng);
                event.putString("action", "long-press");
                mMapManager.pushEvent(mReactContext, mMapView.getView(), "onLongPress", event);
            }
        });
    }

    private void setCameraChangeListener() {
        mMapView.getAMap().setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLngBounds bounds = mMapView.getAMap().getProjection().getVisibleRegion().latLngBounds;
                LatLng center = cameraPosition.target;
                mLastBoundsEmitted = bounds;
                mEventDispatcher.dispatchEvent(new RegionChangeEvent(mMapView.getView().getId(), bounds, center, isTouchDown));
                stopMonitoringRegion();
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
            }
        });
    }

    private void setLifecycleEventListener() {
        // We need to be sure to disable location-tracking when app enters background, in-case some
        // other module
        // has acquired a wake-lock and is controlling location-updates, otherwise, location-manager
        // will be left
        // updating location constantly, killing the battery, even though some other location-mgmt
        // module may
        // desire to shut-down location-services.
        mlifecycleListener = new LifecycleEventListener() {
            @Override
            public void onHostResume() {
                if (hasPermissions()) {
                    mMapView.getAMap().setMyLocationEnabled(isShowUserLocation);
                }

                synchronized (AMapViewViewDelegate.this) {
                    if (!isDestroyed) {
                        mMapView.onMapResume();
                    }

                    isPaused = false;
                }
            }

            @Override
            public void onHostPause() {
                if (hasPermissions()) {
                    mMapView.getAMap().setMyLocationEnabled(false);
                }

                synchronized (AMapViewViewDelegate.this) {
                    if (!isDestroyed) {
                        mMapView.onMapResume();
                    }

                    isPaused = true;
                }
            }

            @Override
            public void onHostDestroy() {
                doDestroy();
            }
        };

        mReactContext.addLifecycleEventListener(mlifecycleListener);
    }

    private void onPanDrag(MotionEvent motionEvent) {
        Point point = new Point((int) motionEvent.getX(), (int) motionEvent.getY());
        LatLng coords = mMapView.getAMap().getProjection().fromScreenLocation(point);
        WritableMap event = makeClickEventData(coords);
        mMapManager.pushEvent(mReactContext, mMapView.getView(), "onPanDrag", event);
    }

    private void startMonitoringRegion() {
        if (mMapView.getAMap() == null || isMonitoringRegion) {
            return;
        }

        mTimerHandler.postDelayed(mTimerRunnable, 100);
        isMonitoringRegion = true;
    }

    private void stopMonitoringRegion() {
        if (mMapView.getAMap() == null || isMonitoringRegion) {
            return;
        }

        mTimerHandler.removeCallbacks(mTimerRunnable);
        isMonitoringRegion = false;
    }

    private Runnable mTimerRunnable = new Runnable() {
        @Override
        public void run() {
            if (mMapView.getAMap() != null) {
                Projection projection = mMapView.getAMap().getProjection();
                VisibleRegion region = projection != null ? projection.getVisibleRegion() : null;
                LatLngBounds bounds = region != null ? region.latLngBounds : null;

                if ((bounds != null) &&
                        (mLastBoundsEmitted == null) ||
                        LatLngBoundsUtils.isBoundsDifferent(bounds, mLastBoundsEmitted)) {
                    LatLng center = mMapView.getAMap().getCameraPosition().target;
                    mLastBoundsEmitted = bounds;
                    mEventDispatcher.dispatchEvent(new RegionChangeEvent(mMapView.getView().getId(), bounds, center, true));
                }
            }

            mTimerHandler.postDelayed(this, 100);
        }
    };

    private WritableMap makeClickEventData(LatLng point) {
        WritableMap event = Arguments.createMap();
        WritableMap coordinate = Arguments.createMap();

        coordinate.putDouble("latitude", point.latitude);
        coordinate.putDouble("longitude", point.longitude);
        event.putMap("coordinate", coordinate);

        Projection projection = mMapView.getAMap().getProjection();
        Point screenPoint = projection.toScreenLocation(point);

        WritableMap position = Arguments.createMap();
        position.putDouble("x", screenPoint.x);
        position.putDouble("y", screenPoint.y);
        event.putMap("position", position);

        return event;
    }

    private synchronized void destroy() {
        if (isDestroyed) {
            return;
        }

        isDestroyed = true;

        if (mlifecycleListener != null && mReactContext != null) {
            mReactContext.removeLifecycleEventListener(mlifecycleListener);
            mlifecycleListener = null;
        }

        if (!isPaused) {
            mMapView.onMapPause();
            isPaused = true;
        }

        mMapView.onMapDestroy();
    }

    private boolean hasPermissions() {
        return PermissionChecker.checkSelfPermission(mContext, PERMISSIONS[0]) == PackageManager.PERMISSION_GRANTED ||
                PermissionChecker.checkSelfPermission(mContext, PERMISSIONS[1]) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void setMoveOnMarkerPress(boolean moveOnMarkerPress) {
        isMoveOnMarkerPress = moveOnMarkerPress;
    }

    @Override
    public void setShowUserLocation(boolean showUserLocation) {
        isShowUserLocation = showUserLocation;
    }

    @Override
    public void setInitialRegion(ReadableMap initialRegion) {
        if (!isInitialRegionSet && initialRegion != null) {
            setRegion(initialRegion);
            isInitialRegionSet = true;
        }
    }

    @Override
    public void setRegion(ReadableMap region) {
        if (region == null) {
            return;
        }

        Double lng = region.getDouble("longitude");
        Double lat = region.getDouble("latitude");
        Double lngDelta = region.getDouble("longitudeDelta");
        Double latDelta = region.getDouble("latitudeDelta");
        LatLngBounds bounds = new LatLngBounds(
                new LatLng(lat - latDelta / 2, lng - lngDelta / 2), // southwest
                new LatLng(lat + latDelta / 2, lng + lngDelta / 2)  // northeast
        );

        if (mMapView.getView().getHeight() <= 0 || mMapView.getView().getWidth() <= 0) {
            // in this case, our map has not been laid out yet, so we save the bounds in a local
            // variable, and make a guess of zoomLevel 10. Not to worry, though: as soon as layout
            // occurs, we will move the camera to the saved bounds. Note that if we tried to move
            // to the bounds now, it would trigger an exception.
            mMapView.getAMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10));
            mBounds2Move = bounds;
        } else {
            mMapView.getAMap().moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));
            mBounds2Move = null;
        }
    }

    @Override
    public void setHandlePanDrag(boolean handlePanDrag) {
        isHandlePanDrag = handlePanDrag;
    }

    @Override
    public void animateToRegion(LatLngBounds bounds, int duration) {
        if (mMapView.getAMap() != null) {
            startMonitoringRegion();
            mMapView.getAMap().animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0), duration, null);
        }
    }

    @Override
    public void animateToCoordinate(LatLng coordinate, int duration) {
        if (mMapView.getAMap() != null) {
            startMonitoringRegion();
            mMapView.getAMap().animateCamera(CameraUpdateFactory.newLatLng(coordinate), duration, null);
        }
    }

    @Override
    public void fitToElements(boolean animated) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        boolean addedPosition = false;

        for (AMapFeature feature : mMapFeatures) {
            if (feature instanceof AMapMarker) {
                Marker marker = (Marker) feature.getFeature();
                builder.include(marker.getPosition());
                addedPosition = true;
            }
        }

        if (addedPosition) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, BASE_MAP_PADDING);
            if (animated) {
                startMonitoringRegion();
                mMapView.getAMap().animateCamera(cameraUpdate);
            } else {
                mMapView.getAMap().moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void fitToSuppliedMarkers(ReadableArray markerIDsArray, boolean animated) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        final int size = markerIDsArray.size();
        String[] markerIds = new String[size];
        for (int i = 0; i < size; i++) {
            markerIds[i] = markerIDsArray.getString(i);
        }

        boolean addedPosition = false;

        List<String> markerIdList = Arrays.asList(markerIds);
        for (AMapFeature feature : mMapFeatures) {
            if (feature instanceof AMapMarker) {
                String identifier = ((AMapMarker) feature).getIdentifier();
                Marker marker = (Marker) feature.getFeature();
                if (markerIdList.contains(identifier)) {
                    builder.include(marker.getPosition());
                    addedPosition = true;
                }
            }
        }

        if (addedPosition) {
            LatLngBounds bounds = builder.build();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, BASE_MAP_PADDING);
            if (animated) {
                startMonitoringRegion();
                mMapView.getAMap().animateCamera(cameraUpdate);
            } else {
                mMapView.getAMap().moveCamera(cameraUpdate);
            }
        }
    }

    @Override
    public void fitToCoordinates(ReadableArray coordinatesArray, ReadableMap edgePadding, boolean animated) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        final int size = coordinatesArray.size();
        for (int i = 0; i < size; i++) {
            ReadableMap latLng = coordinatesArray.getMap(i);
            Double lat = latLng.getDouble("latitude");
            Double lng = latLng.getDouble("longitude");
            builder.include(new LatLng(lat, lng));
        }

        LatLngBounds bounds = builder.build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, BASE_MAP_PADDING);

        if (animated) {
            startMonitoringRegion();
            mMapView.getAMap().animateCamera(cameraUpdate);
        } else {
            mMapView.getAMap().moveCamera(cameraUpdate);
        }
    }

    @Override
    public void addFeature(View child, int index) {
        // Our desired API is to pass up annotations/overlays as children to the mapview component.
        // This is where we intercept them and do the appropriate underlying mapview action.
        if (child instanceof AMapMarker) {
            AMapMarker annotation = (AMapMarker) child;
            annotation.addToMap(mMapView.getAMap());
            mMapFeatures.add(index, annotation);
            Marker marker = (Marker) annotation.getFeature();
            mMarkerMap.put(marker, annotation);
        } else {
            ViewGroup children = (ViewGroup) child;
            int count = children.getChildCount();
            for (int i = 0; i < count; i++) {
                addFeature(children.getChildAt(i), index);
            }
        }
    }

    @Override
    public int getFeatureCount() {
        return mMapFeatures.size();
    }

    @Override
    public View getFeatureAt(int index) {
        return mMapFeatures.get(index);
    }

    @Override
    public void removeFeatureAt(int index) {
        AMapFeature feature = mMapFeatures.remove(index);
        if (feature instanceof AMapMarker) {
            mMarkerMap.remove(feature.getFeature());
        }

        feature.removeFromMap(mMapView.getAMap());
    }

    @Override
    public void updateExtraData(Object extraData) {
        if (mBounds2Move != null) {
            HashMap<String, Float> data = (HashMap<String, Float>) extraData;
            float width = data.get("width");
            float height = data.get("height");

            mMapView.getAMap().moveCamera(
                    CameraUpdateFactory.newLatLngBounds(mBounds2Move, (int) width, (int) height, 0)
            );

            mBounds2Move = null;
        }
    }

    @Override
    public void doDestroy() {
        destroy();
    }
}
