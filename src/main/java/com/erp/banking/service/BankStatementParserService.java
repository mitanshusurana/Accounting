package com.erp.banking.service;

import com.erp.banking.domain.ParsedTransaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.BasicExtractionAlgorithm;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankStatementParserService {

    public List<ParsedTransaction> parseStatement(File pdfFile) throws IOException {
        List<ParsedTransaction> transactions = new ArrayList<>();

        try (PDDocument document = PDDocument.load(pdfFile)) {
            ObjectExtractor extractor = new ObjectExtractor(document);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            BasicExtractionAlgorithm bea = new BasicExtractionAlgorithm();

            // Iterate over pages
            for (int i = 1; i <= document.getNumberOfPages(); i++) {
                Page page = extractor.extract(i);

                // You can choose sea or bea based on the PDF structure.
                // Tabula's SpreadsheetExtractionAlgorithm works better when there are ruling lines.
                // BasicExtractionAlgorithm works better for stream-like data.
                List<Table> tables = bea.extract(page);

                for (Table table : tables) {
                    List<List<RectangularTextContainer>> rows = table.getRows();

                    for (List<RectangularTextContainer> row : rows) {
                        // Very simplified parsing logic.
                        // Real-world Bank PDF extraction requires complex heuristics,
                        // handling multi-line descriptions, header skipping, and varied date formats.

                        if (row.size() >= 6) {
                            String dateStr = row.get(0).getText().trim();
                            String descStr = row.get(1).getText().trim();
                            String refStr = row.get(2).getText().trim();
                            String debitStr = row.get(3).getText().trim().replace(",", "");
                            String creditStr = row.get(4).getText().trim().replace(",", "");
                            String balanceStr = row.get(5).getText().trim().replace(",", "");

                            try {
                                // Assume a common format for example
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                                LocalDate date = LocalDate.parse(dateStr, formatter);

                                ParsedTransaction pt = new ParsedTransaction();
                                pt.setTransactionDate(date);
                                pt.setDescription(descStr);
                                pt.setReferenceNumber(refStr);

                                if (!debitStr.isEmpty()) {
                                    pt.setDebitAmount(new BigDecimal(debitStr));
                                }
                                if (!creditStr.isEmpty()) {
                                    pt.setCreditAmount(new BigDecimal(creditStr));
                                }
                                if (!balanceStr.isEmpty()) {
                                    pt.setBalance(new BigDecimal(balanceStr));
                                }

                                transactions.add(pt);
                            } catch (DateTimeParseException | NumberFormatException e) {
                                // Likely a header row or badly formatted row, skip
                            }
                        }
                    }
                }
            }
        }

        return transactions;
    }
}
