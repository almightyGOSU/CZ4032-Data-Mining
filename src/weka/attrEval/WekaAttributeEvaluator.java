package weka.attrEval;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import classes.RemovedAttribute;

import util.Const;
import util.FileIOHelper;
import util.WekaHelper;

import weka.core.Instances;

public class WekaAttributeEvaluator {
	
	private static RemovedAttribute _binaryBaseline = null;
	private static ArrayList<RemovedAttribute> _binaryAttrList = null;
	
	private static RemovedAttribute _gradeBaseline = null;
	private static ArrayList<RemovedAttribute> _gradeAttrList = null;
	
	private static StringBuilder _sbAttrEval = new StringBuilder();
	private static StringBuilder _sbGoodBadAttr = new StringBuilder();

	public static void main(String[] args) {
		
		BufferedReader binaryDataFile = FileIOHelper
				.readDataFile(Const.MATH_BINARY_FILENAME);
		Instances binaryDataInstances = null;
		try {
			binaryDataInstances = new Instances(binaryDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (binaryDataInstances != null) {
			
			System.out.println(
					"Calculating baseline accuracies for binary data....");
			_binaryBaseline = WekaHelper.calcAccuracy(binaryDataInstances);
			int[] ranks = WekaHelper.rankTechniques(_binaryBaseline);
			double[] weights = WekaHelper.rankToWeight(ranks);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Binary Data (Technique/Rank/Weight): ");
			for(int i = 0; i < Const.MODELS.length; i++) {
				sb.append("(");
				sb.append(Const.MODELS[i]).append("/");
				sb.append(ranks[i]).append("/").append(weights[i]);
				sb.append("), ");
			}
			sb.delete(sb.lastIndexOf(","), sb.length());
			sb.append("\n");
			
			WekaHelper.printLine(_sbAttrEval, sb.toString());
			
			_binaryAttrList = new ArrayList<>();
			WekaHelper.removeAttrAndCalc(binaryDataInstances, _binaryAttrList);
			
			WekaHelper.printLine(_sbAttrEval, WekaHelper.getColumnHeaders());
			WekaHelper.printLine(_sbAttrEval, _binaryBaseline.toString());
			
			// Calculate accuracy gain/loss
			for(RemovedAttribute ra : _binaryAttrList) {
				ra.calcAccuracyGainLoss(_binaryBaseline, weights);
			}
			
			Collections.sort(_binaryAttrList);
			for(RemovedAttribute ra : _binaryAttrList) {
				WekaHelper.printLine(_sbAttrEval, ra.toString());
			}
			
			// Save TOP_N Least Significant Attributes to the file
			// i.e. Removed attributes resulting in most accuracy gain
			for(int i = 0; i < Const.TOP_N; i++) {
				
				_sbGoodBadAttr.append(
						_binaryAttrList.get(i).getAttributeName());
				_sbGoodBadAttr.append(",");
			}
			_sbGoodBadAttr.deleteCharAt(_sbGoodBadAttr.lastIndexOf(","));
			_sbGoodBadAttr.append("\n");
			
			// Save TOP_N Most Significant Attributes to the file
			// i.e. Removed attributes resulting in most accuracy loss
			for(int i = _binaryAttrList.size() - 1;
					i > (_binaryAttrList.size() - 1 - Const.TOP_N); i--) {
				
				_sbGoodBadAttr.append(
						_binaryAttrList.get(i).getAttributeName());
				_sbGoodBadAttr.append(",");
			}
			_sbGoodBadAttr.deleteCharAt(_sbGoodBadAttr.lastIndexOf(","));
			_sbGoodBadAttr.append("\n");
		}
		
		BufferedReader gradeDataFile = FileIOHelper
				.readDataFile(Const.MATH_GRADE_FILENAME);
		Instances gradeDataInstances = null;
		try {
			gradeDataInstances = new Instances(gradeDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (gradeDataInstances != null) {
			
			System.out.println(
					"\n\nCalculating baseline accuracies for grade data....");
			_gradeBaseline = WekaHelper.calcAccuracy(gradeDataInstances);
			int[] ranks = WekaHelper.rankTechniques(_gradeBaseline);
			double[] weights = WekaHelper.rankToWeight(ranks);
			
			StringBuilder sb = new StringBuilder();
			sb.append("Grade Data (Technique/Rank/Weight): ");
			for(int i = 0; i < Const.MODELS.length; i++) {
				sb.append("(");
				sb.append(Const.MODELS[i]).append("/");
				sb.append(ranks[i]).append("/").append(weights[i]);
				sb.append("), ");
			}
			sb.delete(sb.lastIndexOf(","), sb.length());
			sb.append("\n");
			
			WekaHelper.printLine(_sbAttrEval, sb.toString());
			
			_gradeAttrList = new ArrayList<>();
			WekaHelper.removeAttrAndCalc(gradeDataInstances, _gradeAttrList);
			
			WekaHelper.printLine(_sbAttrEval, WekaHelper.getColumnHeaders());
			WekaHelper.printLine(_sbAttrEval, _gradeBaseline.toString());
			
			// Calculate accuracy gain/loss
			for(RemovedAttribute ra : _gradeAttrList) {
				ra.calcAccuracyGainLoss(_gradeBaseline, weights);
			}
			
			Collections.sort(_gradeAttrList);
			for(RemovedAttribute ra : _gradeAttrList) {
				WekaHelper.printLine(_sbAttrEval, ra.toString());
			}
			
			// Save TOP_N Least Significant Attributes to the file
			// i.e. Removed attributes resulting in most accuracy gain
			for(int i = 0; i < Const.TOP_N; i++) {
				
				_sbGoodBadAttr.append(
						_gradeAttrList.get(i).getAttributeName());
				_sbGoodBadAttr.append(",");
			}
			_sbGoodBadAttr.deleteCharAt(_sbGoodBadAttr.lastIndexOf(","));
			_sbGoodBadAttr.append("\n");
			
			// Save TOP_N Most Significant Attributes to the file
			// i.e. Removed attributes resulting in most accuracy loss
			for(int i = _gradeAttrList.size() - 1;
					i > (_gradeAttrList.size() - 1 - Const.TOP_N); i--) {
				
				_sbGoodBadAttr.append(
						_gradeAttrList.get(i).getAttributeName());
				_sbGoodBadAttr.append(",");
			}
			_sbGoodBadAttr.deleteCharAt(_sbGoodBadAttr.lastIndexOf(","));
			_sbGoodBadAttr.append("\n");
		}
		
		FileIOHelper.saveToFile(_sbAttrEval.toString(),
				Const.RESULTS_FILENAME, false);
		FileIOHelper.saveToFile(_sbGoodBadAttr.toString(),
				Const.GOOD_BAD_ATTR_FILENAME, false);
	}
}