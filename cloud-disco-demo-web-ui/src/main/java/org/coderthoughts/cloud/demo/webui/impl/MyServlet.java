package org.coderthoughts.cloud.demo.webui.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.OSGiFramework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Collection<String> reportedProperties = Arrays.asList(
            "org.coderthoughts.framework.ip", "org.osgi.framework.uuid"
        );

    private final BundleContext bundleContext;
    private final List<ServiceReference> frameworksRefs = new CopyOnWriteArrayList<ServiceReference>();
    private final List<ServiceReference> testServicesRefs = new CopyOnWriteArrayList<ServiceReference>();

    MyServlet(BundleContext context) {
        bundleContext = context;

        ServiceTracker frameworkTracker = new ServiceTracker(context, OSGiFramework.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                frameworksRefs.add(reference);
                return super.addingService(reference);
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                frameworksRefs.remove(reference);
                super.removedService(reference, service);
            }
        };
        frameworkTracker.open();

        ServiceTracker testServiceTracker = new ServiceTracker(context, TestService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                testServicesRefs.add(reference);
                return super.addingService(reference);
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                testServicesRefs.remove(reference);
                super.removedService(reference, service);
            }
        };
        testServiceTracker.open();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>Test Web UI</title></head><body>");

        printFrameworks(out);
        printTestServices(out);

        out.println("</body></html>");
        out.close();
    }

    private void printFrameworks(PrintWriter out) {
        out.println("<H2>Frameworks in the Cloud Ecosystem</H2>");

        for (ServiceReference ref : frameworksRefs) {
            Map<String, Object> sortedProps = new TreeMap<String, Object>();
            for (String key : ref.getPropertyKeys()) {
                if (!reportedProperties.contains(key))
                    // Don't display all they props to keep things tidy
                    continue;
                sortedProps.put(key, ref.getProperty(key));
            }

            out.println("OSGi Framework<UL>");
            for (String key : sortedProps.keySet()) {
                out.println("<li>" + key + " - " + sortedProps.get(key) + "</li>");
            }
            out.println("</UL>");
        }
    }

    private void printTestServices(PrintWriter out) {
        out.println("<H2>TestService instances available</H2>");

        for (ServiceReference ref : testServicesRefs) {
            try {
                TestService svc = (TestService) bundleContext.getService(ref);
                out.println("TestService instance<ul>");
                out.println("<li>invoking: " + svc.doit("Hi there"));
                out.println("</li></ul>");
                bundleContext.ungetService(ref);
            } catch (Exception ex) {
                // This service is potentially remote so it might throw an exception if it
                // disappeared before Discovery knows about it.
            }
        }
    }
}
