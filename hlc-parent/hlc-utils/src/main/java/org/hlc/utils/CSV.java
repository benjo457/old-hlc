package org.hlc.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

public class CSV {
    private CSVPrinter printer;

    private CSV() {

    }

    /** Holder */
    private static class SingletonHolder {
        /** Instance unique non préinitialisée */
        private final static CSV instance = new CSV();
    }

    static void process(String file_name_in, String file_name_out, Consumer<CSVRecord> process_row) throws IOException {
        Reader in = new FileReader(file_name_in);
        CSVParser records = CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().parse(in);
        SingletonHolder.instance.printer = new CSVPrinter(new FileWriter(file_name_out), CSVFormat.EXCEL.withDelimiter(';'));
        SingletonHolder.instance.printer.printRecord(records.getHeaderNames());
        records.forEach(process_row);
        SingletonHolder.instance.printer.flush();
        SingletonHolder.instance.printer.close();
    }

    static void print(Object value) {
        try {
            SingletonHolder.instance.printer.print(value);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static void println() {
        try {
            SingletonHolder.instance.printer.println();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }
}
