import java.util.ArrayList;
import java.util.List;

public class CSPHelper {

    List<String> listS;
    List<Character> alfabet;

    public CSPHelper()
    {
    }

    public int sprawdzHD(String sprawdzany)
    {
        int HD = 0;
        int HDObecny;
        int l = sprawdzany.length();
        for(String s:  listS)
        {
            HDObecny = 0;
            for(int i = 0 ; i < l ; i++)
                if(s.charAt(i) != sprawdzany.charAt(i))
                    HDObecny++;
            if(HD < HDObecny)
                HD = HDObecny;
        }
        return HD;
    }

    public List<String> getListS() {
        return listS;
    }

    public void setListS(List<String> listS) {
        this.listS = listS;
    }

    public List<Character> getAlfabet() {
        return alfabet;
    }

    public void setAlfabet(List<Character> alfabet) {
        this.alfabet = alfabet;
    }

    //    //TODO Wczytac z jakiegos pliku - moze byc w strukturze lokalnej projektu
//    static List<Character> getAlfabet()
//    {
//        List<Character> alfabet = new ArrayList<Character>();
//
//        return alfabet;
//    }
//
//    //TODO Wczytac z jakiegos pliku - moze byc w strukturze lokalnej projektu
//    static List<String> getInputStrings()
//    {
//        List<String> S = new ArrayList<String>();
//
//        return S;
//    }
//
//    //TODO To w ogole zadziala? xd
//    static List<List> getAlfabetAndStrings()
//    {
//        List<List> lista = new  ArrayList<List>();
//        lista.add(getAlfabet());
//        lista.add(getInputStrings());
//        return lista;
//    }
}