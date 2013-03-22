package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.CloudConstants;
import org.coderthoughts.cloud.framework.service.api.FrameworkStatus;
import org.coderthoughts.cloud.framework.service.api.FrameworkStatusAddition;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;

public class Activator implements BundleActivator {
//    private ServiceRegistration lrsReg;

    @Override
    public void start(BundleContext context) throws Exception {
        System.err.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ Starting");
        Dictionary<String, Object> props = new Hashtable<String, Object>();
        props.put(FrameworkStatusAddition.ADD_PROPERTIES_KEY, new String [] {"org.coderthoughts.my-application.role"});
        props.put("org.coderthoughts.my-application.role", "data-store-image");
        context.registerService(FrameworkStatusAddition.class.getName(), new FrameworkStatusAdditionImpl(), props);

        // TestService ts = new TestServiceImpl(context.getProperty("org.osgi.framework.uuid"));
        Dictionary<String, Object> tsProps = new Hashtable<String, Object>();
        tsProps.put("service.exported.interfaces", "*");
        tsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        tsProps.put("service.exported.type", TestService.class);
        RemoteServiceFactory<TestService> ts = new TestServiceRSF(context);
        ServiceRegistration tsReg = context.registerService(RemoteServiceFactory.class.getName(), ts, tsProps);

        // Register the monitorable too for the TestService.
        Dictionary<String, Object> mProps = new Hashtable<String, Object>();
        String monitorablePID = FrameworkStatus.MONITORABLE_SERVICE_PID_PREFIX +
                tsReg.getReference().getProperty(Constants.SERVICE_ID);
        mProps.put(Constants.SERVICE_PID, monitorablePID);
        context.registerService(Monitorable.class.getName(), ts, mProps);

        System.err.println("### Registered demo services");

        /*
        LongRunningService lrs = new LongRunningServiceImpl();
        Dictionary<String, Object> lrsProps = new Hashtable<String, Object>();
        lrsProps.put("service.exported.interfaces", "*");
        lrsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        RemoteServiceFactory lrsFactory = new LongRunningServiceRSF();
        lrsProps.put("org.coderthoughts.remote.service.factory", lrsFactory);
        lrsReg = context.registerService(LongRunningService.class.getName(), lrs, lrsProps);
        */
    }

    @Override
    public void stop(BundleContext context) throws Exception {
//        lrsReg.unregister();
//        tsReg.unregister();
//        mReg.unregister();
    }
}
