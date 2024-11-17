import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static Point[] generateGraph(File inputFile){
        int dimension = 0;
        Point[] graph = null;
        try{
            Scanner inputReader = new Scanner(inputFile);
            if(!inputReader.hasNext()){
                inputReader.close();
                inputFile.delete();
                throw new FileNotFoundException();
            }
            while(inputReader.hasNext()){
                String data = (String) inputReader.next();
                if(data.equals("DIMENSION:")){
                    dimension = inputReader.nextInt();
                    graph = new Point[dimension];
                }
                else if(data.equals("DIMENSION")){
                    inputReader.next();
                    dimension = inputReader.nextInt();
                    graph = new Point[dimension];
                }
                else if (data.equals("NODE_COORD_SECTION")){
                    for(int i = 0; i < dimension; i++){
                        int node = inputReader.nextInt();
                        double x = inputReader.nextDouble();
                        double y = inputReader.nextDouble();
                        graph[node-1] = new Point(x, y);
                    }
                    break;                    
                }
            }
            inputReader.close();
        }catch(FileNotFoundException e){
            System.out.println("File not found");
        }          
        return graph;
    }
    public static String getBestPerformer(int x,int y){
        if(x==0){
            if(y==0) return "NNH";
            if(y==1) return "NNH + 2-OPT";
            if(y==2) return "NNH + Node Shift";
            if(y==3) return "NNH + Node Swap";
        }
        if(x==1){
            if(y==0) return "NIH";
            if(y==1) return "NIH + 2-OPT";
            if(y==2) return "NIH + Node Shift";
            if(y==3) return "NIH + Node Swap";
        }
        if(x==2){
            if(y==0) return "CIH";
            if(y==1) return "CIH + 2-OPT";
            if(y==2) return "CIH + Node Shift";
            if(y==3) return "CIH + Node Swap";
        }
        return "Error";
    }
    
    public static void main(String[] args) {
        File directory = new File("data");
        File[] files = null;
        if(directory.isDirectory()){
            files = directory.listFiles();
        }
        else{
            System.out.println("Error in reading directory");
            System.exit(1);
        }

        if(files == null || files.length == 0){
            System.out.println("No files found in the directory");
            System.exit(1);
        }
        try{
            FileWriter writer = new FileWriter("output.csv");
            writer.write("Filename,NNH,NNH + 2-OPT,NNH + Node Shift,NNH + Node Swap,NIH,NIH + 2-OPT,NIH + Node Shift,NIH + Node Swap,CIH,CIH + 2-OPT,CIH + Node Shift,CIH + Node Swap, Best Performer\n");
            // The last file is skipped as it is a pdf file
            for(int i = 0;i<files.length -1;i++){
                Point[] graph = generateGraph(files[i]);
                if(graph == null){
                    System.out.println("Error in generating graph from file "+files[i].getName());
                    System.exit(1);
                }
                TSP tsp = new TSP(graph);
                double[][] result = tsp.simulationResult();
                String output = files[i].getName()+",";
                double min = Double.MAX_VALUE;
                int x = -1;
                int y = -1; 
                for(int j=0;j<result.length;j++){
                    for(int k=0;k<result[0].length;k++){
                        output += result[j][k]+",";
                        if(result[j][k] < min){
                            min = result[j][k];
                            x = j;
                            y = k;
                        }
                    }
                }
                output += getBestPerformer(x, y);
                writer.write(output+"\n");
            }
            writer.close();
        }catch(IOException e){
            System.out.println("Error in writing to file");
        }


    }
    
}
