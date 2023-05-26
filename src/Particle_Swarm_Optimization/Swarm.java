package Particle_Swarm_Optimization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import Helper.MatrixFunctions;

public class Swarm{
	private int SWARM_SIZE = 70;
	private int ITERATIONS = 100;
	
	protected ArrayList<PSO_Particle> swarm = new ArrayList<PSO_Particle>();
	protected PSO_Particle best_particle ;
	
	protected double best_fitness; // should me minimum for makespan
	/** Cognitive knowledge factor */
	private static double cognitive = 2;
	/** Social knowledge factor */
	private static double social = 2;
	/** Inertia factor initial value */
	private static double inertia = 0.1;
	public double x = (0.9 - inertia)/ITERATIONS;
	Random rand = new Random();
	double r1 = rand.nextDouble();
	double r2 = rand.nextDouble();
	int nt ,nv;
	public static int mode = 0;
	/**
	 * mode = 0 -> makespan
	 * mode = 1 -> total execution time
	 * mode = 2 -> throughput
	 * 
	 * */
	Swarm(int nt,int nv){
		this.nt = nt;
		this.nv = nv;		
	}
	
	public void run_pso() {
		
		for(int i=0 ; i<ITERATIONS ; i++) {
			for(int j=0 ; j<swarm.size() ; j++) {
				
				// calculate velocity
				double [][] v = calculateVelocity(swarm.get(j));
				
				// set velocity
				swarm.get(j).setVelocity(v);
				
				// update position
				swarm.get(j).updatePosition();
				
				
				// check is paricle curr position is better than best then setbest as curr
				
				
				/**
				 * if(swarm.get(j).getBestFitness() < swarm.get(j).getFitness()) -> for throughput fitness
				 * if(swarm.get(j).getBestFitness() > swarm.get(j).getFitness()) -> for makespan fitness
				 * if(swarm.get(j).getBestFitness() > swarm.get(j).getFitness()) -> for average execution time fitness			 *  
				 */
				if(mode==0 || mode ==1) {
					if(swarm.get(j).getBestFitness() > swarm.get(j).getFitness()) {
						// set curr position as best
						swarm.get(j).setBestPosition(swarm.get(j).getPosition());
						
						// set curr pos fitness as best fitness of that particle
						swarm.get(j).setBestFitness(swarm.get(j).getFitness());
					}
				}
				else if(mode==2) {
					if(swarm.get(j).getBestFitness() < swarm.get(j).getFitness()) {
						// set curr position as best
						swarm.get(j).setBestPosition(swarm.get(j).getPosition());
						// set curr pos fitness as best fitness of that particle
						swarm.get(j).setBestFitness(swarm.get(j).getFitness());
					}
					
				}
				
				
				/**
				 * if(swarm.get(j).getBestFitness() > best_particle.getFitness()) -> for throughput fitness
				 * if(swarm.get(j).getBestFitness() < best_particle.getFitness()) -> for makespan fitness
				 * if(swarm.get(j).getBestFitness() < best_particle.getFitness()) -> for average execution time fitness			 *  
				 */
				if(mode==0 || mode ==1) {
					if(swarm.get(j).getBestFitness() < best_particle.getFitness()) {
						setBestParticleInSwarm(swarm.get(j));
					}
				}
				else if(mode==2) {
					if(swarm.get(j).getBestFitness() > best_particle.getFitness()) {
						setBestParticleInSwarm(swarm.get(j));
					}
					
				}
				
				
								
			}
			System.out.println("iteration  : "+ i+" , best fitness : "+best_particle.getFitness());
			inertia +=x;
		}
		
	}
	
	public void init(){
        
		// add particle to swarm
		for (int i = 0; i < SWARM_SIZE; i++) {
            swarm.add(new PSO_Particle(nt, nv));
        }
        
        // set global best particle as swarm[0];
		setBestParticleInSwarm(swarm.get(0));
			
		// update best particle and fitness considering whole swarm
		for (int i = 1; i < swarm.size(); i++) {
			if(mode == 0 || mode ==1) {
				if (swarm.get(i).getFitness() < best_particle.getFitness()) {
					  setBestParticleInSwarm(swarm.get(i));
				 }
			}else if(mode==2) {
				if (swarm.get(i).getFitness() > best_particle.getFitness()) {
					  setBestParticleInSwarm(swarm.get(i));
				 }
			}
			  
		}
	}
	
	
	
	
	public void setBestParticleInSwarm(PSO_Particle p) {
		best_particle = p;
		best_fitness = p.getFitness();
	}
	
	
	public double[][]calculateVelocity(PSO_Particle p){
		
		double [][] currPos = p.getPosition();
		double [][] bestPos = p.getBestPosition();
		double [][] currV = p.getVelocity();
		double[][] v = MatrixFunctions.addMatrix(
				MatrixFunctions.scalarMultipicationMatrix(currV, inertia),
				MatrixFunctions.scalarMultipicationMatrix(MatrixFunctions.subtractMatrix(bestPos,currPos),cognitive*r1),
				MatrixFunctions.scalarMultipicationMatrix(MatrixFunctions.subtractMatrix(best_particle.getPosition(),currPos),social*r2));
		
		return v;
		
	}
	
	public PSO_Particle getBestParticle() {
		return best_particle;
	}
	
	public double[][] getBestParticlePosition() {
		return best_particle.getPosition();
	}
	

	
}