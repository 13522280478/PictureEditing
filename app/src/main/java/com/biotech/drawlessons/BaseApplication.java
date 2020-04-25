package com.biotech.drawlessons;

import android.app.Application;

/**
 * @author TuXin
 * @date 2020/4/24 9:18 PM.
 * <p>
 * Email : tuxin@pupupula.com
 */
public class BaseApplication extends Application {
    private static BaseApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static BaseApplication getInstance() {
        return instance;
    }
}
