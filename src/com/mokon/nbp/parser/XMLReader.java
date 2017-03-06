package com.mokon.nbp.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mokon.nbp.parser.Messages.*;

public class XMLReader {

    private TablesFinder tablesFinder;
    private List<Double> buy;
    private List<Double> sell;
    private String currency;
    private LocalDate fromDay;
    private LocalDate untilDay;

    public XMLReader(String currency, String fromDay, String untilDay) {
        this.currency = currency;
        try {
            this.fromDay = LocalDate.parse(fromDay);
            this.untilDay = LocalDate.parse(untilDay);
        } catch (java.time.format.DateTimeParseException e) {
            System.out.println(WRONG_FORMAT);
            return;
        }
        checkDates();
        buy = new ArrayList<>();
        sell = new ArrayList<>();
        tablesFinder = new TablesFinder();
    }

    private void checkDates() {
        if (fromDay.compareTo(untilDay) > 0) {
            throw new IllegalArgumentException(DATES_ERROR);
        }
    }

    public void readFromNBP() {
        tablesFinder.searchTablesExceptionHandler();
        LocalDate startChecking = fromDay.minusDays(5);
        LocalDate stopChecking = untilDay.plusDays(5);

        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();

            for (String e : tablesFinder.getCurrencyTables()) {
                LocalDate dateFile = LocalDate.parse("20" + e.substring(e.length() - 6, e.length() - 4) + "-" + e.substring(e.length() - 4, e.length() - 2) + "-" + e.substring(e.length() - 2, e.length()));

                if (startChecking.compareTo(dateFile) <= 0 && stopChecking.compareTo(dateFile) >= 0) {
                    Document doc = b.parse(URL_GENERAL + e + ".xml");
                    doc.getDocumentElement().normalize();
                    readXML(doc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (buy.size() == 0) {
            System.out.println(DATA_READ_ERROR);
        } else {
            getReadFromNBP();
        }
    }


    private void readXML(Document doc) {
        //System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
        //System.out.println("Data publikacji : " + doc.getElementsByTagName("data_publikacji").item(0).getTextContent());
        LocalDate dataPublic = LocalDate.parse(doc.getElementsByTagName("data_publikacji").item(0).getTextContent());

        if (fromDay.compareTo(dataPublic) <= 0 && untilDay.compareTo(dataPublic) >= 0) {
            NodeList nList = doc.getElementsByTagName("pozycja");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                //System.out.println("\nCurrent Element :" + nNode.getNodeName());
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;

                    if (currency.equals(eElement.getElementsByTagName("kod_waluty").item(0).getTextContent())) {
                        try {
                            NumberFormat df = NumberFormat.getInstance(Locale.GERMAN);

                            buy.add(df.parse(eElement.getElementsByTagName("kurs_kupna").item(0).getTextContent())
                                .doubleValue());
                            sell.add(df.parse(eElement.getElementsByTagName("kurs_sprzedazy").item(0).getTextContent())
                                .doubleValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
    }

    private void getReadFromNBP() {
        System.out.println(currency + " " + CURRENCY_CODE);
        System.out.println(fromDay + " " + FROM_DATE);
        System.out.println(untilDay + " " + END_DATE);
        System.out.printf("%.4f " + AVG_BUY_COURSE + "\n", averageCurrencyBuyCourse());
        if (standDeviationSell() != 0.0) {
            System.out.printf("%.4f " + STANDARD_DEVIATION, standDeviationSell());
        } else {
            System.out.println(STANDARD_DEVIATION_ERROR);
        }
    }

    private double averageCurrencyBuyCourse() {
        double sum = 0;
        for (double e : buy) sum += e;
        return sum / buy.size();
    }

    private double standDeviationSell() {
        if (sell.size() == 1) return 0;
        double sum = 0;
        for (double e : sell) sum += e;
        sum = sum / sell.size();
        double standardDeviation = 0;
        for (double e : sell) standardDeviation = Math.pow(e - sum, 2) + standardDeviation;
        standardDeviation = Math.sqrt(standardDeviation / (sell.size()));
        return standardDeviation;
    }
}
