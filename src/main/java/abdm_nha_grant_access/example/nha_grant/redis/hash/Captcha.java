package abdm_nha_grant_access.example.nha_grant.redis.hash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("grant-captcha")
public class Captcha {
    
    @Id
    private String id;
    private Integer num1;
    private Integer num2;
    private String operation;
    private Integer result;
    private Boolean expired;
    
    @TimeToLive(unit = TimeUnit.MINUTES)
    private long timeToLive;
}
