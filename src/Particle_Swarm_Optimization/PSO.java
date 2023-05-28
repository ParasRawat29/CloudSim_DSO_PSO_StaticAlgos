package Particle_Swarm_Optimization;


import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import WriteToCsv.*;
import Helper.*;

public class PSO{
	
	
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static int reqTasks = 1000;
	private static int reqVms = 200;
	private static WriteToCsv writeTofileObj;
	
	private static PSOBroker broker;
	private static double[][] ETC_MATRIX;
	
	
	public static void main(String[] args) {
		writeTofileObj = new WriteToCsv("C:\\Users\\Home\\Desktop\\cloudsim _DSO_PSO_and_StaticAlgos\\src\\results\\output.csv",
				"PSO");
		Log.printLine("Starting PSO...");
		
		
		try {
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			Datacenter datacenter0 = createDatacenter("Datacenter_0");
			
			broker = createBroker();
			int brokerId = broker.getId();
			
			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId);
			
			broker.submitVmList(vmlist);
			
			cloudletList = new CloudletCreator3().createUserCloudlet(reqTasks, brokerId);
			
			broker.submitCloudletList(cloudletList);
			
			
			fillExecutionTimeMatrix();
			
			broker.scheduleTaskstoVms(reqTasks,reqVms,cloudletList,vmlist);

			CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			CalculateSimulationResults.calulate(newList, vmlist, reqTasks, reqVms,null);
			
			Log.printLine("PSO finished!");
			
		}catch(Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
		
		
	}	
	
	
	private static void fillExecutionTimeMatrix() {
		ETC_MATRIX = new double[reqTasks][reqVms];
	        for (int i = 0; i < reqTasks; i++) {
	            for (int j = 0; j < reqVms; j++) {
	            	ETC_MATRIX[i][j] = (double) cloudletList.get(i).getCloudletLength() /(vmlist.get(j).getMips()*vmlist.get(j).getNumberOfPes());
	           }
	      }
	}
	
	
	
	private static PSOBroker createBroker() {

		PSOBroker broker = null;
		try {
			broker = new PSOBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	
	private static Datacenter createDatacenter(String name) {
		Datacenter datacenter = new DataCenterCreator().createUserDatacenter(name, reqVms);
		return datacenter;
	}
	

    	
    public double calculatePredictedMakespan(double[][] position) {
    	    
    	double [] tempPosition = new double[position.length];
    	   	
    	for(int i=0 ; i<position.length ; i++) {
    		for(int j=0 ; j<position[i].length ; j++) {
    			if(position[i][j]==1) {
    				tempPosition[i] = j;
    				break;
    			}
    		}
    	}
    	double[] finishTimes = new double[reqVms];
    	
    	for (int i = 0; i < reqTasks; i++) {
    		finishTimes[(int)tempPosition[i]] += ETC_MATRIX[i][(int)tempPosition[i]];
        }
    	
    	double maxFinishTime = 0;
        for(int i = 0; i < reqVms; i++) {
             if (finishTimes[i] > maxFinishTime) {
                maxFinishTime = finishTimes[i];
             }
         }
         
         return maxFinishTime;
    }
	
    
    public double calculatePredictedTotalExecutionTime(double[][] position) {
    	double [] tempPosition = new double[position.length];
	   	
    	for(int i=0 ; i<position.length ; i++) {
    		for(int j=0 ; j<position[i].length ; j++) {
    			if(position[i][j]==1) {
    				tempPosition[i] = j;
    				break;
    			}
    		}
    	}
    	
    	double totalExecutionTime = 0;
    	for (int i = 0; i < reqTasks; i++) {
    		totalExecutionTime += ETC_MATRIX[i][(int)tempPosition[i]];
        }
    	
    	return totalExecutionTime;
    	
    	
    }
    
    
    public double calculatePredictedThroughput(double[][] position) {
    	double makespan = calculatePredictedMakespan(position);
//    	System.out.println("oo " + makespan/position.length);
    	return (double)((double)position.length/makespan);
    }
    
	
	
}