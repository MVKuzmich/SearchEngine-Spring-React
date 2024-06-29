package com.kuzmich.searchengineapp.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class HashGenerator {

    public static String generateHash(String input) {
        return DigestUtils.sha256Hex(input);
    } 
    
}
