package net.sf.mzmine.modules.rawdatamethods.peakpicking.massdetection.PeakInvestigator;

import net.sf.mzmine.datamodel.DataPoint;
import net.sf.mzmine.datamodel.Scan;
import net.sf.mzmine.datamodel.impl.SimpleMassList;

public class PeakInvestigatorMassList extends SimpleMassList {

	public PeakInvestigatorMassList(String name, Scan scan, DataPoint[] mzPeaks) {
		super(name, scan, mzPeaks);
	}

	public PeakInvestigatorDataPoint[] getDataPoints() {
		DataPoint[] dataPoints = super.getDataPoints();
		PeakInvestigatorDataPoint[] newDataPoints = new PeakInvestigatorDataPoint[dataPoints.length];
		for (int i = 0; i < dataPoints.length; i++) {
			newDataPoints[i] = new PeakInvestigatorDataPoint(dataPoints[i]);
		}
		return newDataPoints;
	}
}
