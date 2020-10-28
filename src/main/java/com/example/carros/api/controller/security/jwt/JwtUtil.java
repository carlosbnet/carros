package com.example.carros.api.controller.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import static java.util.Objects.nonNull;
import static java.util.Objects.isNull;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class JwtUtil {


    private static final String JWT_SECRET = "6w9z$C&F)J@NcRfUjXnZr4u7x!A%D*G-KaPdSgVkYp3s5v8y/B?E(H+MbQeThWmZ";


        //Cria um metodo usado para fazer o parse para o JWT
    public static Claims getClams(String token){

        byte[] signinkey = JwtUtil.JWT_SECRET.getBytes();

        token = token.replace("Bearer", "");

 //Faz o parse para o JWT, para ficar no seu padrao
        return Jwts.parser()
                .setSigningKey(signinkey)
                .parseClaimsJws(token).getBody();

    }

    //Retorna o login do token
    public static String getLogin(String token){

        Claims claims = getClams(token);

        if (!claims.isEmpty()){
            return claims.getSubject();
        }

        return null;

    }

    //Retorna as roles do usuario de acordo com seu token
    public static List<GrantedAuthority>getRoles(String tokem){

        Claims claims = getClams(tokem);

        if (isNull(claims)){
            return null;
        }

        //Retorna todas a roles do usuario informado o token
        return ((List<?>) claims.get("rol")).stream().map(authority -> new SimpleGrantedAuthority((String) authority)).collect(Collectors.toList());


    }


    //Verifica se o token é valid pela a data de expiração
    public static boolean isTokenValid(String token){

        Claims claims = getClams(token);

        if(nonNull(claims)){

            String login = claims.getSubject();
            Date expiration = claims.getExpiration();
            Date now = new Date(System.currentTimeMillis());

            //faz uma pequena validação para saber se tem algum dado null

            boolean resp = login != null && expiration != null && now.before(expiration);

            return resp;

        }

        return false;


    }

    //Metodo para criar token

    public static String createToken(UserDetails user){

        //Pega as roles
        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        byte[] sigingKey = JwtUtil.JWT_SECRET.getBytes();

        //Calcula a proxima data para 10 dias apos o atual
        int days = 10;
        long time = days * 24 /*Horas*/ * 60 /*min*/ * 60 /*seg*/ * 1000 /*millis*/;
        Date expiration = new Date(System.currentTimeMillis()+time);

        //Envia toda a informação formatada acima para o JWTS para gerar um novo token
        //Chama nossa api da JWT

        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(sigingKey), SignatureAlgorithm.HS512) //Recebe a chave de encriptação e como foi encriptado
                .setSubject(user.getUsername()) //Informar o nome do usuario
                .setExpiration(expiration)
                .claim("rol", roles)
                .compact();

    }




}
