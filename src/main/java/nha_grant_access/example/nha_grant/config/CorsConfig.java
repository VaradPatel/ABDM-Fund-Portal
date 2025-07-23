package nha_grant_access.example.nha_grant.config;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;




@Configuration
public class CorsConfig {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String ACCESS_TOKEN = "access-token";
    public static final String CHECKSUM_HEADER = "x-hash";
    public static final String CORRELATION_ID = "X-Correlation-ID";
    public static final String REFRESH_TOKEN = "refresh-token";

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        LOGGER.info("Configuring CORS");
        return new WebMvcConfigurer() {
            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//
//                        .allowedMethods("*");
//
//            }
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
//                        .allowedOrigins("*")
                        .allowedOrigins(
                                "https://fundreleasesbx.nha.gov.in",
                                "http://fundreleasesbx.nha.gov.in",
                                "http://10.20.4.110:3000"
                        )
                        .allowedMethods("*")
                        //               .allowCredentials(true)
                        .exposedHeaders(ACCESS_TOKEN, REFRESH_TOKEN, CORRELATION_ID, CHECKSUM_HEADER);
            }
        };
    }
}

