package org.geogebra.common.kernel.geos;

import org.geogebra.common.gui.inputfield.AutoCompleteTextField;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.arithmetic.ExpressionNode;
import org.geogebra.common.kernel.arithmetic.FunctionalNVar;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.kernel.commands.EvalInfo;
import org.geogebra.common.kernel.kernelND.GeoElementND;
import org.geogebra.common.kernel.kernelND.GeoPointND;
import org.geogebra.common.main.App;
import org.geogebra.common.main.MyError;
import org.geogebra.common.main.error.ErrorHandler;
import org.geogebra.common.main.error.ErrorHelper;
import org.geogebra.common.main.error.InputBoxErrorHandler;
import org.geogebra.common.plugin.Operation;
import org.geogebra.common.util.AsyncOperation;
import org.geogebra.common.util.debug.Log;

import com.himamis.retex.editor.share.util.Unicode;

/**
 * Updates linked element for an input box from user input
 */
public class InputBoxProcessor implements AsyncOperation<GeoElementND> {

	private GeoInputBox inputBox;
	private GeoElementND linkedGeo;
	private Kernel kernel;
	private App app;
	private AlgebraProcessor algebraProcessor;
	private InputBoxErrorHandler errorHandler;
	private boolean showErrorDialog;

	/**
	 * @param inputBox
	 *            parent input box
	 * @param linkedGeo
	 *            linked element
	 */
	public InputBoxProcessor(GeoInputBox inputBox, GeoElementND linkedGeo) {
		this.inputBox = inputBox;
		this.linkedGeo = linkedGeo;
		this.kernel = inputBox.getKernel();
		this.app = kernel.getApplication();
		this.algebraProcessor = kernel.getAlgebraProcessor();
		this.showErrorDialog = app.getConfig().isShowingErrorDialogForInputBox();
		ErrorHandler errorHandler = showErrorDialog ? app.getErrorHandler() : ErrorHelper.silent();
		this.errorHandler = new InputBoxErrorHandler(this, errorHandler);
	}

	/**
	 * @param inputText
	 *            user input
	 * @param tpl
	 *            template
	 * @param useRounding
	 *            whether to use rounding
	 */
	public void updateLinkedGeo(String inputText, StringTemplate tpl, boolean useRounding) {
		if (!linkedGeo.isLabelSet() && linkedGeo.isGeoText()) {
			((GeoText) linkedGeo).setTextString(inputText);
			return;
		}
		inputBox.setTempUserInput(null);
		String defineText = preprocess(inputText, tpl);

		ExpressionNode parsed = null;

		if (linkedGeo.isGeoNumeric()) {
			try {
				parsed = kernel.getParser().parseExpression(inputText);
			} catch (Throwable e) {
				// nothing to do
			}
		}

		// for a simple number, round it to the textfield setting (if set)
		if (parsed != null && parsed.isConstant() && !linkedGeo.isGeoAngle()
				&& useRounding) {
			try {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				double num = algebraProcessor.evaluateToDouble(inputText, false, null);
				defineText = kernel.format(num, tpl);

			} catch (Exception e) {
				// user has entered eg 33+
				// do nothing
				e.printStackTrace();
			}
		}

		try {
			if (linkedGeo instanceof GeoNumeric && linkedGeo.isIndependent() && parsed != null
					&& parsed.isConstant()) {
				// can be a calculation eg 1/2+3
				// so use full GeoGebra parser
				algebraProcessor.evaluateToDouble(defineText, false, (GeoNumeric) linkedGeo);

				// setValue -> avoid slider range changing

				linkedGeo.updateRepaint();
				inputBox.setLinkedGeo(linkedGeo);
			} else {
				EvalInfo info = new EvalInfo(!kernel.getConstruction().isSuppressLabelsActive(),
						linkedGeo.isIndependent(), false).withSliders(false);

				algebraProcessor.changeGeoElementNoExceptionHandling(linkedGeo,
						defineText, info, true, this, errorHandler);
			}
		} catch (MyError error) {
			maybeShowError(error);
		} catch (Exception exception) {
			Log.error(exception.getMessage());
			maybeShowError(MyError.Errors.InvalidInput);
		}
	}

	private String preprocess(String inputText, StringTemplate tpl) {
		String defineText = inputText;

		if (linkedGeo.isGeoText()) {
			defineText = "\"" + defineText + "\"";
		} else if ("?".equals(inputText.trim()) || "".equals(inputText.trim())) {
			defineText = "?";
		} else if (linkedGeo.isGeoLine()) {

			// not y=
			// and not Line[A,B]
			if ((defineText.indexOf('=') == -1) && (defineText.indexOf('[') == -1)) {
				// x + 1 changed to
				// y = x + 1
				defineText = "y=" + defineText;
			}

			String prefix = linkedGeo.getLabel(tpl) + ":";
			// need a: in front of
			// X = (-0.69, 0) + \lambda (1, -2)
			if (!defineText.startsWith(prefix)) {
				defineText = prefix + defineText;
			}
		} else if (isComplexNumber()) {

			// make sure user can enter regular "i"
			defineText = defineText.replace('i', Unicode.IMAGINARY);

			// z=2 doesn't work for complex numbers (parses to
			// GeoNumeric)
			defineText = defineText + "+0" + Unicode.IMAGINARY;

		} else if (linkedGeo instanceof FunctionalNVar) {
			// string like f(x,y)=x^2
			// or f(\theta) = \theta
			defineText = linkedGeo.getLabel(tpl) + "("
					+ ((FunctionalNVar) linkedGeo).getVarString(tpl) + ")=" + defineText;
		}
		return defineText;
	}

	private boolean isComplexNumber() {
		return linkedGeo.isGeoPoint()
				&& ((GeoPointND) linkedGeo).getToStringMode() == Kernel.COORD_COMPLEX;
	}

	@Override
	public void callback(GeoElementND obj) {
		if (isComplexNumber()) {
			ExpressionNode def = obj.getDefinition();
			if (def != null && def.getOperation() == Operation.PLUS && def.getRight()
					.toString(StringTemplate.defaultTemplate).equals("0" + Unicode.IMAGINARY)) {
				obj.setDefinition(def.getLeftTree());
				inputBox.setLinkedGeo(obj);
				obj.updateRepaint();
				return;
			}

		}
		inputBox.setLinkedGeo(obj);
	}

	private void maybeShowError(MyError error) {
		if (showErrorDialog) {
			app.showError(error);
		}
	}

	private void maybeShowError(MyError.Errors error) {
		if (showErrorDialog) {
			app.showError(error);
		}
	}

	/**
	 * Returns GeoInputBox which is processed by InputBoxProcessor
	 *
	 * @return input box
	 */
	public GeoInputBox getInputBox() {
		return inputBox;
	}

	/**
	 * Called by a AlgebraProcessor after correct syntax is validated
	 *
	 */
	public void updateTempUserInput() {
		AutoCompleteTextField tf = kernel.getApplication().getActiveEuclidianView()
				.getTextField();
		if (tf != null) {
			inputBox.setTempUserInput(tf.getText());
		} else {
			inputBox.setTempUserInput(null);
		}
	}
}
