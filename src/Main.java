import org.apache.commons.io.FilenameUtils;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static CSPHelper cspHelper = new CSPHelper();
    private static List<Character> alfabet;
    private static List<String> listS;
    private static String[] argumenty;

    private static int iterator = 2;

    private static int sumaHDDokladny = 0;
    private static int sumaHDHeurystyczny = 0;

    private static long pamiecDokladny = 0;
    private static long pamiecHeurystyczny = 0;

    private static XYSeries dokladnyDane = new XYSeries("Dokladny");
    private static XYSeries heurystycznyDane = new XYSeries("Heurystyczny");

    private static XYSeries dokladnyDaneT = new XYSeries("Dokladny teoretyczny");
    private static XYSeries heurystycznyDaneT = new XYSeries("Heurystyczny teoretyczny");

    private static XYSeries dokladnyPamiec = new XYSeries("Pamiec Dokladny");
    private static XYSeries heurystycznyPamiec = new XYSeries("Pamiec Heurystyczny");

    private static XYSeries dokladnyPamiecT = new XYSeries("Teoretyczna Pamiec A. Dokladny");
    private static XYSeries heurystycznyPamiecT = new XYSeries("Teoretyczna Pamiec A. Heurystyczny");


    private static long mnoznikHeurystycznyPamiec = 20;

    //8 5 5
    private static int razyADokladny = 2;
    private static int razyAHeurystyczny = 200;
    private static long mnoznikDokladny = 90000;
    private static long mnoznikHeurystyczny = 1500;
    private static long obnizenieHeurystycznyT = 1;

    //5 6 5
//    private static int razyADokladny = 2;
//    private static int razyAHeurystyczny = 200;
//    private static long mnoznikDokladny = 150000;
//    private static long mnoznikHeurystyczny = 1000;
//    private static long obnizenieHeurystycznyT = 1;

    //5 5 8
//    private static int razyADokladny = 2;
//    private static int razyAHeurystyczny = 200;
//    private static long mnoznikDokladny = 1111190000;
//    private static long mnoznikHeurystyczny = 1500;
//    private static long obnizenieHeurystycznyT = 1;


    //parametry: [dlugoscAlfabetu] [dlugoscCiagowWejsciowych] [liczbaCiagowWejsciowych]
    public static void main(String[] args) throws IOException {
        argumenty = args;
        TaskGenerator.main(args);
        wczytajIRozwiazZadania();
        generujWykresPamieci();
        generujWykresDanych();
    }

    private static void generujWykresPamieci() {
        XYSeriesCollection collection = new XYSeriesCollection(dokladnyPamiec);
        collection.addSeries(dokladnyPamiecT);
        collection.addSeries(heurystycznyPamiec);
        collection.addSeries(heurystycznyPamiecT);
        ChartFrame.generateChart(collection);
    }

    private static void generujWykresDanych() {
        XYSeriesCollection collection = new XYSeriesCollection(dokladnyDane);
        collection.addSeries(dokladnyDaneT);
        collection.addSeries(heurystycznyDane);
        collection.addSeries(heurystycznyDaneT);
        ChartFrame.generateChart(collection);
    }

    private static void wczytajAlfabet() throws IOException {
        File alfabetFile = new File("alfabet.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(alfabetFile));
        alfabet = new ArrayList<>();
        String line = null;

        while ((line = bufferedReader.readLine()) != null) {
            alfabet.add(line.charAt(0));
            if(czyDlugoscAlfabetuToParametr()){
                if(iterator == alfabet.size()){
                    break;
                }
            }
        }
        bufferedReader.close();
        cspHelper.setAlfabet(alfabet);
    }

    private static void wczytajIRozwiazZadania() throws IOException {
        File folderZadan = new File("zadania");
        for(File file: folderZadan.listFiles()){
            wczytajAlfabet();
            System.out.println(FilenameUtils.getBaseName(file.getName()));
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            listS = new ArrayList<>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                listS.add(line);
            }
            bufferedReader.close();
            cspHelper.setListS(listS);

            rozwiazZadanie();
        }
        sumaHDDokladny *= razyAHeurystyczny/razyADokladny;
        System.out.println("Suma HD: \n Dokladny: " + sumaHDDokladny + "\n Heurystyczny: " + sumaHDHeurystyczny + "\n Roznica: " + (sumaHDHeurystyczny - sumaHDDokladny));
    }

    private static void rozwiazZadanie() {
        System.out.print("D: ");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < razyADokladny; i++) {
            sprawdzRozwiazanieDokladne();
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.print(", T: " + totalTime/1000.0 + "s");
        System.out.println(", P: " + pamiecDokladny + " MB");
        RozwiazanieDokladne rozwiazanieDokladne = new RozwiazanieDokladne(cspHelper);//do obliczenia zlozonosci teoretycznej
        dokladnyDane.add(iterator, totalTime*mnoznikDokladny);
        dokladnyDaneT.add(iterator, rozwiazanieDokladne.dajZlozonoscObliczeniowaOczekiwana());
        dokladnyPamiec.add(iterator, pamiecDokladny);
        dokladnyPamiecT.add(iterator, rozwiazanieDokladne.dajPamiecOczekiwana());


        System.out.print("H: ");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < razyAHeurystyczny; i++) {
            sprawdzRozwiazanieHeurystyczne();
        }
        endTime = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.print(", T: " + totalTime/1000.0 + "s");
        System.out.println(", P: " + pamiecHeurystyczny + " MB");
        System.out.println("-----------------------------------------------");
        ACO aco = new ACO(listS, alfabet, 80, 40, cspHelper);//do obliczenia zlozonosci teoretycznej
        heurystycznyDane.add(iterator, totalTime*mnoznikHeurystyczny);
        heurystycznyDaneT.add(iterator, aco.dajZlozonoscObliczeniowaOczekiwana()/obnizenieHeurystycznyT);
        heurystycznyPamiec.add(iterator, pamiecHeurystyczny*mnoznikHeurystycznyPamiec);
        heurystycznyPamiecT.add(iterator, aco.dajPamiecOczekiwana());

        iterator++;
    }

    private static void sprawdzRozwiazanieDokladne() {
        RozwiazanieDokladne rozwiazanieDokladne = new RozwiazanieDokladne(cspHelper);
        String rozwiazanie = rozwiazanieDokladne.rozwiaz();
        long pamiec = rozwiazanieDokladne.dajPamiec();
        if(pamiecDokladny < pamiec){
            pamiecDokladny = pamiec;
        }
        System.out.print(rozwiazanie + ", HD = " + cspHelper.sprawdzHD(rozwiazanie) + " ");
        sumaHDDokladny += cspHelper.sprawdzHD(rozwiazanie);
    }

    private static void sprawdzRozwiazanieHeurystyczne() {
        ACO aco = new ACO(listS, alfabet, 80, 40, cspHelper);
        String rozwiazanie = aco.znajdzNajblizszyString();
        long pamiec = aco.dajPamiec();
        if(pamiecHeurystyczny < pamiec){
            pamiecHeurystyczny = pamiec;
        }
        System.out.print(rozwiazanie + ", HD = " + cspHelper.sprawdzHD(rozwiazanie) + " ");
        sumaHDHeurystyczny += cspHelper.sprawdzHD(rozwiazanie);
    }

    private static boolean czyDlugoscAlfabetuToParametr(){
        if(Integer.parseInt(argumenty[0]) >= Integer.parseInt(argumenty[1]) && Integer.parseInt(argumenty[0]) >= Integer.parseInt(argumenty[2])){
            return true;
        }
        return false;
    }
}
