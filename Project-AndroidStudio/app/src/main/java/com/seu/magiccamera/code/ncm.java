package com.seu.magiccamera.code;

public class ncm {
	 public static void main(String args[]) {
		String P = new String("Hello, this is a~ string needed to be encrypted!!");
		char[] M = ncm_en(P, 8, 1.1, 6, 0.5432106789717177, 1, 1.1, 6.001, 0.5432106789717177, 1);
		String M_ = String.valueOf(M);
		char[] P_get = ncm_de(M_, 8, 1.1, 6, 0.5432106789717177, 1, 1.1, 6.001, 0.5432106789717177, 1);
		System.out.println(M); 
		System.out.println(P_get); 
		return;
	}
	public static char[] ncm_en(String P, int n, double a0_1, double b0_1, double x0_1, int n1, double a0_2, double b0_2, double x0_2, int n2){
		int len = P.length();
		char[] M = new char[len];
		
		//---------------------------------K1------------------------------------
		double x_1 = x0_1;
		double x_1_temp = x0_1;
		double a1 = a0_1;
		double b1 = b0_1;
		for (int i = 0; i < n1; i++) {
			x_1 = ncm_func(x_1, a1, b1);
		}
		int j = 0;
		while (j * n < len) {
			x_1 = ncm_func(x_1_temp, a1, b1);
			x_1_temp = x_1;
			int[] x_1_seq = new int[15];
			System.out.println(x_1);
			x_1 = x_1 - Math.floor(x_1);
			//System.out.println( Math.floor(x_1));
			for (int i = 0; i < 15; i++) {
				//System.out.print((x_1 * 10));
				x_1_seq[i] = (int) Math.floor(x_1 * 10);
				x_1 = x_1 * 10 - Math.floor(x_1 * 10);
			}
		

			int n_curr = n;
			if ((len - n*j) < n) {
				n_curr = len - n*j;
			}
			char[] n_rands = new char[n_curr];
			for (int i = 0; i < n_curr; i++) {
				n_rands[i] = (char) ((100 * x_1_seq[13-n_curr+i] + 10 * x_1_seq[14-n_curr+i] + x_1_seq[15-n_curr+i]) % 256);
				//System.out.print((int)n_rands[i]);
				//System.out.print('\n');
			}

			int sum = 0;
			for (int i = 0; i < n_curr; i++) {
				//System.out.println(P.charAt(j*n + i));
				//System.out.println((char)(P.charAt(j*n + i) ^ n_rands[i]));
				M[j*n + i] =  (char)(P.charAt(j*n + i) ^ n_rands[i]);
				sum += P.charAt(j*n + i) ^ n_rands[i];
			}
			

			if (a1 > a0_1 && sum < 10 * n_curr)  a1 = a1 - (sum + 10 * n)/(2560 * n);
			else if (a1 > a0_1 && sum >= 10 * n_curr)  a1 = a1 - (sum)/(2560 * n);
			else if (a1 <= a0_1 && sum < 10 * n_curr)  a1 = a1 + (sum + 10 * n)/(2560 * n);
			else if (a1 <= a0_1 && sum >= 10 * n_curr)  a1 = a1 + (sum)/(2560 * n);
			
			if (b1 > b0_1 && sum < 10 * n_curr)  b1 = b1 - (sum + 10 * n)/(256000 * n);
			else if (b1 > b0_1 && sum >= 10 * n_curr)  b1 = b1 - (sum)/(256000 * n);
			else if (b1 <= b0_1 && sum < 10 * n_curr)  b1 = b1 + (sum + 10 * n)/(256000 * n);
			else if (b1 <= b0_1 && sum >= 10 * n_curr)  b1 = b1 + (sum)/(256000 * n);
			
			j++;
		}
		
		
		//-----------------------------------K2-------------------------------------

				double x_2 = x0_2;
				double x_2_temp = x0_2;
				double a2 = a0_2;
				double b2 = b0_2;
				for (int i = 0; i < n2; i++) {
					x_2 = ncm_func(x_2, a2, b2);
				}
				j = 0;

				while (j * n < len) {

					x_2 = ncm_func(x_2_temp, a2, b2);
					x_2_temp = x_2;
					int[] x_2_seq = new int[15];
					System.out.println(x_2);
					x_2 = x_2 - Math.floor(x_2);
					//System.out.println( Math.floor(x_1));
					for (int i = 0; i < 15; i++) {
						//System.out.print((x_1 * 10));
						x_2_seq[i] = (int) Math.floor(x_2 * 10);
						x_2 = x_2 * 10 - Math.floor(x_2 * 10);
					}
					
				

					int n_curr = n;
					if ((len - n*j) < n) {
						n_curr = len - n*j;
					}
					char[] n_rands = new char[n_curr];
					for (int i = 0; i < n_curr; i++) {
						n_rands[i] = (char) ((100 * x_2_seq[13-n_curr+i] + 10 * x_2_seq[14-n_curr+i] + x_2_seq[15-n_curr+i]) % 256);
						//System.out.print((int)n_rands[i]);
						//System.out.print('\n');
					}

					int sum = 0;
					for (int i = 0; i < n_curr; i++) {
						//System.out.println(P.charAt(j*n + i));
						//System.out.println((char)(P.charAt(j*n + i) ^ n_rands[i]));
						M[len - 1 - (j*n + i)] = (char)(P.charAt(len - 1 - (j*n + i)) ^ n_rands[i]);
						sum += P.charAt(len - 1 - (j*n + i)) ^ n_rands[i];
					}
					

					if (a2 > a0_2 && sum < 10 * n_curr)  a2 = a2 - (sum + 10 * n)/(2560 * n);
					else if (a2 > a0_2 && sum >= 10 * n_curr)  a2 = a2 - (sum)/(2560 * n);
					else if (a2 <= a0_2 && sum < 10 * n_curr)  a2 = a2 + (sum + 10 * n)/(2560 * n);
					else if (a2 <= a0_2 && sum >= 10 * n_curr)  a2 = a2 + (sum)/(2560 * n);
					
					if (b2 > b0_2 && sum < 10 * n_curr)  b2 = b2 - (sum + 10 * n)/(256000 * n);
					else if (b2 > b0_2 && sum >= 10 * n_curr)  b2 = b2 - (sum)/(256000 * n);
					else if (b2 <= b0_2 && sum < 10 * n_curr)  b2 = b2 + (sum + 10 * n)/(256000 * n);
					else if (b2 <= b0_2 && sum >= 10 * n_curr)  b2 = b2 + (sum)/(256000 * n);
					
					j++;
				}
				
				
		return M;
		
	}

