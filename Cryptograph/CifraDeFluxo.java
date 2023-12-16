package Cryptograph;

import Model.Film;

public class CifraDeFluxo {
    private static String key;

    public CifraDeFluxo(String key) {
        CifraDeFluxo.key = key;
    }

    public static String encrypt(Object object, String key) {
        StringBuilder encrypted = new StringBuilder();
        for (int i = 0; i < ((String) object).length(); i++) {
            char messageChar = ((String) object).charAt(i);
            char keyChar = key.charAt(i % key.length());
            char encryptedChar = (char) (messageChar ^ keyChar);
            encrypted.append(encryptedChar);
        }
        return encrypted.toString();
    }

    public static String decrypt(Object object, String key) {
        StringBuilder decrypted = new StringBuilder();
        for (int i = 0; i < ((String) object).length(); i++) {
            char encryptedChar = ((String) object).charAt(i);
            char keyChar = key.charAt(i % key.length());
            char decryptedChar = (char) (encryptedChar ^ keyChar);
            decrypted.append(decryptedChar);
        }
        return decrypted.toString();
    }

    public static Film encryptFilme(Film f) {
        if (f == null) {
            return null;
        }
        if (f.getTitle() != null) {
            f.setTitle(encrypt(f.getTitle(), key));
        }
        if (f.getType() != null) {
            f.setType(encrypt(f.getType(), key));
        }
        if (f.getCountry() != null) {
            f.setCountry(encrypt(f.getCountry(), key));
        }
        if (f.getDescription() != null) {
            f.setDescription(encrypt(f.getDescription(), key));
        }
        if (f.getDirector() != null) {
            f.setDirector(encrypt(f.getDirector(), key));
        }
        if (f.getRating() != null) {
            f.setRating(encrypt(f.getRating(), key));
        }

        return f;
    }

    public static Film decryptFilme(Film f) {
        if (f == null) {
            return null;
        }
        if (f.getTitle() != null) {
            f.setTitle(decrypt(f.getTitle(), key));
        }
        if (f.getType() != null) {
            f.setType(decrypt(f.getType(), key));
        }
        if (f.getCountry() != null) {
            f.setCountry(decrypt(f.getCountry(), key));
        }
        if (f.getDescription() != null) {
            f.setDescription(decrypt(f.getDescription(), key));
        }
        if (f.getDirector() != null) {
            f.setDirector(decrypt(f.getDirector(), key));
        }
        if (f.getRating() != null) {
            f.setRating(decrypt(f.getRating(), key));
        }

        return f;
    }
}