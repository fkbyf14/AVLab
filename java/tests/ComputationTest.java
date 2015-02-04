import ru.barsic.avlab.physics.Computation;

public class ComputationTest  {

	public static void main(String[] args) {
		testIntersectTest();
	}
	public static void testIntersectTest() {
		int[] x1 = new int []{10, 20, 20, 10};
		int[] y1 = new int []{5, 5, 15, 15};
		int[] x2 = new int []{5, 15, 15, 5};
		int[] y2 = new int []{10, 10, 20, 20};
		System.out.println(Computation.intersect(new int[][]{x1, y1}, new int[][]{x2, y2}));
	}

}
