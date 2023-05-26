import java.util.ArrayList;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 * A Broker that schedules Tasks to the VMs as per FCFS Scheduling Policy
 * 
 * @author Linda J
 *
 */
public class FcfsBroker extends DatacenterBroker {

	public FcfsBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	// scheduling function
	public void scheduleTaskstoVms() {

		ArrayList<Cloudlet> clist = new ArrayList<Cloudlet>();

		for (Cloudlet cloudlet : getCloudletSubmittedList()) {
			clist.add(cloudlet);
			// System.out.println("cid:"+ cloudlet.getCloudletId());
		}

		setCloudletReceivedList(clist);
	}

	@Override
	protected void processCloudletReturn(SimEvent ev) {
		Cloudlet cloudlet = (Cloudlet) ev.getData();
		getCloudletReceivedList().add(cloudlet);
//		Log.printLine(CloudSim.clock() + ": " + getName() + ": Cloudlet " + cloudlet.getCloudletId() + " received");
		cloudletsSubmitted--;
		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) {
			scheduleTaskstoVms();
			cloudletExecution(cloudlet);
		}
	}

	protected void cloudletExecution(Cloudlet cloudlet) {

		if (getCloudletList().size() == 0 && cloudletsSubmitted == 0) { // all cloudlets executed
//			Log.printLine(CloudSim.clock() + ": " + getName() + ": All Cloudlets executed. Finishing...");
			clearDatacenters();
			finishExecution();
		} else { // some cloudlets haven't finished yet
			if (getCloudletList().size() > 0 && cloudletsSubmitted == 0) {
				// all the cloudlets sent finished. It means that some bount
				// cloudlet is waiting its VM be created
				clearDatacenters();
				createVmsInDatacenter(0);
			}

		}
	}
}
