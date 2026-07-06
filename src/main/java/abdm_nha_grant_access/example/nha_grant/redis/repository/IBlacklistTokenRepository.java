package abdm_nha_grant_access.example.nha_grant.redis.repository;

import abdm_nha_grant_access.example.nha_grant.redis.hash.BlacklistToken;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface IBlacklistTokenRepository extends KeyValueRepository<BlacklistToken, String> {
}
