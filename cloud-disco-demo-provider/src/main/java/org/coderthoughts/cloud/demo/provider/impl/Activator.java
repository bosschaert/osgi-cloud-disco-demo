package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.CloudConstants;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.resource.ResourceContext;
import org.osgi.service.resource.ResourceEvent;
import org.osgi.service.resource.ResourceListener;
import org.osgi.service.resource.ResourceMonitor;
import org.osgi.service.resource.ResourceMonitorException;
import org.osgi.service.resource.ResourceMonitorFactory;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        ResourceMonitorFactory rmf = new MyResourceMonitorFactory(context);
        ResourceContext frc = new FrameworkResourceContext(context);
        ResourceMonitor monitor = rmf.createResourceMonitor(frc);

        TestService ts = new TestServiceImpl(context.getProperty("org.osgi.framework.uuid"));
        Dictionary<String, Object> tsProps = new Hashtable<String, Object>();
        tsProps.put("service.exported.interfaces", "*");
        tsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        tsProps.put("service.exported.handler", new TestServiceInvocationHandler(context, (MyServiceResourceMonitor) monitor));
        context.registerService(TestService.class.getName(), ts, tsProps);

        /*
        LongRunningService ls = new LongRunningServiceImpl();
        Dictionary<String, Object> lsProps = new Hashtable<String, Object>();
        lsProps.put("service.exported.interfaces", LongRunningService.class.getName());
        lsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        lsProps.put("service.exported.handler", new LongRunningServiceInvocationHandler());
        context.registerService(LongRunningService.class.getName(), ls, lsProps);
        */

        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(FrameworkNodeAddition.ADD_PROPERTIES_KEY, new String [] {"org.coderthoughts.my-application.role"});
        props.put("org.coderthoughts.my-application.role", "data-store-image");
        props.put(FrameworkNodeAddition.ADD_VARIABLES_KEY, "network.load");
        context.registerService(FrameworkNodeAddition.class.getName(), new FrameworkNodeAdditionImpl(), props);

        context.registerService(ResourceListener.class.getName(), new MyServiceResourceListener(), null);

    }

    @Override
    public void stop(BundleContext context) throws Exception {
    }

    public static class MyServiceResourceListener implements ResourceListener {

        @Override
        public void resourceEvent(ResourceEvent event) {
            System.out.println("$$$$$$$$$$ Resource Event: " + event);
        }

    }

    public static class MyResourceMonitorFactory implements ResourceMonitorFactory {
        private final BundleContext bundleContext;

        public MyResourceMonitorFactory(BundleContext context) {
            bundleContext = context;
        }

        @Override
        public String getType() {
            /* TODO HUH??? */
            return "MyServiceMonitor";
        }

        @Override
        public ResourceMonitor createResourceMonitor(ResourceContext resourceContext) {
            return new MyServiceResourceMonitor(bundleContext, resourceContext);
        }
    }

    public static class FrameworkResourceContext implements ResourceContext {
        private final BundleContext systemBundleContext;

        private FrameworkResourceContext(BundleContext bc) {
            systemBundleContext = bc.getBundle(0).getBundleContext();
        }

        @Override
        public String getName() {
            // TODO what to return here?
            return "The whole framework";
        }

        @Override
        public Bundle[] getBundles() {
            return systemBundleContext.getBundles();
        }

        @Override
        public void addBundle(Bundle b) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void removeBundle(Bundle b, ResourceContext destination) {
            throw new UnsupportedOperationException();
        }

        @Override
        public ResourceMonitor getMonitor(String resourceType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addResourceMonitor(ResourceMonitor resourceMonitor) throws ResourceMonitorException {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeResourceMonitor(ResourceMonitor resourceMonitor) {
            // TODO Auto-generated method stub

        }

        @Override
        public void removeContext(ResourceContext destination) {
            // TODO Auto-generated method stub

        }
    }
}
