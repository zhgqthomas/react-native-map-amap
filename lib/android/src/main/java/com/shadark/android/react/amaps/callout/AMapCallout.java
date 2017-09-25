package com.shadark.android.react.amaps.callout;

import android.content.Context;

import com.facebook.react.views.view.ReactViewGroup;

public class AMapCallout extends ReactViewGroup{
    private boolean toolTip = false;
    public int width;
    public int height;


    public AMapCallout(Context context) {
        super(context);
    }

    public boolean hasToolTip() {
        return toolTip;
    }

    public void setToolTip(boolean toolTip) {
        this.toolTip = toolTip;
    }
}
