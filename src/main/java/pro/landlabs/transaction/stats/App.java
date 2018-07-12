package pro.landlabs.transaction.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pro.landlabs.transaction.stats.service.TransactionStatisticsService;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public TransactionStatisticsService statisticsService() {
        return new TransactionStatisticsService(60);
    }

}
