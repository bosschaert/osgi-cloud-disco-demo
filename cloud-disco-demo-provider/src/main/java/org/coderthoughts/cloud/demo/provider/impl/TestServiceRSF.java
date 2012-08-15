package org.coderthoughts.cloud.demo.provider.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cxf.dosgi.dsw.RemoteServiceFactory;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.OSGiFramework;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.monitor.Monitorable;
import org.osgi.service.monitor.StatusVariable;

public class TestServiceRSF implements RemoteServiceFactory, Monitorable {
    private final BundleContext bundleContext;
    private final ConcurrentMap<String, TestService> services = new ConcurrentHashMap<String, TestService>();
    private final ConcurrentMap<String, AtomicInteger> invocationCount = new ConcurrentHashMap<String, AtomicInteger>();

    public TestServiceRSF(BundleContext bc) {
        bundleContext = bc;
    }

    @Override
    public Object getService(String clientIP, ServiceReference reference) {
        // This assumes that getService/ungetService is called for every remote invocation
        // if that doesn't happen the same can be achieved by using a proxy.
        AtomicInteger count = getCount(clientIP);
        int amount = count.incrementAndGet();
        if (amount > 100)
            throw new InvocationsExhaustedException("Maximum invocations reached for: " + clientIP);

        return getService(clientIP);
    }

    @Override
    public void ungetService(String clientIP, ServiceReference reference, Object service) {
        // Nothing to do
    }

    private TestService getService(String ipAddr) {
        TestService newSvc = new TestServiceImpl(bundleContext.getProperty("org.osgi.framework.uuid"));
        TestService oldSvc = services.putIfAbsent(ipAddr, newSvc);
        return oldSvc == null ? newSvc : oldSvc;
    }

    private AtomicInteger getCount(String ipAddr) {
        AtomicInteger newCnt = new AtomicInteger();
        AtomicInteger oldCnt = invocationCount.putIfAbsent(ipAddr, newCnt);
        return oldCnt == null ? newCnt : oldCnt;
    }

    static class InvocationsExhaustedException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        InvocationsExhaustedException(String reason) {
            super(reason);
        }
    }

    // Monitor Admin API
    @Override
    public String[] getStatusVariableNames() {
        List<String> vars = new ArrayList<String>();
        for (String ip : services.keySet()) {
            vars.add(OSGiFramework.SERVICE_STATUS_PREFIX + ip);
        }
        return vars.toArray(new String [] {});
    }

    @Override
    public StatusVariable getStatusVariable(String var) throws IllegalArgumentException {
        String ip = getIPAddress(var);
        AtomicInteger count = invocationCount.get(ip);

        String status;
        if (count == null) {
            status = OSGiFramework.SERVICE_STATUS_NOT_FOUND;
        } else {
            if (count.get() < 3)
                status = OSGiFramework.SERVICE_STATUS_OK;
            else
                status = OSGiFramework.SERVICE_STATUS_QUOTA_EXCEEDED;
        }
        return new StatusVariable(var, StatusVariable.CM_SI, status);
    }

    @Override
    public boolean notifiesOnChange(String id) throws IllegalArgumentException {
        return false;
    }

    @Override
    public boolean resetStatusVariable(String var) throws IllegalArgumentException {
        return invocationCount.remove(getIPAddress(var)) != null;
    }

    @Override
    public String getDescription(String var) throws IllegalArgumentException {
        return "The status of the " + TestService.class.getName() + " service for client " + getIPAddress(var);
    }

    private String getIPAddress(String var) {
        if (!var.startsWith(OSGiFramework.SERVICE_STATUS_PREFIX))
            throw new IllegalArgumentException("Not a valid status variable: " + var);

        String ip = var.substring(OSGiFramework.SERVICE_STATUS_PREFIX.length());
        return ip;
    }
}
