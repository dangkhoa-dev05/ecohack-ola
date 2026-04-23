package com.ecoquest.backend.config

import com.ecoquest.backend.service.JwtService
import com.ecoquest.backend.service.MockUserStore
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtService: JwtService,
    private val mockUserStore: MockUserStore
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        if (token.isBlank() || !jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response)
            return
        }

        val userId = jwtService.extractUserId(token)
        val user = mockUserStore.findById(userId)
        val currentAuthentication = SecurityContextHolder.getContext().authentication

        if (user != null && currentAuthentication == null) {
            val authentication = UsernamePasswordAuthenticationToken(
                user.id,
                null,
                emptyList()
            ).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }

            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }
}
