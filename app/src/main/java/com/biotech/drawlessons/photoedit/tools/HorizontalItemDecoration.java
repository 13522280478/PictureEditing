package com.biotech.drawlessons.photoedit.tools;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xintu on 2017/11/8.
 */

public class HorizontalItemDecoration extends RecyclerView.ItemDecoration {
    private boolean mIncludeEdge;
    private int mHorizontalSpace, mVerticalSpace;


    public HorizontalItemDecoration(int horizontalSpace, int verticalSpace, boolean includeEdge) {
        mHorizontalSpace = horizontalSpace;
        mVerticalSpace = verticalSpace;
        mIncludeEdge = includeEdge;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // item position

        if (mIncludeEdge) {
            outRect.top = mVerticalSpace;
            outRect.left = mHorizontalSpace / 2;
            outRect.right = mHorizontalSpace / 2;
            outRect.bottom = mHorizontalSpace;
        } else {
            if (position == 0) {
                outRect.top = mVerticalSpace;
                outRect.left = 0;
                outRect.right = mHorizontalSpace / 2;
                outRect.bottom = mHorizontalSpace;
            } else if (position == parent.getChildCount() - 1) {
                outRect.top = mVerticalSpace;
                outRect.left = mHorizontalSpace / 2;
                outRect.right = 0;
                outRect.bottom = mHorizontalSpace;
            } else {
                outRect.top = mVerticalSpace;
                outRect.left = mHorizontalSpace / 2;
                outRect.right = mHorizontalSpace / 2;
                outRect.bottom = mHorizontalSpace;
            }
        }
    }
}
