
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.Host;



import WriteToCsv.*;
import Helper.*;

public class RoundRobin {

	private static List<Cloudlet> cloudletList;

	private static List<Vm> vmlist;


	private static int reqTasks = 100;
	private static int reqVms = 50;
	private static WriteToCsv writeTofileObj;
	
	
	public static void main(String[] args) {
		writeTofileObj = new WriteToCsv("C:\\Users\\Home\\Desktop\\cloudsim _DSO_PSO_and_StaticAlgos\\src\\results\\output.csv","Round Robin");

		
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
		Log.printLine("Starting RoundRobin...");

		try {
			
			int num_user = 1; // number of cloud users
			Calendar calendar = Calendar.getInstance();
			boolean trace_flag = false; // mean trace events

			
			CloudSim.init(num_user, calendar, trace_flag);

			Datacenter datacenter0 = createDatacenter("Datacenter_0");

		
			RoundRobinDatacentreBroker broker = createBroker();
			int brokerId = broker.getId();

			vmlist = new VmsCreator().createRequiredVms(reqVms, brokerId);

			
			broker.submitVmList(vmlist);

			
			cloudletList = new CloudletCreator3().createUserCloudlet(reqTasks, brokerId);

		
			broker.submitCloudletList(cloudletList);

			CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();

			CloudSim.stopSimulation();

			CalculateSimulationResults.calulate(newList, vmlist, reqTasks, reqVms,null);

			Log.printLine("RoundRobin finished!");
		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("The simulation has been terminated due to an unexpected error");
		}
	}
	
	
	
	public static class VmAllocationPolicyMinimum extends org.cloudbus.cloudsim.VmAllocationPolicy 
	{

		private Map<String, Host> vm_table = new HashMap<String, Host>();
		
		private final Hosts hosts;
		private Datacenter datacenter;

		public VmAllocationPolicyMinimum(List<? extends Host> list) 
		{
			super(list);
			hosts = new Hosts(list);
		}
		
		public void setDatacenter(Datacenter datacenter) 
		{
			this.datacenter = datacenter;
		}
		
		public Datacenter getDatacenter() 
		{
			return datacenter;
		}

		@Override
		public boolean allocateHostForVm(Vm vm) 
		{

			if (this.vm_table.containsKey(vm.getUid()))
				return true;

			boolean vm_allocated = false;
			int tries = 0;
			
			do 
			{
				Host host = this.hosts.getWithMinimumNumberOfPesEquals(vm.getNumberOfPes());
				vm_allocated = this.allocateHostForVm(vm, host);
				
			} while (!vm_allocated && tries++ < hosts.size());

			return vm_allocated;
		}

		@Override
		public boolean allocateHostForVm(Vm vm, Host host) 
		{
			if (host != null && host.vmCreate(vm)) 
			{
				vm_table.put(vm.getUid(), host);
				Log.formatLine("%.4f: VM #" + vm.getId() + " has been allocated to the host#" + host.getId() + 
						" datacenter #" + host.getDatacenter().getId() + "(" + host.getDatacenter().getName() + ") #", 
						CloudSim.clock());
				return true;
			}
			return false;
		}

		@Override
		public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) 
		{
			return null;
		}

		@Override
		public void deallocateHostForVm(Vm vm) 
		{
			Host host = this.vm_table.remove(vm.getUid());
			
			if (host != null)
			{
				host.vmDestroy(vm);
			}
		}

		@Override
		public Host getHost(Vm vm) 
		{
			return this.vm_table.get(vm.getUid());
		}

		@Override
		public Host getHost(int vmId, int userId) 
		{
			return this.vm_table.get(Vm.getUid(userId, vmId));
		}
	}

	
	
	private static Datacenter createDatacenter(String name) {
		Datacenter datacenter = new DataCenterCreator().createUserDatacenter(name, reqVms);

		return datacenter;

	}

	private static RoundRobinDatacentreBroker createBroker() {

		RoundRobinDatacentreBroker broker = null;
		try {
			broker = new RoundRobinDatacentreBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
}
