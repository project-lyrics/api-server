package com.projectlyrics.server.support.fixture;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseFixture {

    private static final AtomicLong id = new AtomicLong(1);

    protected static long getUniqueId() {
        return id.incrementAndGet();
    }
}
