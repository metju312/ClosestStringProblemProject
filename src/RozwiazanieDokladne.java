import java.util.List;
import java.util.concurrent.TimeUnit;

public class RozwiazanieDokladne {

    private CSPHelper cspHelper;
    private List<Character> alfabet;
    private int bestHD;
    private String znaleziony = "";
    private long pamiecMax = 0;

    public RozwiazanieDokladne(CSPHelper cspHelper) {
        this.cspHelper = cspHelper;
        this.alfabet = cspHelper.getAlfabet();
        this.bestHD = cspHelper.getListS().get(0).length() + 1;
    }

    public String rozwiaz() {
        //TimeUnit.SECONDS.sleep(5);
        dostawZnak("", bestHD - 1);

        return znaleziony;
    }

     //Sumaryczna zlozonosc obliczeniowa wywolan metody: |A|^(m-1) * (n * (1 + 5m) + 12) + ((1-|A|^m)/(1-|A|) - |A|^(m-1)) * (|4A| + 8 )
    void dostawZnak(String s, int i){ // n * (1 + 5m) + 12 dla i == 0 ; 4|A| + 8 ; metoda rekurencyjna, wywolywana (1-|A|^m)/(1-|A|) razy
        // |A|^(m-1) * (n * (1 + 5m) + 12) ; |A|^(m-1) wywołan o i == 0
        // ((1-|A|^m)/(1-|A|) - |A|^(m-1)) * (|4A| + 8) ; (1-|A|^m)/(1-|A|) - |A|^(m-1) wywolan o i != 0
        long pamiec = dajUzyciePamieci(); // 3
        if(pamiecMax < pamiec){ // 1
            pamiecMax = pamiec; // 1
        }

        if (i == 0) { // 1 ; true: n * (1 + 5m) + 7 ; false : 4|A| + 3
            if (cspHelper.sprawdzHD(s) < bestHD) { // 1

                bestHD = cspHelper.sprawdzHD(s); // n * (1 + 5m) + 4
                znaleziony = s; //1
            }
        } else{ // 4|A| + 2
            String konstruowany; //1
            for(Character znak : alfabet){ // |A| * 4 ; 4 = 1 + 1 + 1 + 1 ; 1 - przypisanie
                konstruowany = s; //1
                konstruowany+=znak; // 1
                dostawZnak(konstruowany, i-1); // 1 ; wywolanie rekurencyjne (1-|A|^m)/(1-|A|) - 1 razy
            }
        }
    }

    private static long dajUzyciePamieci(){ // 3
        int mb = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / mb;
    }

    public long dajPamiec() {
        return pamiecMax;
    }
}
