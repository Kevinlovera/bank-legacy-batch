package com.banco.bank_legacy_batch.config;

import com.banco.bank_legacy_batch.model.Transaccion;
import com.banco.bank_legacy_batch.processor.TransaccionProcessor;

import javax.sql.DataSource;

import java.time.LocalDate;

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
public class TransaccionesJobConfig {

    @Bean
    public FlatFileItemReader<Transaccion> transaccionesReader() {

        return new FlatFileItemReaderBuilder<Transaccion>()
                .name("transaccionesReader")
                .resource(
                        new ClassPathResource(
                                "data/transacciones.csv"))
                .linesToSkip(1)
                .delimited()
                .delimiter(",")
                .names(
                        "cuenta_id",
                        "fecha",
                        "tipo",
                        "monto",
                        "descripcion")
                .fieldSetMapper(fieldSet -> {

                    Transaccion item = new Transaccion();

                    item.setCuentaId(
                            fieldSet.readLong("cuenta_id"));

                    item.setFecha(
                            LocalDate.parse(
                                    fieldSet.readString("fecha")));

                    item.setTipo(
                            fieldSet.readString("tipo"));

                    item.setMonto(
                            fieldSet.readBigDecimal("monto"));

                    item.setDescripcion(
                            fieldSet.readString("descripcion"));

                    return item;
                })
                .build();
    }

    @Bean
    public TransaccionProcessor transaccionesProcessor() {
        return new TransaccionProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Transaccion> transaccionesWriter(
            DataSource dataSource) {

        return new JdbcBatchItemWriterBuilder<Transaccion>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO transacciones_procesadas
                    (
                        cuenta_id,
                        fecha,
                        monto,
                        tipo,
                        descripcion,
                        estado
                    )
                    VALUES
                    (
                        :cuentaId,
                        :fecha,
                        :monto,
                        :tipo,
                        :descripcion,
                        :estado
                    )
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    public Step transaccionesStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            FlatFileItemReader<Transaccion> transaccionesReader,
            TransaccionProcessor transaccionesProcessor,
            JdbcBatchItemWriter<Transaccion> transaccionesWriter) {

        return new StepBuilder(
                "transaccionesStep",
                jobRepository)
                .<Transaccion, Transaccion>chunk(
                        10,
                        transactionManager)
                .reader(transaccionesReader)
                .processor(transaccionesProcessor)
                .writer(transaccionesWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(10)
                .build();
    }

    @Bean
    public Job transaccionesJob(
            JobRepository jobRepository,
            Step transaccionesStep) {

        return new JobBuilder(
                "transaccionesJob",
                jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(transaccionesStep)
                .build();
    }
}