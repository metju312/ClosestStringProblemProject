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

    private static int razyADokladny = 100;
    private static int razyAHeurystyczny = 100;

    private static long pamiecDokladny = 0;
    private static long pamiecHeurystyczny = 0;

    private static XYSeries dokladnyDane = new XYSeries("Dokladny");
    private static XYSeries heurystycznyDane = new XYSeries("Heurystyczny");

    private static XYSeries dokladnyPamiec = new XYSeries("Pamiec Dokladny");
    private static XYSeries heurystycznyPamiec = new XYSeries("Pamiec Heurystyczny");

    //parametry: [dlugoscAlfabetu] [dlugoscCiagowWejsciowych] [liczbaCiagowWejsciowych]
    public static void main(String[] args) throws IOException {
        argumenty = args;
        TaskGenerator.main(args);
        wczytajIRozwiazZadania();
        generujWykresDanych();
        generujWykresPamieci();
    }

    private static void generujWykresPamieci() {
        XYSeriesCollection collection = new XYSeriesCollection(dokladnyPamiec);
        collection.addSeries(heurystycznyPamiec);
        ChartFrame.generateChart(collection);
    }

    private static void generujWykresDanych() {
        XYSeriesCollection collection = new XYSeriesCollection(dokladnyDane);
        collection.addSeries(heurystycznyDane);
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
        dokladnyDane.add(iterator, totalTime);
        dokladnyPamiec.add(iterator, pamiecDokladny);


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
        heurystycznyDane.add(iterator, totalTime);
        heurystycznyPamiec.add(iterator, pamiecHeurystyczny);

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
    }


    private static void sprawdzRozwiazanieHeurystyczne() {
        ACO aco = new ACO(listS, alfabet, 80, 40, cspHelper);
        String rozwiazanie = aco.znajdzNajblizszyString();
        long pamiec = aco.dajPamiec();
        if(pamiecHeurystyczny < pamiec){
            pamiecHeurystyczny = pamiec;
        }
        System.out.print(rozwiazanie + ", HD = " + cspHelper.sprawdzHD(rozwiazanie) + " ");
    }

    private static boolean czyDlugoscAlfabetuToParametr(){
        if(Integer.parseInt(argumenty[0]) >= Integer.parseInt(argumenty[1]) && Integer.parseInt(argumenty[0]) >= Integer.parseInt(argumenty[2])){
            return true;
        }
        return false;
    }
}
