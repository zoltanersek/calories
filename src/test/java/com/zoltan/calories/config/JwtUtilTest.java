package com.zoltan.calories.config;

import com.zoltan.calories.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil("test");
    }

    @Test
    void test_isValid_invalidToken_returnsFalse() {
        boolean isValid = jwtUtil.isValid("invalid");

        assertThat(isValid).isFalse();
    }

    @Test
    void test_isValid_validToken_returnsTrue() {
        String token = generateValidTokenWithUsername("zoltan");

        boolean isValid = jwtUtil.isValid(token);

        assertThat(isValid).isTrue();
    }

    @Test
    void test_getUsername_usernameValid_returnsUsername() {
        String token = generateValidTokenWithUsername("zoltan");

        String username = jwtUtil.getUsername(token);

        assertThat(username).isEqualTo("zoltan");
    }

    @Test
    void test_generateToken_userValid_usernameInToken() {
        String token = jwtUtil.generateToken(User.builder().id(1L).username("zoltan").build());

        Claims body = Jwts.parser().setSigningKey("test").parseClaimsJws(token).getBody();
        assertThat(body.getSubject()).isEqualTo("1 zoltan");
    }

    private String generateValidTokenWithUsername(String username) {
        return Jwts.builder()
                .setSubject(String.format("%s %s", 1, username))
                .setIssuer("zoltan.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() +  24 * 60 * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, "test")
                .compact();
    }
}