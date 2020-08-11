package com.viewgroup;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.viewgroup.attributes.AttributeExtractorImpl;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;
import androidx.viewpager.widget.ViewPager;

/**
 * Created by arpit on 10/28/16.
 */
public class SwipeDismissLayout extends ViewGroup {

    private final ViewDragHelper viewDragHelper;

    private View view;

    private View scrollableChild;

    private int verticalDragRange = 0;
    private int horizontalDragRange = 0;

    private int dragState = 0;

    private int draggingOffset;

    private static float BACK_FACTOR = 0.3f;

    private float finishingPoint = 0;

    private boolean isSwipeEnabled = false;

    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;

    public enum DragFrom {
        TOP,
        START,
        BOTTOM,
        END
    }

    private DragFrom dragFrom /*= DragFrom.START*/;

    public SwipeDismissLayout(Context context) {
        super(context);
        viewDragHelper = init();
        globalLayoutListener = this::clearScrollableChild;

        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    public SwipeDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = init();
        initialize(attrs);

        globalLayoutListener = this::clearScrollableChild;

        getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    private ViewDragHelper init() {
        return ViewDragHelper.create(this, 1.0f, new ViewDragHelperCallback());
    }

    private void initialize(AttributeSet attrs) {
        AttributeExtractorImpl.Builder builder = new AttributeExtractorImpl.Builder();

        AttributeExtractorImpl extractor = builder.setContext(getContext())
                .setAttributeSet(attrs)
                .build();

        BACK_FACTOR = extractor.getDismissPosition();
        isSwipeEnabled = extractor.isSwipeEnable();
        switch (extractor.getDismissDirection()){
            case 0:
                dragFrom = DragFrom.TOP;
                break;
            case 1:
                dragFrom = DragFrom.START;
                break;
            case 2:
                dragFrom = DragFrom.BOTTOM;
                break;
            case 3:
                dragFrom = DragFrom.END;
                break;
        }

        extractor.recycleAttributeSets();
    }

    /**
     * api to enable/disable swipe functionality
     *
     * @param swipeEnabled
     */
    public void setSwipeEnabled(boolean swipeEnabled) {
        isSwipeEnabled = swipeEnabled;
    }

    /**
     * method to set the dismiss position.
     *
     * @param dismissPosition
     */
    public void setDismissPosition(float dismissPosition) {
        BACK_FACTOR = dismissPosition;
    }

    /**
     * method to set the swap or drag direction
     *
     * @param dragFrom
     */
    public void setDismissDirection(DragFrom dragFrom) {
        this.dragFrom = dragFrom;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (getChildCount() == 0) return;

        View child = getChildAt(0);

        int childWidth = width - getPaddingLeft() - getPaddingRight();
        int childHeight = height - getPaddingTop() - getPaddingBottom();
        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int childRight = childLeft + childWidth;
        int childBottom = childTop + childHeight;
        child.layout(childLeft, childTop, childRight, childBottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 1) {
            throw new IllegalStateException("SwipeDismissLayout must contains only one direct child.");
        }

        if (getChildCount() > 0) {
            int measureWidth = MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY);
            int measureHeight = MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY);
            getChildAt(0).measure(measureWidth, measureHeight);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        verticalDragRange = h;
        horizontalDragRange = w;

        switch (dragFrom) {
            case TOP:
            case BOTTOM:
                finishingPoint = finishingPoint > 0 ? finishingPoint : verticalDragRange * BACK_FACTOR;
                break;
            case START:
            case END:
                finishingPoint = finishingPoint > 0 ? finishingPoint : horizontalDragRange * BACK_FACTOR;
                break;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean handled = false;
        ensureTarget();
        if (isEnabled() && isSwipeEnabled) {
            handled = viewDragHelper.shouldInterceptTouchEvent(ev);
        } else {
            viewDragHelper.cancel();
        }
        return !handled ? super.onInterceptTouchEvent(ev) : handled;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    private void ensureTarget() {
        if (view == null) {
            if (getChildCount() > 1) {
                throw new IllegalStateException("SwipeDismissLayout must contains only one direct child");
            }
            view = getChildAt(0);

            if (scrollableChild == null && view != null) {
                if (view instanceof ViewGroup) {
                    findScrollView((ViewGroup) view);
                } else {
                    scrollableChild = view;
                }

            }
        }
    }

    private void findScrollView(ViewGroup viewGroup) {
        scrollableChild = viewGroup;
        if (viewGroup.getChildCount() > 0) {
            int count = viewGroup.getChildCount();
            View child;
            for (int i = 0; i < count; i++) {
                child = viewGroup.getChildAt(i);
                if (child instanceof AbsListView || child instanceof ScrollView || child instanceof ViewPager || child instanceof WebView) {
                    scrollableChild = child;
                    return;
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public boolean canChildScrollUp() {
        return scrollableChild!=null && scrollableChild.canScrollVertically(-1);
    }
    public boolean canChildScrollDown() {
        return scrollableChild!=null && scrollableChild.canScrollVertically(1);
    }
    public boolean canChildScrollStart() {
        return scrollableChild!=null && scrollableChild.canScrollHorizontally((isRtl())?1:-1);
    }
    public boolean canChildScrollEnd() {
        return scrollableChild!=null && scrollableChild.canScrollHorizontally((isRtl())?-1:1);
    }

    private boolean isRtl(){
        return getContext().getResources().getConfiguration().getLayoutDirection() == View.LAYOUT_DIRECTION_RTL;
    }
    private void dismiss() {
        Activity activity = (Activity) getContext();
        activity.finish();
        activity.overridePendingTransition(0, android.R.anim.fade_out);
    }

    private class ViewDragHelperCallback extends ViewDragHelper.Callback {

        private int mOriginalCapturedViewLeft;
        private int mOriginalCapturedViewTop;

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return child == view;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            mOriginalCapturedViewLeft = capturedChild.getLeft();
            mOriginalCapturedViewTop = capturedChild.getTop();
        }
        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return verticalDragRange;
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return horizontalDragRange;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

            int min=0, max=0;
            if (dragFrom == DragFrom.TOP && !canChildScrollUp()) {
                    min = mOriginalCapturedViewTop;
                    max = mOriginalCapturedViewTop + verticalDragRange;
            } else if (dragFrom == DragFrom.BOTTOM && !canChildScrollDown()) {
                    min = mOriginalCapturedViewTop - verticalDragRange;
                    max = mOriginalCapturedViewTop;
            }
            return clamp(min, top, max);
        }
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

            int min=0, max=0;
            if (dragFrom == DragFrom.START && !canChildScrollStart()) {
                if (isRtl()) {
                    min = mOriginalCapturedViewLeft - horizontalDragRange;
                    max = mOriginalCapturedViewLeft;
                } else {
                    min = mOriginalCapturedViewLeft;
                    max = mOriginalCapturedViewLeft + horizontalDragRange;
                }
            } else if (dragFrom == DragFrom.END && !canChildScrollEnd()) {
                if (isRtl()) {
                    min = mOriginalCapturedViewLeft;
                    max = mOriginalCapturedViewLeft + horizontalDragRange;
                } else {
                    min = mOriginalCapturedViewLeft - horizontalDragRange;
                    max = mOriginalCapturedViewLeft;
                }
            }
            return clamp(min, left, max);
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == dragState) return;

            if ((dragState == ViewDragHelper.STATE_DRAGGING || dragState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {

                switch (dragFrom) {
                    case TOP:
                    case BOTTOM:
                        if (draggingOffset == verticalDragRange) {
                            dismiss();
                        }
                        break;
                    case START:
                    case END:
                        if (draggingOffset == horizontalDragRange) {
                            dismiss();
                        }
                        break;
                }
            }

            dragState = state;
        }


        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            switch (dragFrom) {
                case TOP:
                case BOTTOM:
                    draggingOffset = Math.abs(top);
                    break;
                case START:
                case END:
                    draggingOffset = Math.abs(left);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (draggingOffset == 0) return;

            switch (dragFrom) {
                case TOP:
                case BOTTOM:
                    if (draggingOffset == verticalDragRange) return;
                    break;
                case START:
                case END:
                    if (draggingOffset == horizontalDragRange) return;
                    break;
            }

            boolean isBack = draggingOffset >= finishingPoint;

            int finalTop = isBack ? verticalDragRange : 0;
            int finalLeft = isBack ? horizontalDragRange : 0;
            switch (dragFrom) {
                case TOP:
                    smoothScrollToY(finalTop);
                    break;
                case START:
                    smoothScrollToX((isRtl())?-finalLeft:finalLeft);
                    break;
                case BOTTOM:
                    smoothScrollToY(-finalTop);
                    break;
                case END:
                    smoothScrollToX((isRtl())?finalLeft:-finalLeft);
                    break;
                default:
                    break;
            }


        }
    }

    private void smoothScrollToY(int finalTop) {
        if (viewDragHelper.settleCapturedViewAt(0, finalTop)) {
            ViewCompat.postInvalidateOnAnimation(SwipeDismissLayout.this);
        }
    }
    private void smoothScrollToX(int finalLeft) {
        if (viewDragHelper.settleCapturedViewAt(finalLeft, 0)) {
            ViewCompat.postInvalidateOnAnimation(SwipeDismissLayout.this);
        }
    }

    private void clearScrollableChild() {
        scrollableChild = null;
        view = null;
    }

    static float clamp(float min, float value, float max) {
        return Math.min(Math.max(min, value), max);
    }
    static int clamp(int min, int value, int max) {
        return Math.min(Math.max(min, value), max);
    }
}



