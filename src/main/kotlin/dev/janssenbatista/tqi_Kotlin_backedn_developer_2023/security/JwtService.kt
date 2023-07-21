package dev.janssenbatista.tqi_Kotlin_backedn_developer_2023.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.security.Key
import java.time.Instant
import java.util.*
import java.util.function.Function

@Service
class JwtService {

    @Value("\${application.security.jwt.secret-key}")
    lateinit var secretKey: String

    @Value("\${application.security.jwt.expiration}")
    lateinit var jwtExpiration: String

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun generateToken(extraClaims: Map<String, Any> = hashMapOf(),
                      userDetails: UserDetails): String {
        return buildToken(extraClaims, userDetails, jwtExpiration.toLong())
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractClaim(token, Claims::getExpiration).before(Date.from(Instant.now()))
    }

    private fun buildToken(extraClaims: Map<String, Any>, userDetails: UserDetails, expiration: Long): String {
        return Jwts.builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plusMillis(expiration)))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact()
    }


    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun getSignInKey(): Key {
        val keyBytes = Decoders.BASE64.decode(secretKey)
        return Keys.hmacShaKeyFor(keyBytes)
    }

}