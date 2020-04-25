package com.biotech.drawlessons.photoedit.views

import android.content.Context
import android.graphics.*
import android.graphics.BitmapFactory.Options
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.biotech.drawlessons.R
import com.biotech.drawlessons.photoedit.draws.*
import com.biotech.drawlessons.photoedit.evaluator.MatrixInfo
import com.biotech.drawlessons.photoedit.gpuimage.GPUImage
import com.biotech.drawlessons.photoedit.gpuimage.filters.GPUImageFilter
import com.biotech.drawlessons.photoedit.gpuimage.filters.ImageFilterFactory
import com.biotech.drawlessons.photoedit.layers.*
import com.biotech.drawlessons.photoedit.layers.CropLayer.OnCropStateChangeListener
import com.biotech.drawlessons.photoedit.layers.StickerLayer.StickerStateListener
import com.biotech.drawlessons.photoedit.test.FakeData
import com.biotech.drawlessons.photoedit.tools.DimensionManager
import com.biotech.drawlessons.photoedit.tools.IStrokeWidthChange
import com.biotech.drawlessons.photoedit.utils.*
import com.biotech.drawlessons.photoedit.utilsmodel.IColorPicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

/**
 * Created by xintu on 2018/1/29.
 */
class DrawingBoardView(private val mContext: Context?, attrs: AttributeSet?, defStyle: Int) :
    View(mContext, attrs, defStyle), IStrokeWidthChange, ILayerParent {
    private var mInvoker: DrawInvoker? = null
    private var mBitmapManager: BitmapsManager? = null
    private var mLayerManager: LayersManager? = null
    private var mIdlLayer: IdleLayer? = null
    private var mInternalLayer: InternalTempLayer? = null
    private var mBrushLayer: BrushLayer? = null
    private var mBackgroundBrushLayer: BackgroundBrushLayer? = null
    private var mBitmapLayer: BaseBitmapLayer? = null
    private var mLightLineLayer: LightLineBrushLayer? = null
    private var mMosaicsLayer: MosaicsLayer? = null
    private var mStickerLayer: StickerLayer? = null
    private var mPhotoFrameLayer: PhotoFrameLayer? = null
    private var mCropLayer: CropLayer? = null
    private var mCurViewWidth = 0
    private var mCurViewHeight = 0
    private var mInitViewWidth = 0
    private var mInitViewHeight = 0
    //    private RectF mClipRect;
    private var mNeedClipRect = false
    private var mDimensionManger: DimensionManager? = null
    private var mDoneCropMatrixInfo: MatrixInfo? = null
    private var mBitmapUri: String? = null
    private var mInitFinishCallback: InitFinishCallback? = null
    private var mColor = 0
    private var mStrokeLevel = 0
    // 当前的滤镜类型
    var mCurFilterType = FakeData.ORIGINAL_FILTER_TYPE
        private set
    // 点击完成时的滤镜类型
    var mDoneFilterType = FakeData.ORIGINAL_FILTER_TYPE
        private set
    private var mInitResultOk = false
    private var mInitiating = false
    private var mOriginalBitmapValidate = false
    private var mIColorPicker: IColorPicker? = null
    private var mStickerStateListener: StickerStateListener? = null

    @JvmOverloads
    constructor(context: Context?, set: AttributeSet? = null) : this(context, set, 0)

    fun init(
        invoker: DrawInvoker?,
        manager: BitmapsManager?,
        bitmapUri: String?
    ) { // 传过来的bitmap，需要直接在这个bitmap上作画
        mInvoker = invoker
        mBitmapManager = manager
        mBitmapUri = bitmapUri
        mColor = Color.WHITE
        mStrokeLevel = IStrokeWidthChange.STOKE_LEVEL_NORMAL
    }

    fun replaceBitmapUrl(bitmapUrl: String?) {
        mBitmapUri = bitmapUrl
        requestLayout()
    }

    private fun initLayers() {
        mIdlLayer = IdleLayer(this)
        mLayerManager!!.insertLayer(mIdlLayer)
        mInternalLayer =
            InternalTempLayer(this, mDimensionManger!!.matrix, mInvoker, mBitmapManager)
        mLayerManager!!.insertLayer(mInternalLayer)
        mBitmapLayer = BaseBitmapLayer(this, mDimensionManger!!.matrix, mBitmapManager)
        mLayerManager!!.insertLayer(mBitmapLayer)
        val layer =
            SecondInternalTempLayer(this, mDimensionManger!!.matrix, mBitmapManager)
        mLayerManager!!.insertLayer(layer)
        initBrushLayer()
        initMosaicLayer()
        initLightLineLayer()
        initPhotoFrameLayer()
        initBackgroundLayer()
        switchToIdleLayer()
        invalidate()
    }

    override fun onSizeChanged(
        w: Int,
        h: Int,
        oldw: Int,
        oldh: Int
    ) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCurViewWidth = w
        mCurViewHeight = h
        // old == 0 说明是第一次算出view的大小，这个时候，根据view的大小，计算出需要的originalBitmap的图片大小
        if (oldh == 0 || oldw == 0 || !mOriginalBitmapValidate) {
            mInitViewHeight = h
            mInitViewWidth = w
            initManagers()
        }
        if (mLayerManager != null) {
            mLayerManager!!.onSizeChange(w, h, oldw, oldh)
        }
        invalidate()
    }

    fun setInitFinishCallback(finishCallback: InitFinishCallback?) {
        mInitFinishCallback = finishCallback
    }

    fun setIColorPicker(iColorPicker: IColorPicker?) {
        mIColorPicker = iColorPicker
        if (mLayerManager != null) {
            mLayerManager!!.setIColorPicker(mIColorPicker)
        }
    }


    private fun initManagers() {
        if (mInitiating) {
            return
        }
        mInitiating = true

        GlobalScope.launch(Dispatchers.Main) {
            createAndSaveOriginalBitmap()
            createFilterImgToSDCard()

            createLayerManager()
            restoreClipState()

            restoreSavedDraw()
            finishLoad()


            setNormalBrushMode(Color.WHITE)
        }
    }

    private fun finishLoad() {
        if (mInitFinishCallback != null) {
            mInitFinishCallback?.onInitFinish()
        }
        mInitResultOk = mOriginalBitmapValidate
        mInitiating = false
        switchToIdleLayer()
        invalidate()
    }

    private suspend fun createAndSaveOriginalBitmap() {
        withContext(Dispatchers.IO) {
            val bitmap =
                BitmapUtils.createMaxSizeBitmapFromUri(mBitmapUri, mInitViewWidth, mInitViewHeight)
            Log.e("DrawingBoard", "createAndSaveOriginalBitmap = $bitmap")
            mOriginalBitmapValidate = bitmap != null
            if (!mOriginalBitmapValidate) {
                return@withContext
            }
            mBitmapManager!!.replaceOriginalBitmap(mBitmapUri, bitmap)
        }
    }

    private suspend fun createFilterImgToSDCard() {
        withContext(Dispatchers.IO) {
            val datas = FakeData.buildFilterDataList()
            var mGPUImage: GPUImage? = null
            var originalBitmap: Bitmap? = null
            //
            for (i in 1 until datas.size) {
                val filterType = datas[i].filterType
                if (TextUtils.isEmpty(filterType)) {
                    return@withContext
                }
                val file = File(BitmapsManager.getFilterUri(context, filterType))
                if (!file.exists()) {
                    if (mGPUImage == null) {
                        mGPUImage = GPUImage(mContext)
                    }
                    if (originalBitmap == null) {
                        originalBitmap = BitmapFactory.decodeResource(
                            resources,
                            R.drawable.bg_filter_view_preview
                        )
                    }
                    mGPUImage.setImage(originalBitmap)
                    val factory = ImageFilterFactory()
                    val filter: GPUImageFilter = factory.createFilter(filterType)
                    mGPUImage.setFilter(filter)
                    val filterPreview = mGPUImage.bitmapWithFilterApplied
                    // 如果文件不存在，生成新的filter预览图并保存在本地
                    BitmapUtils.saveBitmapToSdcard(
                        filterPreview,
                        100,
                        BitmapsManager.getFilterUri(context, filterType)
                    )
                    filterPreview.recycle()
                }
            }
        }
    }

    private suspend fun restoreSavedDraw() {
        withContext(Dispatchers.IO) {
            val bean =
                DrawDataManager.getInstance().getRestoreBean(mBitmapUri) ?: return@withContext
            val brushDatas = bean.brushDatas
            // 还原滤镜状态
            val filterType = bean.filterType
            if (!TextUtils.isEmpty(filterType)) {
                val filter: GPUImageFilter
                val factory = ImageFilterFactory()
                filter = factory.createFilter(filterType)
                var bitmap =
                    BitmapUtils.createMaxSizeBitmapFromUri(
                        mBitmapUri,
                        mInitViewWidth,
                        mInitViewHeight
                    )
                if (filter != null) {
                    val GPUImage = GPUImage(mContext)
                    GPUImage.setImage(bitmap)
                    GPUImage.setFilter(filter)
                    bitmap = GPUImage.bitmapWithFilterApplied
                    mBitmapManager!!.replaceOriginalBitmap(mBitmapUri, bitmap)
                }
                mCurFilterType = filterType
                mDoneFilterType = filterType
            }
            // 还原笔刷状态
            val iterator: Iterator<BaseDrawData> = brushDatas.iterator()
            var brush: BaseBrush?
            // 把根据笔刷的data，新建出笔刷，并保存进队列中
            while (iterator.hasNext()) {
                val data = iterator.next()
                when (data.getType()) {
                    IPhotoEditType.BRUSH_BLOCK_MOSAICS -> {
                        brush = mMosaicsLayer!!.createBlockMosaicsBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                    IPhotoEditType.BRUSH_NORMAL_COLOR -> {
                        brush = mBrushLayer!!.createNormalLineBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                    IPhotoEditType.BRUSH_MOSAICS -> {
                        brush = mMosaicsLayer!!.createBrushMosaicsBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                    IPhotoEditType.BRUSH_LIGHT_COLOR -> {
                        brush = mLightLineLayer!!.createLightLineBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                    IPhotoEditType.BRUSH_STICKERS -> {
                        brush = mBrushLayer!!.createStickerBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                    IPhotoEditType.PHOTO_FRAME_DRAW -> {
                        brush = mPhotoFrameLayer!!.createPhotoFrame(data, mBitmapManager)
                        mInvoker!!.setPhotoFrame(brush)
                    }
                    IPhotoEditType.BRUSH_BACKGROUND -> {
                        brush = mBackgroundBrushLayer!!.createBackgroundBrush(data)
                        mInvoker!!.addBrush(brush)
                    }
                }
            }
            // 还原文字和贴纸
            val stickerDatas = bean.stickerDatas
            val stickerIt: Iterator<StickerData> = stickerDatas.iterator()
            var sticker: BaseSticker
            while (stickerIt.hasNext()) {
                val data = stickerIt.next()
                when (data.getType()) {
                    IPhotoEditType.STICKER_BITMAP -> {
                        if (mBitmapManager!!.getBitmap(data.bitmapUrl) == null) {
                            val bitmap = BitmapFactory.decodeFile(data.bitmapUrl)
                            if (bitmap != null) {
                                mBitmapManager!!.saveBitmap(data.bitmapUrl, bitmap)
                            }
                        }
                        initStickerLayer()
                        sticker = mStickerLayer!!.createBitmapSticker(data)
                        sticker.setMapPoints(data.mapPoints)
                        mInvoker!!.addSticker(sticker)
                    }
                    IPhotoEditType.STICKER_TEXT -> {
                        val bitmap =
                            BitmapUtils.createBitmapFromString(mContext, data.text, data.color)
                        mBitmapManager!!.saveBitmap(
                            BitmapsManager.generateTextStickerUri(
                                data.text,
                                data.color
                            ), bitmap
                        )
                        initStickerLayer()
                        sticker = mStickerLayer!!.createTextSticker(data)
                        sticker.setMapPoints(data.mapPoints)
                        mInvoker!!.addSticker(sticker)
                    }
                }
            }
            // 对所有的笔刷排序，并绘制到中间层中
            mInvoker!!.orderBrushes()
            mInvoker!!.drawAllBrushSecondBitmap()
        }
    }

    private fun createLayerManager() {
        if (!mOriginalBitmapValidate) return
        val bitmap = mBitmapManager!!.originalBitmap
        val bitmapW = bitmap.width
        val bitmapH = bitmap.height
        mDimensionManger = DimensionManager(
            mInitViewWidth,
            mInitViewHeight,
            bitmapW,
            bitmapH,
            resources.getDimension(R.dimen.ll_crop_rotate_height).toInt(),
            mColor,
            mStrokeLevel
        )
        mLayerManager = LayersManager(this@DrawingBoardView, mContext, mDimensionManger)
        if (mIColorPicker != null) {
            mLayerManager!!.setIColorPicker(mIColorPicker)
        }
        initLayers()
    }

    private fun restoreClipState() {
        val bean = DrawDataManager.getInstance().getRestoreBean(mBitmapUri)
        if (bean == null || bean.isCameraPhoto) {
            return
        }
        // 更新矩阵
        mDimensionManger!!.updateMatrix(bean.matrix)
        // 还原裁剪状态
        mDimensionManger!!.clipRect = bean.clipRect
        if (bean.doneCropMatrixInfo != null) {
            mDoneCropMatrixInfo = MatrixInfo(bean.doneCropMatrixInfo)
        }
        mNeedClipRect = bean.needClip
        if (mNeedClipRect) {
            initCropLayer()
            mLayerManager!!.updateClipRectStatus(mNeedClipRect, bean.clipRect)
        }
    }

    private suspend fun generateFilterType(filterType: String?) {
        withContext(Dispatchers.IO) {
            try {
                mCurFilterType = filterType ?: return@withContext
                if (FakeData.ORIGINAL_FILTER_TYPE == filterType) {
                    val bitmap = createOriginalBitmap()
                    mBitmapManager?.replaceOriginalBitmap(mBitmapUri, bitmap)
                    return@withContext
                }

                val factory = ImageFilterFactory()
                val filter = factory.createFilter(filterType)
                var bitmap = createOriginalBitmap()
                val gpuImage = GPUImage(mContext)
                gpuImage.setImage(bitmap)
                gpuImage.setFilter(filter)
                bitmap = gpuImage.bitmapWithFilterApplied
                mBitmapManager?.replaceOriginalBitmap(mBitmapUri, bitmap)
                mCurFilterType = filterType
            } catch (e: Exception) {

            }
        }
    }

    fun setFilter(filterType: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            generateFilterType(filterType)
            invalidate()
        }
    }

    private fun createOriginalBitmap(): Bitmap {
        return BitmapUtils.createMaxSizeBitmapFromUri(mBitmapUri, mInitViewWidth, mInitViewHeight)
    }

    fun saveDrawDatas(isCameraPhoto: Boolean): RestoreBean { // 把所有的截图保存在secondBitmap上
        mInvoker!!.drawAllItemsOnSecondBitmap()
        // 把internal清空，因为有一些图片会绘制到internal层，不清空的话，会导致部分画笔重复
        mBitmapManager!!.erasInternalBitmap()
        // 裁剪图片，并把图片存到本地
        var clipRectOnBitmap = mDimensionManger!!.clipRect
        if (clipRectOnBitmap.width() == 0f) {
            clipRectOnBitmap = mDimensionManger!!.initBitmapRect
            mDimensionManger!!.clipRect = clipRectOnBitmap
        }
        val rotation =
            if (mDoneCropMatrixInfo == null) 0 else mDoneCropMatrixInfo!!.rotateDegree.toInt()
        val bitmapCropped = BitmapUtils.decodeRegionCrop(
            mBitmapManager!!.secondTempBitmap,
            clipRectOnBitmap, rotation
        )
        BitmapUtils.saveBitmapToSdcard(
            bitmapCropped,
            100,
            BitmapsManager.getSaveEditedUri(context, mBitmapUri)
        )
        // 创建一个restoreBean，用来保存数据，同时保存当前的 crop 状态
        val restoreBean = RestoreBean(
            mDimensionManger!!.matrix,
            mDimensionManger!!.clipRect, mDoneCropMatrixInfo, mNeedClipRect
        )
        val drawData = restoreBean.brushDatas
        // 保存所有的笔刷状态
        val iterator: Iterator<BaseBrush> = mInvoker!!.brushList.iterator()
        while (iterator.hasNext()) {
            val brush = iterator.next()
            val newData = BaseDrawData(brush.type)
            val data = brush.drawData
            newData.copy(data)
            drawData.add(newData)
        }
        // 保存所有的sticker状态
        val stickerIt: Iterator<BaseSticker> =
            mInvoker!!.stickerList.iterator()
        val stickerData = restoreBean.stickerDatas
        while (stickerIt.hasNext()) {
            val sticker = stickerIt.next()
            val newData = StickerData(sticker.type)
            newData.copy(sticker.stickerData)
            stickerData.add(newData)
        }
        // 保存当前的滤镜状态
        restoreBean.filterType = mCurFilterType
        //
        restoreBean.setIsCameraPhoto(isCameraPhoto)
        return restoreBean
    }

    override fun onDraw(canvas: Canvas) {
        if (mLayerManager != null && mInitResultOk) {
            canvas.save()
            mLayerManager!!.onDraw(this, canvas)
            canvas.clipRect(mDimensionManger!!.initViewRect)
            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mLayerManager != null && mLayerManager!!.onTouchEvent(event)
    }

    override fun requestInvalidate() {
        invalidate()
    }

    override fun requestInvalidate(rect: Rect) {
        invalidate(rect)
    }

    override fun onStrokeWidthChange(strokeLevel: Int) {
        if (mDimensionManger == null) {
            mStrokeLevel = strokeLevel
        } else {
            mDimensionManger!!.onStrokeWidthChange(strokeLevel)
        }
    }

    fun initBrushLayer() {
        if (mLayerManager == null) {
            return
        }
        if (mBrushLayer == null) {
            mBrushLayer = BrushLayer(
                this, mInvoker, mDimensionManger!!.matrix,
                mBitmapManager, mDimensionManger
            )
        }
        mLayerManager!!.insertLayer(mBrushLayer)
    }

    fun initBackgroundLayer() {
        if (mLayerManager == null) {
            return
        }
        if (mBackgroundBrushLayer == null) {
            mBackgroundBrushLayer = BackgroundBrushLayer(
                this, mDimensionManger!!.matrix, mInvoker,
                mBitmapManager, mDimensionManger
            )
        }
        mLayerManager!!.insertLayer(mBackgroundBrushLayer)
    }

    fun setNormalBrushMode(color: Int) {
        if (mLayerManager == null) {
            return
        }
        initBrushLayer()
        mBrushLayer!!.setNormalColorMode(color)
        mBrushLayer!!.updateClipRectStatus(mNeedClipRect, mDimensionManger!!.clipRect)
        mLayerManager!!.switchToLayer(mBrushLayer)
    }

    fun initLightLineLayer() {
        if (mLayerManager == null) {
            return
        }
        if (mLightLineLayer == null) {
            mLightLineLayer = LightLineBrushLayer(
                this,
                mInvoker,
                mDimensionManger!!.matrix,
                mBitmapManager,
                mDimensionManger
            )
            mLightLineLayer!!.updateClipRectStatus(mNeedClipRect, mDimensionManger!!.clipRect)
        }
        mLayerManager!!.insertLayer(mLightLineLayer)
    }

    fun setLightBrushMode() {
        if (mLayerManager == null) {
            return
        }
        initLightLineLayer()
        mLayerManager!!.switchToLayer(mLightLineLayer)
    }

    private fun initMosaicLayer() {
        if (mLayerManager == null) {
            return
        }
        if (mMosaicsLayer == null) {
            mMosaicsLayer = MosaicsLayer(
                this, mInvoker, mDimensionManger!!.matrix,
                mBitmapManager, mDimensionManger
            )
            mMosaicsLayer!!.updateClipRectStatus(mNeedClipRect, mDimensionManger!!.clipRect)
            val options = Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            val bitmap =
                BitmapFactory.decodeResource(resources, R.drawable.bg_brush_mosaics, options)
            mBitmapManager!!.saveBitmap(BitmapsManager.KEY_BRUSH_MOSAICS, bitmap)
        }
        mLayerManager!!.insertLayer(mMosaicsLayer)
    }

    fun setBlockMosaicsMode() {
        if (mLayerManager == null) {
            return
        }
        initMosaicLayer()
        mMosaicsLayer!!.setBlockMosaicsMode()
        mLayerManager!!.switchToLayer(mMosaicsLayer)
    }

    fun setBrushMosaicsMode(brushName: String?) {
        if (mLayerManager == null) {
            return
        }
        initMosaicLayer()
        mMosaicsLayer!!.setBrushMosaicsMode(brushName)
        mLayerManager!!.switchToLayer(mMosaicsLayer)
    }

    fun setStickerListMode(stickerUri: String?) {
        if (mLayerManager == null) {
            return
        }
        initBrushLayer()
        mBrushLayer!!.setStickerBrushMode(stickerUri)
        mBrushLayer!!.updateClipRectStatus(mNeedClipRect, mDimensionManger!!.clipRect)
        mLayerManager!!.switchToLayer(mBrushLayer)
    }

    fun setBackgroundBrushMode(backgroundUrl: String?) {
        if (mLayerManager == null) {
            return
        }
        initBackgroundLayer()
        mBackgroundBrushLayer!!.setBackgroundBrushMode(backgroundUrl)
        mLayerManager!!.switchToLayer(mBackgroundBrushLayer)
    }

    fun initStickerLayer() {
        if (mStickerLayer == null) {
            mStickerLayer = StickerLayer(
                this, mContext, mInvoker,
                mDimensionManger!!.matrix, mInitViewWidth,
                mInitViewHeight, mBitmapManager, mDimensionManger
            )
        }
        if (mStickerStateListener != null) {
            mStickerLayer!!.setStickerClickListener(mStickerStateListener)
        }
        mLayerManager!!.insertLayer(mStickerLayer)
    }

    fun initPhotoFrameLayer() {
        if (mPhotoFrameLayer == null) {
            mPhotoFrameLayer =
                PhotoFrameLayer(this, mDimensionManger!!.matrix, mInvoker, mBitmapManager)
        }
        mLayerManager!!.insertLayer(mPhotoFrameLayer)
    }

    fun addTextSticker(stickerText: String?, bitmapUri: String?, color: Int) {
        if (mLayerManager == null) {
            return
        }
        initStickerLayer()
        mLayerManager!!.switchToLayer(mStickerLayer)
        mStickerLayer!!.addTextSticker(stickerText, bitmapUri, color)
    }

    fun addBitmapStick(bitmapUri: String?) {
        if (mLayerManager == null) return
        initStickerLayer()
        mLayerManager!!.switchToLayer(mStickerLayer)
        mStickerLayer!!.addBitmapSticker(bitmapUri)
    }

    fun setPhotoFrame(photoUri: String?) {
        if (mLayerManager == null) {
            return
        }
        initPhotoFrameLayer()
        mPhotoFrameLayer!!.setPhotoFrameUri(photoUri)
        mLayerManager!!.switchToLayer(mPhotoFrameLayer)
    }

    fun clearPhotoFrame() {
        if (mPhotoFrameLayer == null) {
            return
        }
        mPhotoFrameLayer!!.clearPhotoFrame()
        mInvoker!!.removePhotoFrame()
    }

    fun setStickerStateChangeListener(listener: StickerStateListener?) {
        mStickerStateListener = listener
        if (mLayerManager == null) {
            return
        }
        initStickerLayer()
        mStickerLayer!!.setStickerClickListener(mStickerStateListener)
    }

    private fun initCropLayer() {
        if (mCropLayer == null) {
            mCropLayer = CropLayer(this, mInvoker, mDimensionManger!!.matrix, mDimensionManger)
            mLayerManager!!.insertLayer(mCropLayer)
            if (mDimensionManger!!.clipRect.width() == 0f) {
                mDimensionManger!!.clipRect = mDimensionManger!!.initBitmapRect
            }
            if (mDoneCropMatrixInfo == null) {
                mDoneCropMatrixInfo = MatrixInfo(mCropLayer!!.doneMatrixInfo)
            } else {
                mCropLayer!!.setCurRotateAngle(mDoneCropMatrixInfo!!.rotateDegree)
            }
        }
    }

    fun startCrop(listener: OnCropStateChangeListener?) {
        if (mLayerManager == null) {
            return
        }
        initCropLayer()
        mCropLayer!!.setOnCropStateChangeListener(listener)
        mLayerManager!!.switchToLayer(mCropLayer)
        mCropLayer!!.startCrop(mDoneCropMatrixInfo, mDimensionManger!!.clipRect)
        mNeedClipRect = false
        mLayerManager!!.updateClipRectStatus(false, mDimensionManger!!.clipRect)
    }

    fun restoreCrop() {
        mCropLayer!!.restoreCrop()
        mNeedClipRect = false
        mLayerManager!!.updateClipRectStatus(false, mDimensionManger!!.clipRect)
    }

    fun rotateCrop() {
        mCropLayer!!.rotateLayer()
    }

    fun switchToIdleLayer() {
        mLayerManager!!.switchToLayer(mIdlLayer)
    }

    fun doneCropMode() {
        if (mCropLayer == null) {
            return
        }
        mCropLayer!!.doneCrop { matrixInfo ->
            mLayerManager!!.switchToLayer(mIdlLayer)
            mDimensionManger!!.clipRect = mCropLayer!!.clipRectOnBitmap
            mDoneCropMatrixInfo!!.set(matrixInfo)
            mNeedClipRect = true
            mLayerManager!!.updateClipRectStatus(true, mDimensionManger!!.clipRect)
        }
    }

    fun cancelCropMode() {
        if (mCropLayer == null) {
            return
        }
        mCropLayer!!.cancelCrop(
            mDoneCropMatrixInfo,
            mDimensionManger!!.clipRect
        ) {
            mLayerManager!!.switchToLayer(mIdlLayer)
            mNeedClipRect = true
            mLayerManager!!.updateClipRectStatus(true, mDimensionManger!!.clipRect)
        }
    }

    val isPhotoEdited: Boolean
        get() = (mInvoker!!.hasBrush()
                || mInvoker!!.hasSticker()
                || mInvoker!!.hasPhotoFrame()
                || FakeData.ORIGINAL_FILTER_TYPE != mCurFilterType
                || mCropLayer != null)

    // 取消滤镜的时候，还原回原来的滤镜类型
    fun cancelSetFilter() {
        setFilter(mDoneFilterType)
    }

    // 滤镜完成的时候，更新mDoneFilter的类型
    fun doneSetFilter() {
        mDoneFilterType = mCurFilterType
    }

    override fun onDetachedFromWindow() {
        if (mBitmapManager != null) {
            mBitmapManager!!.removeAllBitmap()
            mBitmapManager = null
        }
        mInvoker!!.removeAllDraws()
        destroyDrawingCache()
        super.onDetachedFromWindow()
    }

    interface InitFinishCallback {
        fun onInitFinish()
    }

    companion object {
        const val VALIDATE_MOVE_RANGE = 4
    }

}