package com.viewgroup.attributes;

/**
 * Created by arpit on 10/28/16.
 */
public interface AttributeExtractor {

    /**
     * method to verify if swipe to dismiss is enable
     *
     * @return
     */
    boolean isSwipeEnable();

    /**
     * method to get the dismissPosition
     *
     * @return
     */
    float getDismissPosition();

    /**
     * method to get the direction of dismiss the screen
     *
     * @return
     */
    int getDismissDirection();

}
