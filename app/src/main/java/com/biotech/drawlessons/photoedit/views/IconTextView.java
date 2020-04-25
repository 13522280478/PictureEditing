package com.biotech.drawlessons.photoedit.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.biotech.drawlessons.photoedit.utils.Tuples;

import java.lang.ref.WeakReference;

/**
 * Created by xintu on 2017/6/2.
 * 为了减少layout层级，这里写一个带icon的TextView，本来的TextView是支持同时添加图片和文字的
 * 但是不支持图片和文字同时居中，这个控件就是为了解决这个问题。
 * 目前只支持了 drawableLeft和 drawableRight 的居中，不支持 drawableTop 和 drawableBottom 的居中
 * 还有就是不能同时有 drawableLeft 和 drawableRight
 */
public class IconTextView extends androidx.appcompat.widget.AppCompatTextView {

    public static final int LEFT = 0;
    public static final int RIGHT = 2;
    private boolean hasLeft;
    private boolean hasRight;
    private boolean enableShowDrawable = true;
    private int iconRightValue = -1;
    private int iconTopValue = -1;
    private int iconBottomValue = -1;
    private Tuples.Three<WeakReference<Drawable>, Integer, Integer> mCache;

    public IconTextView(Context context) {
        this(context, null);
    }

    public IconTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 强制垂直居中
        int gravity = getGravity() | Gravity.VERTICAL_GRAVITY_MASK;
        setGravity(gravity);
        mCache = new Tuples.Three<>(null, 0, -1);
    }

    public void setDrawable(int orientation, int resLight, int resNight) {
        if (!enableShowDrawable) {
            return;
        }
        if (orientation != LEFT && orientation != RIGHT) {
            return;
        }

        if (orientation == LEFT) {
            hasLeft = true;
            hasRight = false;
        } else {
            hasLeft = false;
            hasRight = true;
        }

        Drawable drawable;
        int temp;
        drawable = getResources().getDrawable(resLight);
        temp = resLight;
        if (mCache.mValue1 != null
                && mCache.mValue2 != null && mCache.mValue2.equals(temp)
                && mCache.mValue3 != orientation){
            return;
        }
        if (drawable != null) {
            mCache.mValue1 = new WeakReference<Drawable>(drawable);
            mCache.mValue2 = temp;
            mCache.mValue3 = orientation;

            iconBottomValue = -1;
            iconRightValue = -1;
            iconTopValue = -1;

            int width = drawable.getIntrinsicWidth();
            int height = drawable.getIntrinsicHeight();
            drawable.setBounds(0, 0, width, height);

            if (orientation == LEFT) {
                setCompoundDrawables(drawable, null, null, null);
                int gravity = Gravity.LEFT | Gravity.VERTICAL_GRAVITY_MASK;
                setGravity(gravity);
            } else {
                setCompoundDrawables(null, null, drawable, null);
                int gravity = Gravity.RIGHT | Gravity.VERTICAL_GRAVITY_MASK;
                setGravity(gravity);
            }
        }
    }

    public void disableShowDrawable(){
        setCompoundDrawables(null, null, null, null);
        enableShowDrawable = false;

        hasLeft = false;
        hasRight = false;
        setGravity(Gravity.CENTER);
    }

    public void setEnableShowDrawable(){
        enableShowDrawable = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (hasLeft){
            Drawable leftDrawable = getCompoundDrawables()[LEFT];
            int drawableWidth = leftDrawable.getIntrinsicWidth();
            float textWidth = getPaint().measureText(getText().toString());
            float translateX = (getWidth() - textWidth - drawableWidth - getCompoundDrawablePadding()) / 2;

            if (iconRightValue == -1 || iconTopValue == -1 || iconBottomValue == -1){
                iconRightValue = (int) (translateX + drawableWidth);
                iconTopValue = (getHeight() - leftDrawable.getIntrinsicHeight()) / 2;
                iconBottomValue = iconTopValue + leftDrawable.getIntrinsicHeight();
            }

            canvas.save();
            canvas.translate(translateX, 0);
            super.onDraw(canvas);
            canvas.restore();

            return;
        }

        if (hasRight){
            Drawable rightDrawable = getCompoundDrawables()[RIGHT];
            int drawableWidth = rightDrawable.getIntrinsicWidth();
            float textWidth = getPaint().measureText(getText().toString());
            float translateX = (textWidth + drawableWidth + getCompoundDrawablePadding() - getWidth()) / 2;

            if (iconRightValue == -1 || iconTopValue == -1 || iconBottomValue == -1) {
                iconRightValue = (int) (translateX + textWidth + drawableWidth);
                iconTopValue = (getHeight() - rightDrawable.getIntrinsicHeight()) / 2;
                iconBottomValue = iconTopValue + rightDrawable.getIntrinsicHeight();
            }

            canvas.save();
            canvas.translate(translateX, 0);
            super.onDraw(canvas);
            canvas.restore();

            return;
        }

        super.onDraw(canvas);

    }

    public int getIconRightValue(){
        return iconRightValue;
    }

    public int getIconTopValue(){
        return iconTopValue;
    }

    public int getIconBottomValue(){
        return iconBottomValue;
    }
}
