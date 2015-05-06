# GeneticTSP
![alt tag](https://raw.githubusercontent.com/kamil-cc/GeneticTSP/master/screen.png)  
Genetic algorithm used to solve TSP problem [Java]  
Use this script to visualise results:  
https://gist.github.com/kamil-cc/ada9411a1880a094cf2a  
  
Program służy do rozwiązywania problemu komiwojażera (każdy wierzchołek w grafie odwiedzany dokładnie jeden raz).  
Aplikacja wykorzystuje algorytm genetyczny ponieważ przeszukanie pełnej przestrzeni rozwiązań jest czasochłonne: (n-1)!/2  
Podczas stosowania GA napotkałem problem reprezentacji pojedynczego osobnika. Zamiast reprezentacji binarnej (która wydaje się być naturalna dla GA), zastosowałem reprezentację ścieżkową: trasa 1-3-5-7-9-2 jest reprezentowana przez osobnik (1, 3, 5, 7, 9, 2).  
Dane wejściowe przyjmowane są w formacie, który jest rozpowszechniony w internecie. Przykładowe pliki wejściowe można znaleźć na stronie: http://comopt.ifi.uni-heidelberg.de/software/TSPLIB95/  
Krzyżowanie zostaje wykonane poprzez operator OX lub PMX w zależności od wyboru użytkownika. 
Program pozwala też na ustalenie innych parametrów algorytmu takich jak: rozmiar populacji, prawdopodobieństwo krzyżowania się, prawdopodobieństwo mutacji oraz ilość pokoleń po osiągnięciu której zakończy się działanie programu.  
Do graficznej prezentacji wyników służy program (uruchamiany w RStudio): https://gist.github.com/kamil-cc/ada9411a1880a094cf2a  

