package kz.kkb.portlet;

import java.util.Date;

/**
 * Общий логгер. 
 * Поскольку System.out в ряде случаев подавляется, и к тому же он ведется сплошным файлом,
 * данный логгер переделан чтобы писать сообщения в log4j. 
 */
public class Logger {
	
	private static org.apache.log4j.Logger logger=org.apache.log4j.Logger.getLogger("PortletLogger");
	
    public static void log(String s) {
        Date date=new Date();
        Exception e=new Exception();
        StackTraceElement stack[]= e.getStackTrace();
        logger.warn("Message: "+date+";"+stack[1].toString()+"; "+s);
    }
    public static void log(Exception e) {
        Date date=new Date();
        //StackTraceElement stack[]= e.getStackTrace();
        //System.out.println("Homebank Exception: "+date+";"+stack[1].toString()+"; "+e.toString());
        //обработка трассировки стека
        StackTraceElement stackTrace[]=e.getStackTrace();
        StringBuffer sbStackTrace=new StringBuffer(e.toString()+"\n");
        for (int i=0;i<stackTrace.length && i<10;i++) {
            sbStackTrace.append("\t").append(stackTrace[i].toString()).append("\n") ;
            if (stackTrace[i].toString().startsWith("org.apache.jasper.runtime.HttpJspBase.service")) break;
        }    
        logger.error("Exception: "+date+";"+sbStackTrace.toString());
        }
}
