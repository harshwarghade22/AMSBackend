package com.harshwarghade.project.service;

import com.harshwarghade.project.dto.AdminTransactionRequest;
import com.harshwarghade.project.entity.Account;
import com.harshwarghade.project.entity.TransactionType;
import com.harshwarghade.project.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class WebClientSeederService {

    private final WebClient webClient;
    private final AccountRepository accountRepository;

    public String seedTransactionsUsingWebClient(String token, int txnPerAccount) {

        List<Account> accounts = accountRepository.findAll();
        Random random = new Random();

        for (Account account : accounts) {

            Flux.range(1, txnPerAccount)
                    .flatMap(i -> {
                        AdminTransactionRequest request = new AdminTransactionRequest();
                        request.setAccountId(account.getId());
                        request.setAmount((double) (10 + random.nextInt(1000)));
                        request.setType(random.nextBoolean() ?
                                TransactionType.DEPOSIT :
                                TransactionType.WITHDRAW);

                        return webClient.post()
                                .uri("/api/admin/transactions")
                                .header("Authorization", "Bearer " + token)
                                .bodyValue(request)
                                .retrieve()
                                .bodyToMono(String.class);
                    }, 50) // concurrency level
                    .blockLast(); // wait for completion
        }

        return "Transactions seeded using WebClient (High Performance)";
    }
}
