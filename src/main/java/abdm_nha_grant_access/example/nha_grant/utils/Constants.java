package abdm_nha_grant_access.example.nha_grant.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String REDIS_HOST = "${spring.redis.host}";
    public static final String REDIS_PASSWORD = "${spring.redis.password}";
    public static final String REDIS_PORT = "${spring.redis.port}";
    public static final String REDIS_DATABASE = "${spring.redis.database}";
}
