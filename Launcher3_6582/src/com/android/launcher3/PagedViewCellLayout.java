/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3;

import java.util.HashMap;
import java.util.Stack;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.mediatek.launcher3.ext.LauncherLog;

/**
 * An abstraction of the original CellLayout which supports laying out items
 * which span multiple cells into a grid-like layout.  Also supports dimming
 * to give a preview of its contents.
 */
public class PagedViewCellLayout extends ViewGroup implements Page {
    static final String TAG = "PagedViewCellLayout";

    private int mCellCountX;
    private int mCellCountY;
    private int mOriginalCellWidth;
    private int mOriginalCellHeight;
    private int mCellWidth;
    private int mCellHeight;
    private int mOriginalWidthGap;
    private int mOriginalHeightGap;
    private int mWidthGap;
    private int mHeightGap;
    protected PagedViewCellLayoutChildren mChildren;
	
    /// M: add for OP09.@{

    private int mMaxGap;

    private DropTarget.DragEnforcer mDragEnforcer;

    private boolean mDragging = false;

    // When a drag operation is in progress, holds the nearest cell to the touch point
    private final int[] mDragCell = new int[2];

    private final Rect mRect = new Rect();

    private final int[] mTmpXY = new int[2];

    private HashMap<PagedViewCellLayout.LayoutParams, Animator> mReorderAnimators = new
            HashMap<PagedViewCellLayout.LayoutParams, Animator>();

    private boolean[][] mOccupied;
    private boolean[][] mTmpOccupied;

    private final int[] mTmpPoint = new int[2];
    private final Stack<Rect> mTempRectStack = new Stack<Rect>();

    /// M: add for OP09.}@
    
