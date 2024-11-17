import java.util.ArrayList;
import java.util.Random;

public class TSP{
    private Point[] graph;
    private int firstCity;

    public TSP(Point[] graph){
        this.graph = graph;
        Random random = new Random();
        // Randomly select the first city
        firstCity = random.nextInt(graph.length);
    }

    private double distance(Point a,Point b){
        return Math.sqrt(Math.pow(a.x-b.x, 2) + Math.pow(a.y-b.y, 2));
    }

    public double getTotalCost(ArrayList<Integer> path){
        double totalCost = 0;
        for(int i = 0; i < path.size()-1; i++){
            totalCost += distance(graph[path.get(i)], graph[path.get(i+1)]);
        }
        return totalCost;
    }

    // Constructive Heuristic - 0
    public ArrayList<Integer> nearestNeighbourHeuristic(){
        ArrayList<Integer> path = new ArrayList<Integer>();
        boolean[] visited = new boolean[graph.length];

        int currentCity = firstCity;
        path.add(currentCity);
        visited[currentCity] = true;

        while(path.size() < graph.length){
            double minDistance = Double.MAX_VALUE;
            int nearestCity = -1;
            for(int i = 0; i < graph.length; i++){
                if(!visited[i]){
                    double distance = distance(graph[currentCity], graph[i]);
                    if(distance < minDistance){
                        minDistance = distance;
                        nearestCity = i;
                    }
                }
            }
            path.add(nearestCity);
            visited[nearestCity] = true;
            currentCity = nearestCity;
        }

        // The salesman returns to the first city
        path.add(path.get(0));
        return path;
    }

    // Constructive Heuristic - 1
    public ArrayList<Integer> nearestInsertionHeuristic(){
        ArrayList<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[graph.length];

        // Generating the initial tour
        int currentCity = firstCity;
        path.add(currentCity);
        visited[currentCity] = true;
        int nearestCity = findNearestCity(currentCity, visited);
        path.add(nearestCity);
        visited[nearestCity] = true;
        path.add(currentCity);

        while(path.size() < graph.length){
            double minDistance = Double.MAX_VALUE;
            double minIncrease = Double.MAX_VALUE;
            int cityToInsert = -1;
            int positionToInsert = -1;

            for(int i=0;i<path.size()-1;i++){
                for(int j=0;j<graph.length;j++){
                    if(!visited[j]){
                        double distance = distance(graph[path.get(i)], graph[j]);
                        if(distance < minDistance){
                            minDistance = distance;
                            cityToInsert = j;
                        }
                    }
                }
            }

            for(int i=0;i<path.size()-1;i++){
                int city1 = path.get(i);
                int city2 = path.get(i+1);

                double costIncrease = distance(graph[city1],graph[cityToInsert]) + distance(graph[cityToInsert],graph[city2]) - distance(graph[city1],graph[city2]);
                if(costIncrease < minIncrease){
                    minIncrease = costIncrease;
                    positionToInsert = i+1;
                }
            }

            path.add(positionToInsert, cityToInsert);
            visited[cityToInsert] = true;
        }
        return path;
    }

    // Constructive Heuristic - 2
    public ArrayList<Integer> cheapestInsertionHeuristic(){
        ArrayList<Integer> path = new ArrayList<>();
        boolean[] visited = new boolean[graph.length];
       
        // Generating the initial tour
        int currentCity = firstCity;
        path.add(currentCity);
        visited[currentCity] = true;
        int nearestCity = findNearestCity(currentCity, visited);
        path.add(nearestCity);
        visited[nearestCity] = true;
        path.add(currentCity);

        while(path.size() < graph.length){
            double minIncrease = Double.MAX_VALUE;
            int cityToInsert = -1;
            int positionToInsert = -1;

            for(int i=0;i<path.size()-1;i++){
                for(int j=0;j<graph.length;j++){
                    if(!visited[j]){
                        int city1 = path.get(i);
                        int city2 = path.get(i+1);
                        double costIncrease = distance(graph[city1],graph[j]) + distance(graph[j],graph[city2]) - distance(graph[city1],graph[city2]);
                        if(costIncrease < minIncrease){
                            minIncrease = costIncrease;
                            cityToInsert = j;
                            positionToInsert = i+1;
                        }
                    }
                }
            }
            path.add(positionToInsert, cityToInsert);
            visited[cityToInsert] = true;
        }
        return path;
    }

