import java.util.List;
import java.util.concurrent.TimeUnit;

public class RozwiazanieDokladne {

    private CSPHelper cspHelper;
    private List<Character> alfabet;
    private int bestHD;
    private String znaleziony = "";

    public RozwiazanieDokladne(CSPHelper cspHelper) {
        this.cspHelper = cspHelper;
        this.alfabet = cspHelper.getAlfabet();
        this.bestHD = cspHelper.getListS().get(0).length();
    }

    public String rozwiaz() {
        //TimeUnit.SECONDS.sleep(5);
        dostawZnak("", bestHD);

        return znaleziony;
    }

    void dostawZnak(String s, int i){
        if (i == 0) {
            //System.out.println("Kompletny string: " + s + " HD: " + cspHelper.sprawdzHD(s));
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
}
