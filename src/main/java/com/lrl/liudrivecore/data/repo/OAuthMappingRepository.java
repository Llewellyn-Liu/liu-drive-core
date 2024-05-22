package com.lrl.liudrivecore.data.repo;

import com.lrl.liudrivecore.data.pojo.OAuthMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthMappingRepository extends JpaRepository<OAuthMapping, Long> {

    OAuthMapping getOAuthMappingByUrl(String url);

}
