package com.biotech.drawlessons.photoedit.utilsmodel;


import com.biotech.drawlessons.photoedit.tools.FilterDataClickListener;

/**
 * Created by xintu on 2018/3/27.
 */

public interface IExpandLayout extends FilterDataClickListener {
    void onCancelIconClick();

    void onDoneIconClick();

    void onRestoreIconClick();

    void onRotateIconClick();
}
