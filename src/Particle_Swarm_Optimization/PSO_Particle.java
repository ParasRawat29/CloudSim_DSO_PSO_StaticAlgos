package Particle_Swarm_Optimization;
import java.util.concurrent.ThreadLocalRandom;
import java.util.*;


public class PSO_Particle{
		
	
	private int noOfCloudlet , noOfVm ;
	
	private double[][] position; 

	protected double[][] best_position;
	/** Velocity vector */
	protected double[][] velocity;
	/** Problem bounds */
	/** Fitness of actual position */
	protected double current_fitness;
	/** Fitness of best position */
	protected double best_fitness = Integer.MAX_VALUE;
	
	
	PSO_Particle(int nt , int nv ){
		noOfCloudlet =nt;
		noOfVm = nv;	
		position = new double[nt][nv];
		best_position = new double[nt][nv];
		velocity = new double[nt][nv];
		
		init();
	}
	
	private void init() {
		
		// initialize particle randomly (assign task to any vm);
		for(int i=0 ; i<noOfCloudlet ; i++) {
			int rndvm = ThreadLocalRandom.current().nextInt(0, noOfVm);
			position[i][rndvm]=1;
		}
		
		// set current position as best position
		setBestPosition(position);
		
		
		calcAndSetFitness();
		
		// setting inital best Fitness;
		double f=0;
		if(Swarm.mode ==0) {
			f = calculateMaxspanFitness();
			
		}else if(Swarm.mode ==1) {
			f  = calculateAverageExecutionFitness();
		}else if(Swarm.mode ==2) {
			f = calculateThroughput();
		}
		setBestFitness(f);

	}
	
	
	
	public double[][] getPosition(){
		return position;
	};
	
	
	public void setBestPosition(double [][]pos) {
		for(int i = 0; i < pos.length; i++)
		    best_position[i] = pos[i].clone();
	}
	
	public double[][] getBestPosition(){
		return best_position;
	};
	
	
	public double[][] getVelocity(){
		return velocity;
	};
	
	public void setVelocity(double[][] v){
		for(int i = 0; i < v.length; i++)
			velocity[i] = v[i].clone();
	};
		
	
	
	public double getFitness(){
		return current_fitness;
	};
	public void setFitness(double fit){
		current_fitness  = fit;
	};
	
	
	
	public double getBestFitness(){
		return best_fitness;
	};
	public void setBestFitness(double fitness) {
		best_fitness = fitness;
	}
	
	
	public void updatePosition() {
			
		// setting all to zero then i will change according to max in row in velocity
		for(int i=0 ; i<position.length ; i++) {
			for(int j=0 ; j<position[0].length ; j++) {
				position[i][j] = 0;
			}
		}
		
		for(int i=0 ; i<velocity.length ; i++) {
			int maxIdx  = 0;
			for(int j=0 ; j<velocity[0].length ; j++) {
				if(velocity[i][j]>velocity[i][maxIdx]) {
					maxIdx = j;
				}
			}
			position[i][maxIdx] = 1;
		}
		
		calcAndSetFitness();
		
	}
	
	
	
	
	

	
	private void calcAndSetFitness() {
		double f=0;
		if(Swarm.mode ==0) {
			f = calculateMaxspanFitness();			
		}else if(Swarm.mode ==1) {
			f  = calculateAverageExecutionFitness();
		}else if(Swarm.mode ==2) {
			f = calculateThroughput();
		}
		setFitness(f);
	}
	
	
	
	public double calculateMaxspanFitness() {
		return  new PSO().calculatePredictedMakespan(position);
	}
	public double calculateAverageExecutionFitness() {
		return  new PSO().calculatePredictedAverageExecutionTime(position);
	}
	
	public double calculateThroughput(){
		return  new PSO().calculatePredictedThroughput(position);
	}
//	public void setCurrentFitness() {
//		current_fitness = calculateFitness(position);
//	}
	
	
	
}