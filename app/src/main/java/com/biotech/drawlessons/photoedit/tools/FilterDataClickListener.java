package com.biotech.drawlessons.photoedit.tools;

import android.view.View;

import com.biotech.drawlessons.photoedit.resourcepicker.custom.recyclerview.model.FilterData;

/**
 * Created by xintu on 2018/2/21.
 */

public interface FilterDataClickListener {
    void onFilterItemClick(View view, int position, FilterData data);
}
