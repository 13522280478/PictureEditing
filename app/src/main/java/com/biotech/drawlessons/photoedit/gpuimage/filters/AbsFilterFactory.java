package com.biotech.drawlessons.photoedit.gpuimage.filters;

/**
 * Created by xintu on 2018/2/21.
 */

public abstract class AbsFilterFactory {
    public abstract <T extends GPUImageFilter> T createFilter(Class<T> clazz);
}
