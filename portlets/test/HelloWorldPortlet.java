package test;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import kz.kkb.portlet.XmlPortlet;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/*
 * Простой пример портлета.
 * Если скомпиленный класс удалить из /WEB-INF/classes и положить в /WEB-INF/portlets
 * то он станет видим только для лоадера PortletEngine
 * Тогда его можно перегружать без рестарта приложения
 */

public class HelloWorldPortlet extends XmlPortlet {
	
	public void init(ServletContext context) {
		
	}
	
	public Element process(HttpServletRequest request, HashMap<String, String> params) throws Exception {
		Element e=DocumentHelper.createElement("div");
		e.addAttribute("class", this.getClass().getSimpleName());
		e.setText("Hello World!");
		e.addElement("params").addText(params.toString());
		return e;
	}

}
