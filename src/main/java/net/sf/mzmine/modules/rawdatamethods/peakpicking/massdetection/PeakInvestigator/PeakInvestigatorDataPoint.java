package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import java.nio.FloatBuffer;
import java.text.Format;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.impl.SimpleDataPoint;
import net.sf.mzmine.main.MZmineCore;

public class PeakInvestigatorDataPoint extends SimpleDataPoint {

	private final double mzError, intensityError, mzMinimumError;

	/**
	 * Constructor which copies the data from another DataPoint
	 */
	public PeakInvestigatorDataPoint(DataPoint dp) {
		super(dp);
		this.mzError = Double.NaN;
		this.intensityError = Double.NaN;
		this.mzMinimumError = Double.NaN;
	}

	public PeakInvestigatorDataPoint(PeakInvestigatorDataPoint dp) {
		super(dp.getMZ(), dp.getIntensity());
		this.mzError = dp.getMzError();
		this.intensityError = dp.getIntensityError();
		this.mzMinimumError = dp.getMzMinimumError();
	}

	public PeakInvestigatorDataPoint(double mz, double intensity,
			double mzError, double intensityError, double mzMinimumError) {
		super(mz, intensity);
		this.mzError = mzError;
		this.intensityError = intensityError;
		this.mzMinimumError = mzMinimumError;
	}

	public double getMzError() {
		return mzError;
	}

	public double getIntensityError() {
		return intensityError;
	}

	public double getMzMinimumError() {
		return mzMinimumError;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PeakInvestigatorDataPoint)) {
			return false;
		}

		PeakInvestigatorDataPoint dp = (PeakInvestigatorDataPoint) obj;
		if (this.getMZ() == dp.getMZ()
				&& this.getIntensity() == dp.getIntensity()
				&& this.getMzError() == dp.getMzError()
				&& this.getIntensityError() == dp.getIntensityError()) {
			return true;
		}

		return false;
	}

	@Override
	public int getNumberOfValues() {
		return 5;
	}

	@Override
	public void addToBuffer(FloatBuffer buffer) {
		super.addToBuffer(buffer);
		buffer.put((float) mzError);
		buffer.put((float) intensityError);
		buffer.put((float) mzMinimumError);
	}

	@Override
	public int hashCode() {
		return (int) (super.hashCode() + this.mzError + this.intensityError);
	}

	@Override
	public String toString() {
		Format mzFormat = MZmineCore.getConfiguration().getMZFormat();
		Format intensityFormat = MZmineCore.getConfiguration()
				.getIntensityFormat();
		return String.format("m/z: %s±%s, intensity: %s±%s",
				mzFormat.format(getMZ()), mzFormat.format(mzError),
				intensityFormat.format(getIntensity()),
				intensityFormat.format(intensityError));
	}
}
