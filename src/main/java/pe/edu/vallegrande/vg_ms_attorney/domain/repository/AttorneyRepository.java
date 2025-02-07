package pe.edu.vallegrande.vg_ms_attorney.domain.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import pe.edu.vallegrande.vg_ms_attorney.domain.model.Attorney;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AttorneyRepository extends ReactiveMongoRepository  <Attorney, String>{

    Flux<Attorney> findByStatus (String Status);
    Mono<Attorney> findByDocumentNumber(String documentNumber);

    Mono<Attorney> findByEmail(String email);

}