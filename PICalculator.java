import java.util.Scanner;

public class PICalculator {

	public static void main(String[] args) {
		int n;
		System.out.println("Please enter number of variables");
		Scanner scan = new Scanner (System.in);
		n = scan.nextInt();
		while (n<=0) {
			System.out.println("Number of variables must be a positive number");
			System.out.println("Please enter number of variables");
			n = scan.nextInt();			
		}
		int MAX,i,term;
		MAX = 1<<n;
		int minterms[] = new int[MAX];
		int dCare[] = new int[MAX];
		System.out.println("Please enter the minterms separated by space");
		System.out.println("(close the bracket after the last minterm)");
		System.out.println();
		System.out.printf("m( ");
		i = 0 ;
		int size = 0;
		while (true) {
			if (scan.hasNextInt()) {
				term = scan.nextInt();
				if (term >= 0 && term < MAX) {
					minterms[i++] = term;
					size++;
				}
			}
			else if (scan.next().equals(")")) {
				break;
			}
			else {
				System.out.println("wrong input");
			}
		}
		System.out.println();
		System.out.println("Please enter the don't-care separated by space");
		System.out.println("(close the bracket after the last value)");
		System.out.println();
		System.out.printf("d( ");
		i = 0 ;
		int sizeDC = 0;
 		while (true) {
			if (scan.hasNextInt()) {
				term = scan.nextInt();
				if (term >= 0 && term < MAX) {
					dCare[i++] = term;
					sizeDC++;
				}
			}
			else if (scan.next().equals(")")) {
				break;
			}
			else {
				System.out.println("wrong input");
			}
		}
		scan.close();
		int j;
		int values = 3*n ;
		int MAXtabular = MAX * n;
		// tabular table with minterms and don't care values in the first column
		int tabular [][] = new int [MAXtabular][values] ;
		for (i = 0 ; i < MAXtabular ; i++) {
			for ( j = 0 ; j < values ; j++) {
				tabular[i][j] = -1;
			}
		}
		//for every 3 cols first is the value of the expression, second is the excluded bits and third is -2 if it is PI
		for ( i = 0 ; i < size ; i++ ) {
			tabular[i][0] = minterms[i];
			tabular[i][1] = 0;
			tabular[i][2] = -2;
			
		}
		for ( j = 0 ; j < sizeDC ; j++ ) {
			tabular[i][0] = dCare[j];
			tabular[i][1] = 0;
			tabular[i][2] = -2;
			i++;
		}
		int l ;
		int x;
		int m = 0 ;
		int least = 0 ;
		// filling tabular table using tabular method
		for ( i = 3 ; i < values ; i += 3) {
			for (j = 0 ; tabular[j][i-3] != -1 ; j++) {
				for (l = j+1 ; tabular[l][i-3] != -1 ; l++) {
					x = tabular [j][i-3] ^ tabular [l][i-3];
					if (((x & x-1) == 0) && (tabular[j][i-2] == tabular[l][i-2]) && (tabular [j][i-3] != tabular [l][i-3])) {
						if (tabular [j][i-3] < tabular [l][i-3]) least = j;
						else least = l;
						tabular [m][i] = tabular [least][i-3];
						tabular[m][i+1] = x + tabular[least][i-2];
						tabular [m][i+2] = -2;
						tabular [j][i-1] = -3;
						tabular [l][i-1] = -3;
						m++;
					}
				}
			}
			m = 0 ;
		}
		int PIsSize = 0;
		// number of all prime implicants with duplicates
		for (i = 2 ; i < values ; i+=3 ) {
			for (j = 0 ; tabular[j][i] != -1 ; j++) {
				if (tabular[j][i] == -2) {
					PIsSize ++;
				}
			}
		}
		int PIs[][] = new int [PIsSize][2];
		l = 0 ;
		// moving all the PI with duplicates
		for (i = 2 ; i < values ; i+=3 ) {
			for (j = 0 ; tabular[j][i] != -1 ; j++) {
				if (tabular[j][i] == -2) {
					PIs[l][0] =  tabular [j][i-2] ;
					PIs[l][1] =  tabular [j][i-1] ;
					l++;
				}
			}
		}
		int temp[][] = new int [PIsSize][2] ;
		int lastPIsSize = 0 ;		
		// removing the duplicates
		for (i = 0 ; i < PIsSize ; i++) {
			for (j = 0 ; j < lastPIsSize ; j++) {
				if ((PIs[i][0] == temp[j][0]) && (PIs[i][1] == temp[j][1])) {
					break;
				}
			}
			if (j == lastPIsSize) {
				temp[j][0] = PIs[i][0];
				temp[j][1] = PIs[i][1];
				lastPIsSize ++;
			}
		}
		int lastPIs[][] = new int [lastPIsSize][2];
		for (i = 0 ; i < lastPIsSize ; i++) {
			lastPIs[i][0] = temp [i][0];
			lastPIs[i][1] = temp [i][1];
		}
		//initializing PI table
		int PItableRows = lastPIsSize + 1;
		int PItableCols = size + 2 ;
		int PItable [][] = new int[PItableRows][PItableCols];
		// all PIs in the first column
		for (i = 1 ; i < PItableRows ; i ++) {
			PItable[i][0] = lastPIs[i-1][0];
			PItable[i][1] = lastPIs[i-1][1];
		}
		// all minterms in the first row
		for (i = 2 ; i < PItableCols ; i ++) {
			PItable[0][i] = minterms[i-2];
		}
		// marking the minterms covered by each PI
		for ( i = 1 ; i < PItableRows ; i ++ ) {
			for ( j = 0 ; j <= PItable[i][1] ; j++) {
				for ( l = 2 ; l < PItableCols ; l ++) {
					if (PItable[i][0] + (PItable[i][1] & j ) == PItable[0][l]) {
						PItable[i][l] = -1;
					}
				}
			}
		}
		
		l = 0 ;
		int essentialPI[][] = new int[size][2];
		int essentialsSize = 0 ; 
		int counter = 0 , row = 0 ;
		// collecting the essential PIs from each column with duplicates
		for ( i = 2 ; i < PItableCols ; i++) {
			for (j = 1 ; j < PItableRows ; j++) {
				if (PItable[j][i] == -1) {
					counter++;
					row = j;
				}
			}
			if (counter == 1) {
				essentialPI[l][0] = PItable[row][0];
				essentialPI[l][1] = PItable[row][1];
				essentialsSize++;
				l++;
			}
			counter = 0 ;
		}
		// removing the duplicates
		int tempEssentials[][] = new int [lastPIsSize][2] ;
		int lastEssentialsSize = 0 ;
		for (i = 0 ; i < essentialsSize ; i ++) {
			for ( j = 0 ; j < lastEssentialsSize ; j++) {
				if ((essentialPI[i][0] == tempEssentials[j][0]) && (essentialPI[i][1] == tempEssentials[j][1])) {
					break;
				}
			}
			if (j == lastEssentialsSize) {
				tempEssentials[j][0] = essentialPI[i][0];
				tempEssentials[j][1] = essentialPI[i][1];
				lastEssentialsSize++;
			}
		}
		int lastEssentialsPI[][] = new int[lastEssentialsSize][2];
		for ( i = 0 ; i < lastEssentialsSize ; i++) {
			lastEssentialsPI[i][0] = tempEssentials[i][0];
			lastEssentialsPI[i][1] = tempEssentials[i][1];
		}
		// converting every essential to its expression
		char[] symbol = new char[n];
		int[] binaryPI = new int[n];
		for (i = 0 ; i < n ; i ++) {
			symbol[i] = (char) ('A' + i) ;
		}
		int tempp;
		System.out.println();
		System.out.print("PIs are ");
		for ( i = 0 ; i < lastPIsSize ; i++) {
			tempp = lastPIs[i][0] ;
			for ( j = n-1 ; j >= 0 ; j--) {
				binaryPI[j] = tempp % 2 ;
				tempp /= 2;
			}
			for (j = 0 ; j <= lastPIs[i][1] ; j++) {
				for (l = 0 ; l < n ; l++) {
					if ((j & lastPIs[i][1]) == (1<<l)) {
						binaryPI[n-1-l] = -1;
						break;
					}
				}
			}
			for (j = 0 ; j < n ; j ++) {
				if (binaryPI[j] == 1) {
					System.out.print(symbol[j]);
				}
				else if (binaryPI[j] == 0) {
					System.out.print(symbol[j]);
					System.out.print('\'');
				}
			}
			System.out.print(", ");
		}
		System.out.println();
		System.out.println();
		System.out.print("Essential PIs are ");
		for ( i = 0 ; i < lastEssentialsSize ; i++) {
			tempp = lastEssentialsPI[i][0] ;
			for ( j = n-1 ; j >= 0 ; j--) {
				binaryPI[j] = tempp % 2 ;
				tempp /= 2;
			}
			for (j = 0 ; j <= lastEssentialsPI[i][1] ; j++) {
				for (l = 0 ; l < n ; l++) {
					if ((j & lastEssentialsPI[i][1]) == (1<<l)) {
						binaryPI[n-1-l] = -1;
						break;
					}
				}
			}
			for (j = 0 ; j < n ; j ++) {
				if (binaryPI[j] == 1) {
					System.out.print(symbol[j]);
				}
				else if (binaryPI[j] == 0) {
					System.out.print(symbol[j]);
					System.out.print('\'');
				}
			}
			System.out.print(", ");
		}
	}
}
