package Dove_Swarm_Optimization;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import WriteToCsv.WriteToCsv;
import Helper.*;

public class DSO{
	/** The cloudlet list. */
	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static int reqTasks = 100;
	private static int reqVms = 6;
	private static WriteToCsv writeTofileObj;
	
	private static DSOBroker broker;
	private static double[][] ETC_MATRIX;
	
	
	public static void main(String[]args) {
		writeTofileObj = new WriteToCsv("C:\\Users\\Home\\Desktop\\Cloudsim-Code-master\\src\\results\\output.csv",
				"DSO");
		Log.printLine("Starting DSO...");
		
		try {
			int num_user = 1;
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag =false;
			
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

			printCloudletList(newList);
			
			
			Log.printLine("DSO finished!");
		}catch(Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	private static void fillExecutionTimeMatrix() {
		ETC_MATRIX = new double[reqTasks][reqVms];
	        for (int i = 0; i < reqTasks; i++) {
	            for (int j = 0; j < reqVms; j++) {
	            	ETC_MATRIX[i][j] = (double) cloudletList.get(i).getCloudletLength() / vmlist.get(j).getMips();
	           }
	      }
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
    	/** temp position (ith task assigned to which tempPosition[i] Vm  */
    	double[] finishTimes = new double[reqVms];
    	
    	for (int i = 0; i < reqTasks; i++) {
    		// finishTime[vm] += timetaken by current task;
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
    
    public double calculatePredictedAverageExecutionTime(double[][] position) {
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
    	
    	return totalExecutionTime/reqTasks;
    	
    	
    }
    
    
    public double calculatePredictedThroughput(double[][] position) {
    	double makespan = calculatePredictedMakespan(position);
    	System.out.println("oo " + makespan/position.length);
    	return (double)(makespan/position.length);
    }
    
	
	private static Datacenter createDatacenter(String name) {
		Datacenter datacenter = new DataCenterCreator().createUserDatacenter(name, reqVms);
		return datacenter;
	}
	
	
	private static DSOBroker createBroker() {
		DSOBroker broker = null;
		try {
			broker = new DSOBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
	
	
	private static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		double totalWT = 0.0;
		double totalET = 0.0;
		double makespan = 0.0;
		double totalResponseTime = 0.0;

		String indent = "    ";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time"
				+ indent + "Start Time" + indent + "Finish Time" + indent + "waiting time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId() + indent + indent);

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.print("SUCCESS");

				Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
						+ indent + indent + dft.format(cloudlet.getActualCPUTime()) + indent + indent
						+ dft.format(cloudlet.getExecStartTime()) + indent + indent
						+ dft.format(cloudlet.getFinishTime()) + indent + indent
						+ dft.format(cloudlet.getWaitingTime()));
				totalWT += cloudlet.getWaitingTime();
				totalET += cloudlet.getExecStartTime();
				makespan = Math.max(makespan, cloudlet.getFinishTime());
				totalResponseTime = cloudlet.getExecStartTime() - cloudlet.getSubmissionTime();
			}
		}
		String[] data = { String.valueOf(size), String.valueOf(reqVms), dft.format(totalWT / size),
				dft.format(totalET / size), String.valueOf(makespan), dft.format(totalResponseTime / size),
				dft.format(size / makespan) };
//		writeTofileObj.writeData(data);
		Log.print("data : " + Arrays.toString(data));

	}
	
	
};
