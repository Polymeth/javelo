package ch.epfl.javelo;

public final class Q28_4 {
    private Q28_4() {}

    public static int ofInt(int i) {
        System.out.println(Integer.toBinaryString(i << 4));
        return i << 4;
    }

    public static double asDouble(int q28_4){
        return 0.00;
    }

    public static float asFloat(int q28_4){
        int temp = ofInt(q28_4);
        return Math.scalb(temp, -);
    }

}
