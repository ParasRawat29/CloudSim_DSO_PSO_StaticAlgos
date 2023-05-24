package Helper;
import java.util.ArrayList;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;

/**
 * CloudletCreator Creates Cloudlets as per the User Requirements. More short
 * tasks then long tasks which makes max-min outperrform min-min
 *
 */
public class CloudletCreator3 {

	// cloudlet creator
	public ArrayList<Cloudlet> createUserCloudlet(int reqTasks, int brokerId) {
		ArrayList<Cloudlet> cloudletList = new ArrayList<Cloudlet>();

		// Cloudlet properties
		int id = 0;
		int pesNumber = 1;
//    	long[] length = {9000, 8000, 7800, 2000, 3000, 1800, 2500, 3500, 2800, 3200};
//    	long[] length = {100000, 70000, 5000, 1000, 3000, 10000, 90000, 100000, 15000, 1000,2000,4000,20000,25000,80000};
		long fileSize = 300;
		long outputSize = 300;
		UtilizationModel utilizationModel = new UtilizationModelFull();

		for (id = 0; id < reqTasks; id++) {

			Cloudlet task = new Cloudlet(id, Utils.cloudLetLength[id % Utils.cloudLetLength.length], pesNumber, fileSize, outputSize,
					utilizationModel, utilizationModel, utilizationModel);
			task.setUserId(brokerId);
			// add the cloudlets to the list
			cloudletList.add(task);
		}

		System.out.println("SUCCESSFULLY Cloudletlist created : "+reqTasks);

		return cloudletList;

	}

}