package gatsp;

import java.util.*;

/**
 *
 * @author Kamil Burzyński
 */
public class Algorytm {

    private int[][] populacja; //Tablica osobników w populacji
    private float[] dopasowanie; //Długość trasy każdego osobnika
    private float[] cel; //Wartości funkcji celu czyli duzaStala - dopasowanie[i]
    private float duzaStala; //Stała potrzebna do zamiany minimalizacji na maksymalizację
    private int t; //Numer pokolenia
    private Random rand; //Referencja na obiekt generatora liczb losowych
    private boolean[] uzyteWierzcholki; //Tablica pomocnicza
    private int pom; //Zmienna pomocnicza
    //
    private Dane dane; //Referencja na dane
    private Parametry parametry; //Referencja na parametry

    public Algorytm(Dane dane, Parametry parametry) {
        this.dane = dane;
        this.parametry = parametry;
        rand = new Random();
        pom = 0; //Ustawianie wartości pomocniczej na zero
    }

    public void wykonaj() { //Rozpoczyna wykonanie algorytmu
        t = 0;
        stworzPopulacje();
        liczDopasowanie();
        wyswietlStatystyki();
        while (t < parametry.liczbaGeneracji) {
            t = t + 1;
            selekcja();
            krzyzowanie();
            mutacja();
            liczDopasowanie();
            wyswietlStatystyki();
        }
    }

    private void stworzPopulacje() {
        populacja = new int[parametry.rozmiarPopulacji][dane.rozmiar]; //Tablca rozmiarPopulacji x rozmiarOsobnika
        for (int i = 0; i < parametry.rozmiarPopulacji; ++i) {
            for (int j = 0; j < dane.rozmiar; ++j) {
                populacja[i][j] = losuj(); //Losowanie właściwych osobników
            }
            pom = 0;
        }
        dopasowanie = new float[parametry.rozmiarPopulacji]; //Inicjalizacja zerami
        cel = new float[parametry.rozmiarPopulacji]; //Inicjalizacja zerami
    }

    private int losuj() {  //Losowanie inteligentne
        int losowa;
        if (pom == 0) {
            uzyteWierzcholki = new boolean[dane.rozmiar];
            losowa = rand.nextInt(dane.rozmiar);
            uzyteWierzcholki[losowa] = true;
            pom++;
            return losowa + 1; //Liczba lososwa od 1 do dane.rozmiar
        }
        int nowaGranica = dane.rozmiar - pom;
        int[] cyfry = new int[nowaGranica];
        int j = 0; //Indeks drugiej tablicy
        for (int i = 0; i < dane.rozmiar; ++i) { //Tworzenie odwzorowania
            if (uzyteWierzcholki[i] == false) {
                cyfry[j++] = i;
            }
        }
        losowa = rand.nextInt(nowaGranica);
        uzyteWierzcholki[cyfry[losowa]] = true;
        pom++;
        return cyfry[losowa] + 1; //Liczba losowa od 1 do dane.rozmiar z pominięciem już wylosowanych
    }

    private void liczDopasowanie() { //Liczy długość trasy i funkcję celu
        float maxElement = 0; //Osobnik z najdłuższą trasą
        dopasowanie = new float[parametry.rozmiarPopulacji]; //Nowa tablica długości tras
        for (int i = 0; i < parametry.rozmiarPopulacji; ++i) {
            for (int j = 0; j < (dane.rozmiar - 1); ++j) {
                dopasowanie[i] += liczOdleglosc(populacja[i][j], populacja[i][j + 1]);
            }
            if (dopasowanie[i] > maxElement) {
                maxElement = dopasowanie[i];
            }
        }
        duzaStala = maxElement + 1; //Stała to największy osobnik + 1
        cel = new float[parametry.rozmiarPopulacji]; //Nowa tablica wartości funkcji celu
        for (int i = 0; i < parametry.rozmiarPopulacji; ++i) {
            cel[i] = duzaStala - dopasowanie[i];
        }
    }

    private float liczOdleglosc(int odM, int doM) {
        double odleglosc = Math.pow(dane.punkty[odM][0] - dane.punkty[doM][0], 2)
                + Math.pow(dane.punkty[odM][1] - dane.punkty[doM][1], 2);
        return (float) Math.sqrt(odleglosc);
    }

    private void selekcja() {
        int[][] poSelekcji = new int[parametry.rozmiarPopulacji][dane.rozmiar]; //Populacja po selekcji
        float[] kumulacyjny = new float[parametry.rozmiarPopulacji]; //Rozkład kumulacyjny
        float suma = 0; //Suma wszystkich wartości funkcji celu
        float pomocnicza = 0; //Zmienna pomocnicza
        float losowa; //Wynik losowania
        int przechodzi = 0; //Ten osobnik przechodzi selekcję

        //Liczenie sumy wartości funkcji celu dla wszystkich osobników
        for (int i = 0; i < parametry.rozmiarPopulacji; i++) {
            suma += cel[i];
        }

        //Liczenie rozkładu kumulacyjnego
        for (int i = 0; i < parametry.rozmiarPopulacji; i++) {
            pomocnicza += cel[i] / suma;
            kumulacyjny[i] = pomocnicza;
        }

        //Selekcja stochastyczna
        float lewaGranica; //Lewa granica przedziału
        for (int i = 0; i < parametry.rozmiarPopulacji; ++i) { //Dla każdego nowego osobnika
            losowa = rand.nextFloat();
            for (int j = 0; j < parametry.rozmiarPopulacji; ++j) { //Dla każdego przedziału
                if (j == 0) {
                    lewaGranica = 0;
                } else {
                    lewaGranica = kumulacyjny[j - 1];
                }
                if (lewaGranica <= losowa && losowa < kumulacyjny[j]) {
                    przechodzi = j;
                }
            }
            //Kopiowanie tablic
            System.arraycopy(populacja[przechodzi], 0, poSelekcji[i], 0, dane.rozmiar);
        }
        //Zastąpienie starych osobników nowymi
        populacja = poSelekcji;
    }

    private void krzyzowanie() {
        int[] wybrane = new int[parametry.rozmiarPopulacji]; //Tablica z numerami osobników wybranymi do krzyżowania
        Arrays.fill(wybrane, -1); //Wypełnianie całej tablicy przez -1
        float losowa;
        int j = 0; //Licznik 2 tablicy
        for (int i = 0; i < parametry.rozmiarPopulacji; ++i) {
            losowa = rand.nextFloat();
            if (losowa <= parametry.prawdopodobienstwoKrzyzowania) { //Jeśli warunek spełniony, osobnik zostaje rodzicem
                wybrane[j++] = i;
            }
        }

        //Dobieranie rodziców w pary
        int[][] pary = new int[(int) Math.floor(j / 2.0)][2];
        int k = 0;
        int los;
        int zakres = j;
        //Sprawdzanie czy liczba rodziców jest parzysta, jeśli nie zmniejszanie o 1
        if (j % 2 == 1) {
            j--;
        }
        while (k < j) {
            do {
                los = rand.nextInt(zakres); //Losowanie rodzica do pary
            } while (wybrane[los] == -1);
            //Wstawianie rodziców do tablicy
            pary[(int) Math.floor(k / 2.0)][k % 2] = wybrane[los];
            wybrane[los] = -1; //Zamazywanie poprzedniej wartości
            k++;
        }

        //Wykonaj krzyzowanie
        switch (parametry.operator) {
            case 1: //OX
                for (int i = 0; i < j / 2; ++i) { //Krzyżowanie dla każdych dwóch rodziców
                    k_ox(pary[i][0], pary[i][1]);
                }
                if (parametry.informowanie == 2) {
                    for (int i = 0; i < j / 2; ++i) {
                        System.out.println("Krzyżowanie osobników: " + pary[i][0] + " z " + pary[i][1]);
                    }
                }
                break;
            case 2: //PMX
                for (int i = 0; i < j / 2; ++i) { //Krzyżowanie dla każdych dwóch rodziców
                    k_pmx(pary[i][0], pary[i][1]);
                }
                if (parametry.informowanie == 2) {
                    for (int i = 0; i < j / 2; ++i) {
                        System.out.println("Krzyżowanie osobników: " + pary[i][0] + " z " + pary[i][1]);
                    }
                }
                break;
        }
    }

    private void k_ox(int rodzic1, int rodzic2) {
        int[][] potomkowie = new int[2][dane.rozmiar];
        //
        int ciecie1 = rand.nextInt(dane.rozmiar + 1); //Miejsce 1 cięcia
        int ciecie2;
        do {
            ciecie2 = rand.nextInt(dane.rozmiar + 1); //Miejsce 2 cięcia
        } while (ciecie1 == ciecie2); //Tu można dodać warunek, np odległość między granicami cięcia

        if (ciecie2 < ciecie1) {
            int pomocnicza = ciecie1;
            ciecie1 = ciecie2;
            ciecie2 = pomocnicza;
        }

        //Przepisywanie do potomków (rodzic 1 do pierwszego potomka i rodzic 2 do drugiego potomka)
        for (int i = ciecie1; i < ciecie2; ++i) {
            potomkowie[0][i] = populacja[rodzic1][i];
            potomkowie[1][i] = populacja[rodzic2][i];
        }

        //Przepisywanie wierzchołków do list (w odpowiedniej kolejności)
        ArrayList<Integer> lista1 = new ArrayList<>();
        ArrayList<Integer> lista2 = new ArrayList<>();
        for (int i = 0; i < dane.rozmiar; i++) {
            lista1.add(populacja[rodzic2][(i + ciecie2) % dane.rozmiar]);
            lista2.add(populacja[rodzic1][(i + ciecie2) % dane.rozmiar]);
        }

        //Usuwanie elementów list, które już znajdują się w potomkach
        for (int i = ciecie1; i < ciecie2; i++) {
            lista1.remove(lista1.indexOf(potomkowie[0][i]));
            lista2.remove(lista2.indexOf(potomkowie[1][i]));
        }

        //Wpisywanie list do potomków
        int rozmiarListy = lista1.size();
        for (int i = 0; i < rozmiarListy; i++) {
            potomkowie[0][(i + ciecie2) % dane.rozmiar] = lista1.remove(0);
            potomkowie[1][(i + ciecie2) % dane.rozmiar] = lista2.remove(0);
        }

        //Kopiowanie wyników krzyżowania z potomków na rodziców
        System.arraycopy(potomkowie[0], 0, populacja[rodzic1], 0, dane.rozmiar);
        System.arraycopy(potomkowie[1], 0, populacja[rodzic2], 0, dane.rozmiar);
        spr(rodzic1, rodzic2);
    }

