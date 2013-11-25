/**
 *
 */
package com.thisisnotajoke.connectshades;

import android.app.Activity;

import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class ConsoleGestureDetector extends GestureDetector {
	private Activity parent;
	/**
	 * @param context
	 */
	public ConsoleGestureDetector(Activity parent) {
		super(parent);
		this.parent = parent;
		this.setBaseListener(new Base());

        this.setFingerListener(new Finger());

        this.setScrollListener(new Scroll());
	}

	private class Base implements GestureDetector.BaseListener {
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
                parent.openOptionsMenu();
                return true;
            } else if (gesture == Gesture.TWO_TAP) {
                // do something on two finger tap
                return true;
            } else if (gesture == Gesture.SWIPE_RIGHT) {
                // do something on right (forward) swipe
                return true;
            } else if (gesture == Gesture.SWIPE_LEFT) {
                // do something on left (backwards) swipe
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
            return false;
        }
    }
}
