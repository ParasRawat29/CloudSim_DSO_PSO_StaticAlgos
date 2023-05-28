
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;


public class MaxminBroker extends DatacenterBroker {
	public double[][] completetionTimeMatrix;
	public int reqTasks;
	public int reqVms; 
	public MaxminBroker(String name) throws Exception {
		super(name);
		// TODO Auto-generated constructor stub
	}

	//scheduling function
	
	
	public void scheduleTaskstoVms(){
		reqTasks= cloudletList.size();
		reqVms= vmList.size();
		//int k=0;
		//Log.printLine("req.task "+ reqTasks + " , reqVms : "+reqVms);
		List<Cloudlet>completedTaskList = new ArrayList<Cloudlet>();
		
		
		completetionTimeMatrix = new double[reqTasks][reqVms];
		fillCompletionMatrix();
		
		while(completedTaskList.size() < cloudletList.size()) {
			
//			print(completetionTimeMatrix);
			
			int[] minimumTimeEachTask = getMinimumTimeEachTask();
			
			for(int i=0 ; i<minimumTimeEachTask.length ; i++) {
				System.out.print(minimumTimeEachTask[i]+" , ");
			}
			System.out.println("------");
				
			// cloudletid Vmid;
			int[]maxTaskInMinList = getMaxTaskInMinList(minimumTimeEachTask);
			int task = maxTaskInMinList[0];
			int vm = maxTaskInMinList[1];
			// allocate Vm to that task
			
			System.out.println("task chosen : "+task + " , vm of that task : "+vm);
			
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
		for(int j=0 ; j<reqVms ; j++) {
			completetionTimeMatrix[task][j] = -1;
		}
		
		for(int i=0 ; i<reqTasks ; i++) {
			if(completetionTimeMatrix[i][vm]!=-1) {
				completetionTimeMatrix[i][vm] += time;
			}
		}
	}
	
	
	
	
	private int[] getMaxTaskInMinList(int []minimumTimeEachTask) {
		int result[]= {0,0};
		
		int maxTask = 0 , maxTaskVm=minimumTimeEachTask[0];
		for(int i=0 ; i<minimumTimeEachTask.length ; i++ ) {
			int currVm = minimumTimeEachTask[i];
			if(completetionTimeMatrix[i][currVm] >completetionTimeMatrix[maxTask][maxTaskVm]) {
				maxTask = i;
				maxTaskVm = currVm;
			}
		}
		
		result[0] = maxTask;
		result[1] = maxTaskVm;
		
		return result;
		
	}
	
	
	
	public int[] getMinimumTimeEachTask() {
		int[]result = new int[reqTasks];
				
		for(int i=0 ; i<reqTasks; i++) {
			int minVm=0;
			for(int j=0 ; j<reqVms ; j++) {
				if(completetionTimeMatrix[i][j] == -1) break;
			
				if(completetionTimeMatrix[i][j] < completetionTimeMatrix[i][minVm]) minVm = j;
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
	
	
	
	private double getCompletionTime(Cloudlet cloudlet, Vm vm){
		double waitingTime = cloudlet.getWaitingTime();
		double execTime = cloudlet.getCloudletLength() / (vm.getMips()*vm.getNumberOfPes());
		
		double completionTime = execTime + waitingTime;
		
		return completionTime;
	}
	
	private double getExecTime(Cloudlet cloudlet, Vm vm){
		return cloudlet.getCloudletLength() / (vm.getMips()*vm.getNumberOfPes());
	}
}
