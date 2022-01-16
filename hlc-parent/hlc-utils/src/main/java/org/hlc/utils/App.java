package org.hlc.utils;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import java.io.IOException;

/**
 * Hello anonymous world!
 *
 * CREATE TABLE computers (
 * 	computer_id INTEGER PRIMARY KEY,
 * 	CN TEXT NOT NULL,
 * 	Description TEXT,
 * 	IPv4Address TEXT,
 * 	OperatingSystem TEXT
 * );
 *
 */
public class App 
{
    public static void main (String [] args) {
    }

    public static void csvtest1( String[] args ) throws IOException {
        CSV.process(
                "ITFR-Historique Incidents.csv",
                "incidents-anon.csv",
                (CSVRecord record) -> {
                    String key = record.get(2).toLowerCase() + "+" + record.get(3).toLowerCase();
                    for (int i=0;i<record.size();i++) {
                        switch(i) {
                            case 2:
                                CSV.print(Anon.getNom(key));
                                break;
                            case 3:
                                CSV.print(Anon.getPrenom(key));
                                break;
                            case 6:
                                CSV.print("no summary");
                                break;
                            default :
                                CSV.print(record.get(i));
                                break;
                        }
                    }
                    CSV.println();
                } );
        CSV.process(
                "ITFR-Historique WorkOrder.csv",
                "workorder-anon.csv",
                (CSVRecord record) -> {
                    String key = record.get(2).toLowerCase() + "+" + record.get(3).toLowerCase();
                    for (int i=0;i<record.size();i++) {
                        switch(i) {
                            case 2:
                                CSV.print(Anon.getNom(key));
                                break;
                            case 3:
                                CSV.print(Anon.getPrenom(key));
                                break;
                            case 6:
                                CSV.print("no summary");
                                break;
                            case 10:
                                String val = record.get(i);
                                if (!StringUtils.isEmpty(val)) {
                                    String[] arr = val.split(" ");
                                    String val_prenom = arr[0];
                                    String val_nom = "";
                                    if (arr.length > 1) {
                                        val_nom = val_nom.concat(arr[1]);
                                    }
                                    for (int j = 2; j < arr.length; j++) {
                                        val_nom = val_nom.concat(" ");
                                        val_nom = val_nom.concat(arr[j]);
                                    }
                                    String val_key = val_nom.toLowerCase() + "+" + val_prenom.toLowerCase();
                                    String anon_val = Anon.getPrenom(val_key) + " " + Anon.getNom(val_key);
                                    CSV.print(anon_val);
                                } else {
                                    CSV.print("");
                                }
                                break;
                            default :
                                CSV.print(record.get(i));
                                break;
                        }
                    }
                    CSV.println();
                } );
        Anon.save();
    }
}
