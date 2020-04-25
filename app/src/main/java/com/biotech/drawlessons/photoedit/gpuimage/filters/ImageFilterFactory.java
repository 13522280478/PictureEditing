package com.biotech.drawlessons.photoedit.gpuimage.filters;

/**
 * Created by xintu on 2018/2/21.
 */

public class ImageFilterFactory extends AbsFilterFactory {
    @Override
    public <T extends GPUImageFilter> T createFilter(Class<T> clazz) {
        GPUImageFilter filter = null;
        try {
            filter = (GPUImageFilter) Class.forName(clazz.getName()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) filter;
    }

    public IFImageFilter createFilter(String filterType) {
        IFImageFilter filter = null;
        switch (filterType) {
            case "IFAmaro":
                filter = new IFAmaroFilter();
                break;
            case "IFEarlybird":
                filter = new IFEarlybirdFilter();
                break;

            case "IFHudson":
                filter = new IFHudsonFilter();
                break;

            case "IFLomo":
                filter = new IFLomoFilter();
                break;

            case "IFNashville":
                filter = new IFNashvilleFilter();
                break;

            case "IFSierra":
                filter = new IFSierraFilter();
                break;

            case "IFSutro":
                filter = new IFSutroFilter();
                break;
        }

        return filter;
    }
}
