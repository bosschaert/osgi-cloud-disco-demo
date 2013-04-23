package org.coderthoughts.cloud.demo.provider.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cxf.dosgi.dsw.ClientInfo;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
import org.coderthoughts.cloud.demo.api.TestService;
import org.coderthoughts.cloud.framework.service.api.FrameworkNodeAddition;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class TestServiceRSF implements RemoteServiceInvocationHandler<TestService>, FrameworkNodeAddition {
    private static final int MAX_INVOCATIONS = 5;
    private final BundleContext bundleContext;
    private final ConcurrentMap<String, TestService> services = new ConcurrentHashMap<String, TestService>();
    private final ConcurrentMap<String, AtomicInteger> invocationCount = new ConcurrentHashMap<String, AtomicInteger>();
    private long serviceID;

    public TestServiceRSF(BundleContext bc) {
        bundleContext = bc;
    }

    void setServiceID(long id) {
        serviceID = id;
    }

    @Override
    public Object invoke(ClientInfo client, ServiceReference reference, Method method, Object[] args) {
        return null;
    }

    /*
    @Override
    public TestService getService(ClientInfo clientInfo, ServiceReference reference, Method method, Object[] args) {
        // This assumes that getService/ungetService is called for every remote invocation
        // if that doesn't happen the same can be achieved by using a proxy.
        AtomicInteger count = getCount(clientInfo.getHostIPAddress());
        int amount = count.incrementAndGet();
        if (amount > MAX_INVOCATIONS)
            throw new InvocationsExhaustedException("Maximum invocations reached for: " + clientInfo);

        return getService(clientInfo.getHostIPAddress());
    }

    @Override
    public void ungetService(ClientInfo clientIP, ServiceReference reference, TestService service, Method method, Object[] args, Object rv) {
        // Nothing to do
    } */

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

    @Override
    public String getFrameworkVariable(String name, ClientInfo client) {
        throw new UnsupportedOperationException();
    }

    /*
    @Override
    public String getServiceVariable(long id, String name, ClientInfo client) {
        if (serviceID != id) {
            throw new IllegalArgumentException("Service ID: " + id);
        }
        if ("remaining.invocations".equals(name)) {
            AtomicInteger count = getCount(client.getHostIPAddress());
            return "" + (MAX_INVOCATIONS - count.get());
        }
        throw new IllegalArgumentException(name);
    }
    */
}