    private int findNearestCity(int city, boolean[] visited){
        double minDistance = Double.MAX_VALUE;
        int nearestCity = -1;
        for(int i = 0; i < graph.length; i++){
            if(!visited[i]){
                double distance = distance(graph[city], graph[i]);
                if(distance < minDistance){
                    minDistance = distance;
                    nearestCity = i;
                }
            }
        }
        return nearestCity;
    }


    private void rearrangePath(ArrayList<Integer> path, int i, int j){
        while(i < j){
            int temp = path.get(i);
            path.set(i, path.get(j));
            path.set(j, temp);
            i++;
            j--;
        }
    }

    // Perturbative Heuristic - 0 
    public ArrayList<Integer> twoOptHeuristic(ArrayList<Integer> path){
        ArrayList<Integer> currentPath = new ArrayList<>(path);
        boolean improvement = false;
        do{
            improvement = false;
            // Keeping the first city fixed (the first and last entry in the path)
            // This eliminates redundant solutions that are just rotations of the same path
            for(int i = 1;i<currentPath.size()-2;i++){
                for(int j=i+1;j<currentPath.size()-1;j++){
                    Point a = graph[currentPath.get(i)];
                    Point b = graph[currentPath.get(i+1)];
                    Point c = graph[currentPath.get(j)];
                    Point d = graph[currentPath.get(j+1)];

                    double oldCost = distance(a,b) + distance(c,d);
                    double newCost = distance(a,c) + distance(b,d);

                    if(newCost < oldCost){
                        // Order of the items from i+1 to j is reversed
                        rearrangePath(currentPath, i+1, j);
                        improvement = true;
                    }
                }
            }
        }while(improvement);

        return currentPath;
    }

    // Perturbative Heuristic - 1
    public ArrayList<Integer> nodeShiftHeuristic(ArrayList<Integer> path){
        ArrayList<Integer> currentPath = new ArrayList<>(path);
        double minCost = getTotalCost(currentPath);
        boolean improvement = false;
        do{
            improvement = false;
            for(int i = 1; i < currentPath.size()-1 ; i++){
                for(int j = 1; j < currentPath.size()-1; j++){
                    if(i != j){
                        ArrayList<Integer> newPath = new ArrayList<>(currentPath);
                        int node = newPath.remove(i);
                        newPath.add(j, node);

                        double newCost = getTotalCost(newPath);
                        if(newCost < minCost){
                            minCost = newCost;
                            currentPath = newPath;
                            improvement = true;
                            break;
                        }
                    }
                }
                if(improvement){
                    break;
                }
            }
        }while(improvement);

        return currentPath;
    }


    // Perturbative Heuristic - 2
    public ArrayList<Integer> nodeSwapHeuristic(ArrayList<Integer> path){
        ArrayList<Integer> currentPath = new ArrayList<>(path);
        double minCost = getTotalCost(currentPath);
        boolean improvement = false;

        do{
            improvement = false;
            for(int i=1;i<path.size()-2;i++){
                for(int j=i+1;j<path.size()-1;j++){
                    ArrayList<Integer> newPath = new ArrayList<>(currentPath);
                    int temp = newPath.get(i);
                    newPath.set(i, newPath.get(j));
                    newPath.set(j, temp);

                    double newCost = getTotalCost(newPath);
                    if(newCost < minCost){
                        minCost = newCost;
                        currentPath = newPath;
                        improvement = true;
                        break;
                    }
                }
                if(improvement){
                    break;
                }
            }

        }while(improvement);

        return currentPath;
    }

    private ArrayList<Integer> constructTour(int i){
        if(i == 0){
            return nearestNeighbourHeuristic();
        }
        else if(i == 1){
            return nearestInsertionHeuristic();
        }
        else if(i == 2){
            return cheapestInsertionHeuristic();
        }
        else{
            return null;
        }
    }
    private ArrayList<Integer> improveTour(ArrayList<Integer> path, int i){
        if(i == 0){
            return twoOptHeuristic(path);
        }
        else if(i == 1){
            return nodeShiftHeuristic(path);
        }
        else if(i == 2){
            return nodeSwapHeuristic(path);
        }
        else{
            return null;
        }
    }

    public double[][] simulationResult(){
        double[][] result = new double[3][4];
        // 0 - constructive cost 
        // 1 - 2-opt cost
        // 2 - node shift cost
        // 3 - node swap cost
        for(int i = 0; i < 3 ; i++){
            ArrayList<Integer> path = constructTour(i);
            result[i][0] = getTotalCost(path);
            for(int j = 0 ; j < 3 ; j++){
                ArrayList<Integer> improvedPath = improveTour(path, j);
                result[i][j+1] = getTotalCost(improvedPath);
            }
        }
        return result;
    }


}