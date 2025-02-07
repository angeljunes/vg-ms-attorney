package pe.edu.vallegrande.vg_ms_attorney.application.service;

import pe.edu.vallegrande.vg_ms_attorney.domain.model.Attorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AttorneyService {
    Flux<Attorney> listAllActive();
    Flux<Attorney> listAllInactive();
    Mono<Attorney> createAttorney (Attorney attorney);
    Mono<Attorney> deleteAttorney(String id);
    Mono<Attorney> reactivateAttorney(String id);
    Mono<Attorney> updateAttorney(String id, Attorney attorney);
    Mono<Attorney> updatePassword(String id, String newPassword);
    Mono<Attorney> findByDni(String dni);
    Mono<Attorney> findById(String id);
    Mono<Attorney> findByEmail(String email);

}
