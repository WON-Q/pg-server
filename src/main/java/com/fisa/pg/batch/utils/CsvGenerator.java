package com.fisa.pg.batch.utils;

import com.fisa.pg.entity.payment.Payment;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Component
public class CsvGenerator {

    @Value("${batch.output.directory}")
    private String outputDirectory;

    public File generate(Merchant merchant, List<Payment> payments) throws IOException {
        String fileName = "purchase_" + merchant.getId() + ".csv";
        File dir = new File(outputDirectory);
        if (!dir.exists()) dir.mkdirs();

        File csvFile = new File(dir, fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("merchantId,orderId,amount,currency");
            writer.newLine();

            for (Payment payment : payments) {
                writer.write(String.join(",",
                        String.valueOf(merchant.getId()),
                        payment.getOrderId(),
                        String.valueOf(payment.getAmount()),
                        payment.getCurrency()
                ));
                writer.newLine();
            }
        }

        return csvFile;
    }
}