package programmerzamannow.restful.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import programmerzamannow.restful.entity.User;
import programmerzamannow.restful.model.TokenResponse;

import java.time.Duration;
import java.util.Date;

@Service
public class TokenService {

    private static final String SECRET_KEY = "rahasia-jangan-mudah-ditebak";
    private static final String REFRESH_TOKEN_PREFIX = "refresh-token:";
    private static final Duration ACCESS_TOKEN_EXPIRATION = Duration.ofHours(8);
    private static final Duration REFRESH_TOKEN_EXPIRATION = Duration.ofDays(30);

    private final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
    private final JWTVerifier verifier = JWT.require(algorithm).build();

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String createAccessToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION.toMillis()))
                .sign(algorithm);
    }

    public String createRefreshToken(String username) {
        String token = JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION.toMillis()))
                .sign(algorithm);

        // Simpan refresh token ke Redis
        String redisKey = REFRESH_TOKEN_PREFIX + username;
        redisTemplate.opsForValue().set(redisKey, token, REFRESH_TOKEN_EXPIRATION);

        return token;
    }

    public User fromToken(String token) {
        try {
            DecodedJWT jwt = verifier.verify(token);

            User user = new User();
            user.setUsername(jwt.getSubject());
            user.setName(jwt.getClaim("name").asString());

            return user;
        } catch (JWTVerificationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
    }

    public TokenResponse refreshAccessToken(String refreshToken) {
        DecodedJWT decodedJWT = parseRefreshToken(refreshToken);
        String subject = decodedJWT.getSubject();

        String redisKey = REFRESH_TOKEN_PREFIX + subject;
        String savedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (savedRefreshToken == null || !savedRefreshToken.equals(refreshToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        String newAccessToken = createAccessToken(subject);
        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .build();
    }


    private DecodedJWT parseRefreshToken(String token) {
        try {
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
    }

    public void invalidateTokens(String username) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + username);
    }

}
