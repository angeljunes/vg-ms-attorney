package pe.edu.vallegrande.vg_ms_attorney.application.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.edu.vallegrande.vg_ms_attorney.domain.model.Attorney;
import pe.edu.vallegrande.vg_ms_attorney.domain.repository.AttorneyRepository;
import pe.edu.vallegrande.vg_ms_attorney.application.service.AttorneyService;
import pe.edu.vallegrande.vg_ms_attorney.application.util.AttorneyUtil;
import pe.edu.vallegrande.vg_ms_attorney.application.webClient.AuthServiceClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.modelmapper.ModelMapper;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.commons.validator.routines.UrlValidator;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.edu.vallegrande.vg_ms_attorney.application.util.AttorneyUtil.Activo;
import static pe.edu.vallegrande.vg_ms_attorney.application.util.AttorneyUtil.Inactivo;

@Service
public class AttorneyServiceImpl implements AttorneyService {

    // Lista de dominios permitidos para solicitudes salientes
    private static final List<String> ALLOWED_DOMAINS = List.of("https://api.permitido.com");

    private final AttorneyRepository attorneyRepository;
    private final AuthServiceClient authServiceClient;
    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public AttorneyServiceImpl(AttorneyRepository attorneyRepository, AuthServiceClient authServiceClient) {
        this.attorneyRepository = attorneyRepository;
        this.authServiceClient = authServiceClient;
    }

    // Método auxiliar para validar la URL
    private boolean isValidUrl(String url) {
        // Define los esquemas permitidos como un arreglo de strings
        String[] allowedSchemes = {"https"};
        UrlValidator urlValidator = new UrlValidator(allowedSchemes);  // Usa el constructor con esquemas permitidos
        return urlValidator.isValid(url);
    }


