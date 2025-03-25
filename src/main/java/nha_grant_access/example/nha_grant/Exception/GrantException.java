package nha_grant_access.example.nha_grant.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GrantException extends RuntimeException {
    public  GrantException (String message) {
        super(message);
    }
}
