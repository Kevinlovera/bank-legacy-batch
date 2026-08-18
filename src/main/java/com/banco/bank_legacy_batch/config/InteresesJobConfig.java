package com.banco.bank_legacy_batch.config;

import com.banco.bank_legacy_batch.model.Interes;
import com.banco.bank_legacy_batch.processor.InteresProcessor;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;

import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class InteresesJobConfig {

    @Bean
    public FlatFileItemReader<Interes> interesReader() {

        return new FlatFileItemReaderBuilder<Interes>()
                .name("interesReader")
                .resource(
                        new ClassPathResource(
                                "data/intereses.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(
                        "cuentaId",
                        "nombre",
                        "saldo",
                        "edad",
                        "tipo")
                .targetType(Interes.class)
                .build();
    }

    @Bean
    public InteresProcessor interesProcessor() {
        return new InteresProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Interes> interesWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Interes>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO intereses_procesados
                    (
                        cuenta_id,
                        nombre,
                        saldo,
                        edad,
                        tipo,
                        interes,
                        saldo_final,
                        estado
                    )
                    VALUES
                    (
                        :cuentaId,
                        :nombre,
                        :saldo,
                        :edad,
                        :tipo,
                        :interes,
                        :saldoFinal,
                        :estado
                    )
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    public Step interesesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Interes> interesReader,
            InteresProcessor interesProcessor,
            JdbcBatchItemWriter<Interes> interesWriter) {

        return new StepBuilder(
                "interesesStep",
                jobRepository)
                .<Interes, Interes>chunk(
                        10,
                        transactionManager)
                .reader(interesReader)
                .processor(interesProcessor)
                .writer(interesWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job interesesJob(
            JobRepository jobRepository,
            Step interesesStep) {

        return new JobBuilder(
                "interesesJob",
                jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(interesesStep)
                .build();
    }
}