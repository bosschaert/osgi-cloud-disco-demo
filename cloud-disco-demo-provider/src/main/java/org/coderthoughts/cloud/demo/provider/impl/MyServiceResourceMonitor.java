package org.coderthoughts.cloud.demo.provider.impl;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.resource.ResourceContext;
import org.osgi.service.resource.ResourceEvent;
import org.osgi.service.resource.ResourceListener;
import org.osgi.service.resource.ResourceMonitor;
import org.osgi.service.resource.ResourceMonitorException;
import org.osgi.service.resource.ResourceThreshold;
import org.osgi.service.resource.ThresholdException;

public class MyServiceResourceMonitor implements ResourceMonitor {
    private final BundleContext bundleContext;
    private final ResourceContext resourceContext;
    private int usage;

    MyServiceResourceMonitor(BundleContext bc, ResourceContext rc) {
        bundleContext = bc;
        resourceContext = rc;
    }

    @Override
    public ResourceContext getContext() {
        return resourceContext;
    }

    @Override
    public String getResourceType() {
        // TODO Auto-generated method stub
        return "My Service Invocation usage";
    }

    @Override
    public void delete() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isDeleted() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void enable() throws ResourceMonitorException, IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public void disable() throws IllegalStateException {
        // TODO Auto-generated method stub

    }

    @Override
    public Comparable getUsage() throws IllegalStateException {
        return usage;
    }

    @Override
    public ResourceThreshold getLowerThreshold() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResourceThreshold getUpperThreshold() {
        return new ResourceThreshold() {

            @Override
            public void setWarningThreshold(Comparable value) {
                // TODO Auto-generated method stub

            }

            @Override
            public void setErrorThreshold(Comparable value) {
                // TODO Auto-generated method stub

            }

            @Override
            public boolean isUpper() {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public Comparable getWarningThreshold() {
                return 3;
            }

            @Override
            public int getState() {
                // TODO Auto-generated method stub
                return 0;
            }

            @Override
            public Comparable getErrorThreshold() {
                return 5;
            }
        };
    }

    @Override
    public void setLowerThreshold(ResourceThreshold lowerThreshold) throws ThresholdException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setUpperThreshold(ResourceThreshold upperThreshold) throws ThresholdException {
        // TODO Auto-generated method stub

    }

    @Override
    public long getSamplingPeriod() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getMonitoredPeriod() {
        // TODO Auto-generated method stub
        return 0;
    }

    void updateUsage(int value) {
        usage = value;
        Integer boxed = value;

        if (boxed.equals(getUpperThreshold().getWarningThreshold())) {
            sendEvent(ResourceEvent.WARNING, value);
        } else if (boxed.equals(getUpperThreshold().getErrorThreshold())) {
            sendEvent(ResourceEvent.ERROR, value);
        }
    }

    private void sendEvent(int type, int value) {
        ResourceEvent event = new ResourceEvent(type, getResourceType(), value, value, true);

        try {
            ServiceReference[] refs = bundleContext.getServiceReferences(ResourceListener.class.getName(), null);
            if (refs == null)
                return;

            for (ServiceReference ref : refs) {
                Object obj = bundleContext.getService(ref);
                if (obj instanceof ResourceListener) {
                    ((ResourceListener) obj).resourceEvent(event);
                }
            }
        } catch (InvalidSyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
