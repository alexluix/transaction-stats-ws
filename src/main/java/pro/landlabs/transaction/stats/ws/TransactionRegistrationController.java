package pro.landlabs.transaction.stats.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.landlabs.transaction.stats.service.TransactionStatisticsService;
import pro.landlabs.transaction.stats.ws.value.Transaction;

@RestController
@RequestMapping("/transactions")
public class TransactionRegistrationController {

    @Autowired
    private TransactionStatisticsService statisticsService;

    @PostMapping
    public ResponseEntity<Object> register(@RequestBody Transaction transaction) {
        statisticsService.register(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
