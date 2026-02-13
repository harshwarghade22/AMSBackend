package com.harshwarghade.project.controller;

// import com.harshwarghade.project.service.RestTemplateSeederService;
import com.harshwarghade.project.service.WebClientSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/seeder")
@RequiredArgsConstructor
public class SeederController {

    // private final RestTemplateSeederService restTemplateSeederService;
    private final WebClientSeederService webClientSeederService;

    // @PostMapping("/resttemplate")
    // public String seedUsingRestTemplate(
    //         @RequestParam String token,
    //         @RequestParam int txnPerAccount) {

    //     return restTemplateSeederService
    //             .seedTransactionsUsingRestTemplate(token, txnPerAccount);
    // }

    @PostMapping("/webclient")
    public String seedUsingWebClient(
            @RequestParam String token,
            @RequestParam int txnPerAccount) {

        return webClientSeederService
                .seedTransactionsUsingWebClient(token, txnPerAccount);
    }
}
