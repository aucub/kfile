package com.example.kfile.util;

import java.util.Comparator;

/**
 * 类 windows 文件排序算法
 */
public class NaturalOrderComparator implements Comparator<String> {

    private static final char ZERO_CHAR = '0';

    private static boolean isDigit(char c) {
        return Character.isDigit(c) || c == '.' || c == ',';
    }

    private static char charAt(String s, int i) {
        return i >= s.length() ? 0 : s.charAt(i);
    }

    private static int compareEqual(String a, String b, int nza, int nzb) {
        if (nza - nzb != 0) {
            return nza - nzb;
        }

        if (a.length() == b.length()) {
            return a.compareTo(b);
        }

        return a.length() - b.length();
    }

    private int compareRight(String a, String b) {
        int bias = 0, ia = 0, ib = 0;

        for (; ; ia++, ib++) {
            char ca = charAt(a, ia);
            char cb = charAt(b, ib);

            if (!isDigit(ca) && !isDigit(cb)) {
                return bias;
            }
            if (!isDigit(ca)) {
                return -1;
            }
            if (!isDigit(cb)) {
                return +1;
            }
            if (ca == 0 && cb == 0) {
                return bias;
            }

            if (bias == 0) {
                if (ca < cb) {
                    bias = -1;
                } else if (ca > cb) {
                    bias = 1;
                }
            }
        }
    }

    @Override
    public int compare(String a, String b) {
        int ia = 0, ib = 0;
        int nza, nzb;
        char ca, cb;

        while (true) {
            nza = nzb = 0;

            ca = charAt(a, ia);
            cb = charAt(b, ib);

            while (Character.isSpaceChar(ca) || ca == ZERO_CHAR) {
                if (ca == ZERO_CHAR) {
                    nza++;
                } else {
                    nza = 0;
                }

                ca = charAt(a, ++ia);
            }

            while (Character.isSpaceChar(cb) || cb == '0') {
                if (cb == '0') {
                    nzb++;
                } else {
                    nzb = 0;
                }

                cb = charAt(b, ++ib);
            }

            if (Character.isDigit(ca) && Character.isDigit(cb)) {
                int bias = compareRight(a.substring(ia), b.substring(ib));
                if (bias != 0) {
                    return bias;
                }
            }

            if (ca == 0 && cb == 0) {
                return compareEqual(a, b, nza, nzb);
            }
            if (ca < cb) {
                return -1;
            }
            if (ca > cb) {
                return +1;
            }

            ++ia;
            ++ib;
        }
    }

}