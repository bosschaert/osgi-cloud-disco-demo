package org.coderthoughts.cloud.demo.client.impl;

import org.coderthoughts.cloud.demo.api.TestService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
    private ServiceTracker st;

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("*** Client started...");

        st = new ServiceTracker(context, TestService.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                Object svc = super.addingService(reference);
                if (svc instanceof TestService) {
                    TestService dr = (TestService) svc;
                    System.out.println("*** result: " + dr.doit("###### Invoking from a client"));
                }
                return svc;
            }
        };
        st.open();
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        st.close();
    }
}
