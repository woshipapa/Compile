package org.example.MongoDB;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("org.example")
@PropertySource("classpath:mongodb.properties")
@Data
public class mongoDBConfig {
    @Value("${mongodb.database}")
    private String database;

    @Value("${mongodb.url}")
    private String url;

    @Value("${mongodb.filesCollection}")
    private String filesCollection;

    @Value("${mongodb.chunksCollection}")
    private String chunksCollection;

    @Value("${mongodb.DFACollection}")
    private String DFACollection;

    @Value("${mongodb.regexToIdCollection}")
    private String regexToIdCollection;

}
