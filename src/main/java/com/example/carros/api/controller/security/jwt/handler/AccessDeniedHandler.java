package com.example.carros.api.controller.security.jwt.handler;


import com.example.carros.api.controller.security.jwt.ServletUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Classe chamada quando acontece o erro 403 - FORBIDDEN
 */

@Component //para poder fazer o autowide
public class AccessDeniedHandler implements org.springframework.security.web.access.AccessDeniedHandler {


    //Personaliza nossa exception
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {

        //Pega o contexto da autorização
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        //
        if(auth != null){

            String json = ServletUtil.getJson("error", "Acesso negado.");
            ServletUtil.write(httpServletResponse, HttpStatus.FORBIDDEN, json);

        }



    }
}
