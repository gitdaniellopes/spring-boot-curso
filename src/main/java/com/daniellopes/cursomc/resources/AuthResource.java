package com.daniellopes.cursomc.resources;

import com.daniellopes.cursomc.dto.EmailDTO;
import com.daniellopes.cursomc.security.JWTUtil;
import com.daniellopes.cursomc.security.UserSs;
import com.daniellopes.cursomc.service.AuthService;
import com.daniellopes.cursomc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthResource {

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/refresh_token")
    public ResponseEntity<Void> refreshToken(HttpServletResponse response) {
        UserSs user = UserService.authenticated();
        assert user != null;
        String token = jwtUtil.generatedToken(user.getUsername());
        response.addHeader("Authorization", "Bearer " + token);
        response.addHeader("access-control-expose-headers", "Authorization");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> forgot(@RequestBody @Valid EmailDTO emailDTO) {
        String newPassword = authService.sendNewPassword(emailDTO.getEmail());
        return ResponseEntity.ok().body(newPassword);
    }
}