	public static char[] ncm_de(String M, int n, double a0_1, double b0_1, double x0_1, int n1, double a0_2, double b0_2, double x0_2, int n2) {
		int len = M.length();
		char[] P = new char[len];
		
		//---------------------------------------K1----decrypt--------------------------------------------

		double x_1 = x0_1;
		double x_1_temp = x0_1;
		double a1 = a0_1;
		double b1 = b0_1;
		for (int i = 0; i < n1; i++) {
			x_1 = ncm_func(x_1, a1, b1);
		}
		int j = 0;

		while (j * n < len) {
			// �ٵ������Σ��õ�С�����15λ~
			x_1 = ncm_func(x_1_temp, a1, b1);
			x_1_temp = x_1;
			int[] x_1_seq = new int[15];
			System.out.println(x_1);
			x_1 = x_1 - Math.floor(x_1);
			//System.out.println( Math.floor(x_1));
			for (int i = 0; i < 15; i++) {
				//System.out.print((x_1 * 10));
				x_1_seq[i] = (int) Math.floor(x_1 * 10);
				x_1 = x_1 * 10 - Math.floor(x_1 * 10);
			}
			
		
			// ȡ��n�������, 1 <= n <= 13(���ʣ�µ�������n����ôȡʣ�µĸ���)
			int n_curr = n;
			if ((len - n*j) < n) {
				n_curr = len - n*j;
			}
			char[] n_rands = new char[n_curr];
			for (int i = 0; i < n_curr; i++) {
				n_rands[i] = (char) ((100 * x_1_seq[13-n_curr+i] + 10 * x_1_seq[14-n_curr+i] + x_1_seq[15-n_curr+i]) % 256);
				//System.out.print((int)n_rands[i]);
				//System.out.print('\n');
			}
			// ������
			int sum = 0;
			for (int i = 0; i < n_curr; i++) {
				//System.out.println(P.charAt(j*n + i));
				//System.out.println((char)(P.charAt(j*n + i) ^ n_rands[i]));
				P[j*n + i] = (char)(M.charAt(j*n + i) ^ n_rands[i]);
				sum += M.charAt(j*n + i) ^ n_rands[i];
			}
			
			// �ı�a��b
			if (a1 > a0_1 && sum < 10 * n_curr)  a1 = a1 - (sum + 10 * n)/(2560 * n);
			else if (a1 > a0_1 && sum >= 10 * n_curr)  a1 = a1 - (sum)/(2560 * n);
			else if (a1 <= a0_1 && sum < 10 * n_curr)  a1 = a1 + (sum + 10 * n)/(2560 * n);
			else if (a1 <= a0_1 && sum >= 10 * n_curr)  a1 = a1 + (sum)/(2560 * n);
			
			if (b1 > b0_1 && sum < 10 * n_curr)  b1 = b1 - (sum + 10 * n)/(256000 * n);
			else if (b1 > b0_1 && sum >= 10 * n_curr)  b1 = b1 - (sum)/(256000 * n);
			else if (b1 <= b0_1 && sum < 10 * n_curr)  b1 = b1 + (sum + 10 * n)/(256000 * n);
			else if (b1 <= b0_1 && sum >= 10 * n_curr)  b1 = b1 + (sum)/(256000 * n);
			
			j++;
		}
		
		
		
		//---------------------------------------K2-----decrypt----------------------------------------
		
		// ��ʼ��ncm����n2��
		double x_2 = x0_2;
		double x_2_temp = x0_2;
		double a2 = a0_2;
		double b2 = b0_2;
		for (int i = 0; i < n2; i++) {
			x_2 = ncm_func(x_2, a2, b2);
		}
		j = 0;
		// ���ǵ�j��ѭ����j��0��ʼ����
		while (j * n < len) {
			// �ٵ������Σ��õ�С�����15λ~
			x_2 = ncm_func(x_2_temp, a2, b2);
			x_2_temp = x_2;
			int[] x_2_seq = new int[15];
			System.out.println(x_2);
			x_2 = x_2 - Math.floor(x_2);
			//System.out.println( Math.floor(x_1));
			for (int i = 0; i < 15; i++) {
				//System.out.print((x_1 * 10));
				x_2_seq[i] = (int) Math.floor(x_2 * 10);
				x_2 = x_2 * 10 - Math.floor(x_2 * 10);
			}
		
			// ȡ��n�������, 1 <= n <= 13(���ʣ�µ�������n����ôȡʣ�µĸ���)
			int n_curr = n;
			if ((len - n*j) < n) {
				n_curr = len - n*j;
			}
			char[] n_rands = new char[n_curr];
			for (int i = 0; i < n_curr; i++) {
				n_rands[i] = (char) ((100 * x_2_seq[13-n_curr+i] + 10 * x_2_seq[14-n_curr+i] + x_2_seq[15-n_curr+i]) % 256);
				//System.out.print((int)n_rands[i]);
				//System.out.print('\n');
			}
			// ������
			int sum = 0;
			for (int i = 0; i < n_curr; i++) {
				//System.out.println(P.charAt(j*n + i));
				//System.out.println((char)(P.charAt(j*n + i) ^ n_rands[i]));
				P[len - 1 - (j*n + i)] = (char)(M.charAt(len - 1 - (j*n + i)) ^ n_rands[i]);
				sum += M.charAt(len - 1 - (j*n + i)) ^ n_rands[i];
			}
			
			// �ı�a��b
			if (a2 > a0_2 && sum < 10 * n_curr)  a2 = a2 - (sum + 10 * n)/(2560 * n);
			else if (a2 > a0_2 && sum >= 10 * n_curr)  a2 = a2 - (sum)/(2560 * n);
			else if (a2 <= a0_2 && sum < 10 * n_curr)  a2 = a2 + (sum + 10 * n)/(2560 * n);
			else if (a2 <= a0_2 && sum >= 10 * n_curr)  a2 = a2 + (sum)/(2560 * n);
			
			if (b2 > b0_2 && sum < 10 * n_curr)  b2 = b2 - (sum + 10 * n)/(256000 * n);
			else if (b2 > b0_2 && sum >= 10 * n_curr)  b2 = b2 - (sum)/(256000 * n);
			else if (b2 <= b0_2 && sum < 10 * n_curr)  b2 = b2 + (sum + 10 * n)/(256000 * n);
			else if (b2 <= b0_2 && sum >= 10 * n_curr)  b2 = b2 + (sum)/(256000 * n);
			
			j++;
		}
		
		
		
		return P;
		
	}
	
	private static double ncm_func(double xn, double a, double b) {
		return (1-Math.pow(b,-4)) * Math.atan(a/(1+b)) * Math.pow((1+1/b),b) * Math.tan(a * xn) * Math.pow((1 - xn),b);
	}
}
