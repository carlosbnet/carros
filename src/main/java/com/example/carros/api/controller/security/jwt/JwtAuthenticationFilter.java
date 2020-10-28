package com.example.carros.api.controller.security.jwt;


import com.example.carros.domain.User;
import com.example.carros.domain.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


//Herdamos do propio filtro do spring, um filtro por usuario e senha
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    //Criamos uma constante para armazenar o path do login, nossa url
    public static final String AUTH_URL = "/api/v1/login";

    private final AuthenticationManager authenticationManager;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;

        //diz ao nosso spring que esta url é de login, nosso endpoint de login
        setFilterProcessesUrl(AUTH_URL);
    }

    //Tenta fazer a autenticação e verifica se teve sucesso ou não

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        try {
            //Mapea nosso modelo para um objeto capturando senha e login, usamos para isso o ObjetcMapper, informamos de onde vem os dados e para onde queremos mapear ele.
            JwtLoginInput login = new ObjectMapper().readValue(request.getInputStream(), JwtLoginInput.class);
            String username = login.getUsername();
            String password = login.getPassword();

            //Verifica se as informações de username e senha não estão vazias se tiver ele lança uma exceção
            if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
                throw new BadCredentialsException("Invalid Username/password");
            }

            //Caso esteje tudo correto Criamos um UsernamePasswordAuthentication, passando como paramentros senha e username
            Authentication auth = new UsernamePasswordAuthenticationToken(username, password);

            //aqui auteticamos com o usuario e depois retornamos ele

            return authenticationManager.authenticate(auth);

        } catch (IOException e) {
            throw new BadCredentialsException(e.getMessage());
        }

    }

    //Caso nossa autenticação seje um sucesso vamos gerar e retorna  o tokem para o usuario
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {

        //resulpera nosso usuario para gerar o tokem para ele
        User user = (User) authResult.getPrincipal();

        String jwtToken = JwtUtil.createToken(user);

        String json = UserDTO.create(user,jwtToken).toJson();
        ServletUtil.write(response, HttpStatus.OK, json);


    }

    //Usando quando a pessoa nao tiver acesso, ou der algum problema
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        String json = ServletUtil.getJson("error", "Login Incorreto");
        ServletUtil.write(response, HttpStatus.UNAUTHORIZED, json);

    }
}

