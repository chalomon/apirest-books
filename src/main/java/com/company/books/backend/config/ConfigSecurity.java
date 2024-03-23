package com.company.books.backend.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.company.books.backend.filter.JwtReqFilter;

@Configuration
public class ConfigSecurity {

	@Autowired
	@Lazy
	private JwtReqFilter jwtReqFilter;
	
	
	
	@Bean
	public UserDetailsManager userDetailsManager(DataSource datasource) {
		return new JdbcUserDetailsManager(datasource);
	} 
	
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests( configure  -> {
			configure
				.requestMatchers(HttpMethod.GET, "/v1/libros").hasRole("Empleado")
				.requestMatchers(HttpMethod.GET, "/v1/libros/**").hasRole("Empleado")
				.requestMatchers(HttpMethod.POST, "/v1/libros").hasRole("Jefe")
				.requestMatchers(HttpMethod.PUT, "/v1/libros/**").hasRole("Jefe")
				.requestMatchers(HttpMethod.DELETE, "/v1/libros/**").hasRole("Jefe")
				.requestMatchers(HttpMethod.GET, "/v1/categorias").hasRole("Empleado")
				.requestMatchers(HttpMethod.GET, "/v1/categorias/**").hasRole("Empleado")
				.requestMatchers(HttpMethod.POST, "/v1/categorias").hasRole("Jefe")
				.requestMatchers(HttpMethod.PUT, "/v1/categorias/**").hasRole("Jefe")
				.requestMatchers(HttpMethod.DELETE, "/v1/categorias/**").hasRole("Jefe")
				.requestMatchers("/v1/authenticate", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll();		
		})
		.addFilterBefore(jwtReqFilter, UsernamePasswordAuthenticationFilter.class)
		.sessionManagement((session) -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			);
		
		http.httpBasic(Customizer.withDefaults());
		
		http.csrf( csrf -> csrf.disable() );
		
		return http.build();
		
	}
	
	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration 
			authenticationConfiguration) throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}
	
}



/*
 * Este método se utilizó para la creación de usuarios manualmente, sin acceder a la base de datos.
 * 
@Bean
public InMemoryUserDetailsManager userDetailsManager() {
	
	UserDetails chalo = User.builder()
			.username("chalo")
			.password("{noop}chalo123")
			.roles("Empleado", "Jefe")
			.build();
	
	UserDetails agustin = User.builder()
			.username("agustin")
			.password("{noop}agustin123")
			.roles("Empleado")
			.build();
	
	UserDetails edita = User.builder()
			.username("edita")
			.password("{noop}edita123")
			.roles("Empleado", "Jefe")
			.build();
	
	return new InMemoryUserDetailsManager(chalo, agustin, edita);
}
*/
