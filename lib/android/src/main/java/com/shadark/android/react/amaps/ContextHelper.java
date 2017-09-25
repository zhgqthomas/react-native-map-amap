package com.shadark.android.react.amaps;

import android.content.Context;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;

public final class ContextHelper {

    private ContextHelper() {
    }

    // This solution solve problems like
    // https://github.com/airbnb/react-native-maps/issues/271
    // https://github.com/airbnb/react-native-maps/issues/1147
    // Just in case it also happen on AMap
    public static Context getNonBuggyContext(ThemedReactContext reactContext,
                                             ReactApplicationContext appContext) {
        Context superContext = reactContext;
        if (!contextHasBug(appContext.getCurrentActivity())) {
            superContext = appContext.getCurrentActivity();
        } else if (contextHasBug(superContext)) {
            if (!contextHasBug(reactContext.getCurrentActivity())) {
                superContext = reactContext.getCurrentActivity();
            } else if (!contextHasBug(reactContext.getApplicationContext())) {
                superContext = reactContext.getApplicationContext();
            }
        }

        return superContext;
    }

    public static boolean contextHasBug(Context context) {
        return context == null ||
                context.getResources() == null ||
                context.getResources().getConfiguration() == null;
    }
}
