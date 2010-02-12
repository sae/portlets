<%@page import="org.dom4j.Element"%><%@
page import="kz.kkb.portlet.PortletEngine"%><%

PortletEngine.init(session);

Element e=PortletEngine.exec("test.HelloWorldPortlet",request,"name1=value1;name2=value2");
response.getWriter().write(e.asXML());
%>