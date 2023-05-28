package MCT_DSO;

import java.util.concurrent.ThreadLocalRandom;

import java.util.Arrays;
import Helper.MatrixFunctions;
import Helper.MyMath;


public class Dove{
	
	private int noOfCloudlet , noOfVm ;
	private double[][] position;
	private double[][] vmAllocations;
	private double cost = 0;
	private double satiety = 0;
	public int id;
    private double distanceFromMostSatisfied = 0;
	
	Dove(int nt , int nv , int id){
		this.id = id;
		this.noOfCloudlet =nt;
		this.noOfVm = nv;	
		this.position = new double[nt][nv];		
		this.vmAllocations = new double[nt][nv];		
		init();
	}
	
	
	private void init() {
		// initialize with MCT
		if(this.id == 10 || this.id == 15 || this.id == 20 || this.id == 25 || this.id == 30 || this.id == 35) {
			System.out.println(this.id+" -=-=-=-=-=-=-=-=-=-=-=-=\n\n");
			this.vmAllocations = MCTInitialization();
			
			for(int i=0 ; i<position.length ; i++) {
				for(int j=0 ; j<position[i].length ; j++) {
					if(vmAllocations[i][j]!=1)this.position[i][j] = ThreadLocalRandom.current().nextDouble(0, 0.9);
					else this.position[i][j] = 1.1;
				}
			}
		}
		
		else {	
			// initialize particle randomly (assign task to any vm);		
			for(int i=0 ; i<position.length ; i++) {
				for(int j=0 ; j<position[i].length ; j++) {
					this.position[i][j] = ThreadLocalRandom.current().nextDouble(0, 1);
				}
			}
			// update according to position
			updateVmAllocations(); 
		}
		// update cost according to position
		updateCost(); 
		this.satiety=0;
	}
	
	
	private double[][] MCTInitialization() {
		
		double [][]estimatedCompletionTime = new double[this.noOfCloudlet][this.noOfVm];
		double[][] allocationMat = new double[this.noOfCloudlet][this.noOfVm]; 
		
		// fill estimateCompletioTimeMatrix
		for(int i=0 ; i<this.noOfCloudlet ; i++) {
			for(int j=0 ; j<this.noOfVm ; j++) {
				estimatedCompletionTime[i][j] = MCT_DSO.getCompletionTime(i, j);
			}
		}
		
		
		for(int i=0 ; i<this.noOfCloudlet ; i++) {
			// find minimum execution time across all vm for that task
			double minTime =0 ; int minVm =0;
			for(int j=0 ; j<this.noOfVm ; j++) {
				if(estimatedCompletionTime[i][j]<minTime) {
					minTime = estimatedCompletionTime[i][j];
					minVm = j;
				}
			}
			
			// allocate the task to that Vm;
			allocationMat[i][minVm] = 1;
			
			for(int k=0 ; k<this.noOfCloudlet; k++) {
				estimatedCompletionTime[k][minVm] +=minTime; 
			}
		}
		
		return allocationMat;
	}
	
	
	
	
	
	public void updateVmAllocations() {
			 for(int i=0 ; i<vmAllocations.length ; i++) {
					for(int j=0 ; j<vmAllocations[0].length ; j++) {
						this.vmAllocations[i][j] = 0;
					}
				}
			
			for(int i=0 ; i<position.length ; i++) {
				int maxIdx  = 0;
				for(int j=0 ; j<position[i].length ; j++) {
					if(position[i][j]>position[i][maxIdx]) {
						maxIdx = j;
					}
				}
				this.vmAllocations[i][maxIdx] = 1;
			}
	}
	
	
	
	public double getCost() {
		return this.cost;
	}
	
	
	public double[][] getPosition() {
		return this.position;
	}
	
	
	public double[][] getVmAllocations() {
		return this.vmAllocations;
	}
	
	public void updateSatiety(Dove bestDove , int epoch , double lambda , double maxEpoch , double minDiffCost , double maxDiffCost) {
		if(bestDove.getCost() == 0)
            this.satiety = lambda*this.satiety + 1;
        else {
 
        	this.satiety = (lambda*this.satiety + Math.pow(epoch/maxEpoch, MyMath.mapRange(0.0, 1, minDiffCost, maxDiffCost, (this.getCost() - bestDove.getCost()))));
        }
	
	}
	public double getSatiety() {
		return this.satiety;
	}
	
	
	public void updateDistanceFromMostSatisfied(Dove mostSatisfiedDove) {
		this.distanceFromMostSatisfied = MatrixFunctions.distanceBetween(this.position , mostSatisfiedDove.getPosition());
	}
	public double getDistanceFromMostSatisfied() {
		return this.distanceFromMostSatisfied;
	}
	
	
	public void updatePosition(Dove bestDove , Dove mostSatisfiedDove , double maxDistance , double learningRate) {
			
		double conservativeFactor = (bestDove.getSatiety() - this.getSatiety())/bestDove.getSatiety();      
		double socialFactor = (1-((this.getSatiety()-mostSatisfiedDove.getSatiety())/maxDistance));

        double beta = conservativeFactor*socialFactor;
        double scalar = learningRate*beta;
      
        double [][] yoyo = MatrixFunctions.subtractMatrix(mostSatisfiedDove.getPosition(),this.position);
        
        double[][] changeInPosition = MatrixFunctions.scalarMultipicationMatrix(yoyo,scalar);
        
        boolean flag = true; // all changeInPositon cells are same
        for(int i=0 ; i<changeInPosition.length ; i++) {
        	double curr = changeInPosition[i][0];
        	for(int j=0 ; j<changeInPosition[i].length ; j++) {
        		if(curr!=changeInPosition[i][j]) {
        			flag = false;
        			break;
        		}
        	}
        	if(!flag) break;
        }
        if(flag) {
        	return;
        };
        
        
        this.position = MatrixFunctions.addMatrix(this.position, changeInPosition);
                
        updateVmAllocations();	
		
	}
	
	
	public void updateCost() {
		if(Swarm.mode==0)this.cost = calculateMakespanFitness(this.vmAllocations);
		else if(Swarm.mode==1)this.cost = calculateTotalExecutionFitness(this.vmAllocations);
		else if(Swarm.mode==2)this.cost = calculateThroughput(this.vmAllocations);
		
	}
	
	public double calculateMakespanFitness(double [][] allocations){
		return  new MCT_DSO().calculatePredictedMakespan(allocations);
	}
	
	public double calculateTotalExecutionFitness(double [][] allocations) {
		return  new MCT_DSO().calculatePredictedTotalExecutionTime(allocations);
	}
	
	public double calculateThroughput(double [][] allocations){
		return  new MCT_DSO().calculatePredictedThroughput(allocations);
	}
	
}