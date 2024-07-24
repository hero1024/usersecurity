package cn.edu.moe.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "secure.urls")
public class UrlsConfig {

    private List<String> ignored;
    private String login;
    private String forward;

}
