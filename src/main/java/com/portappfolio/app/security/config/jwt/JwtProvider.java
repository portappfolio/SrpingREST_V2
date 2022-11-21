package com.portappfolio.app.security.config.jwt;

import com.portappfolio.app.appUser.AppUser;
import com.portappfolio.app.appUser.AppUserService;
import com.portappfolio.app.appUser.role.Role;
import com.portappfolio.app.appUser.role.RoleService;
import com.portappfolio.app.appUser.role.Roles;
import com.portappfolio.app.assignment.Assignment;
import com.portappfolio.app.models.JwtRequest;
import com.portappfolio.app.security.config.UserClassSecurity;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.time.login}")
    private int expirationLogin;

    @Value("${security.jwt.time.multilogin}")
    private int expirationMultiLogin;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private RoleService roleService;

    public JwtRequest generateToken(Authentication authentication){

        //Authentication provider
        UserClassSecurity userClassSecurity = (UserClassSecurity) authentication.getPrincipal();

        //User Info
        Optional<AppUser> appUser = appUserService.getByEmail(userClassSecurity.getUsername());

        // The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        //Current timestamp in millis
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //  sign JWT with our ApiKey secret
        //byte[] apiSecretBytes = DatatypeConverter.parseBase64Binary(secret);

        //Expiration Token
        long expMillis = nowMillis + expirationLogin;
        Date exp = new Date(expMillis);


        //  set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .addClaims(Map.of("authorities",appUser.get().getRoles()
                ))
                .setSubject(appUser.get().getEmail())
                .setIssuedAt(now)
                .setIssuer(issuer)
                .setExpiration(exp)
                .signWith(signatureAlgorithm,secret.getBytes());

        JwtRequest jwtRequest = new JwtRequest(
                builder.compact()
        );

        return jwtRequest;

    }

    public String updateToken(AppUser appUser){

        // The JWT signature algorithm used to sign the token
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

        //Current timestamp in millis
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        //Expiration Token
        long expMillis = nowMillis + expirationMultiLogin;
        Date exp = new Date(expMillis);

        //  set the JWT Claims
        JwtBuilder builder = Jwts.builder()
                .addClaims(Map.of("authorities",appUser.getRoles()
                ))
                .setSubject(appUser.getEmail())
                .setIssuedAt(now)
                .setIssuer(issuer)
                //.setId(appUser.get().getId().toString())
                .setExpiration(exp)
                .signWith(signatureAlgorithm,secret.getBytes());

        return builder.compact();
    }

    public String getEmailFromToken(String token){
        return Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token).getBody().getSubject();
    }

    public Boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(secret.getBytes()).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException malformedJwtException){

        } catch (UnsupportedJwtException unsupportedJwtException){

        } catch (ExpiredJwtException expiredJwtException){

        } catch (IllegalArgumentException illegalArgumentException){

        } catch (SignatureException signatureException){}
        return false;
    }

}
