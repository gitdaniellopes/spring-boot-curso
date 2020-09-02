package com.daniellopes.cursomc.service;

import com.daniellopes.cursomc.security.UserSs;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserService {

    //vai pegar o usuario logado com as suas authorities (o Usuario do Spring Security)
    //SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    //retorna o usuario logado so spring e fazemos um cast para o nosso UserSs
    public static UserSs authenticated() {
        try {
            return (UserSs) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }
}
