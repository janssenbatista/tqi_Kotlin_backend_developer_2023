package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class WebSecurity {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http.csrf { it.disable() }.authorizeHttpRequests {
            it.apply {
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
                requestMatchers(HttpMethod.GET, "/products/**")
                    .authenticated()
                requestMatchers(HttpMethod.PUT, "/products/**")
                    .hasAnyRole("EMPLOYEE")
                requestMatchers(HttpMethod.DELETE, "/products/**")
                    .hasAnyRole("EMPLOYEE")
                anyRequest().denyAll()
            }
        }.httpBasic(Customizer.withDefaults()).build()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(12)

}