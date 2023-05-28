package Helper;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.Log;
import WriteToCsv.*;

public class CalculateSimulationResults{
	public static void calulate(List<Cloudlet> cloudletList , List<Vm> vmList , int reqTasks , int reqVms , WriteToCsv writeTofileObj){
			int size = cloudletList.size();
			Cloudlet cloudlet;

			double totalWT = 0.0;
			double totalETofVm = 0.0;
			double makespan = 0.0;
			double minVmExecutionTime = 0;
			double[] vmExecutionTime = new double [vmList.size()];
			double maxWaitingTime = 0;

			DecimalFormat dft = new DecimalFormat("###.##");
			for (int i = 0; i < size; i++) {
				cloudlet = cloudletList.get(i);
				if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {

					totalWT += cloudlet.getWaitingTime();
					maxWaitingTime = Math.max(maxWaitingTime, cloudlet.getWaitingTime());
					vmExecutionTime[cloudlet.getVmId()]+=cloudlet.getActualCPUTime();
					makespan = Math.max(makespan, cloudlet.getFinishTime());
				}
			}
			for(int i=0 ; i<vmExecutionTime.length ; i++){
				totalETofVm+=vmExecutionTime[i];
				minVmExecutionTime = Math.min(vmExecutionTime[i], minVmExecutionTime);
			}
			
			double doi = reqVms*((makespan -minVmExecutionTime) / totalETofVm);
			
			
			
			String[] data1 = {"No of Cloudlet : "+ String.valueOf(size), '\n' + "No of Vm : "+ String.valueOf(reqVms), '\n' + "Average Waiting time : "+ dft.format(totalWT / size),
					'\n'+"Maximum Waiting Time : "+dft.format(maxWaitingTime),'\n' + "Total Execution time : "+ dft.format(totalETofVm), '\n' + "Makespan : " +dft.format(makespan),'\n' + "Degree Of Imbalance : "+dft.format(doi),
					'\n' + "Throughput : "+dft.format(size / makespan) };
			Log.print('\n');
			Log.print( Arrays.toString(data1));
			
			String[] data = {String.valueOf(size),String.valueOf(reqVms), dft.format(totalWT / size),dft.format(maxWaitingTime),
					dft.format(totalETofVm),String.valueOf(makespan),dft.format(doi),dft.format(reqTasks / makespan) };
			
			if(writeTofileObj!=null){
				writeTofileObj.writeData(data);
				
			}
	
	}
}