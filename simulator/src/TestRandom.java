import es.ull.isaatc.random.*;

import java.io.*;

/*
 * TestRandom.java
 *
 * Created on 15 de abril de 2004, 16:35
 */

/**
 *
 * @author  Oswaldo García
 */
public class TestRandom {
    public static PrintWriter sal_normal, sal_poisson, sal_beta, sal_chi, sal_exp, sal_geo, sal_tri, sal_uni;
    public static final int NSAMPLES = 1000000; 
    public static final int NTEST = 100; 

    public static void generador_normal (double media, double desv) {
        Normal gen_normal = new Normal (media, desv * desv);
           try {
         sal_normal = new PrintWriter(
                             new BufferedWriter(new FileWriter("normal_10_2.txt")));
        for (int i = 0; i < NSAMPLES; i++) 
            sal_normal.println (gen_normal.sampleDouble());
         sal_normal.close();
           } catch (IOException i) {}
    }
    
    public static void generador_poisson (double media) {
        Poisson gen_poisson = new Poisson (media);
    
        try {
         sal_poisson = new PrintWriter(
                             new BufferedWriter(new FileWriter("poisson_20.txt")));

            for (int i = 0; i < NSAMPLES; i++) 
            sal_poisson.println (gen_poisson.sampleDouble());
        sal_poisson.close();
        } catch (IOException e){}
    }

    public static void generador_beta (int grado_lib1, int grado_lib2) {
        Beta gen_beta = new Beta (grado_lib1, grado_lib2);

        try {
        sal_beta = new PrintWriter(
                             new BufferedWriter(new FileWriter("beta_7_11.txt")));

            for (int i = 0; i < NSAMPLES; i++) 
            sal_beta.println (gen_beta.sampleDouble());
        sal_beta.close();
        } catch (IOException e){}
    }
    
    public static void generador_chi (int grado_lib) {
        ChiSquare gen_chi = new ChiSquare (grado_lib);

        try {
        sal_chi = new PrintWriter(
                             new BufferedWriter(new FileWriter("chi_10.txt")));

            for (int i = 0; i < NSAMPLES; i++) 
            sal_chi.println (gen_chi.sampleDouble());
        sal_chi.close();
        } catch (IOException e) {}
    }

    public static void generador_exponencial (double media) {
        Exponential gen_exp = new Exponential (media);

        try {
        sal_exp = new PrintWriter(
                             new BufferedWriter(new FileWriter("exponencial_10.txt")));

            for (int i = 0; i < NSAMPLES; i++) 
            sal_exp.println (gen_exp.sampleDouble());
        sal_exp.close();
        } catch (IOException e){}
    }

    public static void generador_geometrico (double prob) {
        Geometric gen_geo = new Geometric (prob);

        try {
        sal_geo = new PrintWriter(
                             new BufferedWriter(new FileWriter("geometrico_07.txt")));
        
        for (int i = 0; i < NSAMPLES; i++) 
            sal_geo.println (gen_geo.sampleDouble());
        sal_geo.close();
        } catch (IOException e) {}
    }

    public static void generador_triangular (double izq, double centro, double der) {
        Triangular gen_tri = new Triangular (izq, centro, der);

        try{
        sal_tri = new PrintWriter(
                             new BufferedWriter(new FileWriter("triangular3_7_10.txt")));

            for (int i = 0; i < NSAMPLES; i++) 
            sal_tri.println (gen_tri.sampleDouble());
        sal_tri.close();
        } catch (IOException e) {}
    }
    
    public static void generador_uniforme (double lim_inf, double lim_sup) {
        Uniform gen_uni = new Uniform (lim_inf, lim_sup);
try {                            
        sal_uni = new PrintWriter(
                             new BufferedWriter(new FileWriter("uniforme_10_20.txt")));

    for (int i = 0; i < NSAMPLES; i++) 
            sal_uni.println (gen_uni.sampleDouble());
        sal_uni.close();
} catch (IOException e) {}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	int test = 1;
    	if (test == 1) {
        	for (int i = 0; i < NTEST; i++) {
            	long tIni = System.currentTimeMillis();
                Normal genNormal = new Normal(10.0, 2.0);
                long tMid = System.currentTimeMillis();
                for (int j = 0; j < NSAMPLES; j++) 
                	genNormal.sampleDouble();
                long tEnd = System.currentTimeMillis();
                System.out.println("Random T INIT: " + (tMid - tIni) + "\tT PROC: " + (tEnd - tMid));
        	}
    	}
    	else {
            generador_normal (10.0, 2.0);
            
            generador_poisson (20.0);
            
            generador_beta (7,11);     

//            generador_chi (10);             
            
            generador_exponencial (10);                     
            
            generador_geometrico (0.7);                     
           
            generador_triangular (3.0,7.0,10);                     
            
            generador_uniforme (10.0, 20.0);
    	}
    }
    
}
