package com.WillKopa.CardIdentifier.converter;

import com.WillKopa.CardIdentifier.model.User;
import com.WillKopa.CardIdentifier.repo.UserRepo;
import lombok.AllArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GoogleJwtAuthConverter implements Converter<Jwt, UsernamePasswordAuthenticationToken> {
    private final UserRepo userRepo;

    @Override
    public UsernamePasswordAuthenticationToken convert(Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        String name = jwt.getClaimAsString("name");

        userRepo.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserName(name != null ? name : email.split("@")[0]); // Username will be the same as Google account or the email address before the @

            return userRepo.save(newUser);
        });

        List<GrantedAuthority> authorityList = List.of(new SimpleGrantedAuthority("ROLE_REGISTERED_USER"));

        return new UsernamePasswordAuthenticationToken(jwt, null, authorityList);
    }
}
