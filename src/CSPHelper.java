import java.util.ArrayList;
import java.util.List;

public class CSPHelper {

    List<String> listS;
    List<Character> alfabet;

    public CSPHelper()
    {
    }

    public int sprawdzHD(String sprawdzany) // n * (1 + 5m) + 4
    {
        int HD = 0; // 1
        int HDObecny; // 1
        int l = sprawdzany.length(); // 1
        for(String s:  listS) // n * ( 1 + 4m)
        {
            HDObecny = 0; // 1
            for(int i = 0 ; i < l ; i++) // m * 5
                if(s.charAt(i) != sprawdzany.charAt(i)) // 1
                    HDObecny++; // 1
            if(HD < HDObecny) // 1
                HD = HDObecny; // 1
        }
        return HD; // 1
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