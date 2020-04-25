package com.biotech.drawlessons.photoedit.utilsmodel;


import com.biotech.drawlessons.photoedit.tools.ColorPickerView;
import com.biotech.drawlessons.photoedit.tools.IStrokeWidthChange;

/**
 * Created by xintu on 2018/3/28.
 */

public interface IColorPicker extends IStrokeWidthChange, ColorPickerView.OnColorPickerListener {
    int SHOW_DELAY_TIME = 500;
    int DISMISS_DELAY_TIME = 0;

    void showColorPicker(int delay);

    void dismissColorPicker(int delay);

    void onUndoStateChange(boolean canUndo);
}
