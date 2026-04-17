package br.com.zenon.fraud;


import br.com.zenon.fraud.service.TransactionReport;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import static java.text.DecimalFormat.getCurrencyInstance;

public class ReportMain {

    private static final Logger LOGGER = Logger.getLogger(ReportMain.class.getName());

    static void main(String[] args) {
        String language = Arrays.stream(args).filter(a -> a.equals("en")).findFirst().orElse("pt");

        Locale locale = Locale.of(language);

        NumberFormat numberFormat = NumberFormat.getIntegerInstance(locale);
        NumberFormat decimalFormat = getCurrencyInstance(locale);
        decimalFormat.setCurrency(Currency.getInstance("USD"));

        ResourceBundle resourceBundle = ResourceBundle.getBundle("report", locale);

        String labelTotalRegistro = resourceBundle.getString("label.total.registros");
        String labelTotalFraudes = resourceBundle.getString("label.total.fraudes");
        String labelTotalTransacionado = resourceBundle.getString("label.total.transacionado");


        TransactionReport.Statistics statistics = TransactionReport.Statistics.readTransactions("data/PS_20174392719_1491204439457_log.csv");

        LOGGER.info(String.format("""
                        %s: %s
                        %s: %s
                        %s: %s""",
                labelTotalRegistro, numberFormat.format(statistics.totalRegister()),
                labelTotalFraudes, numberFormat.format(statistics.totalIsFraud()),
                labelTotalTransacionado, decimalFormat.format(statistics.totalAmount())));
    }
}