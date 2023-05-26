
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;

import Helper.CalculateSimulationResults;
import Helper.CloudletCreator3;
import Helper.DataCenterCreator;
import Helper.VmsCreator;
import WriteToCsv.*;


public class MinMin {


	private static List<Cloudlet> cloudletList;

	/** The vmlist. */
	private static List<Vm> vmlist;

	private static int reqTasks = 100;
	private static int reqVms = 50;
	private static WriteToCsv writeTofileObj;

	
	public static void main(String[] args) {
		writeTofileObj = new WriteToCsv("C:\\Users\\Home\\Desktop\\cloudsim _DSO_PSO_and_StaticAlgos\\src\\results\\output.csv",
				"MinMin");
		run();
		reqTasks=200;
		run();
		reqTasks = 500;
		run();
		reqTasks = 1000;
		run();
		reqTasks= 5000;
		run();

	}


	public static void run() {
		Log.printLine("Starting Min-Min...");
		try {
		
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

		
			CloudSim.init(num_user, calendar, trace_flag);

		
			Datacenter datacenter0 = createDatacenter("Datacenter_0");

			MinminBroker broker = createBroker();
			int brokerId = broker.getId();

			// Fourth step: Create one virtual machine
			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId);

			// submit vm list to the broker
			broker.submitVmList(vmlist);

			cloudletList = new CloudletCreator3().createUserCloudlet(reqTasks, brokerId);

			broker.submitCloudletList(cloudletList);

			// call the scheduling function via the broker
			broker.scheduleTaskstoVms();

			CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			CalculateSimulationResults.calulate(cloudletList, vmlist, reqTasks, reqVms,writeTofileObj);


			Log.printLine("Min-Min finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	private static Datacenter createDatacenter(String name) {
		Datacenter datacenter = new DataCenterCreator().createUserDatacenter(name, reqVms);

		return datacenter;

	}
	private static MinminBroker createBroker() {

		MinminBroker broker = null;
		try {
			broker = new MinminBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	
}
