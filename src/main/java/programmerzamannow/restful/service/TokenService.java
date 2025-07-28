package programmerzamannow.restful.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import programmerzamannow.restful.entity.User;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {
    private final Algorithm algorithm = Algorithm.HMAC256("rahasia-jangan-mudah-ditebak");

    private final JWTVerifier verifier = JWT.require(algorithm).build();

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String create(User user){
        String token =  JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("name", user.getName())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(30)))
                .sign(algorithm);

        redisTemplate.opsForValue().set(token, user.getUsername(), Duration.ofDays(30));

        return token;
    }

    public User fromToken(String token){

        if (!redisTemplate.hasKey(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token Expired");
        }

        DecodedJWT jwt = verifier.verify(token);

        User user = new User();
        user.setUsername(jwt.getClaim("username").asString());
        user.setName(jwt.getClaim("name").asString());

        return user;
    }

}
