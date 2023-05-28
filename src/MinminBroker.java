
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Vm;

/**
 * A Broker that schedules Tasks to the VMs as per FCFS Scheduling Policy
 * 
 * @author Linda J
 *
 */
public class MinminBroker extends DatacenterBroker {
	public double[][] completetionTimeMatrix;
	public int reqTasks;
	public int reqVms; 
	
	public MinminBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	// scheduling function

	public void scheduleTaskstoVms() {
		reqTasks= cloudletList.size();
		reqVms= vmList.size();
		//int k=0;
		//Log.printLine("req.task "+ reqTasks + " , reqVms : "+reqVms);
		List<Cloudlet>completedTaskList = new ArrayList<Cloudlet>();
		
		
		completetionTimeMatrix = new double[reqTasks][reqVms];
		fillCompletionMatrix();
		
		while(completedTaskList.size() < cloudletList.size()) {
			
			//print(completetionTimeMatrix);
			
			int[] minimumTimeEachTask = getMinimumTimeEachTask();
			
//			for(int i=0 ; i<minimumTimeEachTask.length ; i++) {
//				System.out.print(minimumTimeEachTask[i]+" , ");
//			}
//			System.out.println("------");
				
			// cloudletid Vmid;
			int[]minTaskInMinList = getMinTaskInMinList(minimumTimeEachTask);
			int task = minTaskInMinList[0];
			int vm = minTaskInMinList[1];
			// allocate Vm to that task
			
//			System.out.println("task chosen : "+task + " , vm of that task : "+vm);
			
			bindCloudletToVm(cloudletList.get(task).getCloudletId() , vmList.get(vm).getId());
			// push task in allocatedList
			completedTaskList.add(cloudletList.get(task));
			
			// updateCompletionMaxtrix
			double time = completetionTimeMatrix[task][vm];
			updateCompletionMatrix(task,vm,time);
			
		}

	}
	
	private void print(double[][]mat) {
		 for (double[] row : mat)
	        System.out.println(Arrays.toString(row));
		 
		 System.out.println("---------------------------\n");
		}
	
	
	private void updateCompletionMatrix(int task,int vm,double time) {
		// mark task as complete
		for(int j=0 ; j<reqVms ; j++) {
			completetionTimeMatrix[task][j] = Integer.MAX_VALUE;
		}
		
		for(int i=0 ; i<reqTasks ; i++) {
			if(completetionTimeMatrix[i][vm]!=Integer.MAX_VALUE) {
				completetionTimeMatrix[i][vm] += time;
			}
		}
	}
	
	
	private int[] getMinTaskInMinList(int []minimumTimeEachTask) {
		int result[]= {0,0};
		 System.out.println("minimumTimeEachTask : "+Arrays.toString(minimumTimeEachTask));
		int minTask = 0 , minTaskVm=minimumTimeEachTask[0];
		for(int i=0 ; i<minimumTimeEachTask.length ; i++ ) {
			int currVm = minimumTimeEachTask[i];
			if(completetionTimeMatrix[i][currVm] < completetionTimeMatrix[minTask][minTaskVm]) {
				minTask = i;
				minTaskVm = currVm;
			}
		}
		
		result[0] = minTask;
		result[1] = minTaskVm;
		
		return result;
		
	}
	
	
	public int[] getMinimumTimeEachTask() {
		int[]result = new int[reqTasks];
				
		for(int i=0 ; i<reqTasks; i++) {
			int minVm=0;
			for(int j=0 ; j<reqVms ; j++) {
				if(completetionTimeMatrix[i][j]==Integer.MAX_VALUE) break;
			
				else if(completetionTimeMatrix[i][j] < completetionTimeMatrix[i][minVm]) minVm = j;
			}
			
			result[i] = minVm;
		}
				
		return result;
		
	}
	
	
	public void fillCompletionMatrix(){
		
		for(int i=0 ; i<reqTasks ; i++) {
			for(int j=0 ; j<reqVms ; j++) {
				completetionTimeMatrix[i][j] = (double) cloudletList.get(i).getCloudletLength() /(vmList.get(j).getMips()*vmList.get(j).getNumberOfPes());
			}
		}
		
		
	}



	private double getCompletionTime(Cloudlet cloudlet, Vm vm) {
		double waitingTime = cloudlet.getWaitingTime();
		double execTime = cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());

		double completionTime = execTime + waitingTime;

		return completionTime;
	}

	private double getExecTime(Cloudlet cloudlet, Vm vm) {
		return cloudlet.getCloudletLength() / (vm.getMips() * vm.getNumberOfPes());
	}
}
