package dev.akatriggered.cache;

import dev.akatriggered.util.datastructure.EvictingList;
import org.jetbrains.annotations.Nullable;

public final class OptOutCache {
    private static final int MAX_SERVERS = 10;
    private final EvictingList<String> optedOutServers = new EvictingList<>(MAX_SERVERS);
    private final EvictingList<String> notifiedServers = new EvictingList<>(MAX_SERVERS);
    private volatile boolean optedOut;

    public void markOptedOut(@Nullable String serverKey) {
        if (serverKey == null) return;
        synchronized (this.optedOutServers) {
            if (!this.optedOutServers.contains(serverKey)) {
                this.optedOutServers.add(serverKey);
            }
        }
        this.optedOut = true;
    }

    public boolean isServerOptedOut(@Nullable String serverKey) {
        if (serverKey == null) return false;
        synchronized (this.optedOutServers) {
            return this.optedOutServers.contains(serverKey);
        }
    }

    public boolean hasNotified(@Nullable String serverKey) {
        if (serverKey == null) return false;
        synchronized (this.notifiedServers) {
            return this.notifiedServers.contains(serverKey);
        }
    }

    public void markNotified(@Nullable String serverKey) {
        if (serverKey == null) return;
        synchronized (this.notifiedServers) {
            if (!this.notifiedServers.contains(serverKey)) {
                this.notifiedServers.add(serverKey);
            }
        }
    }

    public void clearCurrentSession() {
        this.optedOut = false;
    }

    public boolean isOptedOut() {
        return this.optedOut;
    }

    public void setOptedOut(boolean optedOut) {
        this.optedOut = optedOut;
    }
}
