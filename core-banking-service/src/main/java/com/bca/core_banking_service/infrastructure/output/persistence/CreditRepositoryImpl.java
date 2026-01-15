package com.bca.core_banking_service.infrastructure.output.persistence;


import com.bca.core_banking_service.domain.ports.output.persistence.CreditRepository;
import com.bca.core_banking_service.infrastructure.input.dto.Credit;
import com.bca.core_banking_service.infrastructure.output.persistence.mapper.CreditMapper;
import com.bca.core_banking_service.infrastructure.output.persistence.repository.CreditMongoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
@RequiredArgsConstructor
public class CreditRepositoryImpl implements CreditRepository {

    private final CreditMongoRepository mongoRepository;

    @Override
    public Mono<Credit> save(Credit credit) {
        return mongoRepository.save(CreditMapper.toEntity(credit))
                .map(CreditMapper::toDomain);
    }

    @Override
    public Mono<Credit> findById(String id) {
        return mongoRepository.findById(id)
                .map(CreditMapper::toDomain);
    }

    
    @Override
    public Mono<Credit> findByCustomerId(String customerId) {
        return mongoRepository.findByCustomerId(customerId)
                .map(CreditMapper::toDomain);
    }

    @Override
    public Mono<Long> countByCustomerId(String customerId) {
        return mongoRepository.countByCustomerId(customerId);   
    }
}