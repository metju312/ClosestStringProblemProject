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

    void dostawZnak(String s, int i){
        // TODO pomiar pamięci
        long pamiec = dajUzyciePamieci();
        if(pamiecMax < pamiec){
            pamiecMax = pamiec;
        }

        if (i == 0) {
            if (cspHelper.sprawdzHD(s) < bestHD) {

                bestHD = cspHelper.sprawdzHD(s);
                znaleziony = s;
            }
        } else{
            String konstruowany;
            for(Character znak : alfabet){
                konstruowany = s;
                konstruowany+=znak;
                dostawZnak(konstruowany, i-1);
            }
        }
    }

    private static long dajUzyciePamieci(){
        int mb = 1024*1024;
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / mb;
    }

    public long dajPamiec() {
        return pamiecMax;
    }
}
