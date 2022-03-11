package com.example.laborator5.socialnetwork.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class has a static method which encrypts a string given as parameter using a specific algorithm
 */
public class MD5 {

    /**
     * The role of this method is to encrypt the input given as parameter using MD5 algorithm
     *
     * @param input - the input we want to encrypt
     * @return the encrypted input using MD5 algorithm
     */
    public static String getMD5(String input) {

        try {

            // This call provides inside md variable, the algorithm we want to use to encrypt the string
            // If we want to use another algortithm we just change the parameter of getInstance
            MessageDigest md = MessageDigest.getInstance("MD5");

            // This variable contains the result of the encryption algorithm
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert the message into hex value
            StringBuilder hashtext = new StringBuilder(no.toString(16));

            while (hashtext.length() < 32)

                hashtext.insert(0, "0");

            return hashtext.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException(e);
        }
    }
}
