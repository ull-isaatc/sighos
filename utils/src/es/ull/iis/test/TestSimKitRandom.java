package es.ull.iis.test;
import simkit.random.*;

import java.io.*;

/*
 * TestRandom.java
 *
 * Created on 15 de abril de 2004, 16:35
 */

/**
 *
 * @author Iván Castilla
 */
public class TestSimKitRandom {
    public static final int NSAMPLES = 1000000; 
    public static final int NTEST = 100; 

    public static void generar(RandomVariate rnd, String fileName) {
    	try {
            PrintWriter prn = new PrintWriter(new BufferedWriter(new FileWriter(fileName)));
            for (int i = 0; i < NSAMPLES; i++) 
            	prn.println (rnd.generate());
            prn.close();
        } catch (IOException i) {
        	i.printStackTrace();
        }    	
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//    	int test = 1;
    	RandomVariate rnd = RandomVariateFactory.getInstance("ScaledVariate", RandomVariateFactory.getInstance("ConstantVariate", 1), 1, 2.1);
    	for (int i = 0; i < NTEST; i++)
    		System.out.println(rnd.generate());
    		
    	/*
    	if (test == 1) {
        	for (int i = 0; i < NTEST; i++) {
            	long tIni = System.currentTimeMillis();
                NormalVariate genNormal = new NormalVariate ();
                genNormal.setMean(10.0);
                genNormal.setStandardDeviation(2.0);
                long tMid = System.currentTimeMillis();
                for (int j = 0; j < NSAMPLES; j++) 
                	genNormal.generate();
                long tEnd = System.currentTimeMillis();
                System.out.println("Random T INIT: " + (tMid - tIni) + "\tT PROC: " + (tEnd - tMid));
        	}
    	}
    	else {
            NormalVariate genNormal = new NormalVariate ();
            genNormal.setMean(10.0);
            genNormal.setStandardDeviation(2.0);
            generar(genNormal, "Snormal_10_2.txt");
        
    	
	        PoissonVariate genPoisson = new PoissonVariate ();
	        genPoisson.setMean(20.0);
	        generar(genPoisson, "Spoisson_20.txt");
        
//        BetaVariate genBeta = new BetaVariate ();
//        genBeta.setAlpha(7);
//        genBeta.setBeta(11);
//        generar(genBeta, "Sbeta_7_11.txt");
        
	        ExponentialVariate genExp = new ExponentialVariate ();
	        genExp.setMean(10);
	        generar(genExp, "Sexponencial_10.txt");
	        
	        GeometricVariate genGeo = new GeometricVariate ();
	        genGeo.setP(0.7);
	        generar(genGeo, "Sgeometrico_07.txt");
	        
	    	TriangleVariate genTri = new TriangleVariate ();
	    	genTri.setLeft(3);
	    	genTri.setCenter(7);
	    	genTri.setRight(10);
	    	generar(genTri, "Striangular3_7_10.txt");
	        
	        UniformVariate genUni = new UniformVariate ();
	        genUni.setMinimum(10.0);
	        genUni.setMaximum(20.0);
	        generar(genUni, "Suniforme_10_20.txt");
    	}*/
    }
}
