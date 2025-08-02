package com.biancodavide3.budgeting.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "supabase")
@Getter
@Setter
public class SupabaseProperties  {
    private String url;
    private String secretKey;
    public String publishableKey;
    private String jwksUrl;
}
