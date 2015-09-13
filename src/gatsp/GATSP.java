package gatsp;

/**
 *
 * @author Kamil Burzyński
 */
public class GATSP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) { 
        
        System.out.println("Proszę podać dane wejściowe:");
        Dane dane = new Dane(); //Obiekt, do którego wczytujs się dane wejściowe
        int rezultat = dane.wczytaj();
        if (rezultat == 1) { //Wyświetla komunikat jeśli niepoprawne dane
            System.out.println("TO NIE JEST PROBLEM TSP");
            return;
        } else if (rezultat == 2) {
            System.out.println("NIEPRAWIDŁOWY TYP WAGI KRAWĘDZI");
            return;
        }else if(rezultat == 3){
            System.out.println("NIEPOPRAWNY FORMAT DANYCH WEJŚCIOWYCH");
            return;
        }
        
        dane.wypiszInformacje(); //Wyświetla ile danych wczytano, nazwę pliku i komentarz
        
        Parametry parametry = new Parametry(); //Obiekt przechowujący rozmiar populacji, prawdop. krzyżowania itp.
        rezultat = parametry.wczytaj(); //Żądanie parametrów od użytkownika
        if(rezultat == 1){ //Jeśli wystąpił błąd wyświetla stosowny komunikat
            System.out.println("PODANO ZŁE PARAMETRY");
            return;
        }
        
        parametry.wypiszInformacje(); //Wyświetla jakie dane wprowadził użytkownik
        
        Algorytm algorytm = new Algorytm(dane, parametry); //Stworzenie obiektu klasy Algorytm
        algorytm.wykonaj(); //Główny algorytm programu
    }
}
