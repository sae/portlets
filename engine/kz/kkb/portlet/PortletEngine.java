package kz.kkb.portlet;

import org.dom4j.Element;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

public class PortletEngine {
	
	private static ServletContext context;
	private static ClassLoader loader;
	private static String portletLocation="/WEB-INF/portlets/";
	
	
	public static void init(HttpSession session) {
		context=session.getServletContext();
	}
	
	private static PortletEngine getInstance() throws Exception {
		PortletEngine engine = (PortletEngine)context.getAttribute("PortletEngine");
		if (engine == null) {
			engine = new PortletEngine();
			context.setAttribute("PortletEngine", engine);
			
			//******* Установка загрузчика классов *****************
			//если парент - системный лоадер, то он не знает классов приложения
			//поэтому вылазит ClassCastException
			//ClassLoader parent = java.lang.Thread.currentThread().getContextClassLoader().getParent();//вот так - ClassCastException
			//нужно иметь парентом лоадер приложения
			//но он и сам может загружать портлеты, потому что он их видит
			//тогда портлеты живут на уровне лоадера приложения и перегружаются только вместе с ним
			ClassLoader parent = java.lang.Thread.currentThread().getContextClassLoader();//вот так - портлеты грузятся приложением (если оно их видит)
			String sPath = context.getRealPath(portletLocation);//правильно будет сложить портлеты отдельно от классов приложения
			//но тогда на странице надо явно указывать название портлета, потому как приложение не знает такого класса
			//ИТОГО: 
			//если приложение знает класс, оно его загрузит 
			//если не знает - лоадер загрузит его по полному имени и его можно перегружать
			URL[] urls = new URL[1];
            urls[0] = new File(sPath).toURL();
            //System.out.println("file://"+sPath+"/");
            loader = new URLClassLoader(urls, parent);
		}
		return engine; 
	}
	
	private static String sEngineName="PortletEngine";
	
	public static void kill(HttpSession session) {
		if (session.getServletContext().getAttribute(sEngineName)!=null)
			session.getServletContext().removeAttribute(sEngineName);
	}
	
	/**
	 * Простой метод выполнения портлетов
	 * @param portletName
	 * @param request
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static Element exec(String portletName, HttpServletRequest request, String parameters) throws Exception {
		XmlPortlet p = getPortlet(portletName);
		//здесь можно ввести различные отладочные процедуры:
		//-тайминг
		//-счетчик обращений
		// итп
		long lStart = System.currentTimeMillis();
		
		Element e = p.process(request, parameters);
		
		long lEnd = System.currentTimeMillis();
		if ((lEnd - lStart) > 5000) {
			//здесь можно отфильтровать по имени портлета (если некоторые исполняются дольше
			Logger.log("WARNING! Execution portlet " + portletName + " exceeded 5 sec !");
		}
		return e;
	}

	/** 
	 * Вариант метода с сессией в параметрах
	 * @param portletName
	 * @param session
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public static Element exec(String portletName, HttpSession session, String parameters) throws Exception {
		XmlPortlet p = getPortlet(portletName);
		//здесь можно ввести различные отладочные процедуры:
		//-тайминг
		//-счетчик обращений
		// итп
		long lStart = System.currentTimeMillis();
		
		Element e = p.process(session, parameters);
		
		long lEnd = System.currentTimeMillis();
		if ((lEnd - lStart) > 5000) {
			//здесь можно отфильтровать по имени портлета (если некоторые исполняются дольше
			Logger.log("WARNING! Execution portlet " + portletName + " exceeded 5 sec !");
		}
		return e;
	}
	/**
	 * Держит таблицу портлетов, подгружает требуемые по необходимости
	 * либо выдает хранящиеся в таблице
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static XmlPortlet getPortlet(String name) throws Exception {
		PortletEngine engine = PortletEngine.getInstance();
        if (engine.portlets.containsKey(name)) {
			return engine.portlets.get(name);
		}
		Class c = loader.loadClass(name);
		XmlPortlet p = (XmlPortlet)c.newInstance();
		p.init(context);
		engine.portlets.put(name, p);
		return p;
	}
	//таблица портлетов
	protected HashMap<String, XmlPortlet> portlets = new HashMap<String, XmlPortlet>();

}
 