package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.coderthoughts.cloud.demo.api.LongRunningService;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.OSGiFramework;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.monitor.Monitorable;

public class Activator implements BundleActivator {
    private ServiceRegistration lrsReg;
    private ServiceRegistration tsReg;
    private ServiceRegistration mReg;

    @Override
    public void start(BundleContext context) throws Exception {
        TestService ts = new TestServiceImpl(context.getProperty("org.osgi.framework.uuid"));
        Dictionary<String, Object> tsProps = new Hashtable<String, Object>();
        tsProps.put("service.exported.interfaces", "*");
        tsProps.put("service.exported.configs", new String [] {"org.coderthoughts.configtype.cloud", "<<nodefault>>"});
        TestServiceRSF tsControl = new TestServiceRSF(context);
        tsProps.put("org.coderthoughts.remote.service.factory", tsControl);
        tsReg = context.registerService(TestService.class.getName(), ts, tsProps);

        // Register the monitorable too for the TestService.
        Dictionary<String, Object> mProps = new Hashtable<String, Object>();
        String monitorablePID = OSGiFramework.MONITORABLE_SERVICE_PID_PREFIX +
                tsReg.getReference().getProperty(Constants.SERVICE_ID);
        mProps.put(Constants.SERVICE_PID, monitorablePID);
        mReg = context.registerService(Monitorable.class.getName(), tsControl, mProps);

        LongRunningService lrs = new LongRunningServiceImpl();
        Dictionary<String, Object> lrsProps = new Hashtable<String, Object>();
        lrsProps.put("service.exported.interfaces", "*");
        lrsProps.put("service.exported.configs", new String [] {"org.coderthoughts.configtype.cloud", "<<nodefault>>"});
        RemoteServiceFactory lrsFactory = new LongRunningServiceRSF();
        lrsProps.put("org.coderthoughts.remote.service.factory", lrsFactory);
        lrsReg = context.registerService(LongRunningService.class.getName(), lrs, lrsProps);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        lrsReg.unregister();
        tsReg.unregister();
        mReg.unregister();
    }
}
