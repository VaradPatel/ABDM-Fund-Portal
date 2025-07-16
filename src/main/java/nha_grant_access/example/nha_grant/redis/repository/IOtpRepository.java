package nha_grant_access.example.nha_grant.redis.repository;


import nha_grant_access.example.nha_grant.redis.hash.Otp;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

import java.util.List;

public interface IOtpRepository extends KeyValueRepository<Otp, String> {

    List<Otp> findAllByContact(String contact);
}

