package dbms.cli;

import java.util.ArrayList;
import java.util.List;

public class Measure {

	private String measureName;
	private String unit;
	long upperLimit;
	long lowerLimit;
	private String frequency;
	private String dataType;
	long numberOfObservation;
	double threshold;

	public String getMeasureName() {
		return measureName;
	}

	public void setMeasureName(String measureName) {
		this.measureName = measureName;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public long getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(long upperLimit) {
		this.upperLimit = upperLimit;
	}

	public long getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(long lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public long getNumberOfObservation() {
		return numberOfObservation;
	}

	public void setNumberOfObservation(long numberOfObservation) {
		this.numberOfObservation = numberOfObservation;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

}
