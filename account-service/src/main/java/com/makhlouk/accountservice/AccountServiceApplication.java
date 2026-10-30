package com.makhlouk.accountservice;

import com.makhlouk.accountservice.domain.Account;
import com.makhlouk.accountservice.domain.AccountStatus;
import com.makhlouk.accountservice.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(AccountRepository accountRepository) {
        return args -> {
            accountRepository.save(Account.builder()
                    .customerReference("CUST-001")
                    .currency("EUR")
                    .balance(2500.0)
                    .status(AccountStatus.ACTIVE)
                    .build());

            accountRepository.save(Account.builder()
                    .customerReference("CUST-002")
                    .currency("EUR")
                    .balance(800.0)
                    .status(AccountStatus.ACTIVE)
                    .build());

            accountRepository.save(Account.builder()
                    .customerReference("CUST-003")
                    .currency("USD")
                    .balance(1200.0)
                    .status(AccountStatus.BLOCKED)
                    .build());
        };
    }
}