    public PagedViewCellLayout(Context context) {
        this(context, null);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PagedViewCellLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setAlwaysDrawnWithCacheEnabled(false);

        // setup default cell parameters
        LauncherAppState app = LauncherAppState.getInstance();
        DeviceProfile grid = app.getDynamicGrid().getDeviceProfile();
        mOriginalCellWidth = mCellWidth = grid.cellWidthPx;
        mOriginalCellHeight = mCellHeight = grid.cellHeightPx;
        mCellCountX = (int) grid.numColumns;
        mCellCountY = (int) grid.numRows;
        mOriginalWidthGap = mOriginalHeightGap = mWidthGap = mHeightGap = -1;

        mChildren = new PagedViewCellLayoutChildren(context);
        mChildren.setCellDimensions(mCellWidth, mCellHeight);
        mChildren.setGap(mWidthGap, mHeightGap);

        addView(mChildren);

        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "Constructor: mCellCountX = " + mCellCountX + ", mCellCountY = "
                    + mCellCountY + ", this = " + this);
        }

        mDragEnforcer = new DropTarget.DragEnforcer(context);
    }

    public int getCellWidth() {
        return mCellWidth;
    }

    public int getCellHeight() {
        return mCellHeight;
    }

    @Override
    public void cancelLongPress() {
        super.cancelLongPress();

        // Cancel long press for all children
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.cancelLongPress();
        }
    }

    public boolean addViewToCellLayout(View child, int index, int childId,
            PagedViewCellLayout.LayoutParams params) {
        final PagedViewCellLayout.LayoutParams lp = params;
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "addViewToCellLayout: index = " + index + ", child = "
                    + child.getTag() + ", this = " + this);
        }
        // Generate an id for each view, this assumes we have at most 256x256 cells
        // per workspace screen
        if (lp.cellX >= 0 && lp.cellX <= (mCellCountX - 1) &&
                lp.cellY >= 0 && (lp.cellY <= mCellCountY - 1)) {
            // If the horizontal or vertical span is set to -1, it is taken to
            // mean that it spans the extent of the CellLayout
            if (lp.cellHSpan < 0) lp.cellHSpan = mCellCountX;
            if (lp.cellVSpan < 0) lp.cellVSpan = mCellCountY;

            child.setId(childId);
            mChildren.addView(child, index, lp);

            return true;
        }
        return false;
    }

    @Override
    public void removeAllViewsOnPage() {
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "removeAllViewsOnPage: mChildren = " + mChildren + ", this = " + this);
        }

        mChildren.removeAllViews();
        setLayerType(LAYER_TYPE_NONE, null);
    }

    @Override
    public void removeViewOnPageAt(int index) {
        if (LauncherLog.DEBUG) {
            LauncherLog.d(TAG, "removeViewOnPageAt: mChildren = " + mChildren + ", index = " + index);
        }

        mChildren.removeViewAt(index);
    }

    /**
     * Clears all the key listeners for the individual icons.
     */
    public void resetChildrenOnKeyListeners() {
        int childCount = mChildren.getChildCount();
        for (int j = 0; j < childCount; ++j) {
            mChildren.getChildAt(j).setOnKeyListener(null);
        }
    }

    @Override
    public int getPageChildCount() {
        return mChildren.getChildCount();
    }

    public PagedViewCellLayoutChildren getChildrenLayout() {
        return mChildren;
    }

    @Override
    public View getChildOnPageAt(int i) {
        return mChildren.getChildAt(i);
    }

    @Override
    public int indexOfChildOnPage(View v) {
        return mChildren.indexOfChild(v);
    }

    public int getCellCountX() {
        return mCellCountX;
    }

    public int getCellCountY() {
        return mCellCountY;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            throw new RuntimeException("CellLayout cannot have UNSPECIFIED dimensions");
        }

        int numWidthGaps = mCellCountX - 1;
        int numHeightGaps = mCellCountY - 1;

        if (mOriginalWidthGap < 0 || mOriginalHeightGap < 0) {
            int hSpace = widthSpecSize - getPaddingLeft() - getPaddingRight();
            int vSpace = heightSpecSize - getPaddingTop() - getPaddingBottom();
            int hFreeSpace = hSpace - (mCellCountX * mOriginalCellWidth);
            int vFreeSpace = vSpace - (mCellCountY * mOriginalCellHeight);
            mWidthGap = numWidthGaps > 0 ? (hFreeSpace / numWidthGaps) : 0;
            mHeightGap = numHeightGaps > 0 ? (vFreeSpace / numHeightGaps) : 0;
            if (LauncherLog.DEBUG_LAYOUT) {
                LauncherLog.d(TAG, "onMeasure 0: numWidthGaps = "
                        + numWidthGaps + ", hFreeSpace = " + hFreeSpace + ", mOriginalCellWidth ="
                        + mOriginalCellWidth + ", mOriginalCellHeight = " + mOriginalCellHeight
                        + ", mWidthGap = " + mWidthGap);
            }

            mChildren.setGap(mWidthGap, mHeightGap);
        } else {
            mWidthGap = mOriginalWidthGap;
            mHeightGap = mOriginalHeightGap;
        }

        // Initial values correspond to widthSpecMode == MeasureSpec.EXACTLY
        int newWidth = widthSpecSize;
        int newHeight = heightSpecSize;
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "onMeasure 1: newWidth = " + newWidth + ", newHeight = " + newHeight
                    + ", widthSpecMode = " + widthSpecMode + ",mPaddingLeft = " + getPaddingLeft()
                    + ", mPaddingRight = " + getPaddingRight() + ",mCellCountX = " + mCellCountX
                    + ", mCellWidth = " + mCellWidth + ", mWidthGap = " + mWidthGap
                    + ", mOriginalWidthGap =" + mOriginalWidthGap + ", mOriginalHeightGap = "
                    + mOriginalHeightGap + ", mOriginalCellWidth =" + mOriginalCellWidth
                    + ", mOriginalCellHeight = " + mOriginalCellHeight + ", this = " + this);
        }

        if (widthSpecMode == MeasureSpec.AT_MOST) {
            newWidth = getPaddingLeft() + getPaddingRight() + (mCellCountX * mCellWidth) +
                ((mCellCountX - 1) * mWidthGap);
            newHeight = getPaddingTop() + getPaddingBottom() + (mCellCountY * mCellHeight) +
                ((mCellCountY - 1) * mHeightGap);
            if (LauncherLog.DEBUG_LAYOUT) {
                LauncherLog.d(TAG, "onMeasure 2: newWidth = " + newWidth + ", newHeight = "
                        + newHeight + ", this = " + this);
            }

            setMeasuredDimension(newWidth, newHeight);
        }

        final int count = getChildCount();

        /*
         * If user switch two tabs quickly, measure process will be delayed, the
         * newWidth(newHeight) may be 0, after minus the padding, the
         * measure width passed to child may be a negative value. When adding to
         * measureMode to get MeasureSpec, the measure mode could be changed.
         * Using 0 as the measureWidth if this happens to keep measure mode right.
         */
        final int childMeasureWidth = Math.max(0, newWidth - getPaddingLeft() - getPaddingRight());
        final int childMeasureHeight = Math.max(0, newHeight - getPaddingTop() - getPaddingBottom());

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            int childWidthMeasureSpec =
                MeasureSpec.makeMeasureSpec(childMeasureWidth, MeasureSpec.EXACTLY);
            int childheightMeasureSpec =
                MeasureSpec.makeMeasureSpec(childMeasureHeight, MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childheightMeasureSpec);
        }
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "onMeasure 4: newWidth = " + newWidth + ", newHeight = " + newHeight
                    + ", this = " + this);
        }

        setMeasuredDimension(newWidth, newHeight);
    }

    int getContentWidth() {
        return getWidthBeforeFirstLayout() + getPaddingLeft() + getPaddingRight();
    }

    int getContentHeight() {
        if (mCellCountY > 0) {
            return mCellCountY * mCellHeight + (mCellCountY - 1) * Math.max(0, mHeightGap);
        }
        return 0;
    }

    int getWidthBeforeFirstLayout() {
        if (mCellCountX > 0) {
            return mCellCountX * mCellWidth + (mCellCountX - 1) * Math.max(0, mWidthGap);
        }
        return 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            child.layout(getPaddingLeft(), getPaddingTop(),
                r - l - getPaddingRight(), b - t - getPaddingBottom());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
        int count = getPageChildCount();
        if (count > 0) {
            // We only intercept the touch if we are tapping in empty space after the final row
            View child = getChildOnPageAt(count - 1);
            int bottom = child.getBottom();
            int numRows = (int) Math.ceil((float) getPageChildCount() / getCellCountX());
            if (numRows < getCellCountY()) {
                // Add a little bit of buffer if there is room for another row
                bottom += mCellHeight / 2;
            }
            result = result || (event.getY() < bottom);
        }
        return result;
    }

    public void enableCenteredContent(boolean enabled) {
        mChildren.enableCenteredContent(enabled);
    }

    @Override
    protected void setChildrenDrawingCacheEnabled(boolean enabled) {
        mChildren.setChildrenDrawingCacheEnabled(enabled);
    }

    public void setCellCount(int xCount, int yCount) {
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "setCellCount xCount = " + yCount + ", mCellCountX = " + mCellCountX
                    + ", mCellCountY = " + mCellCountY + ", this = " + this, new Throwable(
                    "setCellCount"));
        }
        mCellCountX = xCount;
        mCellCountY = yCount;
        requestLayout();
    }

    public void setGap(int widthGap, int heightGap) {
        mOriginalWidthGap = mWidthGap = widthGap;
        mOriginalHeightGap = mHeightGap = heightGap;
        mChildren.setGap(widthGap, heightGap);
    }

    public int[] getCellCountForDimensions(int width, int height) {
        // Always assume we're working with the smallest span to make sure we
        // reserve enough space in both orientations
        int smallerSize = Math.min(mCellWidth, mCellHeight);

        // Always round up to next largest cell
        int spanX = (width + smallerSize) / smallerSize;
        int spanY = (height + smallerSize) / smallerSize;

        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "getCellCountForDimensions width = " + width + ", height =" + height + ", spanX = " + spanX
                    + ", spanY = " + spanY + ", this = " + this);
        }
        return new int[] { spanX, spanY };
    }

    /**
     * Start dragging the specified child
     *
     * @param child The child that is being dragged
     */
    void onDragChild(View child) {
        PagedViewCellLayout.LayoutParams lp = (PagedViewCellLayout.LayoutParams) child.getLayoutParams();
        lp.isDragging = true;
    }

    /**
     * Estimates the number of cells that the specified width would take up.
     */
    public int estimateCellHSpan(int width) {
        // We don't show the next/previous pages any more, so we use the full width, minus the
        // padding
        int availWidth = width - (getPaddingLeft() + getPaddingRight());

        // We know that we have to fit N cells with N-1 width gaps, so we just juggle to solve for N
        int n = Math.max(1, (availWidth + mWidthGap) / (mCellWidth + mWidthGap));

        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "estimateCellHSpan width = " + width
                    + ", availWidth = " + availWidth + ", n = " + n + ", this = " + this);
        }

        // We don't do anything fancy to determine if we squeeze another row in.
        return n;
    }

    /**
     * Estimates the number of cells that the specified height would take up.
     */
    public int estimateCellVSpan(int height) {
        // The space for a page is the height - top padding (current page) - bottom padding (current
        // page)
        int availHeight = height - (getPaddingTop() + getPaddingBottom());

        // We know that we have to fit N cells with N-1 height gaps, so we juggle to solve for N
        int n = Math.max(1, (availHeight + mHeightGap) / (mCellHeight + mHeightGap));

        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "estimateCellVSpan width = " + height
                    + ", availHeight = " + availHeight + ", n = " + n + ", this = " + this);
        }
        // We don't do anything fancy to determine if we squeeze another row in.
        return n;
    }

    /** Returns an estimated center position of the cell at the specified index */
    public int[] estimateCellPosition(int x, int y) {
        int[] result = new int[] {
                getPaddingLeft() + (x * mCellWidth) + (x * mWidthGap) + (mCellWidth / 2),
                getPaddingTop() + (y * mCellHeight) + (y * mHeightGap) + (mCellHeight / 2)
        };
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "estimateCellPosition x = " + x + ", y = " + y
                    + ", result[0] = " + result[0] + ", result[1] = " + result[1] + ", this = " + this);
        }
        return result;
    }

    public void calculateCellCount(int width, int height, int maxCellCountX, int maxCellCountY) {
        mCellCountX = Math.min(maxCellCountX, estimateCellHSpan(width));
        mCellCountY = Math.min(maxCellCountY, estimateCellVSpan(height));
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "calculateCellCount width = " + width
                    + ", height = " + height + ", maxCellCountX = " + maxCellCountX
                    + ", maxCellCountY = " + maxCellCountY + ", mCellCountX = " + mCellCountX
                    + ", mCellCountY = " + mCellCountY + ", this = " + this);
        }
        requestLayout();
    }

    /**
     * Estimates the width that the number of hSpan cells will take up.
     */
    public int estimateCellWidth(int hSpan) {
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "estimeateCellWidth hSpan = " + hSpan
                    + ", mCellWidth = " + mCellWidth + ", this = " + this);
        }
        // TODO: we need to take widthGap into effect
        return hSpan * mCellWidth;
    }

    /**
     * Estimates the height that the number of vSpan cells will take up.
     */
    public int estimateCellHeight(int vSpan) {
        if (LauncherLog.DEBUG_LAYOUT) {
            LauncherLog.d(TAG, "estimateCellHeight sSpan = " + vSpan
                    + ", mCellHeight = " + mCellHeight + ", this = " + this);
        }
        // TODO: we need to take heightGap into effect
        return vSpan * mCellHeight;
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new PagedViewCellLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof PagedViewCellLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new PagedViewCellLayout.LayoutParams(p);
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        /**
         * Horizontal location of the item in the grid.
         */
        @ViewDebug.ExportedProperty
        public int cellX;

        /**
         * Vertical location of the item in the grid.
         */
        @ViewDebug.ExportedProperty
        public int cellY;

        /**
         * Number of cells spanned horizontally by the item.
         */
        @ViewDebug.ExportedProperty
        public int cellHSpan;

        /**
         * Number of cells spanned vertically by the item.
         */
        @ViewDebug.ExportedProperty
        public int cellVSpan;

        /**
         * Is this item currently being dragged
         */
        public boolean isDragging;

        // a data object that you can bind to this layout params
        private Object mTag;

        // X coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int x;
        // Y coordinate of the view in the layout.
        @ViewDebug.ExportedProperty
        int y;

        public LayoutParams() {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            cellHSpan = 1;
            cellVSpan = 1;
        }

        public LayoutParams(LayoutParams source) {
            super(source);
            this.cellX = source.cellX;
            this.cellY = source.cellY;
            this.cellHSpan = source.cellHSpan;
            this.cellVSpan = source.cellVSpan;
        }

        public LayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
            super(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            this.cellX = cellX;
            this.cellY = cellY;
            this.cellHSpan = cellHSpan;
            this.cellVSpan = cellVSpan;
        }

        public void setup(Context context,
                          int cellWidth, int cellHeight, int widthGap, int heightGap,
                          int hStartPadding, int vStartPadding) {

            final int myCellHSpan = cellHSpan;
            final int myCellVSpan = cellVSpan;
            final int myCellX = cellX;
            final int myCellY = cellY;

            width = myCellHSpan * cellWidth + ((myCellHSpan - 1) * widthGap) -
                    leftMargin - rightMargin;
            height = myCellVSpan * cellHeight + ((myCellVSpan - 1) * heightGap) -
                    topMargin - bottomMargin;

            if (LauncherAppState.getInstance().isScreenLarge()) {
                x = hStartPadding + myCellX * (cellWidth + widthGap) + leftMargin;
                y = vStartPadding + myCellY * (cellHeight + heightGap) + topMargin;
            } else {
                x = myCellX * (cellWidth + widthGap) + leftMargin;
                y = myCellY * (cellHeight + heightGap) + topMargin;
            }
        }

        public Object getTag() {
            return mTag;
        }

        public void setTag(Object tag) {
            mTag = tag;
        }

        public String toString() {
            return "(" + this.cellX + ", " + this.cellY + ", " +
                this.cellHSpan + ", " + this.cellVSpan + ")";
        }
    }
}