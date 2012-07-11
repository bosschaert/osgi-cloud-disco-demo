package org.coderthoughts.cloud.demo.webui.impl;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.coderthoughts.cloud.demo.api.LongRunningService;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.OSGiFramework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class MyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Collection<Pattern> reportedProperties = Arrays.asList(
            Pattern.compile("org.coderthoughts.framework.ip"),
            Pattern.compile("org.osgi.framework.uuid")
//            Pattern.compile("org.coderthoughts.*"),
//            Pattern.compile("org.osgi.*"),
//            Pattern.compile("java.*")
        );

    private final BundleContext bundleContext;
    private final List<ServiceReference> frameworkRefs = new CopyOnWriteArrayList<ServiceReference>();
    private final List<ServiceReference> testServicesRefs = new CopyOnWriteArrayList<ServiceReference>();
    private final List<ServiceReference> longRunningServiceRefs = new CopyOnWriteArrayList<ServiceReference>();

    MyServlet(BundleContext context) {
        bundleContext = context;

        openServiceTracker(context, OSGiFramework.class, frameworkRefs);
        openServiceTracker(context, TestService.class, testServicesRefs);
        openServiceTracker(context, LongRunningService.class, longRunningServiceRefs);
    }

    private ServiceTracker openServiceTracker(BundleContext context, Class<?> cls, final List<ServiceReference> refs) {
        ServiceTracker st = new ServiceTracker(context, cls.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                refs.add(reference);
                return super.addingService(reference);
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                refs.remove(reference);
                super.removedService(reference, service);
            }
        };
        st.open();
        return st;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        out.println("<html><head><title>Test Web UI</title></head><body>");

        printFrameworks(out);
        printTestServices(out);
        printConcurrentInvocations(out);

        out.println("</body></html>");
        out.close();
    }

    private void printFrameworks(PrintWriter out) {
        out.println("<H2>Frameworks in the Cloud Ecosystem</H2><table border='0'><tr>");

        for (ServiceReference ref : frameworkRefs) {
            Map<String, Object> sortedProps = new TreeMap<String, Object>();
            for (String key : ref.getPropertyKeys()) {
                for (Pattern p : reportedProperties) {
                    if (p.matcher(key).matches()) {
                        sortedProps.put(key, ref.getProperty(key));
                        continue;
                    }
                }
            }

            out.println("<td>OSGi Framework ");
            if (ref.getProperty("service.imported") != null)
                out.println("(remote)");
            else
                out.println("(local)");

            out.println(" - Free Memory: ");
            try {
                OSGiFramework fwk = (OSGiFramework) bundleContext.getService(ref);
                long bytes = Long.parseLong(fwk.getFrameworkVariable(OSGiFramework.FV_AVAILABLE_MEMORY));
                out.println(bytes/1024l);
            } catch (Exception ex) {
                out.println("unknown");
            }
            out.println(" (kilo bytes) <table border='1' frame='void'>");
            for (String key : sortedProps.keySet()) {
                out.println("<tr><td><small>" + key + "</small></td><td><small>" + sortedProps.get(key) + "</small></td></tr>");
            }
            out.println("</table></td>");
        }
        out.println("</tr></table>");
    }

    private void printTestServices(PrintWriter out) {
        out.println("<H2>TestService invocation</H2><small><ul>");

        for (int i=0; i < 5; i++) {
            for (ServiceReference ref : testServicesRefs) {
                try {
                    out.println("<li>TestService ");
                    if (ref.getProperty("service.imported") != null) {
                        out.println("(remote) ");
                        // ask the framework for its status
                        ServiceReference[] fwrefs = bundleContext.getServiceReferences(OSGiFramework.class.getName(),
                            "(endpoint.framework.uuid=" + ref.getProperty("endpoint.framework.uuid") + ")");
                        out.println(" status: ");
                        if (fwrefs != null && fwrefs.length > 0) {
                            OSGiFramework fw = (OSGiFramework) bundleContext.getService(fwrefs[0]);
                            out.println(fw.getServiceVariable((Long) ref.getProperty("endpoint.service.id"),
                                    OSGiFramework.SV_STATUS));
                        } else {
                            out.println("no matching framework found");
                        }
                    }

                    TestService svc = (TestService) bundleContext.getService(ref);
                    out.println("Result: " + svc.doit() + "</li>");
                    bundleContext.ungetService(ref);
                } catch (Throwable th) {
                    // Unwind the exception to the innermost one
                    while (th.getCause() != null) {
                        th = th.getCause();
                    }

                    // This service is potentially remote so it might throw an exception if it
                    // disappeared before Discovery knows about it.
                    out.println("Exception: " + th.getMessage() + "</li>");
                }
            }
        }
        out.println("</ul></small>");
    }

    private void printConcurrentInvocations(PrintWriter out) {
        List<Thread> threads = new ArrayList<Thread>();
        final List<Object> results = new CopyOnWriteArrayList<Object>();
        for (int i=0; i < 2; i++) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (ServiceReference ref : longRunningServiceRefs) {
                        // Only do this for remote services
                        if (ref.getProperty("service.imported") != null) {
                            LongRunningService svc = (LongRunningService) bundleContext.getService(ref);
                            try {
                                Integer res = svc.invoke(2000);
                                results.add(res); // Make the invocation last a while so that both are overlapping
                            } catch (Throwable th) {
                                // Unwind the exception to the innermost one
                                while (th.getCause() != null) {
                                    th = th.getCause();
                                }
                                results.add(th.getMessage());
                            }
                        }
                    }
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        out.println("<H2>Overlapping invocations</H2><small><ul>");
        for (Object result : results) {
            out.println("<li>result: " + result + "</li>");
        }
        out.println("</ul></small>");
    }
}
