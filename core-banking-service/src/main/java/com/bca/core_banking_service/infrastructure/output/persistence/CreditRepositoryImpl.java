package com.bca.core_banking_service.infrastructure.output.persistence;

import com.bca.core_banking_service.domain.model.Credit;
import com.bca.core_banking_service.domain.ports.output.CreditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

interface CreditMongoRepository extends ReactiveMongoRepository<Credit, String> {
}

@Repository
@RequiredArgsConstructor
public class CreditRepositoryImpl implements CreditRepository {

    private final CreditMongoRepository mongoRepository;

    @Override
    public Mono<Credit> save(Credit credit) {
        return mongoRepository.save(credit);
    }

    @Override
    public Mono<Credit> findById(String id) {
        return mongoRepository.findById(id);
    }
}