    private void k_pmx(int rodzic1, int rodzic2) {
        int[][] potomkowie = new int[2][dane.rozmiar];
        //
        int ciecie1 = rand.nextInt(dane.rozmiar + 1); //Miejsce 1 cięcia
        int ciecie2;
        do {
            ciecie2 = rand.nextInt(dane.rozmiar + 1); //Miejsce 2 cięcia
        } while (ciecie1 == ciecie2); //Tu można dodać warunek, np odległość między granicami cięcia

        if (ciecie2 < ciecie1) {
            int pomocnicza = ciecie1;
            ciecie1 = ciecie2;
            ciecie2 = pomocnicza;
        }

        //Przepisywanie do potomków (rodzic 1 do pierwszego potomka i rodzic 2 do drugiego potomka)
        for (int i = ciecie1; i < ciecie2; ++i) {
            potomkowie[0][i] = populacja[rodzic1][i];
            potomkowie[1][i] = populacja[rodzic2][i];
        }

        //Tworzenie list wierzchołków obecnych w 1 potomku i nieobecnych w drugim rodzicu
        boolean obecny1;
        boolean obecny2;
        HashMap<Integer, Integer> listaNieobecnych1 = new HashMap<>();
        HashMap<Integer, Integer> listaNieobecnych2 = new HashMap<>();
        for (int i = ciecie1; i < ciecie2; i++) {
            obecny1 = false;
            obecny2 = false;
            for (int j = ciecie1; j < ciecie2; j++) {
                if (populacja[rodzic2][i] == potomkowie[0][j]) {
                    obecny1 = true;
                }
                if (populacja[rodzic1][i] == potomkowie[1][j]) {
                    obecny2 = true;
                }
            }
            if (obecny1 == false) {
                listaNieobecnych1.put(i, populacja[rodzic2][i]);
            }
            if (obecny2 == false) {
                listaNieobecnych2.put(i, populacja[rodzic1][i]);
            }
        }

        //Dla każdej liczby z w/w listy
        for (Map.Entry<Integer, Integer> entry : listaNieobecnych1.entrySet()) {
            int klucz = entry.getKey();
            int wartosc = entry.getValue();
            int indeks;
            do {
                indeks = populacja[rodzic1][klucz];
                for (int i = 0; i < dane.rozmiar; i++) {
                    if (populacja[rodzic2][i] == indeks) {
                        klucz = i;
                    }
                }
            } while (!(klucz < ciecie1 || klucz >= ciecie2));
            potomkowie[0][klucz] = wartosc;
        }

        //j.w dla drugiego potomka
        for (Map.Entry<Integer, Integer> entry : listaNieobecnych2.entrySet()) {
            int klucz = entry.getKey();
            int wartosc = entry.getValue();
            int indeks;
            do {
                indeks = populacja[rodzic2][klucz];
                for (int i = 0; i < dane.rozmiar; i++) {
                    if (populacja[rodzic1][i] == indeks) {
                        klucz = i;
                    }
                }
            } while (!(klucz < ciecie1 || klucz >= ciecie2));
            potomkowie[1][klucz] = wartosc;
        }

        //Uzupełnianie braków odpowiednimi wierzchołkami z rodziców
        for (int i = 0; i < dane.rozmiar; i++) {
            if (potomkowie[0][i] == 0) {
                potomkowie[0][i] = populacja[rodzic2][i];
            }
            if (potomkowie[1][i] == 0) {
                potomkowie[1][i] = populacja[rodzic1][i];
            }
        }

        //Kopiowanie wyników krzyżowania na rodziców
        System.arraycopy(potomkowie[0], 0, populacja[rodzic1], 0, dane.rozmiar);
        System.arraycopy(potomkowie[1], 0, populacja[rodzic2], 0, dane.rozmiar);
        spr(rodzic1, rodzic2);
    }

