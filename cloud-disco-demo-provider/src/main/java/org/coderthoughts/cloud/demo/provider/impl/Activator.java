package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.coderthoughts.cloud.demo.api.TestService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    private ServiceRegistration reg;

    @Override
    public void start(BundleContext context) throws Exception {
        TestService dr = new TestServiceImpl();
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put("service.exported.interfaces", "*");
        props.put("service.exported.configs", "org.coderthoughts.configtype.cloud");
        reg = context.registerService(TestService.class.getName(), dr, props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        reg.unregister();
    }

}
