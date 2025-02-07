package pe.edu.vallegrande.vg_ms_attorney.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.vallegrande.vg_ms_attorney.application.service.impl.AttorneyServiceImpl;
import pe.edu.vallegrande.vg_ms_attorney.domain.model.Attorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/public/attorney${api.version}")
public class AttorneyPublicController {

    private final AttorneyServiceImpl attorneyService;

    @Autowired
    public AttorneyPublicController(AttorneyServiceImpl attorneyService) {
        this.attorneyService = attorneyService;
    }

    @GetMapping("/welcome")
    public Mono<ResponseEntity<String>> getWelcomeMessage() {
        return attorneyService.getWelcomeMessage()
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/actives")
    public Mono<ResponseEntity<Flux<Attorney>>> getListAllActive() {
        return Mono.just(ResponseEntity.ok(attorneyService.listAllActive()));
    }

    @GetMapping("/inactive")
    public Mono<ResponseEntity<Flux<Attorney>>> getListAllInactive() {
        return Mono.just(ResponseEntity.ok(attorneyService.listAllInactive()));
    }

    @GetMapping("/document/{dni}")
    public Mono<ResponseEntity<Attorney>> getAttorneyByDni(@PathVariable String dni) {
        return attorneyService.findByDni(dni)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public Mono<ResponseEntity<Attorney>> getAttorneyByEmail(@PathVariable String email) {
        return attorneyService.findByEmail(email)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Attorney>> getAttorneyById(@PathVariable String id) {
        return attorneyService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Mono<ResponseEntity<Attorney>> createAttorney(@RequestBody Attorney attorney) {
        return attorneyService.createAttorney(attorney)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping("/delete/{id}")
    public Mono<ResponseEntity<Attorney>> deleteAttorney(@PathVariable String id) {
        return attorneyService.deleteAttorney(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/reactivate/{id}")
    public Mono<ResponseEntity<Attorney>> reactivateAttorney(@PathVariable String id) {
        return attorneyService.reactivateAttorney(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/update/{id}")
    public Mono<ResponseEntity<Attorney>> updateAttorney(@PathVariable String id, @RequestBody Attorney attorney) {
        return attorneyService.updateAttorney(id, attorney)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PatchMapping("/updatePassword/{id}")
    public Mono<ResponseEntity<Attorney>> updatePassword(@PathVariable String id, @RequestBody String newPassword) {
        return attorneyService.updatePassword(id, newPassword)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/validate-url")  // Método GET para validar la URL
    public Mono<ResponseEntity<String>> validateUrl(@RequestParam String url) {
        return attorneyService.safeExternalRequest(url)
                .map(response -> ResponseEntity.ok("URL válida: " + response))
                .onErrorResume(e -> Mono.just(ResponseEntity.badRequest().body("Error: " + e.getMessage())));
    }

    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Test successful");
    }
}
