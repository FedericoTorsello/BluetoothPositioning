package it.unibo.torsello.bluetoothpositioning.extra;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Federico Torsello.
 * federico.torsello@studio.unibo.it
 */
public class FABBehavior extends FloatingActionButton.Behavior {

    public FABBehavior(Context context, AttributeSet attrs) {
        super();
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                                       View directTargetChild, View target, int nestedScrollAxes) {

//        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL ||
//                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
//                        nestedScrollAxes);
        return true;
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, final FloatingActionButton child,
                               View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);

        if ((dyConsumed > 0 || dyUnconsumed == 0) && child.getVisibility() == View.VISIBLE) {
            child.hide();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    child.show();
                }
            }, 1000);
        }
    }

}
