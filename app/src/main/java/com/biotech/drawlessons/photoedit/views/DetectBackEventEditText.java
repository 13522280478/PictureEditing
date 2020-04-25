package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by xintu on 2018/2/12.
 */

public class DetectBackEventEditText extends androidx.appcompat.widget.AppCompatEditText{
    private KeyPreImeListener mListener;

    public interface KeyPreImeListener {
        boolean onKeyPreIme(int keyCode, KeyEvent event);
    }
    public DetectBackEventEditText(Context context) {
        super(context);
    }

    public DetectBackEventEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetectBackEventEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setKeyPreImeListener(KeyPreImeListener listener) {
        mListener = listener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mListener != null) {
            return mListener.onKeyPreIme(keyCode, event);
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
