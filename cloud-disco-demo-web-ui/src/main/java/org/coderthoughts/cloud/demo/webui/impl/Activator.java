package org.coderthoughts.cloud.demo.webui.impl;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
    private ServiceTracker httpServiceTracker;

    @Override
    public void start(BundleContext context) throws Exception {
        httpServiceTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object svc = super.addingService(reference);
                if (svc instanceof HttpService) {
                    HttpService httpSvc = (HttpService) svc;
                    try {
                        httpSvc.registerServlet("/webui", new MyServlet(context), null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return svc;
            }
        };
        httpServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        httpServiceTracker.close();
    }
}
