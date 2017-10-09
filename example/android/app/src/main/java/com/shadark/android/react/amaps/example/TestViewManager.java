package com.shadark.android.react.amaps.example;

import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.shadark.android.react.amaps.ContextHelper;

import java.util.Map;

import javax.annotation.Nullable;

public class TestViewManager extends SimpleViewManager<ImageView> {

    private static final String TAG = "Thomas";

    private EventDispatcher mEventDispatcher;

    private ReactApplicationContext applicationContext;

    public TestViewManager(ReactApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getName() {
        return "RCTMyCustomView";
    }

    @Override
    protected ImageView createViewInstance(ThemedReactContext reactContext) {
        ImageView view = new ImageView(applicationContext);
        view.setBackgroundColor(Color.BLUE);
        view.setId(R.id.image_id);
        Log.d(TAG, "create view id: " + view.getId());
        mEventDispatcher = reactContext.getNativeModule(UIManagerModule.class).getEventDispatcher();
        return view;
    }

    @Override
    protected void addEventEmitters(ThemedReactContext reactContext, ImageView view) {
        super.addEventEmitters(reactContext, view);

        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(view.getId(), "onMapPress", null);
        reactContext.getJSModule(RCTEventEmitter.class)
                .receiveEvent(view.getId(), "topSelect", null);
        mEventDispatcher.dispatchEvent(new ChangeEvent(view.getId()));
    }

    @Nullable
    @Override
    public Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                "onMapPress", MapBuilder.of("registrationName", "onMapPress")
        );
    }

    private static class ChangeEvent extends Event<ChangeEvent> {

        public ChangeEvent(int viewTag) {
            super(viewTag);
            Log.d("Thomas", "view id: " + viewTag);
        }

        @Override
        public String getEventName() {
            return "topChange";
        }

        @Override
        public void dispatch(RCTEventEmitter rctEventEmitter) {
            rctEventEmitter.receiveEvent(getViewTag(), getEventName(), Arguments.createMap());
        }
    }
}
