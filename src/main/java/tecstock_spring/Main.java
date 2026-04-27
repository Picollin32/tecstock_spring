package tecstock_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
public class Main {
    
    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
} 