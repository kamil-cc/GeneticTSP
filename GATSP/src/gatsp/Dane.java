package gatsp;

import java.util.Scanner;

/**
 *
 * @author Kamil Burzyński
 */
public class Dane {

    public String nazwa; //Nazwa pliku
    public String komentarz; //Komentarz w pliku
    public int rozmiar; //Ilość danych wejściowych
    //
    public float[][] punkty; //Punkty danych wejściowych
    //
    private int blad; //Czy typ pliku jest odpowiedni (czy występuje błąd)

    public Dane() {
        nazwa = null;
        komentarz = new String();
        rozmiar = 0;
        blad = 0;
    }

    public int wczytaj() {
        Scanner scan = new Scanner(System.in); //Czytanie z wejścia
        String linia; //Przechowuje czytaną linię
        boolean naglowek = true; //Flaga: czytanie z nagłówka
        //
        while (scan.hasNext()) { //Dopóki dane są na wejściu
            linia = scan.nextLine(); //Pobranie jednej linii z wejścia
            if (linia.contains("EOF")) { //Jeśli koniec pliku, kończy wczytywanie z wejścia
                break;
            }
            if (naglowek) { 
                naglowek = czytajNaglowek(linia); //Czytanie nagłówka
            } else {
                czytajDane(linia); //Czytanie danych z wejścia
            }
            if (blad != 0) { //Sygnalizowanie błędu
                return blad;
            }
        }
        return 0;
    }

    public void wypiszInformacje() {
        System.out.println("Nazwa: " + nazwa);
        System.out.println("Komentarz: " + komentarz);
        System.out.println("Rozmiar danych: " + rozmiar + "\n");
    }

    private boolean czytajNaglowek(String linia) {
        if (linia.startsWith("NAME")) {
            nazwa = linia.substring(linia.indexOf(":") + 1).trim();
        } else if (linia.startsWith("TYPE")) {
            if (!(linia.substring(linia.indexOf(":") + 1).trim().equals("TSP"))) {
                blad = 1;
                return true; //Kończy funkcję z powodu błędu
            }
        } else if (linia.startsWith("COMMENT")) {
            komentarz += linia.substring(linia.indexOf(":") + 1).trim() + " ";
        } else if (linia.startsWith("DIMENSION")) {
            rozmiar = Integer.parseInt(linia.substring(linia.indexOf(":") + 1).trim());
        } else if (linia.startsWith("EDGE_WEIGHT_TYPE")) {
            if (!(linia.contains("EUC_2D"))) {
                blad = 2;
                return true; //Kończy funkcję z powodu błędu
            }
        } else if (linia.startsWith("NODE_COORD_SECTION")) {
            punkty = new float[rozmiar + 1][2]; //Numeracja ludzka
            return false; //Koniec nagłówka
        }
        return true; //Czytadalej nagłówek
    }

    private void czytajDane(String linia) { //Wczytywanie danych
        int element; //Która linia danych, 1,2,3...
        try {
            String[] argumenty = linia.split("\\s+"); //Dzieli linię po jednej lub więcej spacji
            if (linia.startsWith(" ")) { //Linia zaczyna się od spacji
                element = Integer.parseInt(argumenty[1]);
                punkty[element][0] = Float.parseFloat(argumenty[2]);
                punkty[element][1] = Float.parseFloat(argumenty[3]);
            } else { //Linia nie zaczyna się od spacji
                element = Integer.parseInt(argumenty[0]);
                punkty[element][0] = Float.parseFloat(argumenty[1]);
                punkty[element][1] = Float.parseFloat(argumenty[2]);
            }
        } catch (Exception e) {
            blad = 3;
        }
    }
}
