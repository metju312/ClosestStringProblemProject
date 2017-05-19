import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ACO {
	
	List<String> S;
	int m; //dlugosc kazdego stringa
	List<Character> A; //alfabet
	int lA; //liczba elementow alfabetu
	int liczbaGeneracji;
	int liczbaMrowek;
	double p; // wspolczynnik parowania
	double[] sumaWag;
	CSPHelper csphelper;
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
		inicjalizuj(); // 2m + 2m * lA + 3
	}

	public void inicjalizuj() // 2m + 2m * lA + 3
	{
		T = new double[m][lA]; //1
		for (int i = 0 ; i < m ; i++) // 2m * lA
		{
			for(int j = 0 ; j < lA ; j++) // 2lA
			{
				T[i][j] = 1.0/(double)lA; //1
			}
		}
		sumaWag = new double[m]; //1
		for(int i = 0 ; i < m ; i++) //2m
		{
			sumaWag[i] = 1.0; //1
		}
		p = 0.1; //1
	}
	
	public void paruj() // 2m + 3m * lA
	{
		for (int i = 0 ; i < m ; i++)// 2m + 3m * lA
		{
			sumaWag[i] = 0; //1
			for(int j = 0 ; j < lA ; j++) //3lA
			{
				T[i][j] = T[i][j] * (1.0 - p); //1
				sumaWag[i] += T[i][j];  //1
			}
		}
	}

	public void dodajFeromonow(int[] droga) // 5m + 3
	{
		double HD = 0; //1 
		HD = csphelper.sprawdzHD(zbudujString(droga)); //2m + 2
		for( int i = 0 ; i < m ; i++) // 3m
		{
			T[i][droga[i]] += HD/ (double) m; // 1
			sumaWag[i] += HD/ (double) m; // 1
		}
	}

	public String zbudujString(int[] droga) //2m + 1
	{
		String budowany = ""; //1
		for(int i = 0 ; i < m ; i++) //m + m * 1
			budowany += A.get(droga[i]); //1
		return budowany;
	}
	
	public int[] znajdzDroge() // 5m + 3m * lA + 5
	{
		int[] droga = new int[m]; // 1
		for(int i = 0 ; i < m ; i++) // 2m
			droga[i] = -1;// 1
		
		double dystrybuanta; // 1
		double wybor; // 1
		int j; // 1
		Random generator = new Random(); // 1
		for(int i = 0 ; i < m ; i++) // 3 m + 3 m * lA
		{
			j = 0; //1
			dystrybuanta = 0; // 1
			wybor = (generator.nextDouble()*1000)%sumaWag[i]; // 1
			while(droga[i] == -1) // 3 * lA		j, iterator, nie przekroczy lA
			{
				dystrybuanta += T[i][j]; //1
				if(wybor <= dystrybuanta) //1
					droga[i] = j; //1
				j++; //1
			}
		}
		return droga;
	}
	
	public String znajdzNajblizszyString()
	{
		String obecnejGeneracji; // 1
		String najblizszy = wymarszKolonii(); // 
		for(int i = 1 ; i < liczbaGeneracji ; i++) // Od pierwszego, ponieważ zmienna najblizsze inicjowana lista otrzymana przez pierwsza generacje
		{
			obecnejGeneracji = wymarszKolonii();
			if(csphelper.sprawdzHD(najblizszy) > csphelper.sprawdzHD(obecnejGeneracji))
				najblizszy = obecnejGeneracji;
		}
		inicjalizuj();
		return najblizszy;
	}
	

	public String wymarszKolonii()
	{
		int[] najkrotszaDroga = new int[m]; // 1
		String sprawdzany; // 1
		int[] obecnaDroga = znajdzDroge(); // 5m + 3m * lA + 5
		najkrotszaDroga = obecnaDroga; // 1
		int hdNajkrotszych = csphelper.sprawdzHD(zbudujString(najkrotszaDroga)); //2m + |S| * 2l + 2|S| + 3
		
		for(int i = 1 ; i < liczbaMrowek ; i++) // Od pierwszego, ponieważ zmienna "obecnaDroga" inicjowana jest tablica otrzymana przez pierwsza mrowke
		{
			obecnaDroga = znajdzDroge(); // 5m + 3m * lA + 5
			sprawdzany = zbudujString(obecnaDroga); // 2m + 1
			// TODO pomiar pamięci
			long pamiec = dajUzyciePamieci();
			if(pamiecMax < pamiec){
				pamiecMax = pamiec;
			}

			if(csphelper.sprawdzHD(sprawdzany) < hdNajkrotszych)
			{
				najkrotszaDroga = obecnaDroga;
				hdNajkrotszych = csphelper.sprawdzHD(sprawdzany); // 
			}
		}
		return zbudujString(najkrotszaDroga);
	}

	private static long dajUzyciePamieci(){
		int mb = 1024*1024;
		Runtime runtime = Runtime.getRuntime();
		return (runtime.totalMemory() - runtime.freeMemory()) / mb;
	}

	public long dajPamiec() {
		return pamiecMax;
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
