package com.biotech.drawlessons.photoedit.layers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;

import com.biotech.drawlessons.UtilsKt;
import com.biotech.drawlessons.photoedit.evaluator.DoubleRectF;
import com.biotech.drawlessons.photoedit.evaluator.MatrixInfo;
import com.biotech.drawlessons.photoedit.tools.DimensionManager;
import com.biotech.drawlessons.photoedit.utils.AnimFinishCallback;
import com.biotech.drawlessons.photoedit.utils.AnimationHelper;
import com.biotech.drawlessons.photoedit.utils.DrawInvoker;
import com.biotech.drawlessons.photoedit.utils.IPhotoEditType;
import com.biotech.drawlessons.photoedit.utils.RotatableRectF;


/**
 * Created by xintu on 2018/1/31.
 */

public class CropLayer extends BaseLayer {
    private static final int POSITION_BITMAP_TOUCH = -1;
    private static final int MULTIPLE_TOUCH = -2;
    private static final float MAX_DRAWABLE_SCALE = 15.0f;
    private static final int FINAL_MASK_ALPHA = 210;
    private static final int DISMISS_MASK = 0;
    private static final int SHOW_MASK = 1;
    private static final int START_ZOOM_ANIM = 2;
    private static final int START_MASK_ALPHA_ANIM = 3;
    private DrawInvoker mInvoker;
    // 屏幕上的裁剪框的rect
    private RectF mCropperRect;
    private RotatableRectF mRotateRect;
    // 裁剪框四个边角的 path
    private Path mCornerPath;
    private Path mCropperDividerPath;
    // 外的黑色区域
    private Path mMaskPath;
    private int mCropperLineStroke, mCornerLength, mCornerStroke, mCropDividerStroke, mCropperStroke, mMidCornerLength;
    private int mTouchPosition;
    private int mMaskAlpha = FINAL_MASK_ALPHA;
    private boolean mShowCropper;
    private boolean mShowMask;
    private boolean mIsClipMode;
    private Paint mPaint;
    private boolean mTouchPointerDown;
    private boolean mCropperRectChange;
    private CropHandler mHandler;
    private RectF mToCropRect, mToDrawableRect;
    private boolean mAnimating;
    private float mRotateDegree;
    private boolean mRotating;
    private MatrixInfo mDoneMatrixInfo, mInitCropMatrixInfo, mInitDrawableMatrixInfo;
    private RectF mClipRectOnBitmap;
    private RectF mClipRect;
    private boolean mCanBeRestore;
    /**
     * 把当前 drawable 移动到目标 rect 的动画方法
     */
    private DimensionManager mDimensionManager;
    private AnimationHelper mAnimationHelper;
    private OnCropStateChangeListener mCropStateChangeListener;

    public CropLayer(ILayerParent parent, DrawInvoker invoker, Matrix matrix, DimensionManager dimensionManager) {
        super(parent, IPhotoEditType.LAYER_CROP, matrix);
        mInvoker = invoker;
        mDimensionManager = dimensionManager;
        mToCropRect = new RectF();
        mToDrawableRect = new RectF();

        init();
    }

    private void init() {
        mCropperRect = new RectF();
        mClipRectOnBitmap = new RectF();
        mClipRect = new RectF();

        mCornerPath = new Path();
        mMaskPath = new Path();
        mMaskPath.setFillType(Path.FillType.EVEN_ODD);

        mCropperDividerPath = new Path();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLUE);
        mPaint.setAlpha(50);
        mPaint.setStyle(Paint.Style.STROKE);

