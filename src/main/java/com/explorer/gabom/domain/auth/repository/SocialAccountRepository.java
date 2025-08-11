package com.explorer.gabom.domain.auth.repository;

import com.explorer.gabom.domain.user.entity.SocialAccount;
import com.explorer.gabom.domain.user.type.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {


    Optional<SocialAccount> findByProviderAndProviderId(SocialProvider provider, String providerId);

    Optional<SocialAccount> findByEmail(String email);

    boolean existsByProviderAndProviderId(SocialProvider socialProvider, String providerId);

    boolean existsByUserIdAndProvider(Long id, SocialProvider socialProvider);

    Optional<SocialAccount> findByUserIdAndProvider(Long id, SocialProvider socialProvider);

}
