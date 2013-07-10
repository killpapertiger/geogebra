package geogebra.html5.util;

import com.google.gwt.core.client.JavaScriptObject;

public class JSON {

    public static native String stringify(JavaScriptObject obj) /*-{ 
		return $wnd.JSON.stringify(obj); 
    }-*/;

	public static native JavaScriptObject parse(String obj) /*-{
		return $wnd.JSON.parse(obj);
	}-*/;

	public static native String get(JavaScriptObject obj, String key) /*-{
	   return obj[key] ;
    }-*/;

	public static native void put (JavaScriptObject obj, String key,
            String value) /*-{
	   obj[key] = value;
    }-*/;
}
