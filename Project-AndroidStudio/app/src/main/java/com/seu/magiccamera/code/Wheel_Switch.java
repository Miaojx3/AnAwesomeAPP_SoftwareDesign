package com.seu.magiccamera.code;

public class Wheel_Switch {

	public static void main(String args[]) {
		String P = new String("Hello, this is a~ string needed to be encrypted!!");
		char[] ke = new char[] { 'a', 'b', 'c', 'd' };
		char[] kd = new char[ke.length];
		kd = gen_kd(ke, P);

		double x0 = 0.566;
		double r = 3.588;
		char[] M = ws_en(P, kd, x0, r, 8);
		System.out.println(M);
		String M_ = String.valueOf(M);
		char[] P_get = ws_de(M_, kd, x0, r, 8);
		System.out.println(P_get);
		return;
	}

	public static char[] ws_en(String P, char[] kd, double x0, double r, int n) {
		int len = P.length();
		char[] M = new char[len];

		double x_ = x0;
		double x_temp = x0;
		int j = 0;
		while (n * j < len) {
			x_ = ws_func(x_temp, (int) kd[j % kd.length], r);
			x_temp = x_;

			int[] x_seq = new int[15];
			// System.out.println(x_);
			x_ = x_ - Math.floor(x_);
			for (int i = 0; i < 15; i++) {
				x_seq[i] = (int) Math.floor(x_ * 10);
				x_ = x_ * 10 - Math.floor(x_ * 10);
			}

			// ȡ��n�������, 1 <= n <= 13(���ʣ�µ�������n����ôȡʣ�µĸ���)
			int n_curr = n;
			if ((len - n * j) < n) {
				n_curr = len - n * j;
			}
			char[] n_rands = new char[n_curr];
			for (int i = 0; i < n_curr; i++) {
				n_rands[i] = (char) ((100 * x_seq[13 - n_curr + i] + 10 * x_seq[14 - n_curr + i] + x_seq[15 - n_curr + i]) % 256);
			}
			// ������
			//int sum = 0;
			for (int i = 0; i < n_curr; i++) {
				M[j * n + i] = (char) (P.charAt(j * n + i) ^ n_rands[i]);
				//sum += P.charAt(j * n + i) ^ n_rands[i];
			}
			j++;
		}
		return M;
	}

	
	public static char[] ws_de(String M, char[] kd, double x0, double r, int n) {
		int len = M.length();
		char[] P = new char[len];

		double x_ = x0;
		double x_temp = x0;
		int j = 0;
		while (n * j < len) {
			x_ = ws_func(x_temp, (int) kd[j % kd.length], r);
			x_temp = x_;

			int[] x_seq = new int[15];
			// System.out.println(x_);
			x_ = x_ - Math.floor(x_);
			for (int i = 0; i < 15; i++) {
				x_seq[i] = (int) Math.floor(x_ * 10);
				x_ = x_ * 10 - Math.floor(x_ * 10);
			}

			// ȡ��n�������, 1 <= n <= 13(���ʣ�µ�������n����ôȡʣ�µĸ���)
			int n_curr = n;
			if ((len - n * j) < n) {
				n_curr = len - n * j;
			}
			char[] n_rands = new char[n_curr];
			for (int i = 0; i < n_curr; i++) {
				n_rands[i] = (char) ((100 * x_seq[13 - n_curr + i] + 10 * x_seq[14 - n_curr + i] + x_seq[15 - n_curr + i]) % 256);
			}
			// ������
			//int sum = 0;
			for (int i = 0; i < n_curr; i++) {
				P[j * n + i] = (char) (M.charAt(j * n + i) ^ n_rands[i]);
				//sum += P.charAt(j * n + i) ^ n_rands[i];
			}
			j++;
		}
		return P;
	}
	public static char[] gen_kd(char[] ke, String P) {
		int len_key = ke.length;
		char[] kd = new char[len_key];
		for (int i = 0; i < len_key; i++) {
			kd[i] = ke[i];
		}
		int len_p = P.length();
		for (int i = 0; i < len_p; i++) {
			kd[i % len_key] += P.charAt(i);
		}
		return kd;
	}

	private static double ws_func(double xn, int qi, double r) {
		if (qi % 3 == 0)
			return logistic_func(xn, r);
		else if (qi % 3 == 1)
			return tent_func(xn, r / 2);
		else if (qi % 3 == 2)
			return sine_func(xn, r / 4);
		return 0;
	}

	private static double logistic_func(double xn, double r) {
		return r * xn * (1 - xn);
	}

	private static double tent_func(double xn, double u) {
		if (xn < 0.5)
			return u * xn;
		else
			return u * (1 - xn);
	}

	private static double sine_func(double xn, double a) {
		return a * Math.sin(Math.PI * xn);
	}
}
