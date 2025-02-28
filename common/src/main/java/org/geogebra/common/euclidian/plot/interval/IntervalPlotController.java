package org.geogebra.common.euclidian.plot.interval;

import org.geogebra.common.euclidian.CoordSystemAnimationListener;
import org.geogebra.common.euclidian.CoordSystemInfo;
import org.geogebra.common.euclidian.EuclidianController;

/**
 * Controller for Interval Plotter to handle zoom and moving the view.
 *
 * @author laszlo
 */
public class IntervalPlotController implements CoordSystemAnimationListener {

	private final IntervalPlotModel model;
	private EuclidianController euclidianController;

	/**
	 * Constructor.
	 * @param model {@link IntervalPlotModel}
	 */
	public IntervalPlotController(IntervalPlotModel model) {
		this.model = model;
	}

	/**
	 * @param controller {@link EuclidianController}
	 */
	public void attachEuclidianController(EuclidianController controller) {
		euclidianController = controller;
		euclidianController.addZoomerAnimationListener(this, model.getGeoFunction());
	}

	@Override
	public void onZoomStop(CoordSystemInfo info) {
		info.setXAxisZoom(false);
		if (IntervalPlotSettings.isUpdateEnabled()) {
			model.updateAll();
		}
	}

	@Override
	public void onMoveStop() {
		if (IntervalPlotSettings.isUpdateEnabled()) {
			model.updateAll();
		}
	}

	@Override
	public void onMove(CoordSystemInfo info) {
		if (info.isXAxisZoom() || info.isCenterVew()) {
			return;
		}

		if (IntervalPlotSettings.isUpdateEnabled()) {
			model.updateDomain();
		}
	}

	/**
	 * Remove controller as zoomer animation listener.
	 */
	public void detach() {
		euclidianController.removeZoomerAnimationListener(model.getGeoFunction());
	}
}