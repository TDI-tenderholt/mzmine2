package net.sf.mzmine.parameters.parametertypes.tolerances;

import net.sf.mzmine.parameters.Parameter;
import net.sf.mzmine.parameters.impl.SimpleParameterSet;

import com.google.common.collect.Range;

public abstract class MZTolerance extends SimpleParameterSet {
	public MZTolerance(Parameter<?>[] parameters) {
		super(parameters);
	}

	public abstract Range<Double> getToleranceRange(final double mzValue);
	public abstract Range<Double> getToleranceRange(final Range<Double> mzRange);
	public abstract boolean checkWithinTolerance(final double mz1, final double mz2);
}
