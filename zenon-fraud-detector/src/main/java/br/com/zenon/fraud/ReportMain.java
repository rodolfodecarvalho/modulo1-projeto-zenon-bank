package br.com.zenon.fraud;


import br.com.zenon.fraud.service.TransactionReport;

import java.util.logging.Logger;

public class ReportMain {

    private static final Logger LOGGER = Logger.getLogger(ReportMain.class.getName());

    static void main() {
        TransactionReport.Statistics statistics = TransactionReport.Statistics.readTransactions("data/PS_20174392719_1491204439457_log.csv");


        LOGGER.info(String.format("""
                Total de registros: %d
                Total de fraudes: %d
                Valor total transacionado: %.2f""", statistics.totalRegister(), statistics.totalIsFraud(), statistics.totalAmount()));
    }
}