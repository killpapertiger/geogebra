package org.geogebra.common.kernel.interval;

import static java.lang.Double.NEGATIVE_INFINITY;
import static java.lang.Double.POSITIVE_INFINITY;

public class IntervalMiscOperandsImpl implements IntervalMiscOperands {

	@Override
	public Interval exp(Interval interval) {
		if (!interval.isEmpty()) {
			interval.set(RMath.expLow(interval.getLow()),
					RMath.expHigh(interval.getHigh()));
		}
		return interval;
	}

	@Override
	public Interval log(Interval interval) {
		if (!interval.isEmpty()) {
			if (interval.getHigh() < 0) {
				interval.setEmpty();
			} else {
				double low = interval.getLow();
				interval.set(low <= 0 ? NEGATIVE_INFINITY : RMath.logLow(low),
						RMath.logHigh(interval.getHigh()));
			}
		}
		return interval;
	}

	@Override
	public Interval log2(Interval interval) {
		if (!interval.isEmpty()) {
			Interval interval2 = new Interval(2, 2);
			Interval logExp2 = interval2.getEvaluate().log();
			interval.getEvaluate().log().getEvaluate().divide(logExp2);
		}

		return interval;
	}

	@Override
	public Interval log10(Interval interval) {
		if (!interval.isEmpty()) {
			Interval interval10 = new Interval(10, 10);
			Interval logExp10 = interval10.getEvaluate().log();
			interval.getEvaluate().log().getEvaluate().divide(logExp10);
		}

		return interval;
	}

	@Override
	public Interval hull(Interval interval, Interval other) {
		if (interval.isEmpty() && other.isEmpty()) {
			interval.setEmpty();
 		} else if (interval.isEmpty()) {
			interval.set(other);
		} else if (!other.isEmpty()) {
			interval.set(Math.min(interval.getLow(), other.getLow()),
					Math.max(interval.getHigh(), other.getHigh()));
		}

		return interval;
	}

	@Override
	public Interval intersect(Interval interval, Interval other) {
		if (interval.isEmpty() || other.isEmpty()) {
			interval.setEmpty();
		} else {
			double low = Math.max(interval.getLow(), other.getLow());
			double high = Math.min(interval.getHigh(), other.getHigh());
			if (low <= high) {
				interval.set(low, high);
			} else {
				interval.setEmpty();
			}
		}
		return interval;
	}

	@Override
	public Interval union(Interval interval, Interval other) throws IntervalsNotOverlapException {
		if (!interval.isOverlap(other)) {
			throw new IntervalsNotOverlapException();
		}
		interval.set(Math.min(interval.getLow(), other.getLow()),
				Math.max(interval.getHigh(), other.getHigh()));
		return interval;
	}

	@Override
	public Interval difference(Interval interval, Interval other)
			throws IntervalsDifferenceException {
		if (interval.isEmpty() || other.isWhole()) {
			interval.setEmpty();
			return interval;
		}

		if (interval.isOverlap(other)) {
			if (interval.getLow() < other.getLow() && other.getHigh() < interval.getHigh()) {
				throw new IntervalsDifferenceException();
			}

			if ((other.getLow() <= interval.getLow() && other.getHigh() == POSITIVE_INFINITY)
				|| (other.getHigh() >= interval.getHigh() && other.getLow() == NEGATIVE_INFINITY)) {
				interval.setEmpty();
				return interval;
			}

			if (other.getLow() <= interval.getLow()) {
				interval.halfOpenLeft(other.getHigh(), interval.getHigh());
			} else {
				interval.halfOpenRight(interval.getLow(), other.getLow());
			}
		}
		return interval;
	}

	@Override
	public Interval abs(Interval interval) {
		if (interval.isEmpty() || interval.getLow() >= 0 || interval.isUndefined()) {
			return interval;
		}

		if (interval.getHigh() <= 0) {
			interval.negative();
		} else {
			interval.set(0, Math.max(-interval.getLow(), interval.getHigh()));
		}

		return interval;
	}
}
