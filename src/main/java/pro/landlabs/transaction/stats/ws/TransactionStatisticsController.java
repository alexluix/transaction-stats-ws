package pro.landlabs.transaction.stats.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.landlabs.transaction.stats.service.TransactionStatisticsService;
import pro.landlabs.transaction.stats.ws.value.Statistics;

@RestController
@RequestMapping("/statistics")
public class TransactionStatisticsController {

    @Autowired
    private TransactionStatisticsService statisticsService;

    @GetMapping
    public ResponseEntity<Statistics> read() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

}
