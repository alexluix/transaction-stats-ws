package pro.landlabs.transaction.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pro.landlabs.transaction.stats.service.TransactionStatisticsService;
import pro.landlabs.transaction.stats.service.aggregation.EpochSecondsProvider;
import pro.landlabs.transaction.stats.service.aggregation.TimelineAggregator;

import java.time.Instant;

@SpringBootApplication
public class App {

    public static final int STATS_PERIOD_SECONDS = 60;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public TransactionStatisticsService statisticsService(TimelineAggregator timelineAggregator) {
        return new TransactionStatisticsService(timelineAggregator);
    }

    @Bean
    public TimelineAggregator timelineAggregator(EpochSecondsProvider epochSecondsProvider) {
        return new TimelineAggregator(STATS_PERIOD_SECONDS, epochSecondsProvider);
    }

    @Bean
    public EpochSecondsProvider epochSecondsProvider() {
        return () -> Instant.now().getEpochSecond();
    }

}
