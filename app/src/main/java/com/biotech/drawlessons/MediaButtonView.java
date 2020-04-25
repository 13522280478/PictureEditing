package com.biotech.drawlessons;

/**
 * @author TuXin
 * @date 2020/4/24 6:43 PM.
 * <p>
 * Email : tuxin@pupupula.com
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;

import com.biotech.drawlessons.photoedit.utils.ArgbEvaluator;

import static android.view.MotionEvent.ACTION_CANCEL;
import static android.view.MotionEvent.ACTION_DOWN;
import static android.view.MotionEvent.ACTION_MOVE;
import static android.view.MotionEvent.ACTION_UP;

/**
 * Created by xintu on 2017/9/21.
 * 拍照、录视频、直播页面的按钮。
 * 第一期09/25:只是完成了功能，很多细节和写法规范都没有修改，只是能用的状态
 */

public class MediaButtonView extends View implements ValueAnimator.AnimatorUpdateListener {

    private final static String TAG = "MediaButtonView";
    // 录像按钮正常状态
    private final static int STATUS_RECORD_NORMAL = 0;
    // 录像按钮被点击状态
    private final static int STATUS_RECORD_CLICKED = 1;
    // 录像按钮长按后的状态
    private final static int STATUS_RECORD_LONG_PRESSED = 2;
    // 从录像到拍照的状态
    private final static int STATUS_RECORD_TO_CAMERA = 3;
    // 拍照的普通状态
    private final static int STATUS_CAMERA_NORMAL = 4;
    // 拍照到视频的状态
    private final static int STATUS_CAMERA_TO_RECORD = 5;
    // 直播的普通状态
    private final static int STATUS_LIVE_NORMAL = 6;
    // 视频到直播的状态
    private final static int STATUS_RECORD_TO_LIVE = 7;
    // 直播到录视频的状态
    private final static int STATUS_LIVE_TO_RECORD = 8;
    // 拍照到直播的状态
    private final static int STATUS_CAMERA_TO_LIVE = 9;
    // 直播到拍照的状态
    private final static int STATUS_LIVE_TO_CAMERA = 10;
    // 录像的长按到正常的状态
    private final static int STATUS_LONG_PRESS_TO_NORMAL = 11;
    // 录像的点击状态到正常的状态
    private final static int STATUS_RECORD_CLICKED_TO_NORMAL = 12;
    // 拍照按钮点击(会有0.1s的变浅，然后再变回来的效果，最后再变成完全透明的效果)
    private final static int STATUS_CAMERA_CLICKED = 13;
    ObjectAnimator mOuterRingBoundAnim, mInnerShapeBoundAnim, mInnerShapeWidthAnim,
            mInnerShapeHeightAnim, mInnerShapeColorAnim, mInnerRingColorAnim;
    ObjectAnimator mOuterRingStrokeAnim, mInnerRingStrokeAnim, mInnerShapeStrokeAnim;
    ObjectAnimator mRoundRectRadiusAnim;
    ObjectAnimator mInnerShapeAlphaAnim, mInnerRingAlphaAnim, mOuterRingAlphaAnim;
    ObjectAnimator mTextAlphaAnim;
    AnimatorSet mNormalToPressSet, mNormalToClickSet, mRecordToCameraSet, mCameraToRecordSet, mRecordClickToNormalSet,
            mRecordToLiveSet, mLiveToRecordSet, mCameraToLiveSet, mLiveToCameraSet, mRecordPressToNormalSet, mCameraClickedSet;
    /**
     * 长按松手后的动画
     */
    float currOuterRingRect;
    private Context mContext;
    // normal状态下的宽高
    private int mNormOutWidth, mNormOutHeight, mNormInnWidth, mNormInnHeight;
    // 当前View的状态
    private int mCurViewStatus;
    // 长按timeout
    private long mLongPressTimeout;
    // action down时的时间
    private long mActionDownTime;
    // action up时的时间
    private long mActionUpTime;
    // 上一次action up 时间
    private long mLastActionDownTime;
    // 是否响应long press
    private boolean mLongClickable = true;
    private int mHeight, mWidth;
    private float mOuterRingStroke, mInnerRingStroke, mThirdRingStroke;
    private float mTextX, mTextY, mTextSize;
    private Paint mOuterRingPaint, mInnerRingPaint, mInnerShapePaint;
    private TextPaint mTextPaint;
    private String mTextStr;
    private int mOuterRingColor, mInnerRingColor, mInnerShapeColor;
    private RectF mOuterRingRect, mInnerRingRect, mInnerShapeRect;
    private int mInnerShapeAlpha = 255, mTextPaintAlpha = 255, mInnerRingAlpha = 255, mOuterRingAlpha = 255;
    private int mColNormGray = 0xFFB1B1B1, mColPressGray = 0xB2B1B1B1, mColNormYellow = 0xFFFDD536, mColPressYellow = 0xFFE0B818,
            mColTextStr = 0xFF454545, mColWhite = 0xFFFFFFFF, mColPressWhite = 0xFFEDEDED;
    private float dp60, dp80, dp90, dp3, dp44, dp40, dp20;
    private float mRoundedRectRx, mRoundedRectRy;
    private OnCustomClickListener mClickListener;
    private OnCustomLongPressListener mLongPressListener;
    // 是否响应点击和长按事件
    private boolean mEnableClick = true;
    private Runnable mLongPressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mLongPressListener != null) {
                mLongPressListener.longPressDown();
            }
        }
    };

    public MediaButtonView(Context context) {
        this(context, null);
    }

    public MediaButtonView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MediaButtonView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initData();
        initPaint();
        initColor();
    }

    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private void initData() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        mLongPressTimeout = ViewConfiguration.getLongPressTimeout();

        dp60 = dp2Px(mContext, 60);
        mNormOutWidth = (int) dp60;
        mNormOutHeight = (int) dp60;

        dp44 = dp2Px(mContext, 44);
        mNormInnWidth = (int) dp44;
        mNormInnHeight = (int) dp44;

        mOuterRingStroke = dp2Px(mContext, 3);

        dp20 = dp2Px(mContext, 20);
        dp40 = dp2Px(mContext, 40);
        dp80 = dp2Px(mContext, 80);
        dp90 = dp2Px(mContext, 90);

        mTextSize = UtilsKt.dp2px(18);

        mOuterRingRect = new RectF();
        mInnerShapeRect = new RectF();
        mInnerRingRect = new RectF();
    }

    private void initPaint() {
        mOuterRingPaint = new Paint();
        mOuterRingPaint.setStyle(Paint.Style.STROKE);
        mOuterRingPaint.setAntiAlias(true);
        mOuterRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterRingPaint.setStrokeJoin(Paint.Join.ROUND);
        mOuterRingPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mInnerRingPaint = new Paint();
        mInnerRingPaint.setStyle(Paint.Style.STROKE);
        mInnerRingPaint.setAntiAlias(true);
        mInnerRingPaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerRingPaint.setStrokeJoin(Paint.Join.ROUND);
        mInnerRingPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mInnerShapePaint = new Paint();
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setAntiAlias(true);
        mInnerShapePaint.setStrokeCap(Paint.Cap.ROUND);
        mInnerShapePaint.setStrokeJoin(Paint.Join.ROUND);
        mInnerShapePaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColTextStr);

        mOuterRingColor = mColNormGray;
        mInnerRingColor = mColPressGray;
        mInnerShapeColor = mColNormYellow;
        mTextStr = "开始直播";
    }

    private void initColor() {
//        mColNormGray = 0xFFB1B1B1, mColPressGray = 0xB2B1B1B1, mColNormYellow = 0xFFFDD536, mColPressYellow = 0xFFE0B818,
//                mColTextStr = 0xFF454545, mColWhite = 0xFFFFFFFF, mColPressWhite = 0xFFEDEDED;
//        if (SnsUtil.isThemeDefaultSimple()) {
            mColNormGray = mContext.getResources().getColor(R.color.Blk_6);
            mColPressGray = mContext.getResources().getColor(R.color.Blk_6_alpha_30);
            mColNormYellow = mContext.getResources().getColor(R.color.Ylw_1);
            //TODO:要一个这个颜色的夜间模式
//            mColPressYellow = 0xFFE0B818;
            mColPressYellow = mContext.getResources().getColor(R.color.Ylw_1_alpha_30);
            mColTextStr = mContext.getResources().getColor(R.color.Blk_4);
            mColWhite = mContext.getResources().getColor(R.color.Blk_11);
            mColPressWhite = mContext.getResources().getColor(R.color.Blk_11_alpha_30);
//        } else {
//            mColNormGray = mContext.getResources().getColor(R.color.Blk_6_night);
//            mColPressGray = mContext.getResources().getColor(R.color.Blk_6_night_alpha_30);
//            mColNormYellow = mContext.getResources().getColor(R.color.Ylw_1_night);
//            mColPressYellow = mContext.getResources().getColor(R.color.Ylw_1_night_alpha_30);
//            mColTextStr = mContext.getResources().getColor(R.color.Blk_4_night);
//            mColWhite = mContext.getResources().getColor(R.color.Blk_11_night);
//            mColPressWhite = mContext.getResources().getColor(R.color.Blk_11_night_alpha_30);
//        }
    }

    public void setEnableClick(boolean enable) {
        this.mEnableClick = enable;
    }

    public void setText(String str) {
        this.mTextStr = str;
        invalidate();
    }

    /**
     * 设置为状态为拍照
     */
    public void setCameraNormalStatus() {
        mCurViewStatus = STATUS_CAMERA_NORMAL;
        invalidate();
    }

    /**
     * 设置为状态为录视频
     */
    public void setRecordNormalStatus() {
        mCurViewStatus = STATUS_RECORD_NORMAL;
        invalidate();
    }

    /**
     * 设置为状态为直播
     */
    public void setLiveNormalStatus() {
        mCurViewStatus = STATUS_LIVE_NORMAL;
        invalidate();
    }

    /**
     * 从被点击后的状态 到 正常状态
     */
    public void clickToRecordNormalStatus() {
        mCurViewStatus = STATUS_RECORD_NORMAL;
        startRecordClickToNormalStatus();
    }

    /**
     * 从长按中的状态 到 正常状态
     */
    public void longPressToRecordNormalStatus() {
        mCurViewStatus = STATUS_LONG_PRESS_TO_NORMAL;
        startRecordPressToNormalStatus();
    }

    /**
     * 从正常状态 到被点击后的状态
     */
    public void normalToRecordClickStatus() {
        mCurViewStatus = STATUS_RECORD_CLICKED;
        startRecordNormalToClickStatus();
    }

    /**
     * 正常状态 到 被长按状态
     */
    public void normalToLongPressStatus() {
        mCurViewStatus = STATUS_RECORD_LONG_PRESSED;
        startRecordNormalToPressStatus();
    }

    /**
     * 视频 到 相机
     */
    public void recordToCameraStatus() {
        mCurViewStatus = STATUS_RECORD_TO_CAMERA;
        startRecordToCamera();
    }

    /**
     * 相机 到 视频
     */
    public void cameraToRecordStatus() {
        mCurViewStatus = STATUS_CAMERA_TO_RECORD;
        startCameraToRecord();
    }

    /**
     * 视频 到 直播
     */
    public void recordToLiveStatus() {
        mCurViewStatus = STATUS_RECORD_TO_LIVE;
        startRecordToLive();
    }

    /**
     * 直播 到 视频
     */
    public void liveToRecordStatus() {
        mCurViewStatus = STATUS_LIVE_TO_RECORD;
        startLiveToRecord();
    }

    /**
     * 照相 到 直播
     */
    public void cameraToLiveStatus() {
        mCurViewStatus = STATUS_CAMERA_TO_LIVE;
        startCameraToLive();
    }

    /**
     * 直播 到 拍照
     */
    public void liveToCameraStatus() {
        mCurViewStatus = STATUS_LIVE_TO_CAMERA;
        startLiveToCamera();
    }

    public void clickCameraBtn() {
        mCurViewStatus = STATUS_CAMERA_CLICKED;
        startCameraClicked();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        if (mCurViewStatus == STATUS_RECORD_NORMAL) {
            drawRecordNormalStatus();
        } else if (mCurViewStatus == STATUS_CAMERA_NORMAL) {
            drawCameraNormalStatus();
        } else if (mCurViewStatus == STATUS_LIVE_NORMAL) {
            drawLiveNormalStatus();
        } else if (mCurViewStatus == STATUS_CAMERA_TO_RECORD) {
            resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);
            resetRectSize(mInnerRingRect, dp44, dp2Px(mContext, 1));
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mCurViewStatus == STATUS_RECORD_NORMAL) {
            drawRingAndCircle(canvas);
        } else if (mCurViewStatus == STATUS_RECORD_CLICKED || mCurViewStatus == STATUS_RECORD_CLICKED_TO_NORMAL) {
            drawRingAndRoundedRect(canvas);
        } else if (mCurViewStatus == STATUS_RECORD_TO_CAMERA) {
            drawThreeRing(canvas);
        } else if (mCurViewStatus == STATUS_CAMERA_NORMAL || mCurViewStatus == STATUS_CAMERA_CLICKED) {
            drawTwoRing(canvas);
        } else if (mCurViewStatus == STATUS_RECORD_LONG_PRESSED || mCurViewStatus == STATUS_CAMERA_TO_RECORD
                || mCurViewStatus == STATUS_LONG_PRESS_TO_NORMAL) {
            drawTwoRingAndCircle(canvas);
        } else if (mCurViewStatus == STATUS_LIVE_NORMAL) {
            drawRoundRectAndText(canvas);
        } else if (mCurViewStatus == STATUS_RECORD_TO_LIVE || mCurViewStatus == STATUS_CAMERA_TO_LIVE) {
            drawRingAndRoundRectAndText(canvas);
        } else if (mCurViewStatus == STATUS_LIVE_TO_RECORD || mCurViewStatus == STATUS_LIVE_TO_CAMERA) {
            drawTwoRingAndRoundRectAndText(canvas);
        }
    }

    /**
     * 普通的绘制事件，画外圈和内圈的圆
     */
    private void drawRingAndCircle(Canvas canvas) {
        // 画外圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);

        // 画内圈的圆
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mInnerShapeRect.width() / 2, mInnerShapePaint);
    }

    /**
     * 外层是圆，内层是圆角矩形的绘制
     */
    private void drawRingAndRoundedRect(Canvas canvas) {
        // 画外圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);

        // 画内圈的圆
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawRoundRect(mInnerShapeRect, mRoundedRectRx, mRoundedRectRy, mInnerShapePaint);
    }

    /**
     * 画两个圆环
     */
    private void drawTwoRing(Canvas canvas) {
        // 画外圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);

        // 画内圈的圆
        mInnerRingPaint.setStyle(Paint.Style.STROKE);
        mInnerRingPaint.setColor(mInnerRingColor);
        mInnerRingPaint.setAlpha(mInnerRingAlpha);
        mInnerRingPaint.setStrokeWidth(mInnerRingStroke);
        canvas.drawArc(mInnerRingRect, 0, 360, false, mInnerRingPaint);
    }

    /**
     * 两个圆环和一个圆
     */
    private void drawTwoRingAndCircle(Canvas canvas) {
        // 内圈的圆弧
        mInnerRingPaint.setStrokeWidth(mInnerRingStroke);
        mInnerRingPaint.setColor(mInnerRingColor);
        mInnerRingPaint.setAlpha(mInnerRingAlpha);
        canvas.drawArc(mInnerRingRect, 0, 360, false, mInnerRingPaint);

        // 最内圈的圆
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawCircle(mWidth / 2, mHeight / 2, mInnerShapeRect.width() / 2, mInnerShapePaint);

        // 画外最圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);
    }

    /**
     * 三个圆环，在recordToCamera的时候用到
     */
    public void drawThreeRing(Canvas canvas) {
        // 最内圈的圆弧
        mInnerShapePaint.setStrokeWidth(mThirdRingStroke);
        mInnerShapePaint.setStyle(Paint.Style.STROKE);
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawArc(mInnerShapeRect, 0, 360, false, mInnerShapePaint);

        // 内圈的圆弧
        mInnerRingPaint.setStrokeWidth(mInnerRingStroke);
        mInnerRingPaint.setColor(mInnerRingColor);
        mInnerRingPaint.setAlpha(mInnerRingAlpha);
        canvas.drawArc(mInnerRingRect, 0, 360, false, mInnerRingPaint);

        // 画外最圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);
    }

    /**
     * 画圆角矩形和文字
     */
    private void drawRoundRectAndText(Canvas canvas) {
        // 画内层的圆角矩形
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(mInnerShapeRect, mRoundedRectRx, mRoundedRectRy, mInnerShapePaint);

        // 画文字
        mTextPaint.setColor(mColTextStr);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAlpha(mTextPaintAlpha);
        canvas.drawText(mTextStr, mTextX, mTextY, mTextPaint);
    }

    /**
     * 圆环 + 圆角矩形 + 文字
     */
    private void drawRingAndRoundRectAndText(Canvas canvas) {
        // 画外最圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);

        // 画内层的圆角矩形
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawRoundRect(mInnerShapeRect, mRoundedRectRx, mRoundedRectRy, mInnerShapePaint);

        // 画文字
        mTextPaint.setColor(mColTextStr);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAlpha(mTextPaintAlpha);
        canvas.drawText(mTextStr, mTextX, mTextY, mTextPaint);
    }

    /**
     * 圆环 + 圆（其实也是圆环，但是stroke取值比较大，刚好凑成圆） + 圆角矩形 + 文字
     * 这个状态是直播到录视频的时候用，因为录视频-->直播和 直播 --> 录视频 是不可逆的...
     */
    private void drawTwoRingAndRoundRectAndText(Canvas canvas) {
        // 画内层的圆角矩形
        mInnerShapePaint.setColor(mInnerShapeColor);
        mInnerShapePaint.setStyle(Paint.Style.FILL);
        mInnerShapePaint.setAlpha(mInnerShapeAlpha);
        canvas.drawRoundRect(mInnerShapeRect, mRoundedRectRx, mRoundedRectRy, mInnerShapePaint);

        // 画第二个圆弧（也就是圆）
        mInnerRingPaint.setStrokeWidth(mInnerRingStroke);
        mInnerRingPaint.setColor(mInnerRingColor);
        mInnerRingPaint.setAlpha(mInnerRingAlpha);
        canvas.drawArc(mInnerRingRect, 0, 360, false, mInnerRingPaint);

        // 画文字
        mTextPaint.setColor(mColTextStr);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setAlpha(mTextPaintAlpha);
        canvas.drawText(mTextStr, mTextX, mTextY, mTextPaint);

        // 画外最圈的圆弧
        mOuterRingPaint.setColor(mOuterRingColor);
        mOuterRingPaint.setAlpha(mOuterRingAlpha);
        mOuterRingPaint.setStrokeWidth(mOuterRingStroke);
        canvas.drawArc(mOuterRingRect, 0, 360, false, mOuterRingPaint);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        endAllAnim();
    }

    /**
     * 点击事件放在该View内部处理
     * 调用者设置setOnCustomClickListener监听OnClick事件
     * 设置setOnCustomLongPressListener监听长按事件
     * <p>
     * 该View认为 OnClick和长按事件不能共存
     * 即在响应OnClick后，不再响应长按
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果touch的坐标不在允许的范围内，不处理这touch事件
        if (!mEnableClick || !isTouchBoundLegal(event.getX(), event.getY())) return false;
        switch (event.getAction()) {
            case ACTION_DOWN:
                mActionDownTime = System.currentTimeMillis();
                if (mActionDownTime - mLastActionDownTime < 200) {
                    mLastActionDownTime = mActionDownTime;
                    return false;
                }
                mLastActionDownTime = mActionDownTime;
                // +20是为了防止极端情况，onClick 和 long press都被调用
                if (mCurViewStatus == STATUS_RECORD_NORMAL) {
                    this.postDelayed(mLongPressRunnable, mLongPressTimeout + 20);
                }
                break;
            case ACTION_MOVE:
                break;
            case ACTION_UP:
                mActionUpTime = System.currentTimeMillis();
                this.removeCallbacks(mLongPressRunnable);
                // 认为是click事件
                if (mActionUpTime - mActionDownTime < mLongPressTimeout) {
                    if (mClickListener != null) {
                        mClickListener.onClick();
                    }
                }
                // 长按事件结束
                else {
                    if (mLongPressListener != null &&
                            mCurViewStatus == STATUS_RECORD_LONG_PRESSED) {
                        mLongPressListener.longPressUp();
                    }
                }
                break;
            case ACTION_CANCEL:
                this.removeCallbacks(mLongPressRunnable);
                break;
        }
        return true;
    }

    private boolean isTouchBoundLegal(float x, float y) {
        if (mCurViewStatus == STATUS_LIVE_NORMAL) {
            return (x >= mInnerShapeRect.left - dp20 && x <= mInnerShapeRect.right + dp20)
                    && (y >= mInnerShapeRect.top - dp20) && (y <= mInnerShapeRect.bottom + dp20);
        }
        return (x >= mOuterRingRect.left - dp20 && x <= mOuterRingRect.right + dp20)
                && (y >= mOuterRingRect.top - dp20) && (y <= mOuterRingRect.bottom + dp20);
    }

    /**
     * 录视频的普通状态:
     * 一个外环（灰色）+内圆（黄色）
     */
    public void drawRecordNormalStatus() {
        mCurViewStatus = STATUS_RECORD_NORMAL;
        endAllAnim();
        // outer的rect居中于控件
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingColor = mColNormGray;
        mOuterRingAlpha = 255;
        resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);
        // inner的rect居中于控件
        mInnerShapeColor = mColNormYellow;
        mInnerShapeAlpha = 255;
        resetRectSize(mInnerShapeRect, mNormInnWidth, 0);
        invalidate();
    }

    /**
     * 照相的普通状态:
     * 一个外环（灰色） + 内环（灰色）
     */
    public void drawCameraNormalStatus() {
        mCurViewStatus = STATUS_CAMERA_NORMAL;
        endAllAnim();

        mOuterRingStroke = dp2Px(mContext, 3);
        mOuterRingColor = mColNormGray;
        mOuterRingAlpha = 255;
        resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);

        mInnerRingStroke = dp2Px(mContext, 1);
        mInnerRingColor = mColNormGray;
        mInnerRingAlpha = 255;
        resetRectSize(mInnerRingRect, dp44, dp2Px(mContext, 1));

        invalidate();
    }

    /**
     * 直播的普通状态:
     * 一个圆角矩形 + 文字
     */
    public void drawLiveNormalStatus() {
        mCurViewStatus = STATUS_LIVE_NORMAL;
        endAllAnim();

        mInnerShapeColor = mColWhite;
        resetRectSize(mInnerShapeRect, dp2Px(mContext, 188), dp2Px(mContext, 40), 0);
        mRoundedRectRy = mRoundedRectRx = dp2Px(mContext, 20);

        mTextPaintAlpha = 255;
        // 设置了 textPaint 的 alignCenter，这里直接取x为view的宽的一半就可以居中了
        mTextX = mWidth / 2;
        mTextY = mHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);

        invalidate();
    }

    /**
     * 从普通状态扩散到press状态：
     * 1.外环开始扩大，内环开始收缩
     * 2.1结束之后，开始呼吸的状态。
     */
    private void startRecordNormalToPressStatus() {
        endAllAnim();

        drawRecordNormalStatus();

        mCurViewStatus = STATUS_RECORD_LONG_PRESSED;

        // 外圈的圆弧，做呼吸的状态
        mOuterRingAlpha = 255;
        mOuterRingColor = mColNormGray;
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", dp60, dp2Px(mContext, 78)).setDuration(1400);
        mOuterRingBoundAnim.setRepeatCount(ValueAnimator.INFINITE);
        mOuterRingBoundAnim.setRepeatMode(ValueAnimator.REVERSE);
        mOuterRingBoundAnim.addUpdateListener(this);

        // 中间这个是圆弧和黄色圆之间的填充颜色
        // 这里取了一个巧，就是填充的颜色实际上也是一个圆环，但是它的位置是固定的，只是stroke在改变大小
        // 同时这一层的圆弧画在最底层，这样即使他一直在动画，不会影响黄色那部分
        resetRectSize(mInnerRingRect, dp44, 0);
        mInnerRingStrokeAnim = ObjectAnimator.ofFloat(this, "innerRingStrokeWidth",
                dp2Px(mContext, 16), dp2Px(mContext, 34)).setDuration(1400);
        mInnerRingStrokeAnim.setRepeatCount(ValueAnimator.INFINITE);
        mInnerRingStrokeAnim.setRepeatMode(ValueAnimator.REVERSE);
        mInnerRingColorAnim = ObjectAnimator.ofInt(this, "innerRingColor", mColWhite, mColPressWhite);
        mInnerRingColorAnim.setEvaluator(ArgbEvaluator.getInstance());

        // 内圈的黄色的圆
        mInnerShapeAlpha = 255;
        mInnerShapeColor = mColNormYellow;
        mInnerShapeColorAnim = ObjectAnimator.ofInt(this, "innerShapeColor", mColNormYellow, mColPressYellow).setDuration(240);
        mInnerShapeColorAnim.setEvaluator(ArgbEvaluator.getInstance());
        mInnerShapeBoundAnim = ObjectAnimator.ofFloat(this, "innerShapeRectSize", dp44, dp2Px(mContext, 44)).setDuration(360);

        if (mNormalToPressSet == null) {
            mNormalToPressSet = new AnimatorSet();
            mNormalToPressSet.playTogether(mOuterRingBoundAnim, mInnerRingStrokeAnim,
                    mInnerRingColorAnim, mInnerShapeColorAnim, mInnerShapeBoundAnim);
        }
        mNormalToPressSet.start();
    }

    private void startRecordPressToNormalStatus() {
        currOuterRingRect = mOuterRingRect.width();
        endAllAnim();

        mCurViewStatus = STATUS_LONG_PRESS_TO_NORMAL;
        // 外圈的圆弧，从呼吸的状态回到正常的状态
        mOuterRingAlpha = 255;
        mOuterRingColor = mColNormGray;
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize",
                currOuterRingRect, dp60).setDuration(240);

        // 中间这个是圆弧和黄色圆之间的填充颜色
        resetRectSize(mInnerRingRect, dp44, 0);
        mInnerRingStrokeAnim = ObjectAnimator.ofFloat(this, "innerRingStrokeWidth",
                currOuterRingRect - dp44, dp2Px(mContext, 16)).setDuration(240);
        mInnerRingColorAnim = ObjectAnimator.ofInt(this, "innerRingColor", mColPressWhite, mColWhite);
        mInnerRingColorAnim.setEvaluator(ArgbEvaluator.getInstance());

        // 内圈的黄色的圆
        mInnerShapeAlpha = 255;
        mInnerShapeColorAnim = ObjectAnimator.ofInt(this, "innerShapeColor", mColPressYellow, mColNormYellow).setDuration(240);
        mInnerShapeColorAnim.setEvaluator(ArgbEvaluator.getInstance());
        mInnerShapeBoundAnim = ObjectAnimator.ofFloat(this, "innerShapeRectSize", dp2Px(mContext, 44), dp44).setDuration(240);

        mInnerShapeBoundAnim.addUpdateListener(this);

        if (mRecordPressToNormalSet == null) {
            mRecordPressToNormalSet = new AnimatorSet();
        }
        mRecordPressToNormalSet.playTogether(mOuterRingBoundAnim, mInnerRingStrokeAnim,
                mInnerRingColorAnim, mInnerShapeColorAnim, mInnerShapeBoundAnim);
        mRecordPressToNormalSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawRecordNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mRecordPressToNormalSet.start();
    }

    /**
     * 从普通状态扩散到点击状态：
     * 1.外环开始扩大，内环变为矩形
     * 2.1结束之后，开始呼吸的状态。
     */
    private void startRecordNormalToClickStatus() {
        endAllAnim();
        mCurViewStatus = STATUS_RECORD_CLICKED;

        // 外圈的圆弧，做呼吸的状态
//        mOuterRingAlpha = 255;
        mOuterRingColor = mColNormGray;
        mOuterRingStroke = dp2Px(mContext, 2);
        // 外圈的alpha变化
        mOuterRingAlphaAnim = ObjectAnimator.ofInt(this, "outerRingAlpha", 255, 127).setDuration(1400);
        mOuterRingAlphaAnim.setRepeatCount(ValueAnimator.INFINITE);
        mOuterRingAlphaAnim.setRepeatMode(ValueAnimator.REVERSE);
        // 外圈的大小变化
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", dp60, dp2Px(mContext, 78)).setDuration(1400);
        mOuterRingBoundAnim.setRepeatCount(ValueAnimator.INFINITE);
        mOuterRingBoundAnim.setRepeatMode(ValueAnimator.REVERSE);

        // 画内圈的圆角矩形
        mInnerShapeColor = mColNormYellow;
        // 圆角矩形alpha的变化
        mInnerShapeAlphaAnim = ObjectAnimator.ofInt(this, "innerShapeAlpha", 255, 127).setDuration(120);
        mInnerShapeAlphaAnim.setRepeatCount(1);
        mInnerShapeAlphaAnim.setRepeatMode(ValueAnimator.REVERSE);
        // 内圈圆角矩形的变化
        mInnerShapeBoundAnim = ObjectAnimator.ofFloat(this, "innerShapeRectSize", dp44, dp2Px(mContext, 29)).setDuration(360);
        mRoundRectRadiusAnim = ObjectAnimator.ofFloat(this, "innerRoundRectRadiusSize",
                dp2Px(mContext, 27), dp2Px(mContext, 4)).setDuration(360);

        // 因为外圈一直在循环，时间最长，我们在这个动画上加监听来刷新我们的界面
        mOuterRingBoundAnim.addUpdateListener(this);

        if (mNormalToClickSet == null) {
            mNormalToClickSet = new AnimatorSet();
        }
        mNormalToClickSet.playTogether(mOuterRingAlphaAnim, mInnerShapeAlphaAnim,
                mOuterRingBoundAnim, mInnerShapeBoundAnim, mRoundRectRadiusAnim);
        mNormalToClickSet.start();
    }

    /**
     * 从录视频的点击状态切到正常的状态
     */
    private void startRecordClickToNormalStatus() {
        currOuterRingRect = mOuterRingRect.width();
        endAllAnim();

        drawRecordNormalStatus();
        mCurViewStatus = STATUS_RECORD_CLICKED_TO_NORMAL;

        // 外圈的圆弧，做呼吸的状态
//        mOuterRingAlpha = 255;
        mOuterRingColor = mColNormGray;
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingAlpha = 255;
        // 外圈的大小变化
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", currOuterRingRect,
                dp60).setDuration(240);

        // 画内圈的圆角矩形
        mInnerShapeColor = mColNormYellow;
        mInnerShapeAlpha = 255;
        // 内圈圆角矩形的变化
        mInnerShapeBoundAnim = ObjectAnimator.ofFloat(this, "innerShapeRectSize", dp2Px(mContext, 29), dp44).setDuration(240);
        mRoundRectRadiusAnim = ObjectAnimator.ofFloat(this, "innerRoundRectRadiusSize",
                dp2Px(mContext, 4), dp2Px(mContext, 27)).setDuration(240);

        // 因为外圈一直在循环，时间最长，我们在这个动画上加监听来刷新我们的界面
        mOuterRingBoundAnim.addUpdateListener(this);

        if (mRecordClickToNormalSet == null) {
            mRecordClickToNormalSet = new AnimatorSet();
        }
        mRecordClickToNormalSet.playTogether(mOuterRingBoundAnim, mInnerShapeBoundAnim, mRoundRectRadiusAnim);
        mRecordClickToNormalSet.start();
        mRecordClickToNormalSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawRecordNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 从录视频切换到照相机的状态
     */
    private void startRecordToCamera() {
        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的录视频的状态
        drawRecordNormalStatus();

        mCurViewStatus = STATUS_RECORD_TO_CAMERA;
        // 这一块重置ring大小加一个动态的重置
        // 重置外圈ring的框的大小
        mOuterRingStroke = dp2Px(mContext, 2);
        resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);
        // 重置内圈ring的框的大小
        resetRectSize(mInnerRingRect, dp44, dp2Px(mContext, 1));

        // 外圈轮廓粗细 2pt → 3pt
        mOuterRingStrokeAnim = ObjectAnimator.ofFloat(this, "OuterRingStrokeWidth", dp2Px(mContext, 2), dp2Px(mContext, 3));
        // 内圈轮廓粗细 0pt→1pt
        mInnerRingStrokeAnim = ObjectAnimator.ofFloat(this, "InnerRingStrokeWidth", 0, dp2Px(mContext, 1) / 2f);
        // 最内圈黄色轮廓的变换
        mInnerShapeAlpha = 255;
        mInnerShapeStrokeAnim = ObjectAnimator.ofFloat(this, "thirdRingStrokeWidth", dp2Px(mContext, 22f), 0);
        mInnerShapeBoundAnim = ObjectAnimator.ofFloat(this, "thirdRingSize", dp2Px(mContext, 22), dp44);

        mInnerShapeBoundAnim.addUpdateListener(this);

        mRecordToCameraSet = new AnimatorSet();
        mRecordToCameraSet.playTogether(mOuterRingStrokeAnim, mInnerRingStrokeAnim, mInnerShapeStrokeAnim, mInnerShapeBoundAnim);
        mRecordToCameraSet.setDuration(360);
        mRecordToCameraSet.start();
        mRecordToCameraSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawCameraNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 相机到视频的状态
     */
    private void startCameraToRecord() {
        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的录视频的状态
        drawCameraNormalStatus();

        mCurViewStatus = STATUS_CAMERA_TO_RECORD;

        // 重置外圈ring的框的大小
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingColor = mColNormGray;
        mOuterRingAlpha = 255;
        resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);
        // 外圈轮廓粗细 3pt → 2pt
        mOuterRingStrokeAnim = ObjectAnimator.ofFloat(this,
                "OuterRingStrokeWidth", dp2Px(mContext, 3), dp2Px(mContext, 2)).setDuration(360);
        mOuterRingStrokeAnim.addUpdateListener(this);

        // 重置内圈ring的框的大小
        mInnerRingColor = mColNormGray;
        mInnerRingAlpha = 255;
        resetRectSize(mInnerRingRect, dp44, dp2Px(mContext, 1));
        // 内圈轮廓粗细 1pt→0pt
        mInnerRingStrokeAnim = ObjectAnimator.ofFloat(this,
                "InnerRingStrokeWidth", dp2Px(mContext, 1), 0).setDuration(40);

        // 最内圈的圆
        mInnerShapeColor = mColNormYellow;
        resetRectSize(mInnerShapeRect, mNormInnWidth - dp2Px(mContext, 1), 0);
        mInnerShapeAlphaAnim = ObjectAnimator.ofInt(this, "innerShapeAlpha", 0, 255).setDuration(360);

        if (mCameraToRecordSet == null) {
            mCameraToRecordSet = new AnimatorSet();
            mCameraToRecordSet.play(mOuterRingStrokeAnim).with(mInnerRingStrokeAnim).with(mInnerShapeAlphaAnim);
        }
        mCameraToRecordSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawRecordNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCameraToRecordSet.start();
    }

    /**
     * 录视频 到 直播 的动效
     */
    private void startRecordToLive() {
        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的录视频的状态
        drawRecordNormalStatus();

        mCurViewStatus = STATUS_RECORD_TO_LIVE;

        mOuterRingColor = mColNormGray;
        mOuterRingAlpha = 255;
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", dp60, 0).setDuration(100);

        // 画内层的圆角矩形
        mRoundRectRadiusAnim = ObjectAnimator.ofFloat(this, "innerRoundRectRadiusSize",
                dp2Px(mContext, 27), dp2Px(mContext, 20)).setDuration(440);
        mInnerShapeWidthAnim = ObjectAnimator.ofFloat(this, "innerShapeRectWidth",
                dp44, dp2Px(mContext, 188)).setDuration(440);
        mInnerShapeHeightAnim = ObjectAnimator.ofFloat(this, "innerShapeRectHeight",
                dp44, dp2Px(mContext, 40)).setDuration(440);
        mInnerShapeColor = mColNormYellow;
        mInnerShapeColorAnim = ObjectAnimator.ofInt(this, "innerShapeColor", mColNormYellow, mColWhite).setDuration(360);
        mInnerShapeColorAnim.setEvaluator(ArgbEvaluator.getInstance());

        // 画文字
        mTextPaintAlpha = 0;
        // 设置了 textPaint 的 alignCenter，这里直接取x为view的宽的一半就可以居中了
        mTextX = mWidth / 2;
        mTextY = mHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        mTextAlphaAnim = ObjectAnimator.ofInt(this, "textPaintAlpha", 0, 255).setDuration(440);
        mTextAlphaAnim.addUpdateListener(this);

        if (mRecordToLiveSet == null) {
            mRecordToLiveSet = new AnimatorSet();
            mRecordToLiveSet.playTogether(mOuterRingBoundAnim, mRoundRectRadiusAnim,
                    mInnerShapeWidthAnim, mInnerShapeHeightAnim, mInnerShapeColorAnim, mTextAlphaAnim);
        }
        mRecordToLiveSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawLiveNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mRecordToLiveSet.start();
    }

    /**
     * 直播 到 录视频 的动效
     */
    private void startLiveToRecord() {

        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的直播的状态
        drawLiveNormalStatus();

        mCurViewStatus = STATUS_LIVE_TO_RECORD;
        // 画外最圈的圆弧
        mOuterRingColor = mColNormGray;
        mOuterRingAlphaAnim = ObjectAnimator.ofInt(this, "outerRingAlpha", 0, 255).setDuration(360);
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", dp2Px(mContext, 3), dp60).setDuration(80);

        // 画第二个圆弧（也就是圆）
        mInnerRingStroke = dp2Px(mContext, 22);
        resetRectSize(mInnerRingRect, dp2Px(mContext, 22), dp2Px(mContext, 22), 0);
        mInnerRingColor = mColNormYellow;
        mInnerRingAlpha = 255;
        mInnerRingColorAnim = ObjectAnimator.ofInt(this, "innerRingColor", mColWhite, mColNormYellow).setDuration(300);
        mInnerRingColorAnim.setEvaluator(ArgbEvaluator.getInstance());
        mInnerRingAlphaAnim = ObjectAnimator.ofInt(this, "innerRingAlpha", 0, 255).setDuration(360);
        mInnerRingAlphaAnim.addUpdateListener(this);

        // 画内层的圆角矩形
        mInnerShapeColor = mColWhite;
        mRoundedRectRx = dp2Px(mContext, 20);
        mRoundedRectRy = dp2Px(mContext, 20);
        resetRectSize(mInnerShapeRect, dp2Px(mContext, 188), dp2Px(mContext, 40), 0);
        mInnerShapeAlphaAnim = ObjectAnimator.ofInt(this, "innerShapeAlpha", 255, 0).setDuration(400);

        // 画文字
        mTextPaintAlpha = 255;
        // 设置了 textPaint 的 alignCenter，这里直接取x为view的宽的一半就可以居中了
        mTextX = mWidth / 2;
        mTextY = mHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        mTextAlphaAnim = ObjectAnimator.ofInt(this, "textPaintAlpha", 255, 0).setDuration(200);

        if (mLiveToRecordSet == null) {
            mLiveToRecordSet = new AnimatorSet();
            mLiveToRecordSet.playTogether(mOuterRingBoundAnim, mOuterRingAlphaAnim, mInnerRingColorAnim, mTextAlphaAnim,
                    mInnerRingAlphaAnim, mInnerShapeAlphaAnim);
        }
        mLiveToRecordSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawRecordNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLiveToRecordSet.start();
    }

    /**
     * 相机到直播
     */
    private void startCameraToLive() {
        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的录视频的状态
        drawCameraNormalStatus();

        mCurViewStatus = STATUS_CAMERA_TO_LIVE;

        mOuterRingColor = mColNormGray;
        mOuterRingAlpha = 255;
        mOuterRingStroke = dp2Px(mContext, 2);
        mOuterRingBoundAnim = ObjectAnimator.ofFloat(this, "outerRingRectSize", dp60, 0).setDuration(100);

        // 画内层的圆角矩形
        mInnerShapeColor = mColWhite;
        mRoundRectRadiusAnim = ObjectAnimator.ofFloat(this, "innerRoundRectRadiusSize",
                dp2Px(mContext, 27), dp2Px(mContext, 20)).setDuration(440);
        mInnerShapeWidthAnim = ObjectAnimator.ofFloat(this, "innerShapeRectWidth",
                dp44, dp2Px(mContext, 188)).setDuration(440);
        mInnerShapeHeightAnim = ObjectAnimator.ofFloat(this, "innerShapeRectHeight",
                dp44, dp2Px(mContext, 40)).setDuration(440);

        // 画文字
        mTextPaintAlpha = 0;
        // 设置了 textPaint 的 alignCenter，这里直接取x为view的宽的一半就可以居中了
        mTextX = mWidth / 2;
        mTextY = mHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        mTextAlphaAnim = ObjectAnimator.ofInt(this, "textPaintAlpha", 0, 255).setDuration(440);
        mTextAlphaAnim.addUpdateListener(this);

        if (mCameraToLiveSet == null) {
            mCameraToLiveSet = new AnimatorSet();
            mCameraToLiveSet.playTogether(mOuterRingBoundAnim, mRoundRectRadiusAnim,
                    mInnerShapeWidthAnim, mInnerShapeHeightAnim, mTextAlphaAnim);
        }
        mCameraToLiveSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawLiveNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mCameraToLiveSet.start();
    }

    /**
     * 直播 到 照相
     */
    private void startLiveToCamera() {
        // 取消所有动画
        endAllAnim();
        // 第一帧先绘制普通的直播的状态
        drawLiveNormalStatus();

        mCurViewStatus = STATUS_LIVE_TO_CAMERA;
        // 画外最圈的圆弧
        mOuterRingColor = mColNormGray;
        mOuterRingAlphaAnim = ObjectAnimator.ofInt(this, "outerRingAlpha", 0, 255).setDuration(360);
        mOuterRingStroke = dp2Px(mContext, 3);
        resetRectSize(mOuterRingRect, dp60, dp2Px(mContext, 3));

        // 画第二个圆弧
        mInnerRingStroke = dp2Px(mContext, 1);
        resetRectSize(mInnerRingRect, dp44, mInnerRingStroke);
        mInnerRingColor = mColNormGray;
        mInnerRingAlphaAnim = ObjectAnimator.ofInt(this, "innerRingAlpha", 0, 255).setDuration(360);

        // 画内层的圆角矩形
        mInnerShapeColor = mColWhite;
        mRoundedRectRx = dp2Px(mContext, 20);
        mRoundedRectRy = dp2Px(mContext, 20);
        resetRectSize(mInnerShapeRect, dp2Px(mContext, 188), dp2Px(mContext, 40), 0);
        mInnerShapeAlphaAnim = ObjectAnimator.ofInt(this, "innerShapeAlpha", 255, 0).setDuration(360);

        // 画文字
        mTextPaintAlpha = 255;
        // 设置了 textPaint 的 alignCenter，这里直接取x为view的宽的一半就可以居中了
        mTextX = mWidth / 2;
        mTextY = mHeight / 2 - ((mTextPaint.descent() + mTextPaint.ascent()) / 2);
        mTextAlphaAnim = ObjectAnimator.ofInt(this, "textPaintAlpha", 255, 0).setDuration(400);
        mTextAlphaAnim.addUpdateListener(this);

        if (mLiveToCameraSet == null) {
            mLiveToCameraSet = new AnimatorSet();
            mLiveToCameraSet.playTogether(mOuterRingAlphaAnim, mInnerRingAlphaAnim, mTextAlphaAnim, mInnerShapeAlphaAnim);
        }
        mLiveToCameraSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                drawCameraNormalStatus();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mLiveToCameraSet.start();
    }

    /**
     * 相机按钮点击
     */
    private void startCameraClicked() {
        endAllAnim();
        mCurViewStatus = STATUS_CAMERA_CLICKED;

        mOuterRingAlpha = 77;
        mOuterRingColor = mColNormGray;
        mOuterRingStroke = dp2Px(mContext, 3);
        resetRectSize(mOuterRingRect, mNormOutWidth, mOuterRingStroke);

        mInnerRingStroke = dp2Px(mContext, 1);
        mInnerRingAlpha = 77;
        mInnerRingColor = mColNormGray;
        resetRectSize(mInnerRingRect, dp44, dp2Px(mContext, 1));
        // 外圈透明度变化
        mOuterRingAlphaAnim = ObjectAnimator.ofInt(this, "outerRingAlpha", 77, 77, 255, 128, 0).setDuration(400);
        // 内圈透明度变化
        mInnerRingAlphaAnim = ObjectAnimator.ofInt(this, "innerRingAlpha", 77, 77, 255, 128, 0).setDuration(400);
        mInnerRingAlphaAnim.addUpdateListener(this);

        if (mCameraClickedSet == null) {
            mCameraClickedSet = new AnimatorSet();
        }

        mCameraClickedSet.playTogether(mOuterRingAlphaAnim, mInnerRingAlphaAnim);
        mCameraClickedSet.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        invalidate();
    }

    public void setInnerRoundRectRadiusSize(float size) {
        mRoundedRectRx = size;
        mRoundedRectRy = size;
    }

    public void setOuterRingRectSize(float outerSize) {
        // outer的rect居中于控件
        resetRectSize(mOuterRingRect, outerSize, mOuterRingStroke);
    }

    public void setInnerShapeRectSize(float innerSize) {
        // outer的rect居中于控件
        resetRectSize(mInnerShapeRect, innerSize, 0);
    }

    public void setInnerShapeRectWidth(float width) {
        resetRectSize(mInnerShapeRect, width, mInnerShapeRect.height(), 0);
    }

    public void setInnerShapeRectHeight(float height) {
        resetRectSize(mInnerShapeRect, mInnerShapeRect.width(), height, 0);
    }

    public void setInnerShapeColor(int color) {
        mInnerShapeColor = color;
    }

    public void setOuterRingStrokeWidth(float outerRingStroke) {
        mOuterRingStroke = outerRingStroke;
    }

    public void setOuterRingAlpha(int outerRingAlpha) {
        mOuterRingAlpha = outerRingAlpha;
    }

    public void setInnerRingStrokeWidth(float innerRingStroke) {
        mInnerRingStroke = innerRingStroke;
    }

    public void setInnerRingColor(int color) {
        mInnerRingColor = color;
    }

    public void setInnerRingAlpha(int alpha) {
        mInnerRingAlpha = alpha;
    }

    public void setThirdRingStrokeWidth(float thirdRingStrokeWidth) {
        mThirdRingStroke = thirdRingStrokeWidth;
    }

    public void setThirdRingSize(float thirdRingSize) {
        resetRectSize(mInnerShapeRect, thirdRingSize, 0);
    }

    public void setInnerShapeAlpha(int alpha) {
        mInnerShapeAlpha = alpha;
    }

    public void setTextPaintAlpha(int alpha) {
        mTextPaintAlpha = alpha;
    }

    private void endAllAnim() {
        if (mNormalToPressSet != null && mNormalToPressSet.isRunning()) {
            mNormalToPressSet.removeAllListeners();
            mNormalToPressSet.end();
        }
        if (mNormalToClickSet != null && mNormalToClickSet.isRunning()) {
            mNormalToClickSet.removeAllListeners();
            mNormalToClickSet.end();
        }
        if (mRecordToCameraSet != null && mRecordToCameraSet.isRunning()) {
            mRecordToCameraSet.removeAllListeners();
            mRecordToCameraSet.end();
        }
        if (mCameraToRecordSet != null && mCameraToRecordSet.isRunning()) {
            mCameraToRecordSet.removeAllListeners();
            mCameraToRecordSet.end();
        }
        if (mRecordToLiveSet != null && mRecordToLiveSet.isRunning()) {
            mRecordToLiveSet.removeAllListeners();
            mRecordToLiveSet.end();
        }
        if (mLiveToRecordSet != null && mLiveToRecordSet.isRunning()) {
            mLiveToRecordSet.removeAllListeners();
            mLiveToRecordSet.end();
        }
        if (mCameraToLiveSet != null && mCameraToLiveSet.isRunning()) {
            mCameraToLiveSet.removeAllListeners();
            mCameraToLiveSet.end();
        }
        if (mLiveToCameraSet != null && mLiveToCameraSet.isRunning()) {
            mLiveToCameraSet.removeAllListeners();
            mLiveToCameraSet.end();
        }
        if (mRecordPressToNormalSet != null && mRecordPressToNormalSet.isRunning()) {
            mRecordPressToNormalSet.removeAllListeners();
            mRecordPressToNormalSet.end();
        }
        if (mRecordClickToNormalSet != null && mRecordClickToNormalSet.isRunning()) {
            mRecordClickToNormalSet.removeAllListeners();
            mRecordClickToNormalSet.end();
        }

        if (mCameraClickedSet != null && mCameraClickedSet.isRunning()) {
            mCameraClickedSet.removeAllListeners();
            mCameraClickedSet.end();
        }

    }

    /**
     * 根据宽高重新计算rect的位置
     */
    private void resetRectSize(RectF rectF, float newSize, float strokeSize) {
        rectF.set((mWidth - newSize + strokeSize) / 2f, (mHeight - newSize + strokeSize) / 2f,
                (mWidth + newSize - strokeSize) / 2f, (mHeight + newSize - strokeSize) / 2f);
    }

    // =============================================================================================
    //  点击 长按事件 相关回调方法
    // =============================================================================================

    private void resetRectSize(RectF rectF, float newWidth, float newHeight, float strokeSize) {
        rectF.set((mWidth - newWidth + strokeSize) / 2f, (mHeight - newHeight + strokeSize) / 2f,
                (mWidth + newWidth - strokeSize) / 2f, (mHeight + newHeight - strokeSize) / 2f);
    }

    public void setOnCustomClickListener(OnCustomClickListener listener) {
        this.mClickListener = listener;
    }

    public void setOnCustomLongPressListener(OnCustomLongPressListener listener) {
        this.mLongPressListener = listener;
    }

    public boolean isClickedStatus() {
        return mCurViewStatus == STATUS_RECORD_CLICKED;
    }

    public boolean isLongPressStatus() {
        return mCurViewStatus == STATUS_RECORD_LONG_PRESSED;
    }

    public interface OnCustomLongPressListener {
        // 开始长按
        void longPressDown();

        // 结束长按
        void longPressUp();
    }

    public interface OnCustomClickListener {
        void onClick();
    }
}

