package test;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

public class KNN {
	public static BufferedReader readDataFile(String filename) {
		BufferedReader inputReader = null;
 
		try {
			inputReader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException ex) {
			System.err.println("File not found: " + filename);
		}
 
		return inputReader;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		BufferedReader datafile = readDataFile("E:\\NTU\\kat\\CZ4032\\src\\dataset\\test.txt");
		 
		Instances data = new Instances(datafile);
		data.setClassIndex(data.numAttributes() - 1);
		System.out.println("number of attributes " + data.numAttributes());
		
		//System.out.println(data.toSummaryString());
		//do not use first and second
		Instance first = data.instance(3);
		Instance second = data.instance(1);
		
		//create instance 
		Instance iExample = new DenseInstance(3);
			
		iExample.setValue((Attribute)data.attribute(0), 13.0);
		iExample.setValue((Attribute)data.attribute(1), 1.0);
		iExample.setValue((Attribute)data.attribute(2), 1.0);
		
		//
		iExample.setDataset(data);
		
		
		//data.add(iExample);
		
		//data.delete(0);
		//data.delete(0);
		
		System.out.println(data.toString());
		
		Classifier ibk = new IBk();		
		ibk.buildClassifier(data);
 
		double class1 = ibk.classifyInstance(iExample);
		double class2 = ibk.classifyInstance(second);
 
		System.out.println("iExample: " + class1 + "\nsecond: " + class2);

	}

}
