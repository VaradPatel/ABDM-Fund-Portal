package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import static ch.qos.logback.core.joran.JoranConstants.NULL;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {

    private String Message;
   private String error=NULL;
}
