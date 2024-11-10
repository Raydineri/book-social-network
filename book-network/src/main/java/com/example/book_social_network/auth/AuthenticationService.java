package com.example.book_social_network.auth;

import com.example.book_social_network.email.EmailService;
import com.example.book_social_network.email.EmailTemplateName;
import com.example.book_social_network.role.RoleRepository;
import com.example.book_social_network.user.Token;
import com.example.book_social_network.user.TokenRepository;
import com.example.book_social_network.user.User;
import com.example.book_social_network.user.UserRepository;
import com.example.book_social_network.email.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final PasswordEncoder passwordEncoder;
    private RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    private String activationUrl;

    public void register(RegistrationRequest request) {
        var userRole = roleRepository.findByName("USER")
        // todo - better exception handling
        .orElseThrow(() -> new RuntimeException("Role User was not initialized"));

        var user = User.builder()
                .firstname(request.getFirstname())
                .Lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        try {
            sendValidationEmail(user);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationToken(User user){
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .user(user) // the foreign key to the user table
                .build();
        tokenRepository.save(token);

        return generatedToken;
    }

    private String generateActivationCode(int length){
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();

        SecureRandom secureRandom = new SecureRandom();
        //SecureRandom, a Java class used for generating cryptographically strong random numbers

        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length()); // length = 9
            // nextInt Generate a random integer between 0 (inclusive) and characters.length (exclusive)
            // this line generates a random number between 0 and the length of the characters string
            codeBuilder.append(characters.charAt(randomIndex));
        }

        return codeBuilder.toString();
    }


}