        initStrokeWidth();
        mHandler = new CropHandler(this);
        mAnimationHelper = new AnimationHelper();
        setInitCropMatrixInfo();
    }

    private void initStrokeWidth() {
        mCropperLineStroke = (int) UtilsKt.dp2px(2);
        mCornerStroke = (int) UtilsKt.dp2px(5f);
        mCornerLength = (int) UtilsKt.dp2px(25);
        mCropDividerStroke = (int) UtilsKt.dp2px(1f);
        mCropperStroke = (int) UtilsKt.dp2px(2f);
        mMidCornerLength = (int) UtilsKt.dp2px(30);
    }

    private void setInitCropMatrixInfo() {
        RectF initBitmapRect = mDimensionManager.getInitBitmapRect();
        RectF cropRect = new RectF(initBitmapRect);
        Matrix matrix = getCropToMaxCropMatrix(cropRect);
        matrix.mapRect(cropRect, cropRect);
        float scaleX = cropRect.width() / initBitmapRect.width();
        float scaleY = cropRect.height() / initBitmapRect.height();
        float scale = scaleX < scaleY ? scaleX : scaleY;

        mInitCropMatrixInfo = new MatrixInfo(cropRect.centerX(),
                cropRect.centerY(),
                scale,
                0);

        mInitDrawableMatrixInfo = mDoneMatrixInfo = getCurDrawableMatrixInfo();
    }

    private MatrixInfo getCurDrawableMatrixInfo() {
        RectF drawableRect = mDimensionManager.getCurDrawableRect();
        return new MatrixInfo(drawableRect.centerX(),
                drawableRect.centerY(),
                mDimensionManager.getCurScale(),
                mRotateDegree);
    }

    private void setClipMode() {
        mIsClipMode = true;
        mShowMask = true;
        mShowCropper = false;
    }

    private void setMoveBitmapMode() {
        mIsClipMode = false;
        mShowMask = false;
        mShowCropper = true;
    }

    private void setCropIdlMode() {
        mIsClipMode = false;
        mShowMask = true;
        mShowCropper = true;
    }

    private void setCropRotateMode() {
        mIsClipMode = false;
        mShowMask = true;
        mShowCropper = false;
    }

    public void setCurRotateAngle(float angle) {
        mRotateDegree = angle;
    }

    public void setOnCropStateChangeListener(OnCropStateChangeListener listener) {
        mCropStateChangeListener = listener;
    }

    public void startCrop(MatrixInfo fromDrawableMatrixInfo, RectF cropRectOnBitmap) {
        if (mAnimating) {
            return;
        }
        removeMessages();
        // 开始裁剪的时候，遮罩的透明度应该是最终的透明度
        mMaskAlpha = FINAL_MASK_ALPHA;
        if (fromDrawableMatrixInfo != null) {
            // 进入裁剪时，有信息，且信息和初始的信息不同
            if (fromDrawableMatrixInfo.equals(mInitDrawableMatrixInfo)) {
                // 这个时候应该是不可以restore的
                resetCanBeRestore(false, true);
            } else {
                resetCanBeRestore(true, true);
            }
        } else {
            fromDrawableMatrixInfo = getCurDrawableMatrixInfo();
            resetCanBeRestore(false);
        }

        RectF cropRectOnScreen = new RectF(cropRectOnBitmap);
        // 因为进入裁剪的时候，传进来的cropRect是bitmap的cropRect，所以第一步需要个根据cropRectOnBitmap 得到
        // cropRectOnScreen
        mDimensionManager.mapRectFromMatrix(cropRectOnScreen, cropRectOnBitmap);
        // 获取从 cropRectOnScreen 到 MaxCrop 变换矩阵
        Matrix tempMatrix = getCropToMaxCropMatrix(cropRectOnScreen);
        // 根据变换矩阵获取最终的 rect
        tempMatrix.mapRect(mToCropRect, cropRectOnScreen);
        // 把该变换矩阵关联到当前的矩阵，在根据该矩阵得到 toDrawableRect
        tempMatrix.preConcat(mDimensionManager.getMatrix());
        tempMatrix.mapRect(mToDrawableRect, mDimensionManager.getInitBitmapRect());

        // 把 toCropRect 赋值给 mCropperRect
        mCropperRect.set(mToCropRect);
        mCropperRectChange = true;
        setClipMode();
        resetMaskPath(cropRectOnBitmap);

        MatrixInfo toDrawableMatrixInfo = new MatrixInfo(mToDrawableRect.centerX(),
                mToDrawableRect.centerY(),
                DimensionManager.getScaleFromMatrix(tempMatrix), mRotateDegree);

        startFromSrcDrawableToDstDrawableAnim(fromDrawableMatrixInfo, toDrawableMatrixInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mShowCropper = true;
                mShowMask = true;
                mAnimating = false;
                mIsClipMode = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = false;
            }
        });
    }

    public void doneCrop(final AnimFinishCallback<MatrixInfo> adapter) {
        if (mAnimating) {
            return;
        }
        setClipMode();
        removeMessages();
        setClipMode();

        MatrixInfo fromMatrixInfo = getCurDrawableMatrixInfo();
        // 获取从当前 crop 变动到最终 crop 的矩阵变换
        Matrix tempMatrix = getCurCropToDoneCropMatrix();
        // 前乘上当前的 matrix，得到的matrix可以把initBitmapRect 转换为 cropDoneDrawableRect
        tempMatrix.preConcat(mDimensionManager.getMatrix());
        RectF cropDoneDrawableRect = new RectF();
        tempMatrix.mapRect(cropDoneDrawableRect, mDimensionManager.getInitBitmapRect());

        updateClipRectOnBitmap();
        mDoneMatrixInfo = new MatrixInfo(cropDoneDrawableRect.centerX(),
                cropDoneDrawableRect.centerY(),
                DimensionManager.getScaleFromMatrix(tempMatrix),
                mRotateDegree);
        mClipRect.set(cropDoneDrawableRect);
        resetMaskPath();

        startFromSrcDrawableToDstDrawableAnim(fromMatrixInfo, mDoneMatrixInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
                if (adapter != null) {
                    adapter.onAnimFinish(mDoneMatrixInfo);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });
    }

    public void restoreCrop() {
        if (mAnimating) {
            return;
        }
        removeMessages();
//        setCropRotateMode();
        mIsClipMode = false;
        mShowCropper = false;
        mShowMask = false;
        MatrixInfo fromMatrixInfo = getCurDrawableMatrixInfo();
        if (mRotateDegree == 270) {
            // 这里为什么这么做，是因为如果旋转270度，看得头都晕了
            fromMatrixInfo.setRotateDegree(-90);
        }
        mRotateDegree = 0;

        startFromSrcDrawableToDstDrawableAnim(fromMatrixInfo, mInitCropMatrixInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCropperRect.set(mDimensionManager.getCurDrawableRect());
                mCropperRectChange = true;
                setCropIdlMode();
                resetMaskPath();
                mAnimating = false;

                resetCanBeRestore(false);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });
    }

    public void cancelCrop(MatrixInfo toMatrixInfo, RectF clipRectOnBitmap, final AnimFinishCallback<Object> finishCallback) {
        if (mAnimating) {
            return;
        }
        setClipMode();
        removeMessages();
        // 取消裁剪，需要根据传进来的clipRect来重置mask的状态
        resetMaskPath(clipRectOnBitmap);
        // 如果是取消裁剪，就按照上一次保存的裁剪信息来做动画。对于第一次裁剪没有裁剪信息的情况，在startCrop
        // 的时候，会赋值一个默认的裁剪信息。而在donCrop的时候，会返回一个当前的裁剪信息。
        MatrixInfo fromMatrixInfo = getCurDrawableMatrixInfo();
        if (mRotateDegree - toMatrixInfo.getRotateDegree() == 270) {
            fromMatrixInfo.setRotateDegree(mRotateDegree - 360);
        }
        mRotateDegree = mDoneMatrixInfo.getRotateDegree();
        startFromSrcDrawableToDstDrawableAnim(fromMatrixInfo, toMatrixInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (finishCallback != null) {
                    finishCallback.onAnimFinish(null);
                }
                mAnimating = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });
    }

    public void rotateLayer() {
        if (mAnimating) {
            return;
        }
        removeMessages();
        setCropRotateMode();

        // 对于rotateRect来说，当结束旋转的时候 getLine1Dis实际上相当于宽，getLine2Dis实际上相当于高
        // 旋转之后，但是计算ratio时机上是以旋转结束后为准的，旋转结束之后，宽和高是对调的，所以用
        // maxCrop的宽 比上 rotateRect 的高得到 ratioX，ratioY同理
        final RectF maxCropRect = mDimensionManager.getMaxCropRect();
        float ratioX = maxCropRect.width() / mCropperRect.height();
        float ratioY = maxCropRect.height() / mCropperRect.width();
        final float ratio = ratioX < ratioY ? ratioX : ratioY;

        MatrixInfo fromMatrixInfo = getCurDrawableMatrixInfo();
        fromMatrixInfo.setPivotX(mCropperRect.centerX());
        fromMatrixInfo.setPivotY(mCropperRect.centerY());
        MatrixInfo toMatrixInfo = new MatrixInfo(mCropperRect.centerX(), mCropperRect.centerY(),
                fromMatrixInfo.getScale() * ratio, mRotateDegree + 90);
        mRotateDegree += 90;
        mRotateDegree %= 360;

        startFromSrcDrawableToDstDrawableAnim(fromMatrixInfo, toMatrixInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                setCropIdlMode();
                mAnimating = false;
                // 为什么在这里才从新计算crop呢，因为mask的的位置和cropRect是关联的，如果在动画前就设置了
                // cropRect，会导致mask的位置出错
                Matrix rotateMatrix = new Matrix();
                rotateMatrix.postRotate(90, mCropperRect.centerX(), mCropperRect.centerY());
                rotateMatrix.postScale(ratio, ratio, mCropperRect.centerX(), mCropperRect.centerY());
                rotateMatrix.mapRect(mCropperRect, mCropperRect);
                mCropperRectChange = true;
                resetCanBeRestore(true);
                resetMaskPath();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });

    }

    private void startFromSrcDrawableToDstDrawableAnim(MatrixInfo fromMatrixInfo,
                                                       MatrixInfo toMatrixInfo,
                                                       AnimatorListenerAdapter adapter) {
        final MatrixInfo tempInfo = new MatrixInfo(fromMatrixInfo);
        mAnimationHelper.startMatrixInfoAnim(fromMatrixInfo, toMatrixInfo,
                adapter, new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        MatrixInfo info = (MatrixInfo) animation.getAnimatedValue();

                        float tx = info.getPivotX() - tempInfo.getPivotX();
                        float ty = info.getPivotY() - tempInfo.getPivotY();
                        float rotation = info.getRotateDegree() - tempInfo.getRotateDegree();
                        float scale = info.getScale() / tempInfo.getScale();
                        mDimensionManager.postMatrixTranslate(tx, ty);
                        mDimensionManager.postRotate(rotation, info.getPivotX(), info.getPivotY());
                        mDimensionManager.postMatrixScale(scale, scale, info.getPivotX(), info.getPivotY());

                        tempInfo.set(info);
                        requestInvalidate();
                    }
                });
    }

    public RectF getClipRectOnBitmap() {
        return mClipRectOnBitmap;
    }

    public MatrixInfo getDoneMatrixInfo() {
        return mDoneMatrixInfo;
    }

    private void startCurDrawRectToDestRectAnim(RectF toRect, final AnimFinishCallback<Object> callback) {
        if (mAnimating) {
            return;
        }
        mAnimating = true;

        MatrixInfo fromInfo = getCurDrawableMatrixInfo();
        float scaleX;
        float scaleY;
        if (mRotateDegree == 90 || mRotateDegree == 270) {
            scaleX = toRect.width() / mDimensionManager.getInitBitmapRect().height();
            scaleY = toRect.height() / mDimensionManager.getInitBitmapRect().width();
        } else {
            scaleX = toRect.width() / mDimensionManager.getInitBitmapRect().width();
            scaleY = toRect.height() / mDimensionManager.getInitBitmapRect().height();
        }
        float scale = scaleX > scaleY ? scaleX : scaleY;
        MatrixInfo toInfo = new MatrixInfo(toRect.centerX(), toRect.centerY(), scale, mRotateDegree);

        startFromSrcDrawableToDstDrawableAnim(fromInfo, toInfo, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (callback != null) {
                    callback.onAnimFinish(null);
                }
                mAnimating = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mAnimating = true;
            }
        });
    }

    private void startZoomCropperAnim(RectF toCropRect, RectF toDrawableRect, AnimatorListenerAdapter adapter) {
        if (mAnimating) return;
        mAnimating = true;
        resetMaskPath();
        RectF startCropperRect = new RectF(mCropperRect);
        RectF startDrawableRect = new RectF(mDimensionManager.getCurDrawableRect());
        final RectF lastDrawableRect = new RectF(mDimensionManager.getCurDrawableRect());

        final boolean isCropChange = !startCropperRect.equals(toCropRect);

        mAnimationHelper.startDoubleRectAnim(new DoubleRectF(startCropperRect, startDrawableRect), new DoubleRectF(toCropRect, toDrawableRect), adapter, new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (animation.getAnimatedFraction() == 1f) {
                    mAnimating = false;
                    resetMaskPath();
                }
                DoubleRectF doubleRectF = (DoubleRectF) animation.getAnimatedValue();
                if (isCropChange) {
                    mCropperRectChange = true;
                    mCropperRect.set(doubleRectF.getFirstRect());
                }

                mDimensionManager.postMatrixTranslate(doubleRectF.getSecondRect().centerX() - lastDrawableRect.centerX(),
                        doubleRectF.getSecondRect().centerY() - lastDrawableRect.centerY());
                mDimensionManager.postMatrixScale(doubleRectF.getSecondRect().width() / lastDrawableRect.width(),
                        doubleRectF.getSecondRect().height() / lastDrawableRect.height(),
                        doubleRectF.getSecondRect().centerX(), doubleRectF.getSecondRect().centerY());
                lastDrawableRect.set(doubleRectF.getSecondRect());
                if (mShowMask) {
                    mMaskAlpha = (int) (FINAL_MASK_ALPHA * animation.getAnimatedFraction());
                }
                requestInvalidate();
            }
        });
    }

    public void setMaskAlpha(int alpha) {
        mMaskAlpha = alpha;
        requestInvalidate();
    }

    private void startMaskAlphaAnim() {
        if (mAnimating) {
            return;
        }
        mAnimating = true;
        ObjectAnimator animator = ObjectAnimator.ofInt(this, "maskAlpha", 0, FINAL_MASK_ALPHA);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimating = false;
            }
        });
        animator.setDuration(200).start();
    }

    /**
     * 获取开始裁剪的时候，Drawable 的具体位置
     */
    private RectF getStartDrawableRect() {
        RectF maxCropRect = mDimensionManager.getMaxCropRect();
        float ratioX = maxCropRect.width() / mDimensionManager.getCurDrawableRect().width();
        float ratioY = maxCropRect.height() / mDimensionManager.getCurDrawableRect().height();
        float ratio = ratioX < ratioY ? ratioX : ratioY;
        RectF dst = new RectF();

        Matrix matrix = new Matrix();
        matrix.setTranslate((maxCropRect.centerX() - mDimensionManager.getCurDrawableRect().centerX()),
                maxCropRect.centerY() - mDimensionManager.getCurDrawableRect().centerY());
        matrix.postScale(ratio, ratio, mDimensionManager.getInitViewRect().width() / 2, maxCropRect.centerY());

        matrix.mapRect(dst, mDimensionManager.getCurDrawableRect());

        return dst;
    }

    /**
     * 获取裁剪完成时，drawable 的具体位置
     */
    private RectF getEndDrawableRect() {
        float ratioX = mDimensionManager.getInitViewRect().width() / mCropperRect.width();
        float ratioY = mDimensionManager.getInitViewRect().height() / mCropperRect.height();
        float ratio = ratioX < ratioY ? ratioX : ratioY;
        RectF dst = new RectF();
        Matrix matrix = new Matrix();
        matrix.setTranslate(mDimensionManager.getInitViewRect().width() / 2 - mCropperRect.centerX(), mDimensionManager.getInitViewRect().height() / 2 - mCropperRect.centerY());
        matrix.postScale(ratio, ratio, mDimensionManager.getInitViewRect().width() / 2, mDimensionManager.getInitViewRect().height() / 2);

        matrix.mapRect(dst, mDimensionManager.getCurDrawableRect());
        return dst;
    }

    private void removeMessages() {
        mHandler.removeMessages(START_ZOOM_ANIM);
        mHandler.removeMessages(START_MASK_ALPHA_ANIM);
    }

    private void resetCanBeRestore(boolean canBeRestore) {
        resetCanBeRestore(canBeRestore, false);
    }

    private void resetCanBeRestore(boolean canBeRestore, boolean forceNotify) {
        if ((mCanBeRestore == canBeRestore) && !forceNotify) {
            return;
        }

        if (mCropStateChangeListener != null) {
            mCropStateChangeListener.onCropStateChange(canBeRestore);
        }
        mCanBeRestore = canBeRestore;
    }

    @Override
    public boolean onTouchDown(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        mTouchPosition = getTouchCropperPosition(curXOnScreen, curYOnScreen);
        removeMessages();
        mHandler.sendEmptyMessage(DISMISS_MASK);
        return true;
    }

    @Override
    public boolean onTouchMove(float curXOnScreen, float curYOnScreen, float lastXOnScreen, float lastYOnScreen,
                               float curXOnBitmap, float curYOnBitmap, float lastXOnBitmap, float lastYOnBitmap) {
        // 说明 touch 在 cropper 的四个角或者四条边
        if (isTouchCropper(mTouchPosition)) {
            mCropperRectChange = updateCropperRect(curXOnScreen - lastXOnScreen, curYOnScreen - lastYOnScreen);
            resetCanBeRestore(true);
            return mCropperRectChange;
        }
        // 说明 touch 的是图片，这个时候应该可以移动图片
        else if (mTouchPosition == POSITION_BITMAP_TOUCH && !mTouchPointerDown) {
            float dx = curXOnScreen - lastXOnScreen, dy = curYOnScreen - lastYOnScreen;
            mDimensionManager.postMatrixTranslate(dx, dy);
            boolean changed = dx != 0 || dy != 0;
            if (changed) {
                resetCanBeRestore(true);
            }
            return changed;
        }
        return false;
    }

    @Override
    public boolean onTouchUp(float curXOnScreen, float curYOnScreen, float curXOnBitmap, float curYOnBitmap) {
        removeMessages();

        if (isTouchCropper(mTouchPosition)) {
            mHandler.sendEmptyMessageDelayed(START_ZOOM_ANIM, 1000);

        } else if (mTouchPosition == POSITION_BITMAP_TOUCH) {
            RectF rectF = new RectF();
            boolean needAnim = ensureCurDrawableLegal(rectF);
            if (needAnim) {
                AnimFinishCallback<Object> mTouchUpAnimAdapter = new AnimFinishCallback<Object>() {
                    @Override
                    public void onAnimFinish(Object object) {
                        if (!mToDrawableRect.equals(mDimensionManager.getCurDrawableRect())
                                || !mToCropRect.equals(mCropperRect)) {
                            mHandler.sendEmptyMessageDelayed(START_ZOOM_ANIM, 750);
                        } else {
                            mHandler.sendEmptyMessageDelayed(START_MASK_ALPHA_ANIM, 750);
                        }
                    }
                };
                startCurDrawRectToDestRectAnim(rectF, mTouchUpAnimAdapter);
            } else {
                if (!mToDrawableRect.equals(mDimensionManager.getCurDrawableRect())
                        || !mToCropRect.equals(mCropperRect)) {
                    mHandler.sendEmptyMessageDelayed(START_ZOOM_ANIM, 750);
                } else {
                    mHandler.sendEmptyMessageDelayed(START_MASK_ALPHA_ANIM, 750);
                }
            }
        }

        return false;
    }

    @Override
    public boolean onScale(float scaleFactor, float focusX, float focusY) {
        if (mTouchPosition == POSITION_BITMAP_TOUCH) {
            mDimensionManager.postMatrixScale(scaleFactor, scaleFactor, focusX, focusY);
            return true;
        }
        return false;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (mShowMask) {
            drawMask(canvas);
        }

        if (mShowCropper) {
            drawCropper(canvas);
        }
    }

    private void drawCropper(Canvas canvas) {
        boolean reseted;
        reseted = resetCornerPath();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCornerStroke);
        mPaint.setColor(Color.WHITE);
        canvas.drawPath(mCornerPath, mPaint);

        reseted |= resetCropDividerPath();
        mPaint.setStrokeWidth(mCropDividerStroke);
        canvas.drawPath(mCropperDividerPath, mPaint);

        mPaint.setStrokeWidth(mCropperStroke);
        canvas.drawRect(mCropperRect, mPaint);

        if (reseted) {
            mCropperRectChange = false;
        }
    }

    /**
     * 重新设置 corner 的 path
     */
    private boolean resetCornerPath() {
        if (!mCropperRectChange) {
            return false;
        }
        mCornerPath.reset();
        // 左上角
        mCornerPath.moveTo(mCropperRect.left - mCropperLineStroke,
                mCropperRect.top - mCropperLineStroke + mCornerLength);

        mCornerPath.lineTo(mCropperRect.left - mCropperLineStroke,
                mCropperRect.top - mCropperLineStroke);

        mCornerPath.lineTo(mCropperRect.left - mCropperLineStroke + mCornerLength,
                mCropperRect.top - mCropperLineStroke);


        // 右上角的 Corner
        mCornerPath.moveTo(mCropperRect.right + mCropperLineStroke - mCornerLength,
                mCropperRect.top - mCropperLineStroke);
        mCornerPath.lineTo(mCropperRect.right + mCropperLineStroke,
                mCropperRect.top - mCropperLineStroke);
        mCornerPath.lineTo(mCropperRect.right + mCropperLineStroke,
                mCropperRect.top - mCropperLineStroke + mCornerLength);

        // 右下角的 Corner
        mCornerPath.moveTo(mCropperRect.right + mCropperLineStroke,
                mCropperRect.bottom + mCropperLineStroke - mCornerLength);
        mCornerPath.lineTo(mCropperRect.right + mCropperLineStroke,
                mCropperRect.bottom + mCropperLineStroke);
        mCornerPath.lineTo(mCropperRect.right + mCropperLineStroke - mCornerLength,
                mCropperRect.bottom + mCropperLineStroke);

        // 左下角的 Corner
        mCornerPath.moveTo(mCropperRect.left - mCropperLineStroke + mCornerLength,
                mCropperRect.bottom + mCropperLineStroke);
        mCornerPath.lineTo(mCropperRect.left - mCropperLineStroke,
                mCropperRect.bottom + mCropperLineStroke);
        mCornerPath.lineTo(mCropperRect.left - mCropperLineStroke,
                mCropperRect.bottom + mCropperLineStroke - mCornerLength);

        float horizontalMidCropLeft = mCropperRect.width() / 2 + mCropperRect.left - mMidCornerLength / 2;
        float horizontalMidCropRight = mCropperRect.width() / 2 + mCropperRect.left + mMidCornerLength / 2;
        float verticalMidCropTop = mCropperRect.height() / 2 + mCropperRect.top - mMidCornerLength / 2;
        float verticalMidCropBottom = mCropperRect.height() / 2 + mCropperRect.top + mMidCornerLength / 2;

        mCornerPath.moveTo(horizontalMidCropLeft, mCropperRect.top - mCropperLineStroke);
        mCornerPath.lineTo(horizontalMidCropRight, mCropperRect.top - mCropperLineStroke);
        mCornerPath.moveTo(horizontalMidCropLeft, mCropperRect.bottom + mCropperLineStroke);
        mCornerPath.lineTo(horizontalMidCropRight, mCropperRect.bottom + mCropperLineStroke);

        mCornerPath.moveTo(mCropperRect.left - mCropperLineStroke, verticalMidCropTop);
        mCornerPath.lineTo(mCropperRect.left - mCropperLineStroke, verticalMidCropBottom);
        mCornerPath.moveTo(mCropperRect.right + mCropperLineStroke, verticalMidCropTop);
        mCornerPath.lineTo(mCropperRect.right + mCropperLineStroke, verticalMidCropBottom);
        return true;
    }

    private boolean resetCropDividerPath() {
        if (!mCropperRectChange) {
            return false;
        }

        mCropperDividerPath.reset();

        mCropperDividerPath.moveTo(mCropperRect.width() / 3 + mCropperRect.left, mCropperRect.top);
        mCropperDividerPath.lineTo(mCropperRect.width() / 3 + mCropperRect.left, mCropperRect.bottom);

        mCropperDividerPath.moveTo(mCropperRect.width() * 2 / 3 + mCropperRect.left, mCropperRect.top);
        mCropperDividerPath.lineTo(mCropperRect.width() * 2 / 3 + mCropperRect.left, mCropperRect.bottom);

        mCropperDividerPath.moveTo(mCropperRect.left, mCropperRect.height() / 3 + mCropperRect.top);
        mCropperDividerPath.lineTo(mCropperRect.right, mCropperRect.height() / 3 + mCropperRect.top);

        mCropperDividerPath.moveTo(mCropperRect.left, mCropperRect.height() * 2 / 3 + mCropperRect.top);
        mCropperDividerPath.lineTo(mCropperRect.right, mCropperRect.height() * 2 / 3 + mCropperRect.top);

        return true;
    }

    private void drawMask(Canvas canvas) {
        mPaint.setColor(Color.BLACK);
        if (mIsClipMode) {
            mPaint.setAlpha(255);
        } else {
            mPaint.setAlpha(mMaskAlpha);
        }
        mPaint.setStyle(Paint.Style.FILL);
        canvas.save();
        canvas.concat(mMatrix);
        canvas.drawPath(mMaskPath, mPaint);
        canvas.restore();
    }

    /**
     * 重新设置 mask 的 path
     */
    private void resetMaskPath() {
        mMaskPath.reset();
        updateClipRectOnBitmap();
        mMaskPath.addRect(mClipRectOnBitmap, Path.Direction.CW);
        mMaskPath.addRect(mDimensionManager.getInitBitmapRect(), Path.Direction.CCW);
    }

    /**
     * 重新设置 mask 的 path
     */
    private void resetMaskPath(RectF clipRectOnBitmap) {
        mMaskPath.reset();
        mMaskPath.addRect(clipRectOnBitmap, Path.Direction.CW);
        mMaskPath.addRect(mDimensionManager.getInitBitmapRect(), Path.Direction.CCW);
    }

    private void updateClipRectOnBitmap() {
        mDimensionManager.mapRectFFromInvertMatrix(mClipRectOnBitmap, mCropperRect);
    }

    /**
     * 判断当前点击是否落在 cropper 的边界上
     */
    private boolean isTouchCropper(float x, float y) {
        boolean includeInnerRect = x < mCropperRect.left + 20
                && x > mCropperRect.right - 20
                && x < mCropperRect.top + 20
                && x > mCropperRect.bottom - 20;

        boolean excludeOuterRect = x < mCropperRect.left - 20
                || x > mCropperRect.right + 20
                || y < mCropperRect.top - 20
                || y > mCropperRect.bottom + 20;

        return !includeInnerRect && !excludeOuterRect;
    }

    private boolean isTouchCropper(int position) {
        // 这里用了比较取巧的方式，因为默认上下左右等位置一定会被赋值为Gravity.LEFT 等值，而这些值一定大于
        // Gravity.NO_GRAVITY，另外还有一种情况是 touchBitmap 的情况，而那种情况会被赋值为 -1，是比 NO_GRAVITY 小的
        // 所以只要值大于 NO_GRAVITY 就默认已经 touch 到了 cropper
        return position > Gravity.NO_GRAVITY;
    }

    /**
     * 获取从当前的 mCropperRect 变换到 maxCropperRect 所需要的 matrix
     */
    private Matrix getCropToMaxCropMatrix(RectF cropRect) {
        Matrix matrix = new Matrix();
        RectF maxCropRect = mDimensionManager.getMaxCropRect();

        float scaleX = maxCropRect.width() / cropRect.width();
        float scaleY = maxCropRect.height() / cropRect.height();
        float scale = scaleX < scaleY ? scaleX : scaleY;

        matrix.postTranslate(maxCropRect.centerX() - cropRect.centerX(), maxCropRect.centerY() - cropRect.centerY());
        matrix.postScale(scale, scale, maxCropRect.centerX(), maxCropRect.centerY());
        return matrix;
    }

    private Matrix getCurCropToDoneCropMatrix() {
        Matrix matrix = new Matrix();
        float scaleX = mDimensionManager.getInitViewRect().width() / mCropperRect.width();
        float scaleY = mDimensionManager.getInitViewRect().height() / mCropperRect.height();
        float scale = scaleX < scaleY ? scaleX : scaleY;

        matrix.postTranslate(mDimensionManager.getInitViewRect().centerX() - mCropperRect.centerX(), mDimensionManager.getInitViewRect().centerY() - mCropperRect.centerY());
        matrix.postScale(scale, scale, mDimensionManager.getInitViewRect().centerX(), mDimensionManager.getInitViewRect().centerY());
        return matrix;
    }

    /**
     * 获取 touch 的裁剪框的具体位置（左右上下、左上、左下、右上、右下）
     */
    private int getTouchCropperPosition(float x, float y) {
        if (!isTouchCropper(x, y) && mDimensionManager.getCurDrawableRect().contains(x, y)) {
            return POSITION_BITMAP_TOUCH;
        }
        if (x <= mCropperRect.left + mCornerLength && x >= mCropperRect.left - mCornerLength) {
            if (y >= mCropperRect.bottom - mCornerLength && y <= mCropperRect.bottom + mCornerLength) {
                return Gravity.LEFT | Gravity.BOTTOM;

            } else if (y <= mCropperRect.top + mCornerLength && y >= mCropperRect.top - mCornerLength) {
                return Gravity.LEFT | Gravity.TOP;

            } else if (y < mCropperRect.bottom - mCornerLength && y > mCropperRect.top + mCornerLength) {
                return Gravity.LEFT;

            }
        } else if (x >= mCropperRect.left + mCornerLength && x <= mCropperRect.right - mCornerLength) {
            if (y >= mCropperRect.bottom - mCornerLength && y <= mCropperRect.bottom + mCornerLength) {
                return Gravity.BOTTOM;

            } else if (y <= mCropperRect.top + mCornerLength && y >= mCropperRect.top - mCornerLength) {
                return Gravity.TOP;

            } else if (mDimensionManager.getCurDrawableRect().contains(x, y)) {
                return POSITION_BITMAP_TOUCH;

            }
        } else if (x >= mCropperRect.right - mCornerLength && x <= mCropperRect.right + mCornerLength) {
            if (y >= mCropperRect.bottom - mCornerLength && y <= mCropperRect.bottom + mCornerLength) {
                return Gravity.RIGHT | Gravity.BOTTOM;

            } else if (y <= mCropperRect.top + mCornerLength && y >= mCropperRect.top - mCornerLength) {
                return Gravity.RIGHT | Gravity.TOP;

            } else if (y < mCropperRect.bottom - mCornerLength && y > mCropperRect.top + mCornerLength) {
                return Gravity.RIGHT;

            }
        }
        return Gravity.NO_GRAVITY;
    }

    /**
     * 根据移动的距离差，来更新 cropper 的位置。更新 cropper 边界遵循下面几点原则：
     * 1.必须要 touch 到 cropper，也就是 isTouchCropper(mTouchPosition) == true
     * 2.mCropper 的四边包含于 mMaxCropper 的四边 ==> mCropper.left >= mMaxCropper.left,
     * mCropper.right <= mMaxCropper.right 等
     * 3.mCropper 的四边包含于 mCurDrawable 的四边
     * 4.当 mCurDrawable 的某一边包含于对应的 mMaxCropper 时，正常情况下，按照上面的规则，mCropper 最多只能
     * 到 mCurDrawable 的边界。但是！这里的需求是需要把 mCurDrawable 推到 mMaxCropper 的边界上。根据这个
     * 需求，我们又可以分出两种情况：
     * （1）mCurDrawable 需要放大，才能达到边界。如果这时候 mCurDrawable 还能够继续放大，
     * 即当前的 scale < MAX_DRAWABLE_SCALE，这个时候根据 dx 或者 dy 相对于 mCurDrawable
     * 的 width 或者 height 的比例，算出具体的 postScale值，然后再调整 mCurDrawable 的位置。
     * 如果无法继续放大，就不在移动边界的位置。
     * <p>
     * （2）mCurDrawable 不需要放大，就能达到边界。此时只是 mCurDrawable 被平移出去了，这时候只需要把
     * mCurDrawable 移动回来就好。
     */
    private boolean updateCropperRect(float dx, float dy) {
        float[] val = new float[3];
        val[0] = dx;
        val[1] = dy;
        switch (mTouchPosition) {
            case Gravity.LEFT:
                updateLeftCorner(val);

                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.left += val[0];
                return val[0] != 0;

            case Gravity.TOP:
                updateTopCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.top += val[1];
                return val[1] != 0;

            case Gravity.RIGHT:
                updateRightCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.right += val[0];
                return val[0] != 0;

            case Gravity.BOTTOM:
                updateBottomCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.bottom += val[1];
                return val[1] != 0;

            case Gravity.LEFT | Gravity.TOP:
                updateTopCorner(val);
                updateLeftCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.left += val[0];
                mCropperRect.top += val[1];
                return val[0] != 0 || val[1] != 0;

            case Gravity.RIGHT | Gravity.TOP:
                updateRightCorner(val);
                updateTopCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.right += val[0];
                mCropperRect.top += val[1];
                return val[0] != 0 || val[1] != 0;

            case Gravity.RIGHT | Gravity.BOTTOM:
                updateRightCorner(val);
                updateBottomCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.right += val[0];
                mCropperRect.bottom += val[1];
                return val[0] != 0 || val[1] != 0;

            case Gravity.LEFT | Gravity.BOTTOM:
                updateLeftCorner(val);
                updateBottomCorner(val);
                if (val[2] != Gravity.NO_GRAVITY) {
                    return pushCurDrawBorderToMaxCropBorder(val[0], val[1], (int) val[2]);
                }
                mCropperRect.left += val[0];
                mCropperRect.bottom += val[1];
                return val[0] != 0 || val[1] != 0;
        }
        return false;
    }

    /**
     * @param values 长度为 3 的 float 数组，第一个参数是 dx，第二个参数是 dy，第三个参数是 gravity 的值
     */
    private void updateLeftCorner(float[] values) {
        //
        //   这里是mCropperRect.left
        //    |                       -----这里是mCropperRect.right
        //    |                       |
        //   \|/                     \|/
        //   ------------    ------------
        //  |            |  |            |
        //  |   ---------    --------    |
        //  |   |                   |    |
        //  |   |                   |    |
        //  |   |                   |    |
        //  -----                   ------
        //  |-|             |------------|
        //  /|\                 /|\
        //   |                   |
        //   |                   -------这个长度是 mCornerLength，因为左右两边有两个corner，所以计算时要乘以2
        //   |
        //   --- 这个长度是 mCropperLineStroke，这里也是因为左右两边，所以要乘以2
        //
        // 所以总长度应该是 mCropperRect.left + 2* mCornerLength - 2* mCropperLineStroke == mCropperRect.right
        // 这样解释应该能看明白了吧！！！
        if (mCropperRect.left + values[0] + 2 * mCornerLength - 2 * mCropperLineStroke + mMidCornerLength >= mCropperRect.right) {
            values[0] = mCropperRect.right - 2 * mCornerLength + 2 * mCropperLineStroke - mCropperRect.left + 0.5f - mMidCornerLength;
        }

        RectF maxCropRect = mDimensionManager.getMaxCropRect();
        float maxX = maxCropRect.left > mDimensionManager.getCurDrawableRect().left ? maxCropRect.left : mDimensionManager.getCurDrawableRect().left;
        if (mCropperRect.left + values[0] <= maxX) {
            // 如果最大值是 mMaxCropper.left，说明 mCurDrawable.left 在左边，这个时候继续往左推，
            // 就应该让mCropper.left 的值无法移动。为什么这里要计算 dx？
            // 因为如果直接 break，这时候如果在临界值时，一次 -dx 的值过大，我们 break 会导致
            // mCropper.left 无法达到 mMaxCropper.left 的位置
            if (maxX == maxCropRect.left) {
                values[0] = maxCropRect.left - mCropperRect.left;
            }
            // 如果最大值是 mCurDrawable，说明这个时候 mCurDrawable.left 还没到达 mMaxCropper.left 的位置
            // 我们需要把 mCurDrawable.left 推到 mMaxCropper.left 的位置
            else {
                int temp = (int) values[2];
                temp |= Gravity.LEFT;
                values[2] = temp;
            }
        }
    }

    private void updateTopCorner(float[] values) {
        RectF maxCropRect = mDimensionManager.getMaxCropRect();
        if (mCropperRect.top + values[1] + 2 * mCornerLength - 2 * mCropperLineStroke + mMidCornerLength >= mCropperRect.bottom) {
            values[1] = mCropperRect.bottom - 2 * mCornerLength + 2 * mCropperLineStroke - mCropperRect.top + 0.5f - mMidCornerLength;
        }
        float maxY = maxCropRect.top > mDimensionManager.getCurDrawableRect().top ? maxCropRect.top : mDimensionManager.getCurDrawableRect().top;
        if (mCropperRect.top + values[1] <= maxY) {
            if (maxY == maxCropRect.top) {
                values[1] = maxCropRect.top - mCropperRect.top;
            } else {
                int temp = (int) values[2];
                temp |= Gravity.TOP;
                values[2] = temp;
            }
        }
    }

    private void updateRightCorner(float[] values) {
        RectF maxCropRect = mDimensionManager.getMaxCropRect();
        if (mCropperRect.right + values[0] - 2 * mCornerLength + 2 * mCropperLineStroke - mMidCornerLength <= mCropperRect.left) {
            values[0] = mCropperRect.left + 2 * mCornerLength - 2 * mCropperLineStroke - mCropperRect.right - 0.5f + mMidCornerLength;
        }
        float minX = maxCropRect.right < mDimensionManager.getCurDrawableRect().right ? maxCropRect.right : mDimensionManager.getCurDrawableRect().right;

        if (mCropperRect.right + values[0] >= minX) {
            if (minX == maxCropRect.right) {
                values[0] = maxCropRect.right - mCropperRect.right;
            } else {
                int temp = (int) values[2];
                temp |= Gravity.RIGHT;
                values[2] = temp;
            }
        }
    }

    private void updateBottomCorner(float[] values) {
        RectF maxCropRect = mDimensionManager.getMaxCropRect();
        if (mCropperRect.bottom + values[1] - 2 * mCornerLength + 2 * mCropperLineStroke - mMidCornerLength <= mCropperRect.top) {
            values[1] = mCropperRect.top + 2 * mCornerLength - 2 * mCropperLineStroke - mCropperRect.bottom - 0.5f + mMidCornerLength;
        }

        float minY = maxCropRect.bottom < mDimensionManager.getCurDrawableRect().bottom ? maxCropRect.bottom : mDimensionManager.getCurDrawableRect().bottom;
        if (mCropperRect.bottom + values[1] >= minY) {
            if (minY == maxCropRect.bottom) {
                values[1] = maxCropRect.bottom - mCropperRect.bottom;
            } else {
                int temp = (int) values[2];
                temp |= Gravity.BOTTOM;
                values[2] = temp;
            }
        }
    }

    /**
     * 在移动 cropper 的时候，如果 mCurDrawable 的边界没到达 mMaxCropper 的边界时，我们需要把 mCurDrawable
     * 往 mMaxCropper 的边界推。详细的解释见{@link #updateCropperRect(float dx, float dy)}
     */
    private boolean pushCurDrawBorderToMaxCropBorder(float dx, float dy, int pushFlag) {
        switch (mTouchPosition) {
            case Gravity.LEFT:
                if (pushFlag == Gravity.LEFT) {
                    dx = pushBorderToLeft(dx);
                    mCropperRect.left += dx;
                    return dx != 0;
                }
                break;

            case Gravity.TOP:
                if (pushFlag == Gravity.TOP) {
                    dy = pushBorderToTop(dy);
                    mCropperRect.top += dy;
                    return dy != 0;
                }
                break;

            case Gravity.RIGHT:
                if (pushFlag == Gravity.RIGHT) {
                    dx = pushBorderToRight(dx);
                    mCropperRect.right += dx;
                    return dx != 0;
                }
                break;

            case Gravity.BOTTOM:
                if (pushFlag == Gravity.BOTTOM) {
                    dy = pushBorderToBottom(dy);
                    mCropperRect.bottom += dy;
                    return dy != 0;
                }
                break;

            case Gravity.LEFT | Gravity.TOP:
                if (pushFlag == Gravity.LEFT) {
                    dx = pushBorderToLeft(dx);
                    mCropperRect.left += dx;
                    mCropperRect.top += dy;

                } else if (pushFlag == Gravity.TOP) {
                    dy = pushBorderToTop(dy);
                    mCropperRect.left += dx;
                    mCropperRect.top += dy;

                } else {
                    dx = pushBorderToLeft(dx);
                    dy = pushBorderToTop(dy);
                    mCropperRect.left += dx;
                    mCropperRect.top += dy;

                    float curLeft = mDimensionManager.getCurDrawableRect().left;
                    float curTop = mDimensionManager.getCurDrawableRect().top;
                    mDimensionManager.postMatrixTranslate(mCropperRect.left - curLeft, mCropperRect.top - curTop);
                }
                return dx != 0 || dy != 0;

            case Gravity.RIGHT | Gravity.TOP:
                if (pushFlag == Gravity.RIGHT) {
                    dx = pushBorderToRight(dx);
                    mCropperRect.right += dx;
                    mCropperRect.top += dy;
                    return dx != 0 || dy != 0;
                } else if (pushFlag == Gravity.TOP) {
                    dy = pushBorderToTop(dy);
                    mCropperRect.right += dx;
                    mCropperRect.top += dy;
                } else {
                    dx = pushBorderToRight(dx);
                    dy = pushBorderToTop(dy);
                    mCropperRect.right += dx;
                    mCropperRect.top += dy;

                    float curRight = mDimensionManager.getCurDrawableRect().right;
                    float curTop = mDimensionManager.getCurDrawableRect().top;
                    mDimensionManager.postMatrixTranslate(mCropperRect.right - curRight, mCropperRect.top - curTop);
                }
                return dx != 0 || dy != 0;

            case Gravity.RIGHT | Gravity.BOTTOM:
                if (pushFlag == Gravity.RIGHT) {
                    dx = pushBorderToRight(dx);
                    mCropperRect.right += dx;
                    mCropperRect.bottom += dy;
                } else if (pushFlag == Gravity.BOTTOM) {
                    dy = pushBorderToBottom(dy);
                    mCropperRect.right += dx;
                    mCropperRect.bottom += dy;
                } else {
                    dx = pushBorderToRight(dx);
                    dy = pushBorderToBottom(dy);
                    mCropperRect.right += dx;
                    mCropperRect.bottom += dy;

                    float curRight = mDimensionManager.getCurDrawableRect().right;
                    float curBottom = mDimensionManager.getCurDrawableRect().bottom;
                    mDimensionManager.postMatrixTranslate(mCropperRect.right - curRight, mCropperRect.bottom - curBottom);
                }
                return dx != 0 || dy != 0;

            case Gravity.LEFT | Gravity.BOTTOM:
                if (pushFlag == Gravity.LEFT) {
                    dx = pushBorderToLeft(dx);
                    mCropperRect.left += dx;
                    mCropperRect.bottom += dy;
                } else if (pushFlag == Gravity.BOTTOM) {
                    dy = pushBorderToBottom(dy);
                    mCropperRect.left += dx;
                    mCropperRect.bottom += dy;
                } else {
                    dx = pushBorderToLeft(dx);
                    dy = pushBorderToBottom(dy);
                    mCropperRect.left += dx;
                    mCropperRect.bottom += dy;

                    float curLeft = mDimensionManager.getCurDrawableRect().left;
                    float curBottom = mDimensionManager.getCurDrawableRect().bottom;
                    mDimensionManager.postMatrixTranslate(mCropperRect.left - curLeft, mCropperRect.bottom - curBottom);
                }
                return dx != 0 || dy != 0;
        }
        return false;
    }

    private float pushBorderToLeft(float dx) {
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        RectF maxCropRect = mDimensionManager.getMaxCropRect();

        // 这时候 drawable 实际上是够大的，只是位置不对，平移一下位置就行了
        if (mCurDrawableRect.width() >= maxCropRect.width()) {
            /**
             * 为什么要从新计算 dx 在 updateCropperRect 的第一个 case 里面解释的很清楚了，
             * 这里和下面的 case 都不再解释了
             * @see #updateCropperRect(float, float)  中的 case POSITION_LEFT
             * */
            if (mCurDrawableRect.left + dx < maxCropRect.left) {
                dx = maxCropRect.left - mCurDrawableRect.left;
            }
            mDimensionManager.postMatrixTranslate(dx, 0);
        }
        // 这时候 drawable 是不够大的（想象超长图的情况），所以我们需要先对图像进行放大
        else {
            // 这里还需要再判断一下图片是否放大到最大
            if (mDimensionManager.getCurScale() >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            //              -------缩放后的图片
            //              |
            //             \|/
            //   -------------------
            //  |    -----------    |
            //  |   |           |   |
            //  |   |缩放前的图片|   |
            //  |   |           |   |
            //  |   |           |   |
            //  |    -----------    |
            //   -------------------
            //  |---|
            //   /|\
            //    ------这里是 dx
            // scale 为什么这么算应该不需要多说了吧
            // 唯一需要注意的是，这个时候往左移的，所以 dx 是负数，最后是需要减，而不是加
            float scale = (mCurDrawableRect.width() - 2 * dx) / mCurDrawableRect.width();
            if (scale >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            mDimensionManager.postMatrixScale(scale, scale, mCurDrawableRect.centerX(), mCurDrawableRect.centerY());
        }
        return dx;
    }

    private float pushBorderToTop(float dy) {
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        RectF maxCropRect = mDimensionManager.getMaxCropRect();

        // 这时候 drawable 实际上是够大的，只是位置不对，平移一下位置就行了
        if (mCurDrawableRect.height() >= maxCropRect.height()) {
            if (mCurDrawableRect.top + dy < maxCropRect.top) {
                dy = maxCropRect.top - mCurDrawableRect.top;
            }
            mDimensionManager.postMatrixTranslate(0, dy);
        }
        // 这时候 drawable 是不够大的（想象超长图的情况），所以我们需要先对图像进行放大
        else {
            if (mDimensionManager.getCurScale() >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            float scale = (mCurDrawableRect.height() - 2 * dy) / mCurDrawableRect.height();
            if (scale >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            mDimensionManager.postMatrixScale(scale, scale, mCurDrawableRect.centerX(), mCurDrawableRect.centerY());
        }

        return dy;
    }

    private float pushBorderToRight(float dx) {
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        RectF maxCropRect = mDimensionManager.getMaxCropRect();

        // 这时候 drawable 实际上是够大的，只是位置不对，平移一下位置就行了
        if (mCurDrawableRect.width() >= maxCropRect.width()) {
            /**
             * 为什么要从新计算 dx 在 updateCropperRect 的第一个 case 里面解释的很清楚了，
             * 这里和下面的 case 都不再解释了
             * @see #updateCropperRect(float, float)  中的 case POSITION_LEFT
             * */
            if (mCurDrawableRect.right + dx > maxCropRect.right) {
                dx = maxCropRect.right - mCurDrawableRect.right;
            }
            mDimensionManager.postMatrixTranslate(dx, 0);
        }
        // 这时候 drawable 是不够大的（想象超长图的情况），所以我们需要先对图像进行放大
        else {
            // 这里还需要再判断一下图片是否放大到最大
            if (mDimensionManager.getCurScale() >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            float scale = (mCurDrawableRect.width() + 2 * dx) / mCurDrawableRect.width();
            if (scale >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            mDimensionManager.postMatrixScale(scale, scale, mCurDrawableRect.centerX(), mCurDrawableRect.centerY());
        }

        return dx;
    }

    private float pushBorderToBottom(float dy) {
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        RectF maxCropRect = mDimensionManager.getMaxCropRect();

        // 这时候 drawable 实际上是够大的，只是位置不对，平移一下位置就行了
        if (mCurDrawableRect.height() >= maxCropRect.height()) {
            if (mCurDrawableRect.bottom + dy > maxCropRect.bottom) {
                dy = maxCropRect.bottom - mCurDrawableRect.bottom;
            }
            mDimensionManager.postMatrixTranslate(0, dy);
        }
        // 这时候 drawable 是不够长的（想象超宽图的情况），所以我们需要先对图像进行放大
        else {
            if (mDimensionManager.getCurScale() >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            float scale = (mCurDrawableRect.height() + 2 * dy) / mCurDrawableRect.height();
            if (scale >= MAX_DRAWABLE_SCALE) {
                return 0;
            }
            mDimensionManager.postMatrixScale(scale, scale, mCurDrawableRect.centerX(), mCurDrawableRect.centerY());
        }
        return dy;
    }

    /**
     * 在 touchUp 的时候调用，确保当前 drawable 的位置处于 cropper 之内。
     *
     * @param destRectF 最终的 drawable 需要移动到的位置，如果 @return 为 true，这个位置和 mCurDrawableRect
     *                  的值是不一致的，如果 @return 为 false，这个位置其实和 mCurDrawableRect 是一样的
     * @return 是否需要做动画，如果是true，说明当前 drawable 已经偏移了，需要把 drawable 移回到 cropper 之内
     * 而具体移动到什么位置，保存在 destRect 中
     */
    private boolean ensureCurDrawableLegal(RectF destRectF) {
        boolean needAnim;
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        // 如果当前的 drawable 小于 cropper ，就需要把当前的 drawable 放大
        boolean needZoomInDrawable;
        needZoomInDrawable = (mCurDrawableRect.width() < mCropperRect.width())
                || (mCurDrawableRect.height() < mCropperRect.height());

        float scale = mDimensionManager.getCurScale();
        // 如果当前的 drawable 放得过大，就需要把 drawable 缩小
        boolean needZoomOutDrawable = scale > MAX_DRAWABLE_SCALE || scale > MAX_DRAWABLE_SCALE;
        Matrix matrix = new Matrix(mDimensionManager.getMatrix());

        if (needZoomInDrawable) {
            float scaleX = mCropperRect.width() / mCurDrawableRect.width();
            float scaleY = mCropperRect.height() / mCurDrawableRect.height();
            scale = scaleX > scaleY ? scaleX : scaleY;

            matrix.postScale(scale, scale, mCurDrawableRect.centerX(), mCurDrawableRect.centerY());
            matrix.mapRect(destRectF, mDimensionManager.getInitBitmapRect());

            ensureRectIncludeMaxCrop(destRectF);
            needAnim = true;
        } else if (needZoomOutDrawable) {
            scale = MAX_DRAWABLE_SCALE / scale;

            matrix.postScale(scale, scale, mCropperRect.centerX(), mCropperRect.centerY());
            matrix.mapRect(destRectF, mDimensionManager.getInitBitmapRect());

            ensureRectIncludeMaxCrop(destRectF);

            needAnim = true;
        } else {
            matrix.mapRect(destRectF, mDimensionManager.getInitBitmapRect());
            needAnim = ensureRectIncludeMaxCrop(destRectF);
        }

        return needAnim;
    }

    private boolean ensureRectIncludeMaxCrop(RectF drawableRect) {
        return ensureRectIncludeMaxCrop(drawableRect,
                drawableRect.left - mCropperRect.left,
                drawableRect.top - mCropperRect.top,
                drawableRect.right - mCropperRect.right,
                drawableRect.bottom - mCropperRect.bottom);
    }

    /**
     * 判断 rect 回到 cropper 所在位置是否需要平移，如果要平移，计算好平移的量。
     * 例子：如果dLeft > 0 说明 rect.left 在 mCropperRect.left 的右边，也就是说 rect 已经向右偏移了，
     * 这时候需要让这个 rect 再向左偏移回来，也就是 rect.left = rect.lef - dLeft。
     *
     * @param rectF   需要判断的 rect
     * @param dLeft   rect.left - mCropperRect.left
     * @param dTop    rect.top - mCropperRect.top
     * @param dRight  rect.right - mCropperRect.right
     * @param dBottom rect.bottom - mCropperRect.bottom
     */
    private boolean ensureRectIncludeMaxCrop(RectF rectF, float dLeft, float dTop, float dRight, float dBottom) {
        boolean needAnim = false;
        if (dLeft > 0) {
            rectF.left -= dLeft;
            rectF.right -= dLeft;
            needAnim = true;
        } else if (dRight < 0) {
            rectF.right -= dRight;
            rectF.left -= dRight;
            needAnim = true;
        }

        if (dTop > 0) {
            rectF.top -= dTop;
            rectF.bottom -= dTop;
            needAnim = true;
        } else if (dBottom < 0) {
            rectF.top -= dBottom;
            rectF.bottom -= dBottom;
            needAnim = true;
        }
        return needAnim;
    }

    /**
     * 设置当前 drawable 的 rect。
     * 拿到新的 rect 之后，根据 mCurDrawableRect 和 rect 来调整 mMatrix 的值，同时会通知 invoker，更新
     * matrix
     */
    public void setCurrDrawableRect(RectF rect) {
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();
        mDimensionManager.postMatrixTranslate(rect.centerX() - mCurDrawableRect.centerX(),
                rect.centerY() - mCurDrawableRect.centerY());
        mDimensionManager.postMatrixScale(rect.width() / mCurDrawableRect.width(),
                rect.height() / mCurDrawableRect.height(), rect.centerX(), rect.centerY());
        requestInvalidate();
    }

    /**
     * 设置当前 drawable 和 cropper 的rect。
     * 拿到新的 rect 之后，根据 mCurDrawableRect 和 rect 来调整 mMatrix 的值，同时会通知 invoker，更新
     * matrix
     */
    public void setCurCropAndCurDrawRect(DoubleRectF doubleRect) {
        mCropperRect = doubleRect.getFirstRect();
        mCropperRectChange = true;
        RectF mCurDrawableRect = mDimensionManager.getCurDrawableRect();

        mDimensionManager.postMatrixTranslate(doubleRect.getSecondRect().centerX() - mCurDrawableRect.centerX(),
                doubleRect.getSecondRect().centerY() - mCurDrawableRect.centerY());
        mDimensionManager.postMatrixScale(doubleRect.getSecondRect().width() / mCurDrawableRect.width(),
                doubleRect.getSecondRect().height() / mCurDrawableRect.height(),
                doubleRect.getSecondRect().centerX(),
                doubleRect.getSecondRect().centerY());
        requestInvalidate();
    }

    public interface OnCropStateChangeListener {
        void onCropStateChange(boolean cropBorderChange);
    }

    static class CropHandler extends Handler {
        CropLayer mLayer;

        CropHandler(CropLayer layer) {
            mLayer = layer;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DISMISS_MASK:
                    mLayer.mShowMask = false;
                    break;

                case SHOW_MASK:
                    mLayer.mShowMask = true;
                    break;

                case START_ZOOM_ANIM:
                    Matrix tempMatrix = mLayer.getCropToMaxCropMatrix(mLayer.mCropperRect);
                    tempMatrix.mapRect(mLayer.mToCropRect, mLayer.mCropperRect);
                    float[] mv = new float[9];
                    tempMatrix.mapRect(mLayer.mToDrawableRect, mLayer.mDimensionManager.getCurDrawableRect());

                    tempMatrix.getValues(mv);
                    float scale = mv[Matrix.MSCALE_X] * mLayer.mDimensionManager.getCurScale();
                    if (scale > MAX_DRAWABLE_SCALE) {
                        scale = MAX_DRAWABLE_SCALE / scale;
                        tempMatrix.postScale(scale, scale,
                                mLayer.mDimensionManager.getMaxCropRect().centerX(),
                                mLayer.mDimensionManager.getMaxCropRect().centerY());
                        tempMatrix.mapRect(mLayer.mToDrawableRect, mLayer.mDimensionManager.getCurDrawableRect());
                        mLayer.ensureRectIncludeMaxCrop(mLayer.mToDrawableRect);
                    }
                    mLayer.startZoomCropperAnim(mLayer.mToCropRect, mLayer.mToDrawableRect, null);
                    mLayer.mShowMask = true;
                    break;

                case START_MASK_ALPHA_ANIM:
                    mLayer.startMaskAlphaAnim();
                    mLayer.mShowMask = true;
                    break;
            }
        }
    }
}
