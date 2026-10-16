package com.jaydeep.txn_ledger.step.reader;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.infrastructure.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.jaydeep.txn_ledger.model.dto.TransactionCsvRow;

@Configuration
public class TransactionItemReader {
    @Value("${app.batch.input-file}")
    private Resource inputFile;

    @Bean
    public ItemReader<TransactionCsvRow> transactionItemReader() {
        FlatFileItemReaderBuilder<TransactionCsvRow> reader = new FlatFileItemReaderBuilder<TransactionCsvRow>();

        reader.resource(inputFile);
        reader.linesToSkip(1);
        reader.lineMapper(lineMapper());
        reader.name("transactionItemReader");
        return reader.build();
    }

    private DefaultLineMapper<TransactionCsvRow> lineMapper() {
        DefaultLineMapper<TransactionCsvRow> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("transactionId", "accountNumber", "amount", "currency", "transactionDate");

        BeanWrapperFieldSetMapper<TransactionCsvRow> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(TransactionCsvRow.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

}
