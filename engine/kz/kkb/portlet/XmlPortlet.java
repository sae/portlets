package kz.kkb.portlet;

import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.dom4j.Element;

/**
 * Скелет портлета.
 * Портлет должен:
 * - принимать request и параметры (опционально, можно null или "")
 * - генерировать xml-элемент
 * - название корневого элемента рекомендуется div class=[this.getClass().getSimpleName()]
 * !Важно: точки в названии не допускаются - не работает CSS div#portlet.name
 * 
 * некоторые портлеты могут держать информацию в сессии или приложении
 * 
 *	Предполагается, что портлет вызывается через PortletEngine, метод exec,
 *  так как в этом случае ведется реестр портлетов, кеш, собирается отладочная информация, 
 *  и кроме того, PortletEngine можно перегрузить без перезагрузки приложения.
 *  
 *  инерфейс портлета - статичный метод process(request,String params); params= "name1=value1;name2=value2..."
 *  простой портлет строит элемент div
 *  div id назначает портлет, рекомендуется по this.getClass().getName() 
 *
 * @author sae
 *
 */
public abstract class XmlPortlet {
	
	/**
	 * Основной метод. Вызывается PortletEngine при исполнении метода exec(). 
	 * Либо напрямую, либо, чаще, через перегруженный метод process(request, String params).
	 * @param request
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public abstract Element process(HttpServletRequest request, HashMap<String, String> parameters) throws Exception;
	
	/**
	 * В случае, если портлету невозможно передать request, можно задействовать вызов с session
	 * @param session
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public Element process(HttpSession session, HashMap<String, String> parameters) throws Exception {
		throw new Exception("process(session,...) not supported");
	}
	/**
	 * Инициализация портлета. Вызывается PortletEngine при создании инстанции.
	 * @param context
	 */
	public abstract void init(ServletContext context) throws Exception;
	/**
	 * Перегрузка метода для вызова со строкой параметров
	 * @param request
	 * @param parameters
	 * @return
	 * @throws Exception
	 */
	public Element process(HttpServletRequest request, String parameters) throws Exception {
		Element el=process(request, getParams(parameters));
		return el;
	}

	public Element process(HttpSession session, String parameters) throws Exception {
		Element el=process(session, getParams(parameters));
		return el;
	}
	
	private HashMap getParams(String parameters) {
		HashMap<String, String> params=new HashMap<String, String>();//ХешМап всегда будет, хотя бы пустой
		if (parameters!=null) {
			String[] pairs=parameters.split(";");//параметр всегда есть хоть один
			for (int i=0;i<pairs.length;i++) {
				String[] pair=pairs[i].split("="); 
				if (pair.length>1) //если знак "=" присутствует, то все до него - имя, все после - значение (до следующего =)
					params.put(pair[0], pair[1]);
			}
		}
		return params;
	}
}
