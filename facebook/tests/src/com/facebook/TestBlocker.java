/**
 * Copyright 2012 Facebook
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook;

import android.os.HandlerThread;

public class TestBlocker extends HandlerThread {
    private boolean looperPrepared = false;
    private Throwable exception;
    private int signals;

    private TestBlocker() {
        super("TestBlocker");
    }

    public synchronized static TestBlocker createTestBlocker() {
        TestBlocker blocker = new TestBlocker();
        blocker.start();

        // Wait until we have a Looper.
        synchronized (blocker) {
            while (!blocker.looperPrepared) {
                try {
                    blocker.wait();
                } catch (InterruptedException e) {
                }
            }
        }

        return blocker;
    }

    @Override
    public void run() {
        try {
            super.run();
        } catch (Throwable e) {
            setException(e);
        }
        synchronized (this) {
            notifyAll();
        }
    }

    public void assertSuccess() throws Throwable {
        Throwable e = getException();
        if (e != null) {
            throw e;
        }
    }

    public synchronized void signal() {
        ++signals;
        notifyAll();
    }

    // Call reset before making any calls which could post operations to this thread, or the signal count will
    // not be right.
    public synchronized void reset() {
        signals = 0;
        notifyAll();
    }

    public void waitForSignals(int numSignals) throws Throwable {
        // Make sure we aren't sitting on an unhandled exception before we even start, because that means our
        // thread isn't around anymore.
        assertSuccess();

        // TODO port: allow timeout to be specified

        synchronized (this) {
            while (exception == null && signals < numSignals) {
                wait();
            }
        }
    }

    public void waitForSignalsAndAssertSuccess(int numSignals) throws Throwable {
        waitForSignals(numSignals);
        assertSuccess();
    }

    private synchronized Throwable getException() {
        return exception;
    }

    private synchronized void setException(Throwable e) {
        exception = e;
    }

    @Override
    protected void onLooperPrepared() {
        synchronized (this) {
            looperPrepared = true;
            notifyAll();
        }
    }
}