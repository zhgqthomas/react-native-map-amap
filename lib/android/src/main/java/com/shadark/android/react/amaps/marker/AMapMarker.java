package com.shadark.android.react.amaps.marker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.react.bridge.ReadableMap;
import com.shadark.android.react.amaps.AMapFeature;
import com.shadark.android.react.amaps.callout.AMapCallout;

import javax.annotation.Nullable;

public final class AMapMarker extends AMapFeature {

    private final Context mContext;

    private Marker mMarker;
    private MarkerOptions mMarkerOptions;
    private int mWidth;
    private int mHeight;
    private String mIdentifier;

    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    private BitmapDescriptor mIconBitmapDescriptor;
    private Bitmap mIconBitmap;
    private float mMarkerHue = 0.0f; // should be between 0 and 360

    private float mRotation = 0.0f;
    private boolean isFlat = false;
    private boolean isDraggable = false;
    private int mZIndex = 0;
    private float mOpacity = 1.0f;

    private boolean mAnchorIsSet;
    private float mAnchorX;
    private float mAnchorY;

    private AMapCallout mMapCallout;
    private float mCalloutAnchorX;
    private float mCalloutAnchorY;
    private boolean mCalloutAnchorIsSet;

    private boolean hasCustomMarkerView = false;

    private final DraweeHolder<?> logoHolder;
    private DataSource<CloseableReference<CloseableImage>> mDataSource;

