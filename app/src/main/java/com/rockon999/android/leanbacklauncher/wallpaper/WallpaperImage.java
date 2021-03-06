package com.rockon999.android.leanbacklauncher.wallpaper;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.rockon999.android.leanbacklauncher.recline.util.RefcountBitmapDrawable;

public class WallpaperImage extends AppCompatImageView {
    protected float mZoom;

    public WallpaperImage(Context context) {
        this(context, null);
    }

    public WallpaperImage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WallpaperImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mZoom = 0.0f;
    }

    protected boolean setFrame(int l, int t, int r, int b) {
        boolean changed = super.setFrame(l, t, r, b);
        setScaleMatrix(getDrawable());
        return changed;
    }

    public void setImageDrawable(Drawable drawable) {
        Drawable previousDrawable = getDrawable();
        addRef(drawable);
        super.setImageDrawable(drawable);
        releaseRef(previousDrawable);
    }

    private static void releaseRef(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) drawable).getRefcountObject().releaseRef();
        }
    }

    private static void addRef(Drawable drawable) {
        if (drawable instanceof RefcountBitmapDrawable) {
            ((RefcountBitmapDrawable) drawable).getRefcountObject().addRef();
        }
    }

    public void setZoomLevel(float zoom) {
        this.mZoom = zoom;
        setScaleMatrix(getDrawable());
        invalidate();
    }

    private void setScaleMatrix(Drawable drawable) {
        if (drawable != null) {
            float scale;
            float dx;
            Matrix matrix = getImageMatrix();
            int vwidth = (getWidth() - getPaddingLeft()) - getPaddingRight();
            int vheight = (getHeight() - getPaddingTop()) - getPaddingBottom();
            int dwidth = drawable.getIntrinsicWidth();
            int dheight = drawable.getIntrinsicHeight();
            if (dwidth * vheight > vwidth * dheight) {
                scale = (((float) vheight) / ((float) dheight)) * (this.mZoom + 1.0f);
                dx = (((float) vwidth) - (((float) dwidth) * scale)) * 0.5f;
            } else {
                scale = (((float) vwidth) / ((float) dwidth)) * (this.mZoom + 1.0f);
                dx = ((((float) vwidth) * this.mZoom) * 0.5f) * -1.0f;
            }
            float dy = ((((float) vheight) * this.mZoom) * 0.5f) * -1.0f;
            matrix.setScale(scale, scale);
            matrix.postTranslate((float) ((int) (dx + 0.5f)), (float) ((int) (dy + 0.5f)));
            setImageMatrix(matrix);
        }
    }
}
