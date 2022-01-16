package org.hlc.utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Anon {

    private Map<String, String> h_noms;
    private Map<String, String> h_prenoms;
    private final String h_noms_disk = "h_noms.tbl";
    private final String h_prenoms_disk = "h_prenoms.tbl";

    /** Constructeur privé */
    @SuppressWarnings("unchecked")
    private Anon() {
        try (FileInputStream fis_nom = new FileInputStream(h_noms_disk);
             ObjectInputStream ois_nom = new ObjectInputStream(fis_nom)) {
            h_noms = (Map<String, String>) ois_nom.readObject();
        } catch (Exception e) {
            h_noms = new HashMap<>();
        }
        try (FileInputStream fis_prenom = new FileInputStream(h_prenoms_disk);
             ObjectInputStream ois_prenom = new ObjectInputStream(fis_prenom)) {
            h_prenoms = (Map<String, String>) ois_prenom.readObject();
        } catch (Exception e) {
            h_prenoms = new HashMap<>();
        }
    }


    /** Holder */
    private static class SingletonHolder {
        /** Instance unique non préinitialisée */
        private final static Anon instance = new Anon();
    }

    public static String getNom(String key) {
        String nom = SingletonHolder.instance.h_noms.get(key);
        if (nom == null) {
            nom = Insee.getNom();
            SingletonHolder.instance.h_noms.put(key, nom);
        }
        return nom;
    }

    public static String getPrenom(String key) {
        String prenom = SingletonHolder.instance.h_prenoms.get(key);
        if (prenom == null) {
            prenom = Insee.getPrenom();
            SingletonHolder.instance.h_prenoms.put(key, prenom);
        }
        return prenom;
    }

    public static void save() throws IOException {
        FileOutputStream fos_nom = new FileOutputStream(SingletonHolder.instance.h_noms_disk);
        ObjectOutputStream oos_nom = new ObjectOutputStream(fos_nom);
        oos_nom.writeObject(SingletonHolder.instance.h_noms);
        oos_nom.close();
        FileOutputStream fos_prenom = new FileOutputStream(SingletonHolder.instance.h_prenoms_disk);
        ObjectOutputStream oos_prenom = new ObjectOutputStream(fos_prenom);
        oos_prenom.writeObject(SingletonHolder.instance.h_prenoms);
        oos_prenom.close();
    }
}
