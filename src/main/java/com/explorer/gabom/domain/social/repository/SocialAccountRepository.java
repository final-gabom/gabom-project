package com.explorer.gabom.domain.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.type.SocialProvider;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

	Optional<SocialAccount> findByProviderTypeAndProviderId(SocialProvider providerType, String providerId);

	boolean existsByProviderTypeAndProviderId(SocialProvider socialProvider, String providerId);

	Optional<SocialAccount> findByUserIdAndProviderType(Long id, SocialProvider ProviderType);

}