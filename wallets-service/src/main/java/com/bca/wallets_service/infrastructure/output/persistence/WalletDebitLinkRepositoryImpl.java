package com.bca.wallets_service.infrastructure.output.persistence;

import com.bca.wallets_service.domain.model.WalletDebitLink;
import com.bca.wallets_service.domain.ports.output.WalletDebitLinkRepository;

import reactor.core.publisher.Mono;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class WalletDebitLinkRepositoryImpl implements WalletDebitLinkRepository {

    private final ReactiveMongoTemplate mongoTemplate;

    public WalletDebitLinkRepositoryImpl(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Mono<WalletDebitLink> save(WalletDebitLink link) {
        WalletDebitLinkDocument document = toDocument(link);
        return mongoTemplate.save(document).map(this::toDomain);
    }

    @Override
    public Mono<WalletDebitLink> findByWalletId(String walletId) {
        Query query = new Query(Criteria.where("walletId").is(walletId));
        return mongoTemplate.findOne(query, WalletDebitLinkDocument.class).map(this::toDomain);
    }

    private WalletDebitLinkDocument toDocument(WalletDebitLink link) {
        WalletDebitLinkDocument document = new WalletDebitLinkDocument();
        document.setId(null); // id will be generated
        document.setWalletId(link.getWalletId());
        document.setWalletType(link.getWalletType().name());
        document.setDebitCardId(link.getDebitCardId());
        document.setMainAccountId(link.getMainAccountId());
        document.setStatus(link.getStatus().name());
        document.setLinkedAt(link.getLinkedAt());
        return document;
    }

    private WalletDebitLink toDomain(WalletDebitLinkDocument document) {
        return new WalletDebitLink(
            document.getWalletId(),
            WalletDebitLink.WalletType.valueOf(document.getWalletType()),
            document.getDebitCardId(),
            document.getMainAccountId(),
            WalletDebitLink.LinkStatus.valueOf(document.getStatus()),
            document.getLinkedAt()
        );
    }
}