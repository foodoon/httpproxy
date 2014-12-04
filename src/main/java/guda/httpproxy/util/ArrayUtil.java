package guda.httpproxy.util;


public class ArrayUtil {
    private ArrayUtil() {
    }

    public static boolean equals(boolean[] a, boolean[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(byte[] a, byte[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(short[] a, short[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(int[] a, int[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(float[] a, float[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(double[] a, double[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(long[] a, long[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }

    public static boolean equals(char[] a, char[] b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0, size = a.length; i < size; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }

        return true;
    }
}
