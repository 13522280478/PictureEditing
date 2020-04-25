package com.biotech.drawlessons.photoedit.utils;

/**
 * Created by dajunhu on 4/17/2015.
 */
public class Tuples {


    public static abstract class BaseTuple {
        public int mValueCount;
    }


    public static class One<T> extends BaseTuple {
        public T mValue;

        public One(T value) {
            mValue = value;
            mValueCount = 1;
        }
    }


    public static class Two<M, N> extends BaseTuple {
        public M mValue1;
        public N mValue2;

        public Two(M value1, N value2) {
            mValue1 = value1;
            mValue2 = value2;
            mValueCount = 2;
        }


        public Two<M, N> copy() {
            return new Two<>(mValue1, mValue2);
        }
    }


    public static class Three<O, P, Q> extends BaseTuple {
        public O mValue1;
        public P mValue2;
        public Q mValue3;

        public Three(O value1, P value2, Q value3) {
            mValue1 = value1;
            mValue2 = value2;
            mValue3 = value3;
            mValueCount = 3;
        }
    }

    public static class Four<R, S, T, U> extends BaseTuple {
        public R mValue1;
        public S mValue2;
        public T mValue3;
        public U mValue4;

        public Four(R value1, S value2, T value3, U value4) {
            mValue1 = value1;
            mValue2 = value2;
            mValue3 = value3;
            mValue4 = value4;
            mValueCount = 4;
        }
    }
}
