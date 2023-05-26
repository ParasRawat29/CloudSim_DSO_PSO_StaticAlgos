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


			DecimalFormat dft = new DecimalFormat("###.##");
			for (int i = 0; i < size; i++) {
				cloudlet = cloudletList.get(i);
				if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {

					totalWT += cloudlet.getWaitingTime();
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
					'\n' + "Total Execution time : "+ dft.format(totalETofVm), '\n' + "Makespan : "+String.valueOf(makespan), '\n' + "Degree Of Imbalance : "+dft.format(doi),
					'\n' + "Throughput : "+dft.format(size / makespan) };
			Log.print('\n');
			Log.print( Arrays.toString(data1));
			String[] data = {String.valueOf(size),String.valueOf(reqVms), dft.format(totalWT / size),
					dft.format(totalETofVm),String.valueOf(makespan),dft.format(doi),
					dft.format(reqTasks / makespan) };
			
			if(writeTofileObj!=null){
				writeTofileObj.writeData(data);
				
			}
	
	}
}