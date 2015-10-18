package test;
import java.io.BufferedReader;
import java.io.FileReader;

import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class LoadSaveData{
	public static void main(String args[]) throws Exception{
		DataSource source = new DataSource("E:/NTU/kat/CZ4032/src/iris.arff");
		Instances dataset = source.getDataSet();
		
		
		//Instances dataset = new Instances(new BufferedReader(new FileReader("E:/NTU/kat/CZ4032/src/iris.arff")));
		System.out.println(dataset.toSummaryString());
		
		/*ArffSaver saver = new ArffSaver();
		saver.setInstances(dataset);
		saver.setFile(new File("E:/NTU/new.arff"));
		saver.writeBatch();*/
	}
}