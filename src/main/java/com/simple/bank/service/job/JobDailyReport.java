package com.simple.bank.service.job;

import com.simple.bank.dto.AccountTransactionDTO;
import com.simple.bank.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class JobDailyReport {
    @Autowired
    TransactionService transactionService;

    public void generateDailyTransactionReport() throws JobExecutionException {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDateTime start = yesterday.atStartOfDay();
        LocalDateTime end = today.atStartOfDay();

        List<AccountTransactionDTO> transactions = transactionService.getTransactionBetween(start, end);

        long debitCount = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getTransactionType()))
                .count();

        BigDecimal debitSum = transactions.stream()
                .filter(t -> "DEBIT".equalsIgnoreCase(t.getTransactionType()))
                .map(AccountTransactionDTO::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long creditCount = transactions.stream()
                .filter(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType()))
                .count();

        BigDecimal creditSum = transactions.stream()
                .filter(t -> "CREDIT".equalsIgnoreCase(t.getTransactionType()))
                .map(AccountTransactionDTO::getTransactionAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.info("[Daily Report] Date: {}, DebitCount={}, DebitSum={}, CreditCount={}, CreditSum={}",
                yesterday, debitCount, debitSum, creditCount, creditSum);

        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Daily Transaction Report for ").append(yesterday).append("\n\n")
                .append("DEBIT Transactions: ").append(debitCount)
                .append(", Total Amount = ").append(debitSum).append("\n")
                .append("CREDIT Transactions: ").append(creditCount)
                .append(", Total Amount = ").append(creditSum).append("\n");

        String fileName = "DailyTransactionReport_" +
                yesterday.format(DateTimeFormatter.BASIC_ISO_DATE) + ".txt";

        Path reportDir = Paths.get("report");
        try {
            if (!Files.exists(reportDir)) {
                Files.createDirectories(reportDir);
            }
            Path reportFile = reportDir.resolve(fileName);
            Files.writeString(reportFile, reportContent.toString(), StandardCharsets.UTF_8);
            log.info("Report generated: {}", reportFile.toAbsolutePath());
        } catch (IOException e) {
            throw new JobExecutionException("Failed to write report file", e);
        }
    }

}
