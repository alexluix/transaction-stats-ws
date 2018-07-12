package pro.landlabs.transaction.stats.ws;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.landlabs.transaction.stats.ws.value.Statistics;

@RestController
@RequestMapping("/statistics")
public class TransactionStatisticsController {

    @GetMapping
    public ResponseEntity<Statistics> read() {
        return ResponseEntity.ok(new Statistics(0, 0, 0, 0, 0));
    }

}
