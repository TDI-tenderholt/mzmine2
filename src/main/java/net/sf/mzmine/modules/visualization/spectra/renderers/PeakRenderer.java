/*
 * Copyright 2006-2015 The MZmine 2 Development Team
 * 
 * This file is part of MZmine 2.
 * 
 * MZmine 2 is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * MZmine 2 is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * MZmine 2; if not, write to the Free Software Foundation, Inc., 51 Franklin St,
 * Fifth Floor, Boston, MA 02110-1301 USA
 */

package net.sf.mzmine.modules.visualization.spectra.renderers;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.sf.mzmine.modules.visualization.spectra.datasets.MassListDataSet;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYErrorRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;

public class PeakRenderer extends AbstractXYItemRenderer {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final XYBarRenderer barRenderer = new XYBarRenderer();
    private final XYErrorRenderer errorRenderer = new XYErrorRenderer();

    public static final float TRANSPARENCY = 0.8f;

    public static final AlphaComposite alphaComp = AlphaComposite.getInstance(
	    AlphaComposite.SRC_OVER, TRANSPARENCY);

    private boolean isTransparent;

    public PeakRenderer(Color color, boolean isTransparent) {

	this.isTransparent = isTransparent;

	// Set painting color
	barRenderer.setBasePaint(color);
	barRenderer.setSeriesPaint(0, color);
	barRenderer.setSeriesOutlinePaint(0, color);
	errorRenderer.setBasePaint(color);
	errorRenderer.setSeriesPaint(0, color);

	// Shadow makes fake peaks
	barRenderer.setShadowVisible(false);

	// Set the tooltip generator
	SpectraToolTipGenerator tooltipGenerator = new SpectraToolTipGenerator();
	barRenderer.setBaseToolTipGenerator(tooltipGenerator);
	errorRenderer.setBaseToolTipGenerator(tooltipGenerator);

	// We want to paint the peaks using simple color without any gradient
	// effects
	barRenderer.setBarPainter(new StandardXYBarPainter());
    }

	public void drawItem(Graphics2D g2, XYItemRendererState state,
			Rectangle2D dataArea, PlotRenderingInfo info, XYPlot plot,
			ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset,
			int series, int item, CrosshairState crosshairState, int pass) {

		if (isTransparent)
			g2.setComposite(alphaComp);

		barRenderer.drawItem(g2, state, dataArea, info, plot, domainAxis,
				rangeAxis, dataset, series, item, crosshairState, pass);

		XYDataset errorDataSet = null;
		if (dataset instanceof MassListDataSet) {
			errorDataSet = ((MassListDataSet) dataset).getErrorBarDataSet();
		}

		if (errorDataSet != null) {
			errorRenderer
					.drawItem(g2, state, dataArea, info, plot, domainAxis,
							rangeAxis, errorDataSet, series, item,
							crosshairState, pass);
		}
	}

    @Override
	public void setPlot(XYPlot plot) {
    	barRenderer.setPlot(plot);
    	errorRenderer.setPlot(plot);
    }

    @Override
    public void addChangeListener(RendererChangeListener listener) {
    	barRenderer.addChangeListener(listener);
    	errorRenderer.addChangeListener(listener);
    }

    @Override
    public void removeChangeListener(RendererChangeListener listener) {
    	barRenderer.removeChangeListener(listener);
    	errorRenderer.removeChangeListener(listener);
    }

    /**
     * This method returns null, because we don't want to change the colors
     * dynamically.
     */
    public DrawingSupplier getDrawingSupplier() {
	return null;
    }

}
