import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TaskGenerator {
    private static List<Character> alfabet;
    private static int dlugoscAlfabetu; //<1, 52>
    private static int dlugoscCiagowWejsciowych;
    private static int liczbaCiagowWejsciowych;
    private static int maxDlugoscAlfabetu = 52;
    private static int liczbaZadan;
    private static int maxWartoscParametru = 10;
    private static Parametr parametr;

    public enum Parametr {
        ALFABET, DLUGOSCCIAGOW, LICZBACIAGOW
    }

    //parametry: [dlugoscAlfabetu] [dlugoscCiagowWejsciowych] [liczbaCiagowWejsciowych]
    public static void main(String[] args) throws IOException {
        wczytajParametry(args);
        generujZadania();
    }

    private static void wczytajParametry(String[] args) {
        dlugoscAlfabetu = Integer.parseInt(args[0]);
        dlugoscCiagowWejsciowych = Integer.parseInt(args[1]);
        liczbaCiagowWejsciowych = Integer.parseInt(args[2]);
        if(dlugoscAlfabetu >= dlugoscCiagowWejsciowych && dlugoscAlfabetu >= liczbaCiagowWejsciowych){
            parametr = Parametr.ALFABET;
            maxWartoscParametru = dlugoscAlfabetu;
            dlugoscAlfabetu = 2;
        }else if (dlugoscCiagowWejsciowych >= dlugoscAlfabetu && dlugoscCiagowWejsciowych >= liczbaCiagowWejsciowych){
            parametr = Parametr.DLUGOSCCIAGOW;
            maxWartoscParametru = dlugoscCiagowWejsciowych;
            dlugoscCiagowWejsciowych = 2;
        }else{
            parametr = Parametr.LICZBACIAGOW;
            maxWartoscParametru = liczbaCiagowWejsciowych;
            liczbaCiagowWejsciowych = 2;
        }
        liczbaZadan = maxWartoscParametru;
    }

    private static void generujAlfabet() throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("alfabet.txt", "UTF-8");
        alfabet = new ArrayList<>();
        for (int i = 0, j; i < dlugoscAlfabetu; i++)
        {
            char znak;
            if(i<26){
                znak = (char) ('a' + i);
            }else{
                j = i-26;
                znak = (char) ('A' + j);
            }
            alfabet.add(znak);
            writer.println(znak);
        }
        writer.close();
    }

    private static void generujZadania() throws IOException {
        usunPoprzednieZadania();
        for (int i = 0; i < liczbaZadan; i++) {
            generujAlfabet();
            PrintWriter writer = new PrintWriter("zadania/zad " + (char)('a' + i) + ".txt", "UTF-8");
            for (int j = 0; j < liczbaCiagowWejsciowych; j++)
            {
                writer.println(generujCiag());
            }
            writer.close();

            modyfikujParametr();
        }
    }

    private static void modyfikujParametr() {
        switch (parametr){
            case ALFABET: dlugoscAlfabetu++;
            break;
            case DLUGOSCCIAGOW: dlugoscCiagowWejsciowych++;
            break;
            case LICZBACIAGOW: liczbaCiagowWejsciowych++;
            break;
        }
    }

    private static String generujCiag() {
        String ciag = "";
        Random random = new Random();
        for (int i = 0; i < dlugoscCiagowWejsciowych; i++) {
            int randomNumber = random.nextInt(dlugoscAlfabetu) + 1;
            ciag+=alfabet.get(randomNumber-1);
        }
        return ciag;
    }

    private static void usunPoprzednieZadania() throws IOException {
        File folderZadan = new File("zadania");
        FileUtils.cleanDirectory(folderZadan);
        for(File file: folderZadan.listFiles())
            file.delete();
    }
}
