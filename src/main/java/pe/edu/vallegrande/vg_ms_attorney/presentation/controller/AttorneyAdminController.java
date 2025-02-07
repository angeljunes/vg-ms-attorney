package pe.edu.vallegrande.vg_ms_attorney.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_attorney.application.service.impl.AttorneyServiceImpl;
import pe.edu.vallegrande.vg_ms_attorney.domain.model.Attorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/directives/attorney${api.version}")
public class AttorneyAdminController {

    private final AttorneyServiceImpl attorneyService;

    // Lista de roles permitidos
    private static final List<String> ALLOWED_ROLES = Arrays.asList("DEVELOP", "SUBDIRECTOR", "SUPERIOR", "DIRECTOR", "ADMIN");

    @Autowired
    public AttorneyAdminController(AttorneyServiceImpl attorneyService) {
        this.attorneyService = attorneyService;
    }

    @GetMapping("/actives")
    public Mono<ResponseEntity<Flux<Attorney>>> getListAllActive(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(ResponseEntity.ok(attorneyService.listAllActive()));
                    } else {
                        return Mono.just(ResponseEntity.status(403).build()); // Acceso denegado
                    }
                });
    }

    @GetMapping("/inactive")
    public Mono<ResponseEntity<Flux<Attorney>>> getListAllInactive(@RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return Mono.just(ResponseEntity.ok(attorneyService.listAllInactive()));
                    } else {
                        return Mono.just(ResponseEntity.status(403).build()); // Acceso denegado
                    }
                });
    }

    @GetMapping("/document/{dni}")
    public Mono<ResponseEntity<Attorney>> getAttorneyByDni(@PathVariable String dni, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.findByDni(dni)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Attorney>> getAttorneyByEmail(@PathVariable String email, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.findByEmail(email)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Attorney>> getAttorneyById(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.findById(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Attorney>> createAttorney(@RequestBody Attorney attorney, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.createAttorney(attorney)
                                .map(ResponseEntity::ok);
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Attorney>> deleteAttorney(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.deleteAttorney(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PutMapping("/reactivate/{id}")
    public Mono<ResponseEntity<Attorney>> reactivateAttorney(@PathVariable String id, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.reactivateAttorney(id)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Attorney>> updateAttorney(@PathVariable String id, @RequestBody Attorney attorney, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.updateAttorney(id, attorney)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }

    @PatchMapping("/updatePassword/{id}")
    public Mono<ResponseEntity<Attorney>> updatePassword(@PathVariable String id, @RequestBody String newPassword, @RequestHeader("Authorization") String authorizationHeader) {
        String token = authorizationHeader.substring(7); // Extraer el token Bearer
        return attorneyService.validateTokenAndRoles(token, ALLOWED_ROLES)
                .flatMap(isValid -> {
                    if (isValid) {
                        return attorneyService.updatePassword(id, newPassword)
                                .map(ResponseEntity::ok)
                                .defaultIfEmpty(ResponseEntity.notFound().build());
                    } else {
                        return Mono.just(ResponseEntity.status(403).build());
                    }
                });
    }
}
