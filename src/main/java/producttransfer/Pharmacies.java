package producttransfer;

import java.lang.reflect.Field;

/*Minimize z = 5x112 + 10x113 + 5x114
 *       4x21        + 3x23   + 5x24
 *       10    12               5
 *       15    14      8

1x112 + 1x114 + 1x113 >= 80
1x121 + 1x124 + 1x123 >= 40
1x131 + 1x132 + 1x134 >= 40

1x212 + 1x214 + 1x213 >= 30
1x221 + 1x224 + 1x223 >= 40
1x231 + 1x232 + 1x234 >= 50

1x312 + 1x314 + 11x313 >= 50
1x321 + 1x324 + 1x323 >= 20
1x331 + 1x332 + 1x334 >= 30
1x341 + 1x342 + 1x343 >= 100



x1 >=0, x2>=0, x3 >=0*/

import java.util.ArrayList;
import java.util.List;

import ilog.concert.*;
import ilog.cplex.*;

import ilog.concert.IloException;


public class Pharmacies {

	public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {

		int n = 3;
		int m = 4;
		double[] c = { 41, 35, 96 };
		
		double[][] A = { { 2,   3,    7 }, 
						 { 1,   1,    0 }, 
						 { 5,   3,    0 }, 
						 { 0.6, 0.25, 1 } };
		
		double[] b = { 1250, 250, 900, 232.5 };
		System.out.println("Model is solving..");
		solveModel(n, m, c, A, b);
	}

	public static void solveModel(int n, int m, double[] c, double[][] A, double[] b) throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		try {
			System.setProperty( "java.library.path", "/home/ates/IBM_CPLEX/cplex/bin/x86-64_linux" );
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
			IloCplex model = new IloCplex();

			IloNumVar[] x = new IloNumVar[n];
			for (int i = 0; i < n; i++) {
				x[i] = model.numVar(0,  Double.MAX_VALUE);
			}

			IloLinearNumExpr obj = model.linearNumExpr();
			for (int i = 0; i < n; i++) {
				obj.addTerm(c[i], x[i]);
			}
			model.addMinimize(obj);
			
			List<IloRange> constraints = new ArrayList<IloRange>();

			for (int i = 0; i < m; i++) {
				IloLinearNumExpr constraint = model.linearNumExpr();
				for (int j = 0; j < n; j++) {
					constraint.addTerm(A[i][j], x[j]);
				}
				constraints.add(model.addGe(constraint, b[i]));
			}

			boolean isSolved = model.solve();
			if (isSolved) {
				double objValue = model.getObjValue();
				System.out.println("onb_val = " + objValue);
				for (int k = 0; k < n; k++) {
					System.out.println("x[" + (k + 1) + "] = " + model.getValue(x[k]));
					System.out.println("Reduce cost " + (k + 1) + " = " + model.getReducedCost(x[k]));
				}

				for (int i = 0; i < m; i++) {

					double slack = model.getSlack(constraints.get(1));

					double dual = model.getDual(constraints.get(i));
					if (slack == 0) {
						System.out.println("Constraint " + (i + 1) + " is binding.");
					} else {
						System.out.println("Constraint " + (i + 1) + " is non-binding.");
					}

					System.out.println("Shadow price " + (i + 1) + " = " + dual);
				}
			} else {
				System.out.println("Model is not solved");
			}

		} catch (IloException ex) {
			ex.printStackTrace();
		}
	}
}