    private final ControllerListener<ImageInfo> mLogoControllerListener =
            new BaseControllerListener<ImageInfo>() {
                @Override
                public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable animatable) {
                    CloseableReference<CloseableImage> imageReference = null;
                    try {
                        imageReference = mDataSource.getResult();
                        if (imageReference != null) {
                            CloseableImage image = imageReference.get();
                            if (image != null && image instanceof CloseableStaticBitmap) {
                                CloseableStaticBitmap closeableStaticBitmap = (CloseableStaticBitmap) image;
                                Bitmap bitmap = closeableStaticBitmap.getUnderlyingBitmap();
                                if (bitmap != null) {
                                    bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    mIconBitmap = bitmap;
                                    mIconBitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);
                                }
                            }
                        }
                    } finally {
                        mDataSource.close();
                        if (imageReference != null) {
                            CloseableReference.closeSafely(imageReference);
                        }
                    }
                }
            };

    public AMapMarker(Context context) {
        super(context);
        mContext = context;
        logoHolder = DraweeHolder.create(createDraweeHierarchy(), context);
        logoHolder.onAttach();
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);

        // if children are added, it means we are rendering a custom marker
        if (!(child instanceof AMapCallout)) {
            hasCustomMarkerView = true;
        }

        update();
    }

    @Override
    public void addToMap(AMap aMap) {
        mMarker = aMap.addMarker(getMarkerOptions());
    }

    @Override
    public void removeFromMap(AMap aMap) {
        mMarker.remove();
        mMarker = null;
    }

    @Override
    public Object getFeature() {
        return mMarker;
    }

    private GenericDraweeHierarchy createDraweeHierarchy() {
        return new GenericDraweeHierarchyBuilder(getResources())
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setFadeDuration(0)
                .build();
    }

    public void setImage(String uri) {
        if (uri == null) {
            mIconBitmapDescriptor = null;
            update();
        } else if (uri.startsWith("http://") || uri.startsWith("https://") ||
                uri.startsWith("file://")) {
            ImageRequest imageRequest = ImageRequestBuilder
                    .newBuilderWithSource(Uri.parse(uri))
                    .build();

            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            mDataSource = imagePipeline.fetchDecodedImage(imageRequest, this);
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequest)
                    .setControllerListener(mLogoControllerListener)
                    .setOldController(logoHolder.getController())
                    .build();
            logoHolder.setController(controller);
        } else {
            mIconBitmapDescriptor = getBitmapDescriptorByName(uri);
            if (mIconBitmapDescriptor != null) {
                mIconBitmap = BitmapFactory.decodeResource(getResources(), getDrawableResourceByName(uri));
            }

            update();
        }
    }

    public void setCoordinate(ReadableMap coordinate) {
        mPosition = new LatLng(coordinate.getDouble("latitude"), coordinate.getDouble("longitude"));
        if (mMarker != null) {
            mMarker.setPosition(mPosition);
        }

        update();
    }

    public void setIdentifier(String identifier) {
        mIdentifier = identifier;
        update();
    }

    public String getIdentifier() {
        return mIdentifier;
    }

    public void setTitle(String title) {
        mTitle = title;
        if (mMarker != null) {
            mMarker.setTitle(title);
        }

        update();
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
        if (mMarker != null) {
            mMarker.setSnippet(snippet);
        }

        update();
    }

    public void setRotation(float rotation) {
        mRotation = rotation;
        if (mMarker != null) {
            mMarker.setRotateAngle(rotation);
        }

        update();
    }

    public void setFlat(boolean flat) {
        isFlat = flat;
        if (mMarker != null) {
            mMarker.setFlat(flat);
        }

        update();
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
        if (mMarker != null) {
            mMarker.setDraggable(draggable);
        }

        update();
    }

    public void setZIndex(int zIndex) {
        mZIndex = zIndex;
        if (mMarker != null) {
            mMarker.setZIndex(zIndex);
        }

        update();
    }

    public void setOpacity(float opacity) {
        mOpacity = opacity;
        if (mMarker != null) {
            mMarker.setAlpha(opacity);
        }

        update();
    }

    public void setMarkerHue(float markerHue) {
        mMarkerHue = markerHue;
        update();
    }

    public void setCalloutView(AMapCallout callout) {
        mMapCallout = callout;
    }

    public AMapCallout getCalloutView() {
        return mMapCallout;
    }

    public void setAnchor(double x, double y) {
        mAnchorIsSet = true;
        mAnchorX = (float) x;
        mAnchorY = (float) y;
        if (mMarker != null) {
            mMarker.setAnchor(mAnchorX, mAnchorY);
        }

        update();
    }

    public void update(int width, int height) {
        mWidth = width;
        mHeight = height;
        update();
    }

    public void update() {
        if (mMarker != null) {
            return;
        }

        mMarker.setIcon(getIcon());

        if (mAnchorIsSet) {
            mMarker.setAnchor(mAnchorX, mAnchorY);
        } else {
            mMarker.setAnchor(0.5f, 1.0f);
        }

        // TODO: 设置 callout anchor
    }

    private BitmapDescriptor getIcon() {
        if (hasCustomMarkerView) {

            if (mIconBitmapDescriptor != null) {
                Bitmap viewBitmap = createDrawable();
                int width = Math.max(mIconBitmap.getWidth(), viewBitmap.getWidth());
                int height = Math.max(mIconBitmap.getHeight(), viewBitmap.getHeight());

                Bitmap combinedBitmap = Bitmap.createBitmap(width, height, mIconBitmap.getConfig());
                Canvas canvas = new Canvas(combinedBitmap);
                canvas.drawBitmap(mIconBitmap, 0, 0, null);
                canvas.drawBitmap(viewBitmap, 0, 0, null);
                return BitmapDescriptorFactory.fromBitmap(combinedBitmap);
            } else {
                return BitmapDescriptorFactory.fromBitmap(createDrawable());
            }
        } else if (mIconBitmapDescriptor != null) {
            return mIconBitmapDescriptor;
        } else {
            return BitmapDescriptorFactory.defaultMarker(mMarkerHue);
        }
    }

    public MarkerOptions getMarkerOptions() {
        if (mMarkerOptions == null) {
            mMarkerOptions = createMarkerOptions();
        }

        return mMarkerOptions;
    }

    private BitmapDescriptor getBitmapDescriptorByName(String name) {
        return BitmapDescriptorFactory.fromResource(getDrawableResourceByName(name));
    }

    private int getDrawableResourceByName(String name) {
        return getResources().getIdentifier(
                name,
                "drawable",
                getContext().getPackageName());
    }

    private MarkerOptions createMarkerOptions() {
        MarkerOptions options = new MarkerOptions().position(mPosition);
        if (mAnchorIsSet) options.anchor(mAnchorX, mAnchorY);
        options.title(mTitle);
        options.snippet(mSnippet);
        options.rotateAngle(mRotation);
        options.setFlat(isFlat);
        options.draggable(isDraggable);
        options.zIndex(mZIndex);
        options.alpha(mOpacity);
        options.icon(getIcon());
        return options;
    }

    private Bitmap createDrawable() {
        int width = mWidth <= 0 ? 100 : mWidth;
        int height = mHeight <= 0 ? 100 : mHeight;
        this.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }
}
