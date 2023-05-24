package Dove_Swarm_Optimization;

import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

public class DSOBroker extends DatacenterBroker{
	Swarm swarm;
	public DSOBroker(String name) throws Exception{
		super(name);
	}
	
	
	public void scheduleTaskstoVms(int nt , int nv , List<Cloudlet> cloudletList,List<Vm> vmlist) {
		swarm = new Swarm(nt,nv);
		swarm.init();
		swarm.run_dso();
		
		double [][] pos= swarm.getBestDoveAllocation();
		
		for(int i=0 ; i<pos.length ; i++) {
			for(int j=0 ; j<pos[i].length ; j++) {
				if(pos[i][j]==1) {					
					bindCloudletToVm(cloudletList.get(i).getCloudletId() , vmlist.get(j).getId());
				}
			}
		}
		
		 for (double[] row : pos)
	            System.out.println(Arrays.toString(row));
		 
	}
}