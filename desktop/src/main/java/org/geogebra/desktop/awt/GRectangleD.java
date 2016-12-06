package org.geogebra.desktop.awt;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.geogebra.common.awt.GAffineTransform;
import org.geogebra.common.awt.GPathIterator;
import org.geogebra.common.awt.GPoint2D;
import org.geogebra.common.awt.GRectangle;
import org.geogebra.common.awt.GRectangle2D;
import org.geogebra.common.util.debug.Log;

public class GRectangleD implements GRectangle2DD,
 GRectangle {

	Rectangle impl;

	public GRectangleD() {
		impl = new Rectangle();
	}

	public GRectangleD(GRectangle r) {
		impl = ((GRectangleD) r).impl;
	}

	public GRectangleD(int x, int y, int w, int h) {
		impl = new Rectangle(x, y, w, h);
	}

	public GRectangleD(int w, int h) {
		impl = new Rectangle(w, h);
	}

	public GRectangleD(Rectangle frameBounds) {
		impl = frameBounds;
	}

	public double getY() {
		return impl.getY();
	}

	public double getX() {
		return impl.getX();
	}

	public double getWidth() {
		return impl.getWidth();
	}

	public double getHeight() {
		return impl.getHeight();
	}

	public void setBounds(int x, int y, int width, int height) {
		impl.setBounds(x, y, width, height);

	}

	public void setLocation(int x, int y) {
		impl.setLocation(x, y);

	}

	public void setBounds(GRectangle r) {
		impl.setBounds((int) r.getX(), (int) r.getY(), (int) r.getWidth(),
				(int) r.getHeight());

	}

	public boolean contains(double x, double y) {
		return impl.contains(x, y);
	}

	/**
	 * @param rect
	 *            Common rectangle to unwrap
	 * @return java.awt.Rectangle from the wrapper or null for wrong input type
	 */
	public static Rectangle getAWTRectangle(GRectangle rect) {
		if (!(rect instanceof GRectangleD)) {
			if (rect != null)
				Log.debug("other type");
			return null;
		}
		return ((GRectangleD) rect).impl;
	}

	public boolean contains(GRectangle labelRectangle) {
		return impl.contains(getAWTRectangle(labelRectangle));

	}

	public void add(GRectangle bb) {
		impl.add(((GRectangleD) bb).impl);

	}

	public double getMinX() {
		return impl.getMinX();
	}

	public double getMinY() {
		return impl.getMinY();
	}

	public double getMaxX() {
		return impl.getMaxX();
	}

	public double getMaxY() {
		return impl.getMaxY();
	}

	public void add(double x, double y) {
		impl.add(x, y);
	}

	public void setRect(double x, double y, double width, double height) {
		impl.setRect(x, y, width, height);
	}

	public void setFrame(double x, double y, double width, double height) {
		impl.setFrame(x, y, width, height);
	}

	public boolean intersects(double x, double y, double lengthX, double lengthY) {
		return impl.intersects(x, y, lengthX, lengthY);
	}

	public boolean intersects(GRectangle viewRect) {
		return impl.intersects(GRectangleD.getAWTRectangle(viewRect));
	}

	/*
	 * public boolean contains(PathPoint prevP) { return
	 * impl.contains(Point2D.getAwtPoint2D(prevP)); }
	 */

	public boolean intersects(int i, int j, int k, int l) {
		return impl.intersects(i, j, k, l);
	}

	public boolean contains(int x, int y) {
		return impl.contains(x, y);
	}

	public GRectangleD getBounds() {
		return new GRectangleD(impl.getBounds());
	}

	public GRectangle2D getBounds2D() {
		return new GGenericRectangle2DD(impl.getBounds2D());
	}

	public boolean contains(GRectangleD rectangle) {
		return impl.contains(GRectangleD
				.getAWTRectangle(rectangle));
	}

	public GPathIterator getPathIterator(GAffineTransform affineTransform) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD
						.getAwtAffineTransform(affineTransform)));
	}

	public GPathIterator getPathIterator(GAffineTransform at, double flatness) {
		return new GPathIteratorD(impl.getPathIterator(
				GAffineTransformD.getAwtAffineTransform(at),
				flatness));
	}

	public boolean intersects(GRectangle2D r) {
		return impl.intersects(GGenericRectangle2DD
				.getAWTRectangle2D(r));
	}

	public Shape getAwtShape() {
		return impl;
	}

	public boolean contains(GPoint2D p1) {
		return impl.contains(((GPoint2DD) p1).getAwtPoint2D());
	}

	public GRectangle union(GRectangle bounds) {
		return new GRectangleD(impl.union(GRectangleD
				.getAWTRectangle(bounds)));
	}

	public GRectangle2D createIntersection(GRectangle2D r) {
		return new GGenericRectangle2DD(impl
				.createIntersection(GGenericRectangle2DD
						.getAWTRectangle2D(r)));
	}

	public void setSize(int width, int height) {
		impl.setSize(width, height);
	}

	public static Rectangle2D getAWTRectangle2D(GRectangle2D rectangle) {
		return ((GRectangle2DD) rectangle).getImpl();
	}

	@Override
	public boolean intersectsLine(double x1, double y1, double x2, double y2) {
		return impl.intersectsLine(x1, y1, x2, y2);
	}

	@Override
	public boolean contains(GRectangle2D rectangle) {
		return impl.contains(GRectangleD.getAWTRectangle2D(rectangle));
	}

	@Override
	public Rectangle2D getImpl() {
		return impl;
	}

}
