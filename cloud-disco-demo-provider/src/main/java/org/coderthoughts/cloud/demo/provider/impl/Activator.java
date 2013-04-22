package org.coderthoughts.cloud.demo.provider.impl;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.CloudConstants;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        TestServiceRSF ts = new TestServiceRSF(context);
        Dictionary<String, Object> tsProps = new Hashtable<String, Object>();
        tsProps.put("service.exported.interfaces", "*");
        tsProps.put("service.exported.configs", new String [] {CloudConstants.CLOUD_CONFIGURATION_TYPE, "<<nodefault>>"});
        tsProps.put("service.exported.type", TestService.class);
        ServiceRegistration tsreg = context.registerService(RemoteServiceFactory.class.getName(), ts, tsProps);

        Long tsID = (Long) tsreg.getReference().getProperty(Constants.SERVICE_ID);
        ts.setServiceID(tsID);

        Dictionary<String, Object> fsProps = new Hashtable<String, Object>();
        fsProps.put(FrameworkNodeAddition.SERVICE_VARIABLES_KEY, Collections.singleton("remaining.invocations"));
        fsProps.put(FrameworkNodeAddition.SERVICE_IDS_KEY, tsID);
        context.registerService(FrameworkNodeAddition.class.getName(), ts, fsProps);

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
