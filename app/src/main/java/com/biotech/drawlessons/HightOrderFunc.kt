package com.biotech.drawlessons

/**
 * @author TuXin
 * @date 2020/4/22 11:54 PM.
 *
 * Email : tuxin@pupupula.com
 */
fun <T, R> Collection<T>.flod(initial: R, combine: (acc: R, nextElement: T) -> R): R {
    var accumulator: R = initial
    for (element: T in this) {
        accumulator = combine(accumulator, element)
    }
    return accumulator
}