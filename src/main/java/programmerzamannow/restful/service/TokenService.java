package programmerzamannow.restful.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;
import programmerzamannow.restful.entity.User;

import java.time.Duration;
import java.time.Instant;

@Service
public class TokenService {
    private final Algorithm algorithm = Algorithm.HMAC256("rahasia-jangan-mudah-ditebak");

    private final JWTVerifier verifier = JWT.require(algorithm).build();

    public String create(User user){
        return JWT.create()
                .withClaim("username", user.getUsername())
                .withClaim("name", user.getName())
                .withExpiresAt(Instant.now().plus(Duration.ofDays(30)))
                .sign(algorithm);
    }

    public User fromToken(String token){
        DecodedJWT jwt = verifier.verify(token);

        User user = new User();
        user.setUsername(jwt.getClaim("username").asString());
        user.setName(jwt.getClaim("name").asString());

        return user;
    }



}
