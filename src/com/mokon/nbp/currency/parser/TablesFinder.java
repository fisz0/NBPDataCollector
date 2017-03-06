package com.mokon.nbp.currency.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.mokon.nbp.currency.parser.Messages.URL_DATA_FOR_SPECIFIED_DATE;

public class TablesFinder {
    private List<String> currencyTables;
    private int year;

    public TablesFinder() {
        currencyTables = new ArrayList<>();
        searchTablesExceptionHandler();
    }

    public List<String> getCurrencyTables() {
        return currencyTables;
    }

    public void getCurrentDateFormated() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        this.year = localDate.getYear();
    }

    private void searchTablesExceptionHandler() {
        try {
            searchingTables();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void searchingTables() throws IOException {
        getCurrentDateFormated();
        for (int i = 2002; i <= year; i++) {
            URL dir = null;
            if (i != year) {
                dir = new URL(URL_DATA_FOR_SPECIFIED_DATE + i + ".txt");
            } else if (i == year) {
                dir = new URL(URL_DATA_FOR_SPECIFIED_DATE + ".txt");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(dir.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.charAt(0) == 'c') {
                    currencyTables.add(inputLine);
                }
            }
            in.close();
        }
    }
}
