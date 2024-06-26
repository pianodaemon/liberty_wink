package com.immortalcrab.meh;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo; 
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.util.Iterator; 

class StatusApplicationHelper {
 
   private static final String CONNECTOR_TYPE_REST = "rest";
   private static final String CONNECTOR_TEMPLATE_URL = "service:jmx:{0}://{1}:{2}/IBMJMXConnectorREST";
   private static final String APPLICATION_BEAN_NAME = "WebSphere:service=com.ibm.websphere.application.ApplicationMBean";
   private static final String APPLICATION_BEAN_PREFIX = "{0},name={1}";
 
   public static String shapeServiceURL(final String hostName, final String port) {
 
       Object[] params = new Object[]{
           (CONNECTOR_TYPE_REST), (hostName), (port),};
       return MessageFormat.format(CONNECTOR_TEMPLATE_URL, params);
   }
 
   public static Map<String, String> inquiry(Supplier<String> urlSupplier, Map<String, ?> environment) throws Exception {
 
       JMXServiceURL url = new JMXServiceURL(urlSupplier.get());
       try (JMXConnector jmxc = JMXConnectorFactory.connect(url, environment)) {
           MBeanServerConnection mbsConnection = jmxc.getMBeanServerConnection();
 
           Set<ObjectName> mbeans = mbsConnection.queryNames(new ObjectName(APPLICATION_BEAN_NAME + ",*"), null);

/*	      Iterator itr = mbeans.iterator();

        // check element is present or not. if not loop will
        // break.
        while (itr.hasNext()) {
            System.out.println("--->" + itr.next());
        }

	    for (ObjectName mbean : mbeans ) {
                //String appName = (String) mbsConnection.getAttribute(mbean, "ApplicationName");
//String status = (String) mbsConnection.getAttribute(mbean, "State");
  //              System.out.println("--->" + status);
                String name = (String) mbean.getKeyProperty("name");
                System.out.println("--->" + name);
                //deployedApplications.put(appName, status);
		MBeanInfo beanInfo = mbsConnection.getMBeanInfo(mbean);
		for (MBeanAttributeInfo attr : beanInfo.getAttributes()) {
	             System.out.println("DDDD->" + String.valueOf(mbsConnection.getAttribute(mbean, attr.getName())));
                }            
     AttributeList attributeList = mbsConnection.getAttributes(mbean, new String[]{"*"});
                
                // Iterate through the attributes
                for (Object attribute : attributeList) {
                    Attribute attr = (Attribute) attribute;
                    String attributeName = attr.getName();
                    Object attributeValue = attr.getValue();
                    System.out.println("Attribute: " + attributeName + ", Value: " + attributeValue);
                }
            }*/

           List<String> applicationNames = mbeans.stream()
                   .map(mbean -> mbean.getKeyProperty("name"))
                   .collect(Collectors.toList());
 
           return applicationNames.stream()
                   .collect(Collectors.toMap(
                           appName -> appName,
                           appName -> inquiryStatus(mbsConnection, appName)));
       }
 
   }
 
   private static String inquiryStatus(MBeanServerConnection mbsConnection, final String appName) {
 
       final String objName = MessageFormat.format(
               APPLICATION_BEAN_PREFIX,
               new Object[]{APPLICATION_BEAN_NAME, appName});
 
       try {
           ObjectName applicationMBean = new ObjectName(objName);
           return (String) mbsConnection.getAttribute(applicationMBean, "State");
       } catch (IOException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | MalformedObjectNameException | ReflectionException ex) {
           return "Error retrieving status: " + ex.getMessage();
       }
   }
 
   private StatusApplicationHelper() {}
}