    private void mutacja() {
        float losowa;
        int pomocnicza;
        for (int i = 0; i < parametry.rozmiarPopulacji; i++) { //Dla każdego osobnika populacji
            for (int j = 0; j < dane.rozmiar; j++) { //Dla każdego wierzchołka w osobniku
                losowa = rand.nextFloat();
                if (losowa <= parametry.prawdopodobienstwoMutacji) { //Jeżeli warunek jest spełniony wtedy następuje mutacja
                    int nowaPozycja;
                    do {
                        nowaPozycja = rand.nextInt(dane.rozmiar);
                    } while (nowaPozycja == j);
                    pomocnicza = populacja[i][j];
                    populacja[i][j] = populacja[i][nowaPozycja];
                    populacja[i][nowaPozycja] = pomocnicza;

                    if (parametry.informowanie == 2) {
                        System.out.println("[osobnik " + i + "] "
                                + "Wierzchołek na pozycji: " + j
                                + " uległ mutacji (zamiana z pozycją " + nowaPozycja + ")");
                    }
                }
            }
        }
    }

    private void wyswietlStatystyki() {
        float suma = 0;
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        int idx = -1;
        for (int i = 0; i < parametry.rozmiarPopulacji; i++) {
            if (dopasowanie[i] < min) {
                min = dopasowanie[i];
                idx = i;
            }
            if (dopasowanie[i] > max) {
                max = dopasowanie[i];
            }
            suma += dopasowanie[i];
        }
        float srednia = suma / (float) parametry.rozmiarPopulacji;

        if (parametry.informowanie == 2) {
            System.out.println("\n-------------------------------------------------------\n");
            System.out.println("Pokolenie: " + t);
            for (int i = 0; i < parametry.rozmiarPopulacji; i++) {
                System.out.println("Osobnik (" + i + "): "
                        + Arrays.toString(populacja[i]) + " Długość trasy: "
                        + dopasowanie[i] /*
                         * + " Prawdopodobieństwo: ???"
                         */); //POPRAWIĆ, ŻEBY BYŁO WIDOCZNE PRAWDOPODOBIEŃSTWO!!!!!!!!!!!!!!!
            }
            System.out.println("Indeks minimalnego elemenu: " + idx);
            System.out.println("Minimum: " + min);
            System.out.println("Średnia: " + srednia);
            System.out.println("Maximum: " + max);
        } else {
            System.out.println("Pokolenie: " + t);
            System.out.println("Minimum: " + min);
            System.out.println("Średnia: " + srednia);
            System.out.println("Maximum: " + max);
            System.out.println("");
        }
    }

    private void spr(int rodzic1, int rodzic2) { //NA SAMYM KOŃCU WYCZYŚCIĆ KOD PONIŻEJ!!!!!!!!!!!!!!!!!!!!!
        boolean[] spr1 = new boolean[dane.rozmiar + 1];
        boolean[] spr2 = new boolean[dane.rozmiar + 1];
        for (int i = 0; i < dane.rozmiar; i++) {
            if (populacja[rodzic1][i] != 0) {
                if (spr1[populacja[rodzic1][i]] == false) {
                    spr1[populacja[rodzic1][i]] = true;
                } else {
                    System.out.println("RODZIC1 BŁĄD"); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    System.exit(1); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    int losowa;
                    do {
                        losowa = rand.nextInt(dane.rozmiar) + 1;
                    } while (spr1[losowa]);
                    populacja[rodzic1][i] = losowa;
                    spr1[losowa] = true;
                }
            } else {
                System.out.println("ZERO W R1 BŁĄD"); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                System.exit(1); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                int losowa;
                do {
                    losowa = rand.nextInt(dane.rozmiar) + 1;
                } while (spr1[losowa]);
                populacja[rodzic1][i] = losowa;
                spr1[losowa] = true;
            }
            if (populacja[rodzic2][i] != 0) {
                if (spr2[populacja[rodzic2][i]] == false) {
                    spr2[populacja[rodzic2][i]] = true;
                } else {
                    System.out.println("RODZIC2 BŁĄD"); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    System.exit(1); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    int losowa;
                    do {
                        losowa = rand.nextInt(dane.rozmiar) + 1;
                    } while (spr2[losowa]);
                    populacja[rodzic2][i] = losowa;
                    spr2[losowa] = true;
                }
            } else {
                System.out.println("ZERO W R2 BŁĄD"); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                System.exit(1); //USUNĄĆ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                int losowa;
                do {
                    losowa = rand.nextInt(dane.rozmiar) + 1;
                } while (spr2[losowa]);
                populacja[rodzic2][i] = losowa;
                spr2[losowa] = true;
            }
        }
    }
}
