package abdm_nha_grant_access.example.nha_grant.redis.repository;


import abdm_nha_grant_access.example.nha_grant.redis.hash.Captcha;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface ICaptchaRepository extends KeyValueRepository<Captcha, String> {

}
