package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class WebSecurity(@Lazy private val jwtAuthFilter: JwtAuthFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf { it.disable() }.authorizeHttpRequests {
            it.apply {
                requestMatchers(HttpMethod.POST, "/login")
                    .permitAll()
                requestMatchers(HttpMethod.POST, "/employees")
                    .hasRole("ADMIN")
                requestMatchers(HttpMethod.GET, "/employees/**")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                requestMatchers(HttpMethod.PUT, "/employees/**")
                    .hasAnyRole("ADMIN")
                requestMatchers(HttpMethod.DELETE, "/employees/**")
                    .hasAnyRole("ADMIN")
                requestMatchers(HttpMethod.POST, "/customers")
                    .permitAll()
                requestMatchers(HttpMethod.GET, "/customers/**")
                    .authenticated()
                requestMatchers(HttpMethod.PUT, "/customers/**")
                    .hasAnyRole("ADMIN", "CUSTOMER")
                requestMatchers(HttpMethod.DELETE, "/customers/**")
                    .hasAnyRole("ADMIN", "CUSTOMER")
                requestMatchers(HttpMethod.POST, "/categories")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                requestMatchers(HttpMethod.GET, "/categories/**")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                requestMatchers(HttpMethod.PUT, "/categories/**")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                requestMatchers(HttpMethod.DELETE, "/categories/**")
                    .hasAnyRole("ADMIN", "EMPLOYEE")
                requestMatchers(HttpMethod.POST, "/products")
                    .hasAnyRole("EMPLOYEE")
                requestMatchers(HttpMethod.GET, "/products", "/products/**")
                    .authenticated()
                requestMatchers(HttpMethod.PUT, "/products/**")
                    .hasRole("EMPLOYEE")
                requestMatchers(HttpMethod.DELETE, "/products/**")
                    .hasRole("EMPLOYEE")
                requestMatchers("/shopping-carts", "/shopping-carts/**")
                    .hasRole("CUSTOMER")
                anyRequest().denyAll()
            }
        }
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)
            .httpBasic (Customizer.withDefaults())
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .build()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(12)

}