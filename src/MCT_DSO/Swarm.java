package MCT_DSO;


import java.util.ArrayList;

public class Swarm{
	private int SWARM_SIZE =100;
	private int maxEpoch= 500;
	
	private double learningRate = 0.1;
	private double lambda = 0.09;
	
	private Dove bestDove; // best dove according to cost
	private Dove mostSatisfiedDove; // may not be best dove 
	private double maxDistance; // max Distance between ant two dove in swarm
	private double minDiffCost;  //minimum value of this.getCost() - bestDove.getCost()
	private double maxDiffCost; //maximum value of this.getCost() - bestDove.getCost()
	
	protected ArrayList<Dove>swarm = new ArrayList<Dove>();
	public static int mode = 0;
	/**
	 * mode = 0 -> makespan
	 * mode = 1 -> total execution time
	 * mode = 2 -> throughput
	 * */
	
	
	
	int nt ,nv;
	
	Swarm(int nt,int nv){
		this.nt = nt;
		this.nv = nv;		
	}
	
	public void init(){
        // add dove to swarm
		for (int i = 0; i < SWARM_SIZE; i++) {
            swarm.add(new Dove(nt, nv,i));
        }
	}
	
	public double findMinDiffCost() {
		double min = swarm.get(0).getCost()-bestDove.getCost();
		
		for(int i=1; i<SWARM_SIZE; i++) {
			double curr = swarm.get(i).getCost() - bestDove.getCost();
			if(curr<min)
				min=curr;
		}
		
		return min;
	}
	
	public double findMaxDiffCost() {
		double max = swarm.get(0).getCost()-bestDove.getCost();
		
		for(int i=1; i<SWARM_SIZE; i++) {
			double curr = swarm.get(i).getCost() - bestDove.getCost();
			if(curr>max)
				max=curr;
		}
		
		return max;
	}
	
	public void run_dso() {
		
		for(int e = 1 ; e<=maxEpoch ; e++) {		
			System.out.println("iteration : "+e);
			updateLearningRate(e);
			
			Dove b = findBestDove();
			setBestDove(b);
			
			this.minDiffCost = findMinDiffCost();
			this.maxDiffCost = findMaxDiffCost();
			
			updateAllDovesSatiety(e);
			updateMostSatisfiedDove();
			
			calculateDistance();
			updateMaxDistance(); 
//			System.out.println("maxDistance " + maxDistance);
			
			updateAllDovesPositionAndCost();
			System.out.println("best cost : " + bestDove.getCost());
//			System.out.println("--------------------------");
		}
		
	}
	
	public double[][] getBestDoveAllocation() {
		return bestDove.getVmAllocations();
	}
	
	private void updateLearningRate(int epoch) {
		learningRate = 0.1 * (1-(epoch/maxEpoch));
	}
	
	// best dove is minimum cost dove
	private Dove findBestDove() {
		Dove md = swarm.get(0);

	    for(int i=0; i<swarm.size(); i++){
	    	
	    	if(mode==0 || mode ==1) {
	    		// for makespan and average execution time it should be low
	   	    	if(swarm.get(i).getCost()<md.getCost()){
	   	    		md = swarm.get(i);
	   	    	}
	    	}else if(mode ==2) {
	    		// for throughput it should be high
	    		if(swarm.get(i).getCost() > md.getCost()){
	   	    		md = swarm.get(i);
	   	    	}
	    	}
	    }
	    return md;
	}
	
	private void setBestDove(Dove d) {
		this.bestDove = d;
	}
	
	
	
	public void updateAllDovesSatiety(int e) {
		// loop through swam and update satiety of each dove		
		for(int i=0; i<swarm.size(); i++){
	        swarm.get(i).updateSatiety(bestDove , e ,lambda, maxEpoch ,minDiffCost ,maxDiffCost);
	    }
	}
	
	
	public void updateMostSatisfiedDove() {
		Dove ms = swarm.get(0);
		for(int i=0; i<swarm.size(); i++){
			if(swarm.get(i).getSatiety() > ms.getSatiety())
				ms = swarm.get(i);
		}
		this.mostSatisfiedDove = ms;
	}
	
	
	//calculate distance of each dove from the most satisfied dove
	private void calculateDistance() {
		for(int i=0; i<swarm.size(); i++){			
			swarm.get(i).updateDistanceFromMostSatisfied(this.mostSatisfiedDove);
		}
	}
	
	
	private void updateMaxDistance() {
		double max = 0;
	    for(int i=0; i<swarm.size(); i++){
	      if(swarm.get(i).getDistanceFromMostSatisfied()>max){
	        max = swarm.get(i).getDistanceFromMostSatisfied();
	      }
	    }
	    this.maxDistance = max;
	}
	
	
	// update the all doves and their cost
	private void updateAllDovesPositionAndCost() {
		 for(int i=0; i<swarm.size(); i++){
		        swarm.get(i).updatePosition(bestDove,mostSatisfiedDove , maxDistance , learningRate);
		        swarm.get(i).updateCost();
		   }
	}
	
	
}