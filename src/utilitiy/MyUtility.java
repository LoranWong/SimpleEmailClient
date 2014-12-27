package utilitiy;

import java.awt.Font;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class MyUtility {
	 public static void InitGlobalFont(Font font) {
		    FontUIResource fontRes = new FontUIResource(font);
		    for (Enumeration<Object> keys = UIManager.getDefaults().keys();
		         keys.hasMoreElements(); ) {
		      Object key = keys.nextElement();
		      Object value = UIManager.get(key);
		      if (value instanceof FontUIResource) {
		        UIManager.put(key, fontRes);
		      }
		    }
	 }
	 
	 public static Vector<String> getTo_listFromString(String to) {
			Vector<String> to_list = new Vector<String>();
			String[] tos = to.split(";");
			for (int i = 0; i < tos.length; i++) {
				to_list.addElement(tos[i]);
			}
			return to_list;
	}
	 
	 public static String getStringFromTo_list(Vector<String> to_list) {
		 	String to="";
			for (int i = 0; i < to_list.size(); i++) {
				to += to_list.get(i) + ";" ;
			}
			return to;
	}
}
