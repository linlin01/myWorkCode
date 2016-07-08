package com.mediatek.launcher3.ext;

import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * M: Launcher Operator Checker interface for OP customized.
 */
public interface IOperatorChecker {
    /**
     * Whether support app list edit and hide.
     *
     * @return True for OP09 projects, else false.
     */
    boolean supportEditAndHideApps();

    /**
     * Whether support app list cycle sliding.
     *
     * @return True for OP09 projects, else false.
     */
    boolean supportAppListCycleSliding();

    /**
     * Customize workspace icon text, set text to two max lines and set the text
     * size for OP09 projects.
     *
     * @param tv TextView object.
     * @param orgTextSize default text size.
     */
    void customizeWorkSpaceIconText(TextView tv, float orgTextSize);

    /**
     * Customize compound padding for bubble text view.
     *
     * @param tv TextView object.
     * @param orgPadding default padding.
     */
    void customizeCompoundPaddingForBubbleText(TextView tv, int orgPadding);

    /**
     * Customize folder preview layout params.
     *
     * @param lp LayoutParams.
     */
    void customizeFolderPreviewLayoutParams(FrameLayout.LayoutParams lp);

    /**
     * Customize cell height for the folder.
     *
     * @param orgHeight default cell height.
     * @return folder cell height.
     */
    int customizeFolderCellHeight(int orgHeight);
}
