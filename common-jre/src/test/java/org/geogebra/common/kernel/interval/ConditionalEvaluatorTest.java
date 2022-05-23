package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalConstants.aroundZero;
import static org.geogebra.common.kernel.interval.IntervalConstants.one;
import static org.geogebra.common.kernel.interval.IntervalConstants.undefined;
import static org.geogebra.common.kernel.interval.IntervalConstants.zero;
import static org.geogebra.common.kernel.interval.IntervalHelper.interval;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.geogebra.common.BaseUnitTest;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.plugin.Operation;
import org.junit.Test;

public class ConditionalEvaluatorTest extends BaseUnitTest {

	@Test
	public void testAccepted() {
		assertTrue(newEvaluator(Operation.IF).isAccepted());
		assertTrue(newEvaluator(Operation.IF_ELSE).isAccepted());
		assertTrue(newEvaluator(Operation.IF_LIST).isAccepted());
	}

	private IntervalEvaluator newEvaluator(Operation operation) {
		return new ConditionalEvaluator(operation);
	}

	@Test
	public void testSignum() {
		IntervalFunction function = new IntervalFunction(add("If(x < 0, 0, 1)"));
		assertEquals(zero(), function.evaluate(interval(Double.NEGATIVE_INFINITY,
				0 - Kernel.MAX_PRECISION)));
		assertEquals(one(), function.evaluate(interval(0, Double.POSITIVE_INFINITY)));
	}

	@Test
	public void testIfElse() {
		IntervalFunction function = new IntervalFunction(add("If(x < 0, 2x + 3, 3x)"));
		assertEquals(interval(-1, 1), function.evaluate(interval(-2, -1)));
		assertEquals(interval(3, 6), function.evaluate(interval(1, 2)));
	}

	@Test
	public void testIf() {
		IntervalFunction function = new IntervalFunction(add("If(x < 0, x)"));
		assertEquals(interval(-2), function.evaluate(interval(-2)));
		assertEquals(undefined(), function.evaluate(aroundZero()));
		assertEquals(undefined(), function.evaluate(interval(2)));
	}

	@Test
	public void testIfWithCompoundConditional() {
		IntervalFunction function = new IntervalFunction(add("If(x^2 + 1 < 17, x^2)"));
		assertEquals(interval(4), function.evaluate(interval(2)));
		assertEquals(interval(9), function.evaluate(interval(-3)));
		assertEquals(undefined(), function.evaluate(interval(5)));
	}

	@Test
	public void testIfElseWithCompoundConditional() {
		IntervalFunction function = new IntervalFunction(add("If(x^2 + 1 < 17, x^2, 4x)"));
		assertEquals(interval(4), function.evaluate(interval(2)));
		assertEquals(interval(36), function.evaluate(interval(9)));
	}

	@Test
	public void testIfList() {
		IntervalFunction function =
				new IntervalFunction(add("If(x < -2, 0, -2 < x < 0, 1, x > 0, 2)"));
		assertEquals(interval(0), function.evaluate(interval(-21, -4)));
		assertEquals(interval(1), function.evaluate(interval(-1, 0)));
		assertEquals(interval(2), function.evaluate(interval(1, 4)));

	}
}