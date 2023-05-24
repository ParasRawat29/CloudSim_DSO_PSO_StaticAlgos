package WriteToCsv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;

public class WriteToCsv {

	String filePath;
	CSVWriter file;
	String algoName;
	 
	public WriteToCsv(String filePath, String name) {
		this.filePath = filePath;
		algoName = name;
		this.createFile();
	}

	public void createFile() {
		try {

			File f = new File(filePath);
			if (f.exists())
				return;
			file = new CSVWriter(new FileWriter(new File(filePath)));
			String[] colName = { "Algorithmn", "No of Cloudlets", "No. of Virtual Machine", "Average Waiting Time",
					"Average Execution Time", "Makespan", "Average Response Time", "Throughput" };
			file.writeNext(colName);
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeData(String[] data) {
		try {
			/**
			 * specific location: File file = new
			 * File("C:\\Users\\hp\\Documents\\NetBeansProjects\\Cloudsim3\ \Output2.csv");
			 */
			// automatically the file is created in the project folder
			file = new CSVWriter(new FileWriter(new File(filePath), true));
			String newData[] = Arrays.copyOf(data, data.length + 1);
			newData[0] = algoName;
			System.arraycopy(data, 0, newData, 1, data.length);
			file.writeNext(newData);
			System.out.println("written in file");
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}