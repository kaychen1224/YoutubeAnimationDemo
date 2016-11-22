package idv.oscar.youtube;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.VideoView;

public class ImageViewCustom extends ImageView {

    String TAG = "ImageViewCustom";

    private int mForceHeight = 0;
    private int mForceWidth = 0;

    public ImageViewCustom(Context context) {
        super(context);
    }

    public ImageViewCustom(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageViewCustom(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDimensions(int w, int h) {
        this.mForceHeight = h;
        this.mForceWidth = w;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i(TAG, "onMeasure widthMeasureSpec:" + widthMeasureSpec + " heightMeasureSpec:" + heightMeasureSpec);
        Log.i(TAG, "onMeasure mForceWidth:" + mForceWidth + " mForceHeight:" + mForceHeight);

        if (mForceWidth != 0 && mForceHeight != 0)
            setMeasuredDimension(mForceWidth, mForceHeight);
        else
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}