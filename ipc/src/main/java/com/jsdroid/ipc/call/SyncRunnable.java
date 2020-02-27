package com.jsdroid.ipc.call;

public class SyncRunnable implements Runnable {
    private final Runnable mTarget;
    private Throwable err;
    private boolean mComplete;

    public SyncRunnable(Runnable target) {
        mTarget = target;
    }

    public void run() {
        try {
            mTarget.run();
        } catch (Throwable e) {
            this.err = e;
        }
        synchronized (this) {
            mComplete = true;
            notifyAll();
        }
    }

    public void sync() throws Throwable {
        synchronized (this) {
            while (!mComplete) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }
        if (err != null) {
            throw err;
        }
    }

    public void sync(int time) {
        synchronized (this) {
            if (!mComplete) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
