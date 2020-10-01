/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychessmate.network;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

/**
 *
 * 
 */
public class MD5
{
    //private static final Logger LOG = Logger.getLogger(MD5.class);

    public static String encrypt(String str)
    {
        MessageDigest message;

        try
        {
            message = MessageDigest.getInstance("MD5");
            message.update(str.getBytes(), 0, str.length());
            return new BigInteger(1, message.digest()).toString(16);
        }
        catch (NoSuchAlgorithmException ex)
        {
            //LOG.error("NoSuchAlgorithmException: " + ex);
            return null;
        }
    }
}
