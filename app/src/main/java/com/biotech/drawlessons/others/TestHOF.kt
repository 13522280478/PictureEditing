package com.biotech.drawlessons.others

/**
 * @author TuXin
 * @date 2020/4/23 12:04 AM.
 *
 * Email : tuxin@pupupula.com
 */
class TestHOF {
    fun CharSequence.sumBy(selector:(Char) -> Int):Int{
        var res = 0
        for (element in this) {
            res += selector(element)
        }
        return res
    }
}
