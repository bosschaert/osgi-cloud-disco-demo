package org.coderthoughts.cloud.demo.provider.impl;

import org.coderthoughts.cloud.demo.api.LongRunningService;

public class LongRunningServiceImpl implements LongRunningService {
    @Override
    public int invoke(int delay) {
        System.out.println("Received message, delaying by " + delay + " (ms)");
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 51;
    }
}
