package com.example.carros.api.controller.security.jwt;


import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ServletUtil {

    //Retorna um status para o navegador
    public static void write(HttpServletResponse response, HttpStatus status, String json) throws IOException {

        response.setStatus(status.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);

        response.getWriter().write(json);
    }


    //Joga o json para uma string

    public static String getJson(String key, String value){

        JSONObject jsonObject = new JSONObject();

        jsonObject.append(key,value);

        return jsonObject.toString();

    }



}
