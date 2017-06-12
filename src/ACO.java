import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ACO {
	
	List<String> S;
	int m; //dlugosc kazdego stringa
	List<Character> A; //alfabet
	int n; //liczba stringów
	int lA; //liczba elementow alfabetu
	int liczbaGeneracji;
	int liczbaMrowek;
	double p = 0.05; // wspolczynnik parowania
	double wspDod = 0.1; // wspolczynnik dodania feromonow
	double[] sumaWag;
	CSPHelper csphelper;
	List<Long> listaPamieci = new ArrayList<>();
	private long pamiecMax = 0;
	
	double[][] T; // Feromony

	public ACO(List<String> s, List<Character> a, int liczbaGeneracji, int liczbaMrowek, CSPHelper csph) {
		super();
		S = s;
		A = a;
		this.liczbaGeneracji = liczbaGeneracji;
		this.liczbaMrowek = liczbaMrowek;
		m = s.get(0).length();
		lA = a.size();
		csphelper = csph;
		n=S.size();
		inicjalizuj();
	}
	
	public ACO(List<String> s, List<Character> a, int liczbaGeneracji, int liczbaMrowek) {
		super();
		S = s;
		A = a;
		this.liczbaGeneracji = liczbaGeneracji;
		this.liczbaMrowek = liczbaMrowek;
		m = s.get(0).length();
		lA = a.size();
		csphelper = new CSPHelper();
		csphelper.setListS(S);
		inicjalizuj(); // m * (3|A| + 1) + 3m + 3
	}

	public double dajZlozonoscObliczeniowaOczekiwana(){
		return 14 + 8*m + 11*m*lA + 6*n + 8*m*n + liczbaMrowek * (6 + 6*m + 4*m*lA) + (liczbaGeneracji-1) * (8*m*lA + 4*m + 13*n + 24*m*n + 13 + liczbaMrowek * (6 + 6*m + 4*m*lA));
	}

	public double dajPamiecOczekiwana(){
		//m * (double + 2 * int + 2 * char + |A| * double + n  * char) + |A| * char
		return m*(64+2*32+2*16+lA*64+n*16) + lA * 16;
	}

	public void inicjalizuj() // m * (3|A| + 1) + 3m + 3
	{
		T = new double[m][lA]; //1
		for (int i = 0 ; i < m ; i++) // m * (3|A| + 1)
		{
			for(int j = 0 ; j < lA ; j++) // 3|A| + 1
			{
				T[i][j] = 1.0/(double)lA; //1
			}
		}
		sumaWag = new double[m]; //1
		for(int i = 0 ; i < m ; i++) //3m + 1
		{
			sumaWag[i] = 1.0; //1
		}
	}
	
	public void paruj() // 3m + 2 + m *4|A| + 1
	{
		for (int i = 0 ; i < m ; i++)// 3m + 2 + m *4|A| + 1
		{
			sumaWag[i] = 0; //1
			for(int j = 0 ; j < lA ; j++) //4|A| + 1
			{
				T[i][j] = T[i][j] * (1.0 - p); //1
				sumaWag[i] += T[i][j];  //1
			}
		}
	}

	public void dodajFeromonow(int[] droga) // 7m + 6 + (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4)
	{
		double HD = 0; //1 
		HD = csphelper.sprawdzHD(zbudujString(droga)); // 3m + 4 + (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) ; (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) <- sprawdz HD ; 3m + 4 < - zbudujString
		for( int i = 0 ; i < m ; i++) // 4m + 1
		{
			T[i][droga[i]] += (HD/ (double) m * wspDod); // 1
			sumaWag[i] += (HD/ (double) m * wspDod); // 1
		}
	}

	public String zbudujString(int[] droga) //3m + 4
	{
		String budowany = ""; //1
		for(int i = 0 ; i < m ; i++) //3m + 1
			budowany += A.get(droga[i]); //1
		return budowany; // 1
	}
	
	public int[] znajdzDroge() // 6 + 2m + m * ( pes: 4|A| + 4 ; opt: 13)
	{
		int[] droga = new int[m]; // 1
		for(int i = 0 ; i < m ; i++) // 2m
			droga[i] = -1;// 1
		
		double dystrybuanta; // 1
		double wybor; // 1
		int j; // 1
		Random generator = new Random(); // 1
		for(int i = 0 ; i < m ; i++) // m * ( pes: 4|A| + 4 ; opt: 13)
		{
			j = 0; //1
			dystrybuanta = 0; // 1
			wybor = (generator.nextDouble()*1000)%sumaWag[i]; // 1
			while(droga[i] == -1) // pes: 4|A| + 1 ; opt: 4	;  j, iterator, nie przekroczy lA
			{
				dystrybuanta += T[i][j]; //1
				if(wybor <= dystrybuanta) //1
					droga[i] = j; //1
				j++; //1
			}
		}
		return droga; // 1
	}
	
	public String znajdzNajblizszyString() // 3 + wm + (liczbaGeneracji-1) * (wm + 1 + 2 * (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4))
	{
		String obecnejGeneracji; // 1
		String najblizszy = wymarszKolonii(); // wm + 1
		for(int i = 1 ; i < liczbaGeneracji ; i++) // (liczbaGeneracji-1) * (wm + 1 + 2 * (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4)) Od pierwszego, ponieważ zmienna najblizsze inicjowana lista otrzymana przez pierwsza generacje
		{
			obecnejGeneracji = wymarszKolonii(); // wm + 1
			if(csphelper.sprawdzHD(najblizszy) > csphelper.sprawdzHD(obecnejGeneracji)) // 2 * (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) + 1
				najblizszy = obecnejGeneracji; // 1
		}
		//inicjalizuj();
		return najblizszy; // 1
	}
	

	public String wymarszKolonii() // wm =  1 + 1 + 7 + 2m + m * ( pes: 4|A| + 4 ; opt: 13) + 1 + (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) + 3m + 5 + 1 + liczbaMrowek * (6 + 2m + m * ( pes: 4|A| + 4 ; opt: 13)) + 3m + 2 + m *4|A| + 1 + 7m + 6 + (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) + 3m + 4
	{
		int[] najkrotszaDroga; // 1
		String sprawdzany; // 1
		int[] obecnaDroga = znajdzDroge(); //7 + 2m + m * ( pes: 4|A| + 4 ; opt: 13)
		najkrotszaDroga = obecnaDroga; // 1
		int hdNajkrotszych = csphelper.sprawdzHD(zbudujString(najkrotszaDroga)); // (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4) + 3m + 5
		
		for(int i = 1 ; i < liczbaMrowek ; i++) // 1 + liczbaMrowek * (6 + 2m + m * ( pes: 4|A| + 4 ; opt: 13)) ; Od pierwszego, ponieważ zmienna "obecnaDroga" inicjowana jest tablica otrzymana przez pierwsza mrowke
		{
			obecnaDroga = znajdzDroge(); // 6 + 2m + m * ( pes: 4|A| + 4 ; opt: 13)
			sprawdzany = zbudujString(obecnaDroga); // 3m + 4

			long pamiec = dajUzyciePamieci();
			listaPamieci.add(pamiec);
			if(pamiecMax < pamiec){
				pamiecMax = pamiec;
			}

			if(csphelper.sprawdzHD(sprawdzany) < hdNajkrotszych) //  pes : (pes: n * (3 + 4m) + 6 ; opt: n * (3 + 3m) + 6) ; opt : 1
			{
				najkrotszaDroga = obecnaDroga; // 1
				hdNajkrotszych = csphelper.sprawdzHD(sprawdzany); //  pes: n * (3 + 4m) + 5 ; opt: n * (3 + 3m) + 5
			}
		}
		paruj(); // 3m + 2 + m *4|A| + 1
		dodajFeromonow(najkrotszaDroga); // 7m + 6 + (pes: n * (3 + 4m) + 4 ; opt: n * (3 + 3m) + 4)
		return zbudujString(najkrotszaDroga); //3m + 4
	}

	private static long dajUzyciePamieci(){
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / mb;
	}

	public long dajPamiec() {
		long zwracana = 0;
		for (Long l: listaPamieci) {
			zwracana+=l;
		}
		zwracana = zwracana/listaPamieci.size();
		return zwracana;
	}
	
	/*
	
	public void dodajFeromonow(List<int[]> drogi)
	{
		String budowany;
		double HD = 0;
		for(int[] droga: drogi)
		{
			budowany = zbudujString(droga);
			HD = csphelper.sprawdzHD(budowany);
			for( int i = 0 ; i < m ; i++)
			{
				T[i][droga[i]] += HD/ (double) m;
				sumaWag[i] += HD/ (double) m;
			}
		}
	}
	
	public List<String> znajdzNajblizszyString()
	{
		List<String> obecnejGeneracji;
		List<String> najblizsze = wymarszKolonii();
		for(int i = 1 ; i < liczbaGeneracji ; i++) // Od pierwszego, ponieważ zmienna najblizsze inicjowana lista otrzymana przez pierwsza generacje
		{
			obecnejGeneracji = wymarszKolonii();
			if(csphelper.sprawdzHD(najblizsze.get(0)) > csphelper.sprawdzHD(obecnejGeneracji.get(0)))
				najblizsze = obecnejGeneracji;
			else
				if(csphelper.sprawdzHD(najblizsze.get(0)) == csphelper.sprawdzHD(obecnejGeneracji.get(0)))
				{
					for(String obecny: obecnejGeneracji)
						if(!najblizsze.contains(obecny))
							najblizsze.add(obecny);
				}
					//najblizsze.addAll(obecnejGeneracji);
		}
		
		System.out.println(csphelper.sprawdzHD(najblizsze.get(0)));
		inicjalizuj();
		
		return najblizsze;
	}

	public List<String> wymarszKolonii()
	{
		List<int[]> najkrotszeDrogi = new ArrayList<int[]>();

		String sprawdzany;
		int[] obecnaDroga = znajdzDroge();
		najkrotszeDrogi.add(obecnaDroga);
		int hdNajkrotszych = csphelper.sprawdzHD(zbudujString(najkrotszeDrogi.get(0)));
		
		for(int i = 1 ; i < liczbaMrowek ; i++) // Od pierwszego, ponieważ zmienna "obecnaDroga" inicjowana jest tablica otrzymana przez pierwsza mrowke
		{
			obecnaDroga = znajdzDroge();
			sprawdzany = zbudujString(obecnaDroga);
			if(csphelper.sprawdzHD(sprawdzany) < hdNajkrotszych)
			{
				najkrotszeDrogi.clear();
				najkrotszeDrogi.add(obecnaDroga);
				hdNajkrotszych = csphelper.sprawdzHD(sprawdzany);
			}
			else
				if(csphelper.sprawdzHD(sprawdzany) == hdNajkrotszych)
					if(najkrotszeDrogi.contains(obecnaDroga))
						System.out.println(sprawdzany);
					//najkrotszeDrogi.add(obecnaDroga);
		}
		
		paruj();
		dodajFeromonow(najkrotszeDrogi);
		List<String> najblizszeStringi = new ArrayList<String>();
		for(int[] droga : najkrotszeDrogi)
		{
			najblizszeStringi.add(zbudujString(droga));
		}
		System.out.println(najblizszeStringi.size());
		return najblizszeStringi;
	}
	*/
}
