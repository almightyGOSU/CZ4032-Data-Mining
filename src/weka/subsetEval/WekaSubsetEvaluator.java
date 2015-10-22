package weka.subsetEval;

import java.io.BufferedReader;
import java.io.IOException;

import classes.RemovedAttribute;

import util.Const;
import util.FileIOHelper;
import util.WekaHelper;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class WekaSubsetEvaluator {
	
	private static String[] _binaryLSA = null;
	private static String[] _binaryMSA = null;
	
	private static String[] _gradeLSA = null;
	private static String[] _gradeMSA = null;
	
	private static String[][] _subsetsAttr = null;

	public static void main(String[] args) {
	
		BufferedReader goodBadAttrFile =
				FileIOHelper.readDataFile(Const.GOOD_BAD_ATTR_FILENAME);
		try {
			
			_binaryLSA = goodBadAttrFile.readLine().split(",");
			_binaryMSA = goodBadAttrFile.readLine().split(",");
			
			_gradeLSA = goodBadAttrFile.readLine().split(",");
			_gradeMSA = goodBadAttrFile.readLine().split(",");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader subsetsFile =
				FileIOHelper.readDataFile(Const.SUBSETS_FILENAME);
		try {
			_subsetsAttr = new String[Const.NUM_SUBSETS][];
			for(int i = 0; i < Const.NUM_SUBSETS; i++) {
				_subsetsAttr[i] = subsetsFile.readLine().split(",");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedReader binaryDataFile = 
				FileIOHelper.readDataFile(Const.MATH_BINARY_FILENAME);
		Instances binaryDataInstances = null;
		try {
			binaryDataInstances = new Instances(binaryDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (binaryDataInstances != null) {
			
			// Calculate baseline accuracy for binary data
			RemovedAttribute binaryBaseline =
					WekaHelper.calcAccuracy(binaryDataInstances);
			System.out.println(WekaHelper.getColumnHeaders());
			System.out.println(binaryBaseline.toString());
			
			// Remove Top 5 Least Significant Attributes
			String attrIndicesToRemove =
					findAttributeIndices(binaryDataInstances, _binaryLSA);
			Instances filteredBinaryData = removeUnwantedAttr(
					binaryDataInstances, attrIndicesToRemove);
			
			// Calculate accuracy after removing top 5 least significant attributes
			RemovedAttribute binaryWithoutLSA =
					WekaHelper.calcAccuracy(filteredBinaryData);
			binaryWithoutLSA.setAttributeName("Binary Top 5 LSA");
			binaryWithoutLSA.calcAccuracyGainLoss(binaryBaseline, null);
			System.out.println(binaryWithoutLSA.toString());
			
			// Remove Top 5 Most Significant Attributes
			attrIndicesToRemove = findAttributeIndices(
					binaryDataInstances, _binaryMSA);
			filteredBinaryData = removeUnwantedAttr(
					binaryDataInstances, attrIndicesToRemove);
			
			RemovedAttribute binaryWithoutMSA =
					WekaHelper.calcAccuracy(filteredBinaryData);
			binaryWithoutMSA.setAttributeName("Binary Top 5 MSA");
			binaryWithoutMSA.calcAccuracyGainLoss(binaryBaseline, null);
			System.out.println(binaryWithoutMSA.toString());
			
			// Iterate through the different subsets
			for(int i = 0; i < Const.NUM_SUBSETS; i++) {
				
				// Keep only attributes specified in subset
				String attrIndicesToKeep = findAttributeIndices(
						binaryDataInstances,
						WekaHelper.concat(
								_subsetsAttr[i],
								new String[]{"BinaryData"}));
				
				filteredBinaryData = keepSpecifiedAttr(
						binaryDataInstances, attrIndicesToKeep);
				
				RemovedAttribute subset =
						WekaHelper.calcAccuracy(filteredBinaryData);
				subset.setAttributeName("Binary !(Subset " + (i+1) + ")");
				subset.calcAccuracyGainLoss(binaryBaseline, null);
				System.out.println(subset.toString());
			}
		}
		
		BufferedReader gradeDataFile = 
				FileIOHelper.readDataFile(Const.MATH_GRADE_FILENAME);
		Instances gradeDataInstances = null;
		try {
			gradeDataInstances = new Instances(gradeDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (gradeDataInstances != null) {
			
			// Calculate baseline accuracy for 5-grade data
			RemovedAttribute gradeBaseline =
					WekaHelper.calcAccuracy(gradeDataInstances);
			System.out.println("\n" + WekaHelper.getColumnHeaders());
			System.out.println(gradeBaseline.toString());
			
			// Remove Top 5 Least Significant Attributes
			String attrIndicesToRemove =
					findAttributeIndices(gradeDataInstances, _gradeLSA);
			Instances filteredGradeData = removeUnwantedAttr(
					gradeDataInstances, attrIndicesToRemove);
			
			// Calculate accuracy after removing top 5 least significant attributes
			RemovedAttribute gradeWithoutLSA =
					WekaHelper.calcAccuracy(filteredGradeData);
			gradeWithoutLSA.setAttributeName("Grade Top 5 LSA");
			gradeWithoutLSA.calcAccuracyGainLoss(gradeBaseline, null);
			System.out.println(gradeWithoutLSA.toString());
			
			// Remove Top 5 Most Significant Attributes
			attrIndicesToRemove = findAttributeIndices(
					gradeDataInstances, _gradeMSA);
			filteredGradeData = removeUnwantedAttr(
					gradeDataInstances, attrIndicesToRemove);
			
			RemovedAttribute gradeWithoutMSA =
					WekaHelper.calcAccuracy(filteredGradeData);
			gradeWithoutMSA.setAttributeName("Grade Top 5 MSA");
			gradeWithoutMSA.calcAccuracyGainLoss(gradeBaseline, null);
			System.out.println(gradeWithoutMSA.toString());
			
			// Iterate through the different subsets
			for(int i = 0; i < Const.NUM_SUBSETS; i++) {
				
				// Keep only attributes specified in subset
				String attrIndicesToKeep = findAttributeIndices(
						gradeDataInstances,
						WekaHelper.concat(
								_subsetsAttr[i],
								new String[]{"Grade"}));
				
				filteredGradeData = keepSpecifiedAttr(
						gradeDataInstances, attrIndicesToKeep);
				
				RemovedAttribute subset =
						WekaHelper.calcAccuracy(filteredGradeData);
				subset.setAttributeName("Grade !(Subset " + (i+1) + ")");
				subset.calcAccuracyGainLoss(gradeBaseline, null);
				System.out.println(subset.toString());
			}
		}
	}

	private static String findAttributeIndices(
			Instances dataInstances, String[] attrNameLst) {
		
		StringBuilder sb = new StringBuilder();
		for (int attrIndex = 0; attrIndex < dataInstances.numAttributes();
				attrIndex++) {
			
			for (String attrName : attrNameLst) {
				if (dataInstances.attribute(attrIndex).name().equals(attrName)) {
					sb.append(attrIndex + 1).append(",");
				}
			}
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}
	
	private static Instances removeUnwantedAttr(
			Instances dataInstances, String removeLst) {
		
		// Filter data to get rid of unwanted columns
		Remove removeAttrFilter = new Remove();
		removeAttrFilter.setAttributeIndices(removeLst);
		Instances filteredData = null;
		try {
			removeAttrFilter.setInputFormat(dataInstances);
			filteredData = Filter.useFilter(dataInstances,
					removeAttrFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filteredData;
	}
	
	private static Instances keepSpecifiedAttr(
			Instances dataInstances, String retainLst) {
		
		// Filter data to get rid of unwanted columns
		Remove keepAttrFilter = new Remove();
		keepAttrFilter.setAttributeIndices(retainLst);
		keepAttrFilter.setInvertSelection(true);
		Instances filteredData = null;
		try {
			keepAttrFilter.setInputFormat(dataInstances);
			filteredData = Filter.useFilter(dataInstances,
					keepAttrFilter);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return filteredData;
	}
}
