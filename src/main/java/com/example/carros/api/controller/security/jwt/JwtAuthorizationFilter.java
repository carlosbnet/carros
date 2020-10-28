package com.example.carros.api.controller.security.jwt;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.List;


//Classe usada para verificar autorizações do uzuario, no caso autorizar

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);

    private UserDetailsService userDetailsService;


    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.userDetailsService = userDetailsService;
    }



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        //Pega do cabeçalho
        String token =  request.getHeader("Authorization");

        //Caso nao informe a atutorização
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ")){
            chain.doFilter(request, response);
            return;
        }

        try {
            if (!JwtUtil.isTokenValid(token)){
                throw new AccessDeniedException("Acesso negado");
            }

            //Pega o login ou seja o usuario
            String login = JwtUtil.getLogin(token);

            //Carrega o usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(login);

            //Carrega as roles
            List<GrantedAuthority> authorities = JwtUtil.getRoles(token);

            //Autentica o usuario informando os dados acima
            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,null,authorities);

            //Salva o Authentication no contexto do Spring
            SecurityContextHolder.getContext().setAuthentication(auth);
            chain.doFilter(request,response);

        }catch (RuntimeException e){
            logger.error("Authentication error: " + e.getMessage(),e);
            throw e;
        }


















    }
}
