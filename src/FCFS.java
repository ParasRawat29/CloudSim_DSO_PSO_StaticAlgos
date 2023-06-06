


import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import Helper.CloudletCreator3;
import Helper.DataCenterCreator;
import Helper.VmsCreator;
import Helper.CalculateSimulationResults;
import WriteToCsv.*;


public class FCFS {

	private static List<Cloudlet> cloudletList;

	private static List<Vm> vmlist;

	private static int reqTasks = 1000;
	private static int reqVms = 50;
	private static WriteToCsv writeTofileObj;

	/**
	 * Creates main() to run this example
	 */
	public static void main(String[] args) {
		writeTofileObj = new WriteToCsv("C:\\Users\\Home\\Desktop\\cloudsim _DSO_PSO_and_StaticAlgos\\src\\results\\output.csv",
				"FCFS");
	
		run();
		reqTasks = 200;
//		reqVms = 100;
		run();
		reqTasks = 500;
//		reqVms = 125;
		run();
		reqTasks = 1000;
//		reqVms = 150;
		run();
		reqTasks = 5000;
//		reqVms = 200;
		run();
		
	}
	
	
	public static void run() {
	
		Log.printLine("Starting FCFS...");

		try {
			
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			CloudSim.init(num_user, calendar, trace_flag);

			
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

		
			FcfsBroker broker = createBroker();
			int brokerId = broker.getId();

	
			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId);


			broker.submitVmList(vmlist);

			cloudletList = new CloudletCreator3().createUserCloudlet(reqTasks, brokerId);


			broker.submitCloudletList(cloudletList);


//			broker.scheduleTaskstoVms();


			CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();
			
			CalculateSimulationResults.calulate(newList, vmlist, reqTasks, reqVms,null);

			System.out.println("FCFS finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	
	
	
	private static Datacenter createDatacenter(String name) {
		Datacenter datacenter = new DataCenterCreator().createUserDatacenter(name, reqVms);

		return datacenter;

	}

	private static FcfsBroker createBroker() {

		FcfsBroker broker = null;
		try {
			broker = new FcfsBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
}
