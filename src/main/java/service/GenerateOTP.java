package service;

import java.util.Random;

public class GenerateOTP {
    public static String getOTP() {
        Random random = new Random();
        //random.nextInt(10000): Generates a random integer between 0 and 10000.
        //String.format("%04d", ...) formats the generated integer as a four-digit string with leading zeros if necessary.
        return String.format("%04d",random.nextInt(10000));
    }
}