    private boolean isAllowedDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getScheme() + "://" + uri.getHost();
            return ALLOWED_DOMAINS.contains(domain);
        } catch (URISyntaxException e) {
            return false;
        }
    }

    public Mono<String> safeExternalRequest(String url) {
        if (!isValidUrl(url) || !isAllowedDomain(url)) {
            return Mono.error(new SecurityException("URL no permitida o inválida"));
        }

        // Realizar la solicitud HTTP usando authServiceClient
        return authServiceClient.getWebClient().get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("Error en la solicitud externa", e)));
    }



    // Método de bienvenida
    public Mono<String> getWelcomeMessage() {
        return Mono.just("Bienvenidos al microservicio de apoderados");
    }

    @Override
    public Flux<Attorney> listAllActive() {
        return attorneyRepository.findByStatus(Activo);
    }

    @Override
    public Flux<Attorney> listAllInactive() {
        return attorneyRepository.findByStatus(Inactivo);
    }

    @Override
    public Mono<Attorney> findByDni(String dni) {
        return attorneyRepository.findByDocumentNumber(dni);
    }

    @Override
    public Mono<Attorney> findByEmail(String email) {
        return attorneyRepository.findByEmail(email);
    }

    @Override
    public Mono<Attorney> findById(String id) {
        return attorneyRepository.findById(id);
    }

    @Override
    public Mono<Attorney> createAttorney(Attorney attorney) {
        Attorney newAttorney = modelMapper.map(attorney, Attorney.class);
        newAttorney.setRole("APODERADO");
        newAttorney.setStatus(Activo);
        newAttorney.setCreatedAt(LocalDateTime.now());
        newAttorney.setUpdatedAt(LocalDateTime.now());

        // Crear el usuario en Firebase Authentication
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(attorney.getEmail())
                .setPassword(attorney.getDocumentNumber()) // La contraseña se establece aquí
                .setDisplayName(attorney.getNames() + " " + attorney.getSurnames()); // Concatenar nombre y apellido

        return Mono.fromCallable(() -> FirebaseAuth.getInstance().createUser(request))
                .flatMap(userRecord -> {
                    // Asignar el UID al nuevo usuario
                    newAttorney.setUid(userRecord.getUid());
                    newAttorney.setPassword(attorney.getDocumentNumber()); // Almacenar la contraseña en el campo

                    // Asignar claims personalizados al usuario
                    try {
                        Map<String, Object> claims = new HashMap<>();
                        claims.put("role", "APODERADO");

                        // Asignar los claims de forma sincrónica
                        FirebaseAuth.getInstance().setCustomUserClaims(userRecord.getUid(), claims);
                    } catch (Exception e) {
                        System.err.println("Error setting custom claims: " + e.getMessage());
                        return Mono.error(e);
                    }

                    return attorneyRepository.save(newAttorney);
                })
                .onErrorResume(e -> {
                    // Manejar errores (por ejemplo, si falla la creación del usuario en Firebase)
                    System.err.println("Error creating user in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    @Override
    public Mono<Attorney> deleteAttorney(String id) {
        return attorneyRepository.findById(id)
                .flatMap(existingAttorney -> {
                    // Desactivar cuenta en Firebase
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingAttorney.getUid())
                                    .setDisabled(true)
                    )).then(Mono.defer(() -> {
                        existingAttorney.setStatus(Inactivo);
                        return attorneyRepository.save(existingAttorney);
                    }));
                });
    }

    @Override
    public Mono<Attorney> reactivateAttorney(String id) {
        return attorneyRepository.findById(id)
                .flatMap(existingAttorney -> {
                    // Reactivar cuenta en Firebase
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingAttorney.getUid())
                                    .setDisabled(false)
                    )).then(Mono.defer(() -> {
                        existingAttorney.setStatus(Activo);
                        return attorneyRepository.save(existingAttorney);
                    }));
                });
    }

    @Override
    public Mono<Attorney> updateAttorney(String id, Attorney attorney) {
        return attorneyRepository.findById(id)
                .flatMap(existingAttorney -> {
                    existingAttorney.setNames(attorney.getNames());
                    existingAttorney.setSurnames(attorney.getSurnames());
                    existingAttorney.setSex(attorney.getSex());
                    existingAttorney.setBirth_date(attorney.getBirth_date());
                    existingAttorney.setBaptism(attorney.getBaptism());
                    existingAttorney.setFirst_Communion(attorney.getFirst_Communion());
                    existingAttorney.setConfirmation(attorney.getConfirmation());
                    existingAttorney.setMarriage(attorney.getMarriage());
                    existingAttorney.setRelationship(attorney.getRelationship());
                    existingAttorney.setEmail(attorney.getEmail());
                    existingAttorney.setCellphone(attorney.getCellphone());
                    existingAttorney.setAddress(attorney.getAddress());
                    existingAttorney.setDocumentType(attorney.getDocumentType());
                    existingAttorney.setDocumentNumber(attorney.getDocumentNumber());
                    existingAttorney.setUpdatedAt(LocalDateTime.now());

                    // Actualizar displayName en Firebase si el nombre o apellido cambian
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                                    new UserRecord.UpdateRequest(existingAttorney.getUid())
                                            .setDisplayName(attorney.getNames() + " " + attorney.getSurnames())))
                            .then(attorneyRepository.save(existingAttorney));
                });
    }

    @Override
    public Mono<Attorney> updatePassword(String id, String newPassword) {
        return attorneyRepository.findById(id)
                .flatMap(existingAttorney -> {
                    // Actualizar la contraseña en Firebase Authentication
                    return Mono.fromCallable(() -> FirebaseAuth.getInstance().updateUser(
                            new UserRecord.UpdateRequest(existingAttorney.getUid())
                                    .setPassword(newPassword)
                    )).then(Mono.defer(() -> {
                        // Actualizar la contraseña en la base de datos
                        existingAttorney.setPassword(newPassword);
                        existingAttorney.setUpdatedAt(LocalDateTime.now());
                        return attorneyRepository.save(existingAttorney);
                    }));
                })
                .onErrorResume(e -> {
                    // Manejar errores (por ejemplo, si falla la actualización de la contraseña en Firebase)
                    System.err.println("Error updating password in Firebase: " + e.getMessage());
                    return Mono.error(e);
                });
    }

    // Método para validar el token y el rol en el servicio
    public Mono<Boolean> validateTokenAndRoles(String token, List<String> requiredRoles) {
        return authServiceClient.validateToken(token)
                .flatMap(validationResponse -> {
                    if (validationResponse.isValid() && requiredRoles.contains(validationResponse.getRole())) {
                        return Mono.just(true); // Token válido y rol correcto
                    }
                    return Mono.just(false); // Token no válido o rol no coincide
                });
    }
}
