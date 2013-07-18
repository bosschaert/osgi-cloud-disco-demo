package org.coderthoughts.cloud.demo.provider.impl;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.cxf.dosgi.dsw.ClientContext;
import org.apache.cxf.dosgi.dsw.RemoteServiceInvocationHandler;
import org.apache.cxf.dosgi.dsw.RemoteServiceMetadataHandler;
import org.coderthoughts.cloud.demo.api.TestService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class TestServiceInvocationHandler implements RemoteServiceInvocationHandler<TestService>, RemoteServiceMetadataHandler {
    private static final int MAX_INVOCATIONS = 5;
    private final BundleContext bundleContext;
    private final ConcurrentMap<String, TestService> services = new ConcurrentHashMap<String, TestService>();
    private final ConcurrentMap<String, AtomicInteger> invocationCount = new ConcurrentHashMap<String, AtomicInteger>();

    public TestServiceInvocationHandler(BundleContext bc) {
        bundleContext = bc;
    }

    @Override
    public Object invoke(ClientContext client, ServiceReference reference, Method method, Object[] args) {
        AtomicInteger count = getCount(client.getHostIPAddress());
        int amount = count.incrementAndGet();
        if (amount > MAX_INVOCATIONS)
            throw new InvocationsExhaustedException("Maximum invocations reached for: " + client);

        TestService svc = getService(client.getHostIPAddress());
        try {
            return method.invoke(svc, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String[] listServiceVariableNames(ClientContext client) {
        return new String [] {"remaining.invocations"};
    }

    @Override
    public String getServiceVariable(ClientContext client, String name) {
        if ("remaining.invocations".equals(name)) {
            AtomicInteger count = getCount(client.getHostIPAddress());
            return "" + (MAX_INVOCATIONS - count.get());
        }

        throw new IllegalArgumentException(name);
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
}
