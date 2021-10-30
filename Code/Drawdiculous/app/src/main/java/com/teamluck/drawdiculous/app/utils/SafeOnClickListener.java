package com.teamluck.drawdiculous.app.utils;

import static com.teamluck.drawdiculous.app.utils.AppConst.TOO_SOON_DURATION_MS;

import android.view.View;

/**
 * SafeOnClickListener prevents multiple clicks on buttons
 */
public abstract class SafeOnClickListener implements View.OnClickListener {
    
    private static long lastClickMs = 0;
    
    /**
     * Override onOneClick() instead.
     */
    @Override
    public final void onClick(View v) {
        long nowMs = System.currentTimeMillis();
        if (lastClickMs != 0 && (nowMs - lastClickMs) < TOO_SOON_DURATION_MS) {
            return;
        }
        lastClickMs = nowMs;
        onOneClick(v);
    }
    
    public abstract void onOneClick(View v);
}
