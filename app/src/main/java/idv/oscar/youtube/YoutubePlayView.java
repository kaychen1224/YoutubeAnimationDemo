package idv.oscar.youtube;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class YoutubePlayView extends RelativeLayout implements GestureDetector.OnGestureListener {

    String TAG = "YoutubePlayView";

    private Context mContext;

    private ImageViewCustom mImageView;
    private TextView mTextView;

    private GestureDetectorCompat mDetector;

    private boolean mInit = true;
    private float mRatio;
    private int mImageViewMaxWidth, mImageViewInitialTop, mImageViewInitialLeft, mImageViewFinalTop,
            mImageViewFinalLeft, mImageViewScrollRange, mImageViewWidth, mImageViewHeight;
    private int mTextViewInitialTop, mTextViewGap;
    private int mScreenHeight;

    private boolean mFirstScroll = true;
    private boolean mScrollDown = true;
    private boolean mBlockUserAction = false;
    private boolean mFling = false;

    private final int ANIMATION_DURATION = 300;
    private final float ANIMATION_FACTOR = 1.0f;
    private final int FLING_VELOCITYY = 2000;

    public YoutubePlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        mDetector = new GestureDetectorCompat(mContext, this);

        mImageView = (ImageViewCustom) getChildAt(0);
        mTextView = (TextView) getChildAt(1);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mInit) {
            mInit = false;

            mImageViewWidth = mImageView.getWidth();
            mImageViewHeight = mImageView.getHeight();

            Log.d(TAG, "mImageViewWidth:" + mImageViewWidth + " mImageViewHeight:" + mImageViewHeight);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowmanager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
            mScreenHeight = displayMetrics.heightPixels;

            Log.d(TAG, "mScreenHeight:" + mScreenHeight);

            mRatio = (float) mImageView.getWidth() / (float) mImageView.getHeight();
            mImageViewMaxWidth = mImageView.getWidth();

            mImageViewInitialTop = mImageView.getTop();
            mImageViewInitialLeft = mImageView.getLeft();

            mTextViewInitialTop = mTextView.getTop();
            mTextViewGap = mTextView.getTop() - mImageView.getBottom();
            Log.d(TAG, "mTextViewInitialTop:" + mTextViewInitialTop);

            Log.d(TAG, "mRootView.getBottom():" + getBottom());

            mImageViewFinalTop = getBottom() - (mImageView.getHeight() / 2) - mImageViewInitialTop;
            mImageViewFinalLeft = getWidth() / 2;

            Log.d(TAG, "mImageView mInitialTop:" + mImageViewInitialTop + " mInitialLeft:" + mImageViewInitialLeft
                    + " mFinalTop:" + mImageViewFinalTop + " mFinalLeft:" + mImageViewFinalLeft);

            mImageViewScrollRange = mImageViewFinalTop - mImageViewInitialTop;
            Log.d(TAG, "mImageViewScrollRange:" + mImageViewScrollRange);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return handleTouchEvent(null, event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        // Log.d(TAG, "onDown e.getX():" + e.getX() + " e.getY():" + e.getY());
        // Log.d(TAG, "onDown mImageView.getX():" + mImageView.getX() +
        // " mImageView.getY():" + mImageView.getY());

        if (mBlockUserAction) {
            Log.d(TAG, "block user action while animating...");
            return false;
        }

        Rect rect = new Rect((int) mImageView.getX(), (int) mImageView.getY(), (int) mImageView.getX()
                + mImageView.getWidth(), (int) mImageView.getY() + mImageView.getHeight());

        if (rect.contains((int) e.getX(), (int) e.getY())) {
            Log.d(TAG, "take action...");
            return true;
        } else {
            Log.d(TAG, "ignore...");
            return false;
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

        Log.d(TAG, "onScroll e2x:" + e2.getX() + " e2y:" + e2.getY());
        Log.d(TAG, "onScroll getRawY:" + e2.getRawY());

        if (mFirstScroll) {
            mFirstScroll = false;
            // allow scroll action
            if (mImageView.getY() == mImageViewInitialTop) {
                mScrollDown = true;
            } else {
                mScrollDown = false;
            }
        }

        float movePercentage, alphaValue;
        if (mScrollDown) {
            float moveDistance = e2.getRawY() - e1.getRawY();
            Log.d(TAG, "moveDistance:" + moveDistance);
            movePercentage = moveDistance / ((float) mScreenHeight - e1.getRawY());
            Log.d(TAG, "movePercentage:" + movePercentage);
            float yPosition = mImageViewScrollRange * movePercentage;
            Log.d(TAG, "yPosition:" + yPosition);
            if (yPosition >= mImageViewInitialTop) {
                mImageView.setY(yPosition);
                mTextView.setY((yPosition + mImageView.getHeight()) + mTextViewGap);

                alphaValue = (mScreenHeight - mImageView.getY()) / mScreenHeight;
                mTextView.setAlpha(alphaValue);
            }
        } else {
            float moveDistance = e1.getRawY() - e2.getRawY();
            Log.d(TAG, "moveDistance:" + moveDistance);
            movePercentage = moveDistance / e1.getRawY();
            Log.d(TAG, "movePercentage:" + movePercentage);
            float yPosition = mImageViewScrollRange * (1 - movePercentage);
            Log.d(TAG, "yPosition:" + yPosition);
            if (yPosition < mImageViewFinalTop) {
                mImageView.setY(yPosition);
                mTextView.setY((yPosition + mImageView.getHeight()) + mTextViewGap);

                alphaValue = (mScreenHeight - mImageView.getY()) / mScreenHeight;
                Log.d(TAG, "alphaValue:" + alphaValue);
                mTextView.setAlpha(alphaValue);
            }
        }

        Log.d(TAG, "mImageView position Y:" + mImageView.getY());

        float scale = 1 - (mImageView.getY() / (mImageViewFinalTop - mImageViewInitialTop)) / 2;

        int displayWidth = (int) (mImageViewWidth * scale);
        // Log.d(TAG,"displayWidth:"+displayWidth+" mMaxWidth:"+mMaxWidth);
        if (displayWidth > mImageViewMaxWidth)
            displayWidth = mImageViewMaxWidth;
        int displayHeight = (int) (displayWidth / mRatio);
        // Log.d(TAG,"displayWidth:"+displayWidth+" displayHeight:"+displayHeight);
        mImageView.setDimensions(displayWidth, displayHeight);
        mImageView.requestLayout();

        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        Log.d(TAG, "onFling velocityX:" + velocityX + " velocityY:" + velocityY);

        if (velocityY > FLING_VELOCITYY) {
            mFling = true;
            animateDown();
        } else if (velocityY < -FLING_VELOCITYY) {
            mFling = true;
            animateUp();
        }
        return true;
    }

    private boolean handleTouchEvent(View v, MotionEvent event) {

        boolean result = mDetector.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
        case (MotionEvent.ACTION_UP):

            mFirstScroll = true;

            if (mFling) {
                Log.d(TAG, "Just deal with fling event");
                mFling = false;
                return result;
            }

            Log.d(TAG, "ACTION_UP mImageView getY():" + mImageView.getY());

            if (mImageView.getY() > mImageViewScrollRange / 2) {
                animateDown();
            } else {
                animateUp();
            }
        default:
        }
        mFling = false;
        return result;
    }

    private void animateDown() {
        mBlockUserAction = true;
        mImageView.animate().translationY(mImageViewFinalTop).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).start();
        ResizeAnimation resizeAnimation = new ResizeAnimation(mImageView, mImageView.getWidth(), mImageViewWidth / 2,
                mImageView.getHeight(), mImageViewHeight / 2);
        resizeAnimation.setDuration(ANIMATION_DURATION);
        resizeAnimation.setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR));
        mImageView.startAnimation(resizeAnimation);

        mTextView.animate().translationY(mImageViewFinalTop).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).start();

        mTextView.animate().alpha(0).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).setListener(new AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                        mBlockUserAction = false;
                    }

                    public void onAnimationCancel(Animator animation) {
                    }
                }).start();
    }

    private void animateUp() {
        mBlockUserAction = true;
        mImageView.animate().translationY(0).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).start();

        ResizeAnimation resizeAnimation = new ResizeAnimation(mImageView, mImageView.getWidth(), mImageViewWidth,
                mImageView.getHeight(), mImageViewHeight);
        resizeAnimation.setDuration(ANIMATION_DURATION);
        resizeAnimation.setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR));
        mImageView.startAnimation(resizeAnimation);

        mTextView.animate().translationY(0).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).start();

        mTextView.animate().alpha(1).setDuration(ANIMATION_DURATION)
                .setInterpolator(new DecelerateInterpolator(ANIMATION_FACTOR)).setListener(new AnimatorListener() {
                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                        mBlockUserAction = false;
                    }

                    public void onAnimationCancel(Animator animation) {
                    }
                }).start();
    }

    public boolean isMaxmum() {
        Log.d(TAG, "isMaxmum mImageView.getY():" + mImageView.getY());
        return mImageView.getY() == 0 ? true : false;
    }

    public void startMinimize() {
        animateDown();
    }

}
