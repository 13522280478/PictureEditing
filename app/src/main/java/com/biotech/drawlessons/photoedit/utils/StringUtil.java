package com.biotech.drawlessons.photoedit.utils;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author TuXin
 * @date 2020/4/24 8:56 PM.
 * <p>
 * Email : tuxin@pupupula.com
 */
public class StringUtil {
    public static final char charBegin = '[';
    public static final char charEnd = ']';
    public static  int getAdjustTextLength(char c){
        return c < 128 ? 1 : 2;
    }
    public static int getAdjustTextLength(String c){
        if(c== null||c.length()==0) {
            return 0;
        }
        final  int length =c.length();
        int count = 0;
        for (int i=0;i<length;i++ ){
            count+= getAdjustTextLength(c.charAt(i));
        }
        return count;
    }
    /**
     * @return int adjust text size
     */
    public static int getEmojiAdjustSize(CharSequence text) {
        int len = 0;
        int front = -1;
        int tear = -1;
        String matched = "";
        int drawableEmoji;
        int textLength = text.length();
        for (int i = 0; i < textLength; i++) {
            char c = text.charAt(i);
            len += getAdjustTextLength(c);
            if (c == charBegin && matched.length() == 0) {
                front = i;
                matched += charBegin;
            } else if (c == charBegin && matched.length() > 0) {
                matched = "";
                matched += charBegin;
                front = i;
            } else if (c == charEnd && matched.length() > 0) {
                tear = i;
                matched += c;
                try {
                    drawableEmoji = getMoreDrawAble(matched);
                    if (drawableEmoji > 0 && front < tear) {
                        front = -1;
                        tear = -2;
                        len-= getAdjustTextLength(matched);
                        // 两个字符，计算时x2故为4
                        len+=4;
                        matched = "";
                    }
                }catch (Exception e){
                }

            } else if (matched.length() > 0) {
                matched += c;
            }
        }
        return len;
    }

    private static Map<String, Integer> toShowDrawableMap = new LinkedHashMap<String, Integer>();

    public static int getMoreDrawAble(String code) {
        Object o=toShowDrawableMap.get(code);
        return o==null?-1:((Integer)o).intValue();
    }
}
