package org.hlc.utils;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Insee {

    private EnumeratedDistribution<String> e_prenom;
    private EnumeratedDistribution<String> e_nom;

    /** Constructeur privé */
    private Insee() {
        String insee_prenons = "/nat2018.csv";
        InputStream in_prenoms = Insee.class.getResourceAsStream(insee_prenons);
        String insee_nons = "/noms2008nat_txt.txt";
        InputStream in_noms = Insee.class.getResourceAsStream(insee_nons);
        BufferedReader r_prenoms = new BufferedReader(new InputStreamReader(in_prenoms, StandardCharsets.UTF_8));
        BufferedReader r_noms = new BufferedReader(new InputStreamReader(in_noms));

        try {
            CSVFormat fmt = CSVFormat.EXCEL.withDelimiter(';');
            Iterable<CSVRecord> prenom_records = fmt.parse(r_prenoms);
            boolean out_header = false;
            String prenom_cur = "";
            int prenom_nbr_cur = 0;
            List<Pair<String,Double>> prenom_list = new ArrayList<>();
            for (CSVRecord prenom_record : prenom_records) {
                if (out_header) {
                    if (prenom_cur.equals(prenom_record.get(1))) {
                        prenom_nbr_cur += Integer.parseInt(prenom_record.get(3));
                    } else {
                        if (prenom_cur.length() > 0) {
                            prenom_list.add(new Pair<>(StringUtils.stripAccents(prenom_cur), (double) prenom_nbr_cur));
                        }
                        prenom_cur = prenom_record.get(1);
                        prenom_nbr_cur = 0;
                    }
                } else {
                    out_header = true;
                }
            }
            e_prenom = new EnumeratedDistribution<>(prenom_list);

            CSVFormat fmt2 = CSVFormat.EXCEL.withDelimiter('\t');
            Iterable<CSVRecord> nom_records = fmt2.parse(r_noms);
            out_header = false;
            int nom_nbr = 0;
            List<Pair<String,Double>> nom_list = new ArrayList<>();
            for (CSVRecord nom_record : nom_records) {
                if (out_header) {
                    for (int i = 1; i<nom_record.size(); i++) {
                        nom_nbr += Integer.parseInt(nom_record.get(i));
                    }
                    nom_list.add(new Pair<>(StringUtils.stripAccents(nom_record.get(0)), (double) nom_nbr));
                    nom_nbr = 0;
                } else {
                    out_header = true;
                }
            }
            e_nom = new EnumeratedDistribution<>(nom_list);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /** Holder */
    private static class SingletonHolder {
        /** Instance unique non préinitialisée */
        private final static Insee instance = new Insee();
    }

    public static String getNom() {
        return StringUtils.stripAccents(SingletonHolder.instance.e_nom.sample());
    }

    public static String getPrenom() {
        return StringUtils.stripAccents(SingletonHolder.instance.e_prenom.sample());
    }
}
