package com.yaser.ewallet.utils;

import java.util.Base64;

/**
 * It encodes the username and password into base64 format and creates an authorization header.
 *
 * @param username = user
 * @param password = password
 * @return authorization header result ="Basic dXNlcjpwYXNzd29yZA=="
 */
public class HelperAuthorizationKey {

    public static String createAuthorizationHeader(String username, String password) {
        String auth = username + ":" + password;
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());
        String authHeader = "Basic " + new String(encodedAuth);
        return authHeader;
    }
}
