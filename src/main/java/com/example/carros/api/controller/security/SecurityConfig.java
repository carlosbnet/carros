package com.example.carros.api.controller.security;

import com.example.carros.api.controller.security.jwt.JwtAuthenticationFilter;
import com.example.carros.api.controller.security.jwt.JwtAuthorizationFilter;
import com.example.carros.api.controller.security.jwt.handler.AccessDeniedHandler;
import com.example.carros.api.controller.security.jwt.handler.UnauthorizedHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private UnauthorizedHandler unauthorizedHandler;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        AuthenticationManager authManager = authenticationManager();

        http
       /*         .authorizeRequests() //Esta autorizando todas as requests, posso selecionar tipo, post,get, etc
                .anyRequest().authenticated() //tem acesso a todas as requisições porem se tiver autenticado
                .and()
                .httpBasic() //utiliza o form basico para autenticação, claro que tem o outro mais filé.
                .and()
                .csrf().disable(); //podemos desabilitar esta função para evitar tipo de ataques, em que roubam dados da sessão e fazem chamdas a sua api de forma autenticada
*/
                .authorizeRequests() //Esta autorizando todas as requests, posso selecionar tipo, post,get, etc
                 //tem acesso a todas as requisições porem se tiver autenticado
                .antMatchers(HttpMethod.GET,"/api/v1/login").permitAll()
                .anyRequest().authenticated()
                .and().csrf().disable() //podemos desabilitar esta função para evitar tipo de ataques, em que roubam dados da sessão e fazem chamdas a sua api de forma autenticada
                .addFilter(new JwtAuthenticationFilter(authManager)) //Faz o filtro para autenticar o usuario
                .addFilter(new JwtAuthorizationFilter(authManager, userDetailsService))//Faz um filtro para autorizar o usuario
                .exceptionHandling() //Entra nas exceptions
                .accessDeniedHandler(accessDeniedHandler) //Exception personalizada de acesso negado
                .authenticationEntryPoint(unauthorizedHandler) //Exception personalizada de sem autorização
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);





    }



    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
}
    }

