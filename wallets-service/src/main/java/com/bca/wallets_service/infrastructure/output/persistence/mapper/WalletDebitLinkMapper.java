package com.bca.wallets_service.infrastructure.output.persistence.mapper;

import com.bca.wallets_service.domain.model.WalletDebitLink;
import com.bca.wallets_service.infrastructure.output.persistence.entity.WalletDebitLinkEntity;

public class WalletDebitLinkMapper {
    public static WalletDebitLinkEntity toDocument(WalletDebitLink link) {
        WalletDebitLinkEntity document = new WalletDebitLinkEntity();
        document.setId(null); // id will be generated
        document.setWalletId(link.getWalletId());
        document.setWalletType(link.getWalletType().name());
        document.setDebitCardId(link.getDebitCardId());
        document.setMainAccountId(link.getMainAccountId());
        document.setStatus(link.getStatus().name());
        document.setLinkedAt(link.getLinkedAt());
        return document;
    }

    public static WalletDebitLink toDomain(WalletDebitLinkEntity document) {
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
