package com.immortalcrab.meh;

import java.io.IOException;
import java.io.PrintWriter;
import javax.management.remote.JMXConnector;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.function.Supplier;
import java.util.Map;
import java.util.HashMap;

@WebServlet("/hello")
public class HelloServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req,
		       HttpServletResponse resp) throws ServletException, IOException {

      Map<String, String[]> environment = new HashMap<>();
      String[] credentials = {"admin", "1234qwer"};
      environment.put(JMXConnector.CREDENTIALS, credentials);
      Supplier<String> urlSupplier = () -> StatusApplicationHelper.shapeServiceURL("127.0.0.1","9443");
      Map<String, String> m = null; 
      try {
          m = StatusApplicationHelper.inquiry(urlSupplier, environment);
      }
      catch (Exception ex) {
          throw new ServletException(ex);
      }
      PrintWriter writer = resp.getWriter();
      writer.println("<!DOCTYPE html>");
      writer.println("<html>");
      writer.println("<body>");
      writer.println("<p>Hit " + urlSupplier.get() + "</p>");
      if (m.isEmpty()) {  writer.println("<p>No apps to display along with their status</p>"); }
      else { m.forEach((key, value) -> writer.println("<p>" + key + "::" + value + "</p>")); }
      writer.println("</body>");
      writer.println("</html>");
  }
}
