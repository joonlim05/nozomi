package com.nozomi.utilities;

public class BitmaskUtil {
    public static int createMask(int startSeq, int endSeq){

        if (startSeq < 0 || endSeq <= startSeq) {
            throw new IllegalArgumentException("Invalid station sequence: startSeq must be >= 0 and less than endSeq");
        }

        int mask = 0;
        for (int i=startSeq; i < endSeq; i++){
            mask |= (1 << i);
        }

        return mask;
    }
}
