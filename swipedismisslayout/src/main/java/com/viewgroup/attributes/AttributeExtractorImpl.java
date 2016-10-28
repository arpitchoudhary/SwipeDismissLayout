package com.viewgroup.attributes;

/**
 * Created by achoudhary on 10/28/16.
 */

public class AttributeExtractorImpl implements AttributeExtractor {


    @Override
    public boolean isSwipeEnable() {
        return false;
    }

    @Override
    public float getDismissPosition() {
        return 0;
    }

    @Override
    public String getDismissDirection() {
        return null;
    }
}
