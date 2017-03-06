package com.mokon.nbp.parser;

import static com.mokon.nbp.parser.Messages.WRONG_INPUT_ARGUMENTS;


public class CurrencyDataCollector {
    public static void main(String[] args) {
        if (args.length >= 3) {
            String currency = args[0];
            String startDate = args[1];
            String finishDate = args[2];

            XMLReader reader = new XMLReader(currency, startDate, finishDate);
            reader.readFromNBP();
        } else {
            System.out.println(WRONG_INPUT_ARGUMENTS);
        }

    }

}
