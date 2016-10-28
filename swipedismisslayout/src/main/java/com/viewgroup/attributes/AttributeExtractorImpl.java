package com.viewgroup.attributes;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.viewgroup.sample.R;

import java.lang.ref.WeakReference;

/**
 * Created by achoudhary on 10/28/16.
 */

public class AttributeExtractorImpl implements AttributeExtractor {

    private WeakReference<Context> context;
    private WeakReference<AttributeSet> attributeSet;
    private WeakReference<TypedArray> typedArray;

    private AttributeExtractorImpl(WeakReference<Context> context, WeakReference<AttributeSet> attributeSet) {
        this.context = context;
        this.attributeSet = attributeSet;
    }

    private TypedArray attributeArray() {
        if (typedArray == null) {
            typedArray = new WeakReference<>(context.get().obtainStyledAttributes(attributeSet.get(), R.styleable.SwipeDismissLayout));
        }
        return typedArray.get();
    }

    @Override
    public boolean isSwipeEnable() {
        return attributeArray().getBoolean(R.styleable.SwipeDismissLayout_swipe_enable,false);
    }

    @Override
    public float getDismissPosition() {
        return attributeArray().getFloat(R.styleable.SwipeDismissLayout_dismiss_position,0.3f);
    }

    @Override
    public int getDismissDirection() {
        return attributeArray().getInt(R.styleable.SwipeDismissLayout_dismiss_direction,0);
    }


    public static class Builder {

        private WeakReference<Context> context;
        private WeakReference<AttributeSet> attributeSet;

        public Builder setContext(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("Context cannot be null");
            }
            this.context = new WeakReference<Context>(context);
            return this;
        }

        public Builder setAttributeSet(AttributeSet attributeSet) {
            if (attributeSet == null) {
                throw new IllegalArgumentException("AttributeSet cannot be null");
            }
            this.attributeSet = new WeakReference<AttributeSet>(attributeSet);
            return this;
        }

        public AttributeExtractorImpl build() {
            return new AttributeExtractorImpl(context, attributeSet);
        }
    }
}
