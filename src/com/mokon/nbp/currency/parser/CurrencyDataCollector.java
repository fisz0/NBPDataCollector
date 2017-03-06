package com.mokon.nbp.currency.parser;

import static com.mokon.nbp.currency.parser.Messages.WRONG_INPUT_ARGUMENTS;


public class CurrencyDataCollector {
    public static void main(String[] args) {
        //logMessage(ASK_FOR_DATA);
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
