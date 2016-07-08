package com.mediatek.launcher3.ext;

import android.content.Context;
import android.util.TypedValue;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Default IOperatorChecker implements.
 */
public class DefaultOperatorChecker implements IOperatorChecker {
    private static final String TAG = "DefaultOperatorChecker";
    protected Context mContext;

    /**
     * Constructs a new DefaultOperatorChecker instance with Context.
     * @param context A Context object
     */
    public DefaultOperatorChecker(Context context) {
        mContext = context;
    }

    @Override
    public boolean supportEditAndHideApps() {
        LauncherLog.d(TAG, "default supportEditAndHideApps called.");
        return false;
    }

    @Override
    public boolean supportAppListCycleSliding() {
        LauncherLog.d(TAG, "default supportAppListCycleSliding called.");
        return false;
    }

    @Override
    public void customizeWorkSpaceIconText(TextView tv, float orgTextSize) {
        LauncherLog.d(TAG, "default setWorkSpaceIconTextLine called.");
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, orgTextSize);
    }

    @Override
    public void customizeCompoundPaddingForBubbleText(TextView tv, int orgPadding) {
        tv.setCompoundDrawablePadding(orgPadding);
    }

    @Override
    public void customizeFolderPreviewLayoutParams(FrameLayout.LayoutParams lp) {
        // Do nothing for default implementation.
    }

    @Override
    public int customizeFolderCellHeight(int orgHeight) {
        return orgHeight;
    }
}
