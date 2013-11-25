/**
 *
 */
package com.thisisnotajoke.connectshades;

import org.connectbot.service.PromptHelper;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class ConsoleGestureDetector extends GestureDetector {
	public static final char ctrlC = 0x3;
	private ConsoleActivity parent;
	/**
	 * @param context
	 */
	public ConsoleGestureDetector(ConsoleActivity parent) {
		super(parent);
		this.parent = parent;
		this.setBaseListener(new Base());

        this.setFingerListener(new Finger());

        this.setScrollListener(new Scroll());
	}

	private class Base implements GestureDetector.BaseListener {
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
            	PromptHelper p = parent.getCurrentPromptHelper();
            	if(p != null){
            		parent.getCurrentFocus().performClick();
            	}else{
            		parent.openOptionsMenu();
            	}
                return true;
            } else if (gesture == Gesture.TWO_TAP) {
                parent.getActiveBridge().injectString(Character.toString(ctrlC));
                return true;
            } else if (gesture == Gesture.TWO_SWIPE_RIGHT) {
                parent.getActiveBridge().increaseFontSize();
                return true;
            } else if (gesture == Gesture.TWO_SWIPE_LEFT) {
                // do something on left (backwards) swipe
            	parent.getActiveBridge().decreaseFontSize();
                return true;
            } else if (gesture == Gesture.SWIPE_RIGHT) {
            	return true;
            } else if (gesture == Gesture.SWIPE_LEFT) {
            	return true;
            }
            return false;
        }
    }

	private class Finger implements GestureDetector.FingerListener {
        public void onFingerCountChanged(int previousCount, int currentCount) {
        }
    }

	private class Scroll implements GestureDetector.ScrollListener {
        public boolean onScroll(float displacement, float delta, float velocity) {
        	parent.getTerminalView().scrollBy(0, (int)displacement/20);
        	if(parent.getTerminalView().getScrollY() >= 0)
        		parent.getTerminalView().setScrollY(0);
            return true;
        }
    }
}
