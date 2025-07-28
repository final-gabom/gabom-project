package com.explorer.gabom.domain.title.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.explorer.gabom.domain.title.entity.UserTitle;
import com.explorer.gabom.domain.user.entity.User;

public interface UserTitleRepository extends JpaRepository<UserTitle, Long> {
	List<UserTitle> findByUser(User user);
}
