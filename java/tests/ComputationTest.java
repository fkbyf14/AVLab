import android.app.Application;

public class ComputationTest  extends Application {
	public static double c = 20d;
	public static double m = 2d;
	public static double k = 120d;


	public static void main(String[] args) {
		for (double t = 0d; t < 2d; t+=0.01) {
			System.out.println(functionX(t , 2d));

		}

	}
	public static double functionX(double t, double x0) {
		double fundFreq = Math.sqrt(k / m);
		double ksi = c / (2d*Math.sqrt(k*m));
		double freq = fundFreq*Math.sqrt(1d - ksi*ksi);
		double cos = Math.cos(freq * t);
		double sin = Math.sin(freq * t);
		double c2 = x0*(ksi*fundFreq* cos + freq* sin) / (freq* cos - ksi*fundFreq* sin);
		return Math.exp(-ksi*fundFreq*t)*(x0* cos + c2* sin);

	}

}
