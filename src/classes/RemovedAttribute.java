package classes;

import java.util.HashMap;

public class RemovedAttribute implements Comparable<RemovedAttribute> {

	private String _attrName = null;
	
	private HashMap<String, Double> _accuracies = null;
	private double _accuracyGainLoss = 0;
	
	public RemovedAttribute(String attrName) {
		
		_attrName = attrName;
		_accuracies = new HashMap<>();
	}
	
	public String getAttributeName() {
		return _attrName;
	}
	
	public void addAccuracy(String technique, double accuracy) {
		_accuracies.put(technique, accuracy);
	}
	
	public double getAccuracy(String technique) {
		return _accuracies.get(technique);
	}
	
	public HashMap<String, Double> getAccuracies() {
		return _accuracies;
	}
	
	public double getAccuracyGainLoss() {
		return _accuracyGainLoss;
	}
	
	public void calcAccuracyGainLoss(RemovedAttribute baselineAttr, int[] weights) {
		
		_accuracyGainLoss = 0;
		
		int currAttr = 0;
		HashMap<String, Double> baselineAccuracies = baselineAttr.getAccuracies();
		for(String technique : baselineAccuracies.keySet()) {
			
			double baselineAccuracy = baselineAccuracies.get(technique);
			double selfAccuracy = getAccuracy(technique);
			
			double gainLoss = selfAccuracy - baselineAccuracy;
			
			_accuracyGainLoss += (weights[currAttr++] * gainLoss);
		}
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%-18s", _attrName)).append(" | ");
		for(String key : _accuracies.keySet()) {
			
			sb.append(String.format("%-13s",
					String.format("%5.3f %%", _accuracies.get(key))));
			sb.append(" | ");
		}
		sb.append(String.format("%-13s",
				String.format("%+5.3f %%", _accuracyGainLoss)));
		
		return sb.toString();
	}
	
	@Override
	public int compareTo(RemovedAttribute other) {
		
		double otherAccuracyGainLoss = other.getAccuracyGainLoss();
		
		/*if(otherAccuracyGainLoss > _accuracyGainLoss)
			return 1;
		else if(otherAccuracyGainLoss < _accuracyGainLoss)
			return -1;
		else
			return 0;*/
		
		return ((int) (otherAccuracyGainLoss - _accuracyGainLoss));
	}
}
