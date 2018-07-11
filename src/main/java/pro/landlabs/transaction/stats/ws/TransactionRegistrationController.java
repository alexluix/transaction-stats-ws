package pro.landlabs.transaction.stats.ws;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionRegistrationController {

    @PostMapping
    public ResponseEntity<Object> register() {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
