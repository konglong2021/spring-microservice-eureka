package com.bronx.apigeteway.filter;

import com.bronx.apigeteway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private RouteValidator routeValidator;
    @Autowired
    private RestTemplate template;

    @Autowired
    private JwtUtil jwtUtil;
    public AuthenticationFilter(){
        super(Config.class);
    }


    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
        if (routeValidator.isSecured.test(exchange.getRequest()))
        {
            //check header
            if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                throw new RuntimeException("Missing Authorization header");
            }
            String authHeaders = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeaders != null && authHeaders.startsWith("Bearer ")){
                authHeaders= authHeaders.substring(7);
            }
            try{
                //rest call to validate
                template.getForObject("http://identity-service/validate?token="+authHeaders,String.class);

//                jwtUtil.validateToken(authHeaders);
            }catch (Exception e){
                System.out.println("invalid access");
                throw new RuntimeException("unAuthorized access to application");
            }
        }
            return chain.filter(exchange);
        });
    }

    public static class Config{

    }
}
