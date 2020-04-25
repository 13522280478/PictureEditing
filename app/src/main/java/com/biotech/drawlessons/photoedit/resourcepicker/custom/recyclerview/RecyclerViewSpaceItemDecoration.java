package com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


/***************************************************************************************************
 * 描述：RecyclerView 网格布局 水平 垂直间距
 *
 * 作者：champion
 *
 * 时间：17/12/6
 **************************************************************************************************/


public class RecyclerViewSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpace;
    private int verticalSpace;
    private int spanCount;
    private boolean includeEdge;

    public RecyclerViewSpaceItemDecoration(int horizontalSpace, int verticalSpace, int spanCount, boolean includeEdge) {
        this.horizontalSpace = horizontalSpace;
        this.verticalSpace = verticalSpace;
        this.spanCount = spanCount;
        this.includeEdge = includeEdge;
    }


    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view); // item position

        parent.getChildViewHolder(view);


        int column = position % spanCount; // item column
        if (includeEdge) {
            outRect.left = horizontalSpace - column * horizontalSpace / spanCount; // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * horizontalSpace / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = verticalSpace;
            }
            outRect.bottom = verticalSpace; // item bottom
        } else {
            outRect.left = column * horizontalSpace / spanCount; // column * ((1f / spanCount) * spacing)
            outRect.right = horizontalSpace - (column + 1) * horizontalSpace / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = verticalSpace; // item top
            }
        }
    }

}