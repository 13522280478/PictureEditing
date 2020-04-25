package com.biotech.drawlessons.photoedit.indicator.transition;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biotech.drawlessons.R;
import com.biotech.drawlessons.photoedit.indicator.Indicator;


/**
 * 
 * @author LuckyJayce
 *
 */
public class OnTransitionTextListener implements Indicator.OnTransitionListener {
	private float selectSize = -1;
	private float unSelectSize = -1;
	private ColorGradient gradient;
	private float dFontFize = -1;

	private boolean isPxSize = false;

	public OnTransitionTextListener() {
		super();
	}

	public void changeTabTheme(){
		gradient.applyTheme();
	}

	public OnTransitionTextListener(float selectSize, float unSelectSize, int selectColor, int unSelectColor,int  selectColorNight,int unSelectColorNight) {
		super();
		setColor(selectColor, unSelectColor,selectColorNight,unSelectColorNight);
		setSize(selectSize, unSelectSize);
	}

	public final OnTransitionTextListener setSize(float selectSize, float unSelectSize) {
		isPxSize = false;
		this.selectSize = selectSize;
		this.unSelectSize = unSelectSize;
		this.dFontFize = selectSize - unSelectSize;
		return this;
	}

	public final OnTransitionTextListener setValueFromRes(Context context, int selectColorId, int unSelectColorId, int  selectColorNight, int unSelectColorNight, int selectSizeId,
                                                          int unSelectSizeId) {
		setColorId(context, selectColorId, unSelectColorId,selectColorNight,unSelectColorNight);
		setSizeId(context, selectSizeId, unSelectSizeId);
		return this;
	}

	public final OnTransitionTextListener setColorId(Context context, int selectColorId, int unSelectColorId, int  selectColorNight, int unSelectColorNight) {
		Resources res = context.getResources();
		setColor(res.getColor(selectColorId), res.getColor(unSelectColorId),res.getColor(selectColorNight), res.getColor(unSelectColorNight));
		return this;
	}

	public final OnTransitionTextListener setSizeId(Context context, int selectSizeId, int unSelectSizeId) {
		Resources res = context.getResources();
		setSize(res.getDimensionPixelSize(selectSizeId), res.getDimensionPixelSize(unSelectSizeId));
		isPxSize = true;
		return this;
	}

	public final OnTransitionTextListener setColor(int selectColor, int unSelectColor,int selectColorNight,int  unSelectColorNight) {
		gradient = new ColorGradient(unSelectColor, selectColor,unSelectColorNight,selectColorNight,100);
		return this;
	}

	/**
	 * 如果tabItemView 不是目标的TextView，那么你可以重写该方法返回实际要变化的TextView
	 * 
	 * @param tabItemView
	 *            Indicator的每一项的view
	 * @param position
	 *            view在Indicator的位置索引
	 * @return
	 */
	public RelativeLayout getTextView(View tabItemView, int position) {
		return (RelativeLayout) tabItemView;
	}

	@Override
	public void onTransition(View view, int position, float selectPercent) {
		RelativeLayout parent = getTextView(view, position);
		TextView sun = (TextView)parent.findViewById(R.id.text_channel_name);
		if(selectPercent>=0.7){
			sun.setTextColor(gradient.getColor((int) (1 * 100)));
		}else if(selectPercent<=0.3){
			sun.setTextColor(gradient.getColor((int) (0 * 100)));
		}
	}

}
