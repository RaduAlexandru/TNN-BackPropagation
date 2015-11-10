/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnn.back.propagation;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author Alex
 */
public class TNNBackPropagation {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic 
        System.out.println("Hello world");

        int N = 4;
        int H = 100;
        int M = 2;
        int seed = 100;
        double learning_rate_m=0.05;
        double learning_rate_h=0.05;
        Random generator = new Random(seed);

        //Creating the matrix of weights and initializing them
        double[][] w_nh = new double[N + 1][H];
        double[][] w_hm = new double[H + 1][M];
        
        double[][] change_w_nh = new double[N + 1][H];
        double[][] change_w_hm = new double[H + 1][M];
       
        double[] net_n = new double[N];
        double[] net_h = new double[H];
        double[] net_m = new double[M];

        double[] out_n = new double[N];
        double[] out_h = new double[H];
        double[] out_m = new double[M];

        double[] delta_n = new double[N];
        double[] delta_h = new double[H];
        double[] delta_m = new double[M];
        

        double low = -2.0;
        double high = 2.0;

        //initialize w_nh
        for (int n = 0; n < N; n++) {
            for (int h = 0; h < H; h++) {
                w_nh[n][h] = low + (high - low) * generator.nextDouble();
            }
        }

        //initialize w_hm
        for (int h = 0; h < H; h++) {
            for (int m = 0; m < M; m++) {
                w_hm[h][m] = low + (high - low) * generator.nextDouble();
            }
        }
        
        //initialize the output of the bias neurons
        out_n[0]=1.0;
        out_h[0]=1.0;

        //Read from training file
        File file = new File(".//src//tnn//back//propagation//training.dat");
        Scanner s = new Scanner(file);
        
        double[][] input = new double[11][4];
        double[][] teacher = new double[11][2];
        
        int valuesStored=-1;
        int tableRow=-1;
        

        while(s.hasNextLine()){
        String str = s.nextLine();
        
       
            String[] splited = str.split(" ");
            
            for (int i = 0; i< splited.length; i++) {
                if (!splited[i].isEmpty() && splited.length>7){
               
                    try{
                    double d= Double.parseDouble(splited[i]);
                    
                     valuesStored++;
                     if (valuesStored<4)
                       input[tableRow][valuesStored]=d;
                     else
                       teacher[tableRow][valuesStored-4]=d;
                       
                        
                    }catch(NumberFormatException nfe){
                        
                    }
                       
                }
            }
            System.out.println("");
            if (splited.length>7)
                tableRow++;
            valuesStored=-1;
        }
        
        System.out.println("finished reding from file");
        
        
       
        System.out.println("input values are");
        for (int h = 0; h < 11; h++) {
            for (int m = 0; m < 4; m++) {
                System.out.print(" "+ input[h][m] );
            }
            System.out.println(" ");
        }
        System.out.println("teacher value are");
        for (int h = 0; h < 11; h++) {
            for (int m = 0; m < 2; m++) {
                System.out.print(" "+ teacher[h][m] );
            }
            System.out.println(" ");
        }
        
        
        
        
        
        ///
        //Grab the first line of the pattern
        
        
       
        
        for (int iters=0;iters<100000;iters++){
       
            int patternRow=1;
            //patternRow = generator.nextInt(11);

            //Calculate the input, summed weight (which is just the respective number in the input) 
            //pass the summed input to the tranfer function (which is the identity function for the neuron in the  layer)
            //Therefore the output of the neurons in N is the same as the input
            for (int n=1;n<N;n++) {
                double sum=0.0;
                //For neuron n of layer N
                sum=input[patternRow][n-1];
                net_n[n]=sum;
                
                //Apply net_n to the transfer function and calculate the output
                out_n[n]= identity (net_n[n]);
                
            }
            
            
         
            //Calcate the summed weight of the hidden layer (h) + the bias. 
            //That summed weight is the input to sigmoid function. net_h[m]=sum
            //Applying this weighted sum to the sigmoid function you get the output of that layer
            for(int h=1;h<H;h++){
                double sum=w_nh[0][h];
                for (int n=1; n<N;n++){
                    sum+=w_nh[n][h]*out_n[n];
                }
                net_h[h]=sum;
                
                //Apply the weighter sum to the transfer function to calculate the output
                out_h[h]= tanh (net_h[h]);
            }
            
            //Do the same as above for the output layer M
            for(int m=0;m<M;m++){
                double sum=w_hm[0][m];
                
                for (int h=1; h<H;h++){
                    sum+=w_hm[h][m]*out_h[h];
                }
                
                net_m[m]=sum;
                out_m[m]= tanh (net_m[m]);
            }
        
            //The output of M is
            System.out.println("output is: ");
            for (int m = 0; m < M; m++) {
                System.out.print("   " + out_m[m]);     
            }
            
            //BackPropagation
            
            //Compare with teacher
            /*System.out.println("the differnece between the output adn the teacher is");
            for (int m = 0; m < M; m++) {
                System.out.print("  "  + (teacher[patternRow][m] - out_m[m]));
            }
            System.out.println("");*/
            
             //Calculate delta_m at output layer and all change_w_hm
            for (int m = 0; m < M; m++) {
                delta_m[m]=  ( teacher[patternRow][m] - out_m[m] )  * 
                            (1.0- out_m[m]*out_m[m]);  //derivative of the transfer fcuntion
            }
            for (int m = 0; m < M; m++) {
                for (int h = 0; h < H; h++) {
                     change_w_hm [h][m]=learning_rate_m* delta_m[m] * out_h[h];
                }
            }
            //Calculate delta_h at hidden layer and all change_w_nh
            for (int h = 0; h < H; h++) {
                double sum_delta=0.0;
                for (int m = 0; m < M; m++) {
                    sum_delta+=delta_m[m]*w_hm[h][m];
                }
                delta_h[h]=sum_delta*   (1.0- out_h[h]*out_h[h] );  //Derivative of the trasnfer function
            }
            for (int h = 0; h < H; h++) {
                for (int n = 0; n < N; n++) {
                     change_w_nh [n][h]=learning_rate_h* delta_h[h] * out_n[n];
                }
            }
            //Update all weights  wij+=change_w_ij
            for (int n = 0; n < N; n++) {
                for (int h = 0; h < H; h++) {
                    w_nh[n][h] += change_w_nh[n][h];
                }
            }

            //initialize w_hm
            for (int h = 0; h < H; h++) {
                for (int m = 0; m < M; m++) {
                    w_hm[h][m] += change_w_hm[h][m];
                }
            }
            
            //Stop with a reasonable criteria
            
            
           
        
        }
        
        
        
        
        
        
        
        
        
        
        
    }
    
    
    
    
    
    private static double sigmoid(double x)
{
    return 1 / (1 + Math.exp(-x));
}
    
    private static double tanh(double x)
{
    return Math.tanh(x);
}
     
    
    private static double identity(double x)
{
    return x;
}
    
    
}
