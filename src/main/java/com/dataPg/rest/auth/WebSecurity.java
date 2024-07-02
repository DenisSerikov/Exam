package com.dataPg.rest.auth;

import com.dataPg.rest.auth.filter.JWTAuthenticationFilter;
import com.dataPg.rest.auth.filter.JWTAuthorizationFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpServlet;
import java.util.EnumSet;

@WebListener
public class WebSecurityConfig implements javax.servlet.ServletContextListener {

    @Override
    public void contextInitialized(ServletContext servletContext) {
        // Register authentication and authorization filters
        FilterRegistration.Dynamic authFilter = servletContext.addFilter("JWTAuthenticationFilter", JWTAuthenticationFilter.class);
        authFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        FilterRegistration.Dynamic authzFilter = servletContext.addFilter("JWTAuthorizationFilter", JWTAuthorizationFilter.class);
        authzFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        // Register CORS filter
        FilterRegistration.Dynamic corsFilter = servletContext.addFilter("CORSFilter", new CORSFilter());
        corsFilter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/*");

        // Register any servlets, if needed
        ServletRegistration.Dynamic servlet = servletContext.addServlet("exampleServlet", new HttpServlet() {
        });
        servlet.addMapping("/example");
    }

    @Override
    public void contextDestroyed(ServletContext servletContext) {
        // Cleanup resources
    }
}
