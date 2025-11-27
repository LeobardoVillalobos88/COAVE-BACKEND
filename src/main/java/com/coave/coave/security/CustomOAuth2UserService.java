package com.coave.coave.security;

import com.coave.coave.models.OAuth2UserInfo;
import com.coave.coave.models.Usuario;
import com.coave.coave.models.enums.Rol;
import com.coave.coave.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = extractUserInfo(registrationId, oauth2User.getAttributes());

        processOAuth2User(userInfo, registrationId);

        return oauth2User;
    }

    private OAuth2UserInfo extractUserInfo(String registrationId, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return OAuth2UserInfo.builder()
                    .id((String) attributes.get("sub"))
                    .name((String) attributes.get("name"))
                    .email((String) attributes.get("email"))
                    .imageUrl((String) attributes.get("picture"))
                    .build();
        }
        throw new OAuth2AuthenticationException("Provider no soportado: " + registrationId);
    }

    private void processOAuth2User(OAuth2UserInfo userInfo, String provider) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(userInfo.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Actualizar info si ya existe
            usuario.setNombre(userInfo.getName());
            usuario.setAvatarUrl(userInfo.getImageUrl());
            usuario.setFechaActualizacion(LocalDateTime.now());
            usuarioRepository.save(usuario);
        } else {
            // Crear nuevo usuario OAuth
            Usuario nuevoUsuario = Usuario.builder()
                    .nombre(userInfo.getName())
                    .email(userInfo.getEmail())
                    .provider(provider)
                    .providerId(userInfo.getId())
                    .avatarUrl(userInfo.getImageUrl())
                    .rol(Rol.CONDUCTOR)  // Rol por defecto para usuarios OAuth
                    .activo(true)
                    .fechaCreacion(LocalDateTime.now())
                    .fechaActualizacion(LocalDateTime.now())
                    .build();
            usuarioRepository.save(nuevoUsuario);
        }
    }
}