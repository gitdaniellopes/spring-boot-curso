package com.daniellopes.cursomc.resources.utils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UrlClass {

    //URLDecoder.decode(s, StandardCharsets.UTF_8);
    public static String decodeParam(String s) {
        return URLDecoder.decode(s, StandardCharsets.UTF_8);
    }


    public static List<Integer> decodeInList(String s) {
        String[] vet = s.split(",");
        List<Integer> list = new ArrayList<>();
        for (String value : vet) {
            list.add(Integer.parseInt(value));
        }
        return list;
        //alternativa uma linha
        //return Arrays.asList(s.split(",")).stream().map(x -> Integer.parseInt(x)).collect(Collectors.toList());
    }
}
