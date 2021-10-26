package org.geogebra.common.kernel.interval;

import static org.geogebra.common.kernel.interval.IntervalOperands.*;

import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.ExpressionValue;
import org.geogebra.common.kernel.arithmetic.FunctionVariable;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoFunction;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.debug.Log;

/**
 * Class to evaluate a GeoFunction on a given interval
 * using interval arithmetic.
 *
 * @author laszlo
 */
 public class IntervalFunction {
	private static final UnsupportedOperatorChecker
			operatorChecker = new UnsupportedOperatorChecker();
	private static final Interval EMPTY = IntervalConstants.empty();
	private final GeoFunction function;

	/**
	 * Constructor
	 *
	 * @param function to evaluate.
	 */
	public IntervalFunction(GeoFunction function) {
		this.function = function;
	}

	/**
	 * Evaluates the function on a given interval.
	 *
	 * @param x interval to evaulate on.
	 * @return function result on x.
	 * @throws Exception that occurs on operands (divide by zero, power is not singleton, etc)
	 */
	public Interval evaluate(Interval x) throws Exception {
		ExpressionNode node = function.getFunctionExpression();
		return evaluate(new Interval(x), node);
	}

	static Interval evaluate(Interval x, ExpressionValue ev) throws Exception {
		if (ev == null) {
			return EMPTY;
		}
		if (ev instanceof FunctionVariable) {
			return new Interval(x);
		}
		if (!ev.isExpressionNode()) {
			return new Interval(ev.evaluateDouble());
		}

		ExpressionNode node = ev.wrap();

		IntervalPowerEvaluator power = new IntervalPowerEvaluator(node);
		if (power.isAccepted()) {
			return power.handle(x);
		}

		if (!node.containsFreeFunctionVariable(null)) {
			return new Interval(ev.evaluateDouble());
		}

		Interval left = evaluate(x, node.getLeft());
		Interval right = evaluate(x, node.getRight());
		Operation operation = node.getOperation();
		return evaluate(left, operation, right);
	}

	private static Interval evaluate(Interval left, Operation operation,
			Interval right) throws Exception {

		switch (operation) {
			case NO_OPERATION:
				return left;
			case PLUS:
				return left.add(right);
			case MINUS:
				return left.subtract(right);
			case MULTIPLY:
				return multiply(left, right);
			case DIVIDE:
				return divide(left, right);
			case POWER:
				return pow(left, right);
			case NROOT:
				return nthRoot(left, right);
			case DIFF:
				return difference(left, right);
			case SIN:
				return sin(left);
			case SEC:
				return sec(left);
			case COS:
				return cos(left);
			case CSC:
				return csc(left);
			case COT:
				return cot(left);
			case SQRT:
				return sqrt(left);
			case TAN:
				return tan(left);
			case EXP:
				return exp(left);
			case LOG:
				return log(left);
			case ARCCOS:
				return acos(left);
			case ARCSIN:
				return asin(left);
			case ARCTAN:
				return atan(left);
			case ABS:
				return abs(left);
			case COSH:
				return cosh(left);
			case SINH:
				return sinh(left);
			case TANH:
				return tanh(left);
			case LOG10:
				return log10(left);
			case LOG2:
				return log2(left);

			default:
				Log.warn("No interval operation for " + operation);
				return IntervalConstants.empty();
			}
		}

	private static Interval divide(Interval left, Interval right) {
		if (left.isSingleton()) {
			return multiply(right.multiplicativeInverse(), left);
		}
		return IntervalOperands.divide(left, right);
	}

	/**
	 *
	 * @param geo to check.
	 * @return true if the geo is a function
	 * and supported by our interval arithmetic implementation.
	 */
	public static boolean isSupported(GeoElement geo) {
		if (!(geo instanceof GeoFunction)) {
			return false;
		}
		GeoFunction function = (GeoFunction) geo;
		boolean operationSupported = isOperationSupported(function);
		boolean moreVariables = hasMoreVariables(function);
		return operationSupported && !moreVariables;
	}

	private static boolean isOperationSupported(GeoFunction function) {
		ExpressionNode expression = function.getFunctionExpression();
		if (expression == null) {
			return false;
		}
		return !expression.inspect(operatorChecker);
	}

	private static boolean hasMoreVariables(GeoFunction function) {
		ExpressionNode expression = function.getFunctionExpression();
		if (expression == null) {
			return false;
		}
		return expression.inspect(new MultipleVariableChecker());
	}

	public GeoFunction getFunction() {
		return function;
	}
}