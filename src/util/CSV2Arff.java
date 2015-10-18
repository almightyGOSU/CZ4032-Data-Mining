package util;

import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class CSV2Arff {

	public static void main(String[] args) throws Exception {

		// Load CSV
		CSVLoader loader = new CSVLoader();
		loader.setSource(new File("data\\converted.csv"));
		Instances data = loader.getDataSet();

		// Filter data to get rid of unwanted columns
		Remove removeAttrFilter = new Remove();
		removeAttrFilter.setAttributeIndices("33-34");
		Instances filteredData = null;
		try {
			removeAttrFilter.setInputFormat(data);
			filteredData = Filter.useFilter(data, removeAttrFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Save ARFF
		ArffSaver saver = new ArffSaver();
		saver.setInstances(filteredData);
		saver.setFile(new File("data\\student_math_grade.arff"));
		saver.writeBatch();
	}
}