/*******************************************
 
 * Project Name: 8-Puzzle Solver
 * Author : JL Xia
 * Dependent: Board.java
 * 
 ******************************************/

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Solver {
    private MinPQ<searchNode> trials;
    private Board initial;
    private boolean solvable;
    
    private static Comparator<searchNode> hummingcomparator = new Comparator<searchNode>(){

		@Override
		public int compare(searchNode node0, searchNode node1) {
			int hamming0 = node0.moves + node0.initial.hamming();
			int hamming1 = node1.moves + node1.initial.hamming();
			return  Double.compare((double) hamming0, (double) hamming1); 
		}
    };
    
    private static Comparator<searchNode> manhattancomparator = new Comparator<searchNode>(){

 		@Override
 		public int compare(searchNode node0, searchNode node1) {
 			int hamming0 = node0.moves + node0.initial.manhattan();
 			int hamming1 = node1.moves + node1.initial.manhattan();
 			return  Double.compare((double) hamming0, (double) hamming1); 
 		}
     };
    
	public Solver(Board initial) {           // find a solution to the initial board (using the A* algorithm)
        this.initial = initial;
        solvable = false;
        searchNode node = new searchNode(initial, null, 0, true);
        searchNode nodetwin = new searchNode(initial.twin(), null, 0, false);
        trials = new MinPQ<searchNode>(hummingcomparator);
        trials.insert(node);
        trials.insert(nodetwin);
        

        while (!trials.isEmpty()) {
        	searchNode current = trials.delMin();

        	if (current.initial.isGoal()) {
        		trials.insert(current);
        		break;
        	}
        
        	else {
        		Iterable<Board> leafs = current.initial.neighbors();
        		for (Board leaf : leafs){
        	      if (!leaf.equals(current.previous)) { 
        			searchNode leafnode = new searchNode(leaf, current, current.moves + 1, current.twin);
        			trials.insert(leafnode);
        		  }
        	    }
        	}
        }
    }
    
	public boolean isSolvable(){            // is the initial board solvable?
		searchNode last = trials.delMin();
		boolean isSolvable = last.twin;
		trials.insert(last);
		return isSolvable;
    }
    
    public int moves(){                     // min number of moves to solve initial board; -1 if unsolvable
       if (! isSolvable()) return -1;
       else {
    	   searchNode result = trials.delMin();
    	   trials.insert(result);
    	   return result.moves;
       }
    }
    
    public Iterable<Board> solution(){
		Stack <Board> result = new Stack<Board>();
		searchNode currentNode = trials.delMin();
		while (currentNode.previous != null){
			result.push(currentNode.initial);
			currentNode = currentNode.previous;
		}
		result.push(this.initial);
    	return result;      // sequence of boards in a shortest solution; null if unsolvable
     }
	
    private class searchNode {
       public Board initial;
       public searchNode previous;
       public int moves;
       public boolean twin;
       public searchNode(Board initial, searchNode previous, int moves, boolean twin){
    	   this.initial = initial;
    	   this.previous = previous;
    	   this.moves = moves;
    	   this.twin = twin;
       }
    }
    
    public static void main(String[] args) { // solve a slider puzzle (given below)
    
    	   // create initial board from file
        //In in = new In(args[0]);
    	String filename = "./DB/8puzzle/puzzle03.txt";
    	In in = new In(filename);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);
        StdOut.println(initial.toString());
        StdOut.println("manhattan() = " + initial.manhattan());

        // solve the slider puzzle
        Solver solver = new Solver(initial);

        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
        

    }
}