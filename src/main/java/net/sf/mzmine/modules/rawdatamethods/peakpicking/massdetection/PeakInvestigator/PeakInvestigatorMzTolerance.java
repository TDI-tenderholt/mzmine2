package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import java.awt.Window;

import com.google.common.collect.Range;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.ParameterSet;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;
import net.sf.mzmine.parameters.parametertypes.IntegerParameter;
import net.sf.mzmine.parameters.parametertypes.tolerances.MZTolerance;
import net.sf.mzmine.util.ExitCode;

public class PeakInvestigatorMzTolerance implements MZTolerance {

	private int confidenceLevel;

	public PeakInvestigatorMzTolerance() {
		this.confidenceLevel = 68;
	}

	public PeakInvestigatorMzTolerance(Integer confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	@Override
	public Range<Double> getToleranceRange(double mzValue) {
		throw new PeakInvestigatorMzToleranceException();
	}

	@Override
	public Range<Double> getToleranceRange(DataPoint dataPoint) {
		if (!(dataPoint instanceof PeakInvestigatorDataPoint)) {
			throw new IllegalArgumentException(
					"Expected data point from PeakInvestigator mass list");
		}

		PeakInvestigatorDataPoint dp = (PeakInvestigatorDataPoint) dataPoint;
		final double mzValue = dataPoint.getMZ();
		final double absoluteTolerance = Math.max(dp.getMzError(),
				dp.getMzMinimumError());
		return Range.closed(mzValue - absoluteTolerance, mzValue
				+ absoluteTolerance);
	}

	@Override
	public Range<Double> getToleranceRange(Range<Double> mzRange) {
		throw new PeakInvestigatorMzToleranceException();
	}

	@Override
	public boolean checkWithinTolerance(double mz1, double mz2) {
		throw new PeakInvestigatorMzToleranceException();
	}

	@Override
	public ParameterSet getParameterSet() {
		return new PeakInvestigatorMzToleranceParameters();
	}

	@Override
	public void updateFromParameterSet(ParameterSet parameterSet) {
		confidenceLevel = parameterSet.getParameter(PeakInvestigatorMzToleranceParameters.confidenceLevel)
				.getValue();
	}

	@Override
	public String toString() {
		return String.format("±%d confidence level", confidenceLevel);
	}

	static class PeakInvestigatorMzToleranceParameters extends
			SimpleParameterSet {
		private static IntegerParameter confidenceLevel = new IntegerParameter(
				"confidence level", "The desired confidence level", 68, 0, 100);

		PeakInvestigatorMzToleranceParameters() {
			super(new Parameter<?>[] { confidenceLevel });
		}

    	@Override
    	public ExitCode showSetupDialog(Window parent, boolean valueCheckRequired) {
    		return super.showSetupDialog(parent, valueCheckRequired);
    	}

		public MZTolerance getMzTolerance() {
			return new PeakInvestigatorMzTolerance(confidenceLevel.getValue());
		}
	}

	public class PeakInvestigatorMzToleranceException extends
			IllegalArgumentException {
		private static final long serialVersionUID = 1L;

		public PeakInvestigatorMzToleranceException() {
			super("A mass list needs to be provided");
		}
	}
}
