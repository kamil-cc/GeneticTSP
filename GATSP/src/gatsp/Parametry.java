package gatsp;

import java.util.Scanner;

/**
 *
 * @author Kamil Burzyński
 */
public class Parametry {

    public int rozmiarPopulacji;
    public float prawdopodobienstwoKrzyzowania;
    public float prawdopodobienstwoMutacji;
    public int liczbaGeneracji; //Po tylu iteracjach algorytm kończy działanie
    public int operator; //(1)OX, (2)PMX
    public int informowanie; //(1)nie, (2)tak
    private Scanner scanner;

    public Parametry() {
        rozmiarPopulacji = 0;
        prawdopodobienstwoKrzyzowania = 0;
        prawdopodobienstwoMutacji = 0;
        liczbaGeneracji = 0;
        operator = 0;
        informowanie = 0;
        scanner = new Scanner(System.in);
    }

    public int wczytaj() {
        try { //Kontrola czy użytkownik wprowadza odpowiednie dane
            System.out.println("Proszę podać rozmiar populacji:");
            rozmiarPopulacji = scanner.nextInt();
            if (rozmiarPopulacji <= 0) {
                return 1;
            }
            System.out.println("Proszę podać prawdopodobieństwo krzyżowania się:");
            prawdopodobienstwoKrzyzowania = scanner.nextFloat();
            if (prawdopodobienstwoKrzyzowania <= 0 || prawdopodobienstwoKrzyzowania > 1) {
                return 1;
            }
            System.out.println("Proszę podać prawdopodobieństwo mutacji:");
            prawdopodobienstwoMutacji = scanner.nextFloat();
            if (prawdopodobienstwoMutacji <= 0 || prawdopodobienstwoMutacji > 1) {
                return 1;
            }
            System.out.println("Proszę podać warunek zakończenia (ilość pokoleń):");
            liczbaGeneracji = scanner.nextInt();
            if (liczbaGeneracji <= 0) {
                return 1;
            }
            System.out.println("Proszę podać, którego operatora użyć: (1)OX, (2)PMX");
            operator = scanner.nextInt();
            if (operator <= 0 || operator > 2) {
                return 1;
            }
            System.out.println("Czy uruchomic tryb intensywnego wyswietlania informacji: (1)NIE, (2)TAK");
            informowanie = scanner.nextInt();
            if (informowanie <= 0 || informowanie > 2) {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
        return 0; //Wszystko w porządku
    }

    public void wypiszInformacje() {
        System.out.println("\nRozmiar populacji: " + rozmiarPopulacji);
        System.out.println("Prawdopodobieństwo krzyżowania: " + prawdopodobienstwoKrzyzowania);
        System.out.println("Prawdopodobieństwo mutacji: " + prawdopodobienstwoMutacji);
        System.out.println("Liczba generacji: " + liczbaGeneracji);
        System.out.println("Operator: (" + operator + ")" + ((operator == 1) ? "OX" : "PMX"));
        System.out.println("Tryb intensywnego informowania: (" + informowanie + ")" + ((informowanie == 1) ? "Nie" : "Tak") + "\n");
    }
}
