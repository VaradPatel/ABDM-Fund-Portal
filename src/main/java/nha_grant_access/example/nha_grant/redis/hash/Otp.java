package nha_grant_access.example.nha_grant.redis.hash;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("grant-otp")
public class Otp {

    @Id
    private String id;
    private String otp;
    private Integer attempts;
    
    @Indexed
    private String contact;
    private boolean expired;


    
    @TimeToLive(unit = TimeUnit.MINUTES)
    private long timeToLive;
    private boolean verified;

}
