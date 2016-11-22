package idv.oscar.youtube;

import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {

    String TAG = "ResizeAnimation";

    int mStartWidth, mTargetWidth, mStartHeight, mTargetHeight;
    ImageViewCustom mView;

    public ResizeAnimation(ImageViewCustom view, int startWidth, int targetWidth, int startHeight, int targetHeight) {
        mView = view;
        mStartWidth = startWidth;
        mTargetWidth = targetWidth;
        mStartHeight = startHeight;
        mTargetHeight = targetHeight;
    }

    protected void applyTransformation(float interpolatedTime, Transformation t) {

        int newWidth = (int) (mStartWidth + (mTargetWidth - mStartWidth) * interpolatedTime);
        int newHeight = (int) (mStartHeight + (mTargetHeight - mStartHeight) * interpolatedTime);

        mView.setDimensions(newWidth, newHeight);
        mView.requestLayout();
    }

    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

    }

    public boolean willChangeBounds() {
        return true;
    }
}
