package org.example.singleton.gaeun.practice;

public class Twin {
    private static Twin twin1;
    private static Twin twin2;

    private Twin() {

    }
    public static Twin getTwin(int instanceNumber) {
        if(instanceNumber == 0) {
            if(twin1 == null) {
                twin1 = new Twin();
            }
            return twin1;
        }
        if(instanceNumber == 1) {
            if(twin2 == null) {
                twin2 = new Twin();
            }
            return twin2;
        }
        throw new IllegalArgumentException();
    }

}
