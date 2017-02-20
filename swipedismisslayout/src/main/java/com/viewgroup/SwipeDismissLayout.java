package com.viewgroup;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.viewgroup.attributes.AttributeExtractorImpl;

/**
 * Created by arpit on 10/28/16.
 */
public class SwipeDismissLayout extends ViewGroup{

    private final ViewDragHelper viewDragHelper;

    private View view;

    private View scrollableChild;

    private int verticalDragRange = 0;

    private int dragState = 0;

    private int draggingOffset;

    private static float BACK_FACTOR = 0.3f;

    private float finishingPoint = 0;

    private boolean isSwipeEnabled = false;

    public enum DragFrom {
        TOP
    }

    private DragFrom dragFrom = DragFrom.TOP;

    public SwipeDismissLayout(Context context) {
        super(context);
        viewDragHelper = init();

    }

    public SwipeDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        viewDragHelper = init();
        initialize(attrs);
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
        if(extractor.getDismissDirection() == 0){
            dragFrom = DragFrom.TOP;
        }
        extractor.recycleAttributeSets();
    }

    /**
     * api to enable/disable swipe functionality
     * @param swipeEnabled
     */
    public void setSwipeEnabled(boolean swipeEnabled) {
        isSwipeEnabled = swipeEnabled;
    }

    /**
     * method to set the dismiss position.
     * @param dismissPosition
     */
    public void setDismissPosition(float dismissPosition){
        BACK_FACTOR = dismissPosition;
    }

    /**
     * method to set the swap or drag direction
     * @param dragFrom
     */
    public void setDismissDirection(DragFrom dragFrom){
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

        switch (dragFrom) {
            case TOP:
                finishingPoint = finishingPoint > 0 ? finishingPoint : verticalDragRange * BACK_FACTOR;
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
        return ViewCompat.canScrollVertically(scrollableChild, -1);
    }

    private void dismiss() {
        Activity activity = (Activity) getContext();
        activity.finish();
        activity.overridePendingTransition(0, android.R.anim.fade_out);
    }

    private class ViewDragHelperCallback extends ViewDragHelper.Callback{

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == view;
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return verticalDragRange;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {

            int result = 0;
            if (dragFrom == DragFrom.TOP && !canChildScrollUp() && top > 0) {
                final int topBound = getPaddingTop();
                final int bottomBound = verticalDragRange;
                result = Math.min(Math.max(top, topBound), bottomBound);
            }

            return result;
        }

        @Override
        public void onViewDragStateChanged(int state) {
            if (state == dragState) return;

            if ((dragState == ViewDragHelper.STATE_DRAGGING || dragState == ViewDragHelper.STATE_SETTLING) &&
                    state == ViewDragHelper.STATE_IDLE) {
                if (draggingOffset == verticalDragRange) {
                    dismiss();
                }
            }

            dragState = state;
        }


        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            switch (dragFrom) {
                case TOP:
                    draggingOffset = Math.abs(top);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (draggingOffset == 0) return;

            if (draggingOffset == verticalDragRange) return;
            boolean isBack = false;

            if (draggingOffset >= finishingPoint) {
                isBack = true;
            } else if (draggingOffset < finishingPoint) {
                isBack = false;
            }

            int finalTop;
            switch (dragFrom) {
                case TOP:
                    finalTop = isBack ? verticalDragRange : 0;
                    smoothScrollToY(finalTop);
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
}



