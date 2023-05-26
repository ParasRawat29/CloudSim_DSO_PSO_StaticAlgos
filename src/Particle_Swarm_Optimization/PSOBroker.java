package Particle_Swarm_Optimization;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

import java.util.Arrays;
import java.util.List;

public class PSOBroker extends DatacenterBroker {
	
	Swarm swarm;
	
	public PSOBroker(String name ) throws Exception {
		super(name);
	}
			
	public void scheduleTaskstoVms(int nt ,int nv,List<Cloudlet> cloudletList,List<Vm> vmlist) {
		swarm = new Swarm(nt,nv);
		swarm.init();
		swarm.run_pso();
		double [][] pos= swarm.getBestParticlePosition();
		
		for(int i=0 ; i<pos.length ; i++) {
			for(int j=0 ; j<pos[i].length ; j++) {
				if(pos[i][j]==1) {					
					bindCloudletToVm(cloudletList.get(i).getCloudletId() , vmlist.get(j).getId());
				}
			}
		}
		
//		 for (double[] row : pos)
//	            System.out.println(Arrays.toString(row));
		}
	
	
	
}