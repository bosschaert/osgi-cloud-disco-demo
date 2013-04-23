package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.coderthoughts.cloud.demo.api.LongRunningService;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.CloudConstants;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        TestService ts = new TestServiceImpl(context.getProperty("org.osgi.framework.uuid"));
        Dictionary<String, Object> tsProps = new Hashtable<String, Object>();
        tsProps.put("service.exported.interfaces", "*");
        tsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        tsProps.put("service.exported.handler", new TestServiceInvocationHandler(context));
        context.registerService(TestService.class.getName(), ts, tsProps);

        LongRunningService ls = new LongRunningServiceImpl();
        Dictionary<String, Object> lsProps = new Hashtable<String, Object>();
        lsProps.put("service.exported.interfaces", LongRunningService.class.getName());
        lsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        lsProps.put("service.exported.handler", new LongRunningServiceInvocationHandler());
        context.registerService(LongRunningService.class.getName(), ls, lsProps);

        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(FrameworkNodeAddition.ADD_PROPERTIES_KEY, new String [] {"org.coderthoughts.my-application.role"});
        props.put("org.coderthoughts.my-application.role", "data-store-image");
        props.put(FrameworkNodeAddition.ADD_VARIABLES_KEY, "network.load");
        context.registerService(FrameworkNodeAddition.class.getName(), new FrameworkStatusAdditionImpl(), props);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }
}
