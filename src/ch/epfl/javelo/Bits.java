package ch.epfl.javelo;

public final class Bits {
    private Bits() {}

    public static int extractSigned(int value, int start, int length){

        Preconditions.checkArgument(length < 32 && length >= 0 && value <= 32 && start >= 0 && start < 32);
        int temp = value << 32 - start - length;
        System.out.println("first shift" + Integer.toBinaryString(temp));
        return temp >> 32 - length;
    }

   public static int extractUnsigned(int value, int start, int length){
        Preconditions.checkArgument(value != 32 && length < 32 && length >= 0 && start >= 0 && start < 32);
        int temp = value << 32 - start - length;
        return temp >>> 32-length;
   }
}
