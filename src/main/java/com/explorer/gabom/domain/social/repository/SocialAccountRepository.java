package com.explorer.gabom.domain.social.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.social.entity.SocialAccount;
import com.explorer.gabom.domain.social.type.SocialProvider;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {

	Optional<SocialAccount> findByProviderAndProviderId(SocialProvider provider, String providerId);

	boolean existsByProviderAndProviderId(SocialProvider socialProvider, String providerId);

	Optional<SocialAccount> findByUserIdAndProvider(Long id, SocialProvider socialProvider);

}