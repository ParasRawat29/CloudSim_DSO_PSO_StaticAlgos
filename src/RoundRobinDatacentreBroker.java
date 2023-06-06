
import java.util.List;

import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;

public class RoundRobinDatacentreBroker extends DatacenterBroker {

	 public RoundRobinDatacentreBroker(String name) throws Exception 
	    {
	        super(name);
	    }

	    @Override
	    protected void processResourceCharacteristics(SimEvent ev) 
	    {        
	        DatacenterCharacteristics characteristics = (DatacenterCharacteristics) ev.getData();
	        getDatacenterCharacteristicsList().put(characteristics.getId(), characteristics);

	        if (getDatacenterCharacteristicsList().size() == getDatacenterIdsList().size()) 
	        {
	        	distributeReqForNewVmsAcrossDatacentersUsingRR();
	        }
	    }

	    /**
	     * Distributes the VMs across the data centers using the round-robin approach. A VM is allocated to a data center only if there isn't  
	     * a VM in the data center with the same id.     
	     */
	    protected void distributeReqForNewVmsAcrossDatacentersUsingRR()
		{
			// TODO Auto-generated method stub
			int numOfVmsAllocated = 0;
			int i= 0;
			
			final List<Integer> availableDatacenters= getDatacenterIdsList();
			
			for (Vm vm : getVmList())
			{
				int datacenterID = availableDatacenters.get(i++ % availableDatacenters.size());
				String datacenterName = CloudSim.getEntityName(datacenterID);
				
				if (!getVmsToDatacentersMap().containsKey(vm.getId()))
				{
					Log.printLine(CloudSim.clock() + ":" + getName() + ": Trying to Create VM #" + vm.getId() + "in" + datacenterName);
					sendNow(datacenterID, CloudSimTags.VM_CREATE_ACK,vm);
					numOfVmsAllocated++;
				}
			}
			
			setVmsRequested(numOfVmsAllocated);
			setVmsAcks(0);
		}
}

