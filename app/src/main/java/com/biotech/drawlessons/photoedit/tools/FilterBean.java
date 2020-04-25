package com.biotech.drawlessons.photoedit.tools;

/**
 * Created by xintu on 2018/2/21.
 */

public class FilterBean extends BaseBean {
    private String desc;
    private Class filterClass;

    public FilterBean(int type, String desc, Class filterClass) {
        setType(type);
        this.desc = desc;
        this.filterClass = filterClass;
    }

    public Class getFilterClass() {
        return filterClass;
    }

    public void setFilterClass(Class filterClass) {
        this.filterClass = filterClass;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
