package dev.akatriggered.util;

public final class PerformanceGuard {
    private static final long WINDOW_NANOS = 1_000_000_000L;
    private static final long BREAK_ENTITY_COOLDOWN_NANOS = 46_000_000L;
    private static final long PLACE_BASE_INTERVAL_NANOS = 45_000_000L;
    private static final long PLACE_MIN_INTERVAL_FLOOR_NANOS = 33_000_000L;
    private static final long PLACE_MAX_PING_BOOST_NANOS = 14_000_000L;
    private static final long PLACE_MAX_BREAK_SYNC_BOOST_NANOS = 12_000_000L;
    private static final long PLACE_RECENT_BREAK_SYNC_BOOST_NANOS = 6_000_000L;
    private static final long PLACE_RECENT_BREAK_WINDOW_NANOS = 110_000_000L;
    private static final long PLACE_MIN_HARD_FLOOR_NANOS = 29_000_000L;
    private static final long BREAK_BASE_INTERVAL_NANOS = 92_000_000L;
    private static final long BREAK_MIN_INTERVAL_FLOOR_NANOS = 58_000_000L;
    private static final long BREAK_MIN_HARD_FLOOR_NANOS = 50_000_000L;
    private static final long BREAK_MAX_PING_BOOST_NANOS = 18_000_000L;
    private static final int PLACE_CPS_FOR_MAX_BREAK_SPEED = 20;
    private static final long PLACE_CPS_SAMPLE_FLOOR_NANOS = 220_000_000L;
    private static final int BREAK_CPS_FOR_MAX_PLACE_SYNC = 14;
    private static final long BREAK_CPS_SAMPLE_FLOOR_NANOS = 180_000_000L;
    private static final int PING_BOOST_START_MS = 35;
    private static final int PING_BOOST_FULL_MS = 240;
    private static final int PLACE_MAX_PING_BUDGET_BONUS = 4;
    private static final int PLACE_MAX_BREAK_BUDGET_BONUS = 6;
    private static final int BREAK_MAX_PING_BUDGET_BONUS = 4;

    private final int maxPlaceBoostsPerSecond;
    private final int maxBreakPredictionsPerSecond;
    private long placeWindowStartNanos;
    private int placeBoostsInWindow;
    private long placeLastGrantNanos;
    private long breakWindowStartNanos;
    private int breakPredictionsInWindow;
    private long breakLastGrantNanos;
    private int smoothedPingMillis;
    private int lastBrokenEntityId;
    private long lastBrokenEntityUntilNanos;

    public PerformanceGuard() {
        this(25, 26);
    }

    public PerformanceGuard(int maxPlaceBoostsPerSecond, int maxBreakPredictionsPerSecond) {
        this.maxPlaceBoostsPerSecond = Math.max(1, maxPlaceBoostsPerSecond);
        this.maxBreakPredictionsPerSecond = Math.max(1, maxBreakPredictionsPerSecond);
        this.smoothedPingMillis = 35;
        long now = System.nanoTime();
        this.placeWindowStartNanos = now;
        this.breakWindowStartNanos = now;
        this.lastBrokenEntityId = Integer.MIN_VALUE;
    }

    public synchronized void observeUseKeyState(boolean isDown) {}

    public synchronized void observePingMillis(int pingMillis) {
        if (pingMillis > 0) {
            int clamped = Math.min(450, pingMillis);
            this.smoothedPingMillis = (this.smoothedPingMillis * 3 + clamped) / 4;
        }
    }

    public synchronized boolean allowPlaceBoost() {
        long now = System.nanoTime();
        if (now - this.placeWindowStartNanos >= WINDOW_NANOS) {
            this.placeWindowStartNanos = now;
            this.placeBoostsInWindow = 0;
        }

        int pingBoostLevel = resolvePingBoostLevel();
        int breakActivityLevel = resolveBreakActivityLevel(now);
        long pingBoostNanos = PLACE_MAX_PING_BOOST_NANOS * pingBoostLevel / 100L;
        long breakSyncBoostNanos = PLACE_MAX_BREAK_SYNC_BOOST_NANOS * breakActivityLevel / 100L;
        long recentBreakBoostNanos = (now - this.breakLastGrantNanos <= PLACE_RECENT_BREAK_WINDOW_NANOS)
            ? PLACE_RECENT_BREAK_SYNC_BOOST_NANOS : 0L;
        long minInterval = Math.max(PLACE_MIN_HARD_FLOOR_NANOS,
            Math.max(PLACE_MIN_INTERVAL_FLOOR_NANOS, PLACE_BASE_INTERVAL_NANOS - pingBoostNanos)
                - breakSyncBoostNanos - recentBreakBoostNanos);

        if (this.placeLastGrantNanos != 0L && now - this.placeLastGrantNanos < minInterval) return false;

        int budget = this.maxPlaceBoostsPerSecond
            + PLACE_MAX_PING_BUDGET_BONUS * pingBoostLevel / 100
            + PLACE_MAX_BREAK_BUDGET_BONUS * breakActivityLevel / 100;
        if (this.placeBoostsInWindow >= budget) return false;

        this.placeBoostsInWindow++;
        this.placeLastGrantNanos = now;
        return true;
    }

    public synchronized boolean allowBreakPrediction(int entityId) {
        long now = System.nanoTime();
        if (entityId == this.lastBrokenEntityId && now < this.lastBrokenEntityUntilNanos) return false;

        if (now - this.breakWindowStartNanos >= WINDOW_NANOS) {
            this.breakWindowStartNanos = now;
            this.breakPredictionsInWindow = 0;
        }

        int pingBoostLevel = resolvePingBoostLevel();
        long breakMinInterval = resolveBreakMinIntervalFromPlaceCps(now, pingBoostLevel);
        if (this.breakLastGrantNanos != 0L && now - this.breakLastGrantNanos < breakMinInterval) return false;

        int budget = this.maxBreakPredictionsPerSecond + BREAK_MAX_PING_BUDGET_BONUS * pingBoostLevel / 100;
        if (this.breakPredictionsInWindow >= budget) return false;

        this.breakPredictionsInWindow++;
        this.breakLastGrantNanos = now;
        this.lastBrokenEntityId = entityId;
        this.lastBrokenEntityUntilNanos = now + BREAK_ENTITY_COOLDOWN_NANOS;
        return true;
    }

    private long resolveBreakMinIntervalFromPlaceCps(long now, int pingBoostLevel) {
        long pingBoostNanos = BREAK_MAX_PING_BOOST_NANOS * pingBoostLevel / 100L;
        long adaptiveBase = Math.max(BREAK_MIN_HARD_FLOOR_NANOS, BREAK_BASE_INTERVAL_NANOS - pingBoostNanos);
        long adaptiveFloor = Math.max(BREAK_MIN_HARD_FLOOR_NANOS, BREAK_MIN_INTERVAL_FLOOR_NANOS - pingBoostNanos * 4L / 5L);
        long placeElapsed = now - this.placeWindowStartNanos;
        if (placeElapsed > 0L && placeElapsed < WINDOW_NANOS && this.placeBoostsInWindow > 0) {
            long sampleWindow = Math.max(PLACE_CPS_SAMPLE_FLOOR_NANOS, placeElapsed);
            long placeCps = (long) this.placeBoostsInWindow * WINDOW_NANOS / sampleWindow;
            long cappedCps = Math.min(Math.max(0L, placeCps), PLACE_CPS_FOR_MAX_BREAK_SPEED);
            long speedupRange = Math.max(0L, adaptiveBase - adaptiveFloor);
            long speedup = speedupRange * cappedCps / PLACE_CPS_FOR_MAX_BREAK_SPEED;
            return Math.max(adaptiveFloor, adaptiveBase - speedup);
        }
        return adaptiveBase;
    }

    private int resolveBreakActivityLevel(long now) {
        long breakElapsed = now - this.breakWindowStartNanos;
        if (breakElapsed > 0L && breakElapsed < WINDOW_NANOS && this.breakPredictionsInWindow > 0) {
            long sampleWindow = Math.max(BREAK_CPS_SAMPLE_FLOOR_NANOS, breakElapsed);
            long breakCps = (long) this.breakPredictionsInWindow * WINDOW_NANOS / sampleWindow;
            long cappedCps = Math.min(Math.max(0L, breakCps), BREAK_CPS_FOR_MAX_PLACE_SYNC);
            return (int) (cappedCps * 100L / BREAK_CPS_FOR_MAX_PLACE_SYNC);
        }
        return 0;
    }

    private int resolvePingBoostLevel() {
        if (this.smoothedPingMillis <= PING_BOOST_START_MS) return 0;
        return this.smoothedPingMillis >= PING_BOOST_FULL_MS ? 100
            : (this.smoothedPingMillis - PING_BOOST_START_MS) * 100 / (PING_BOOST_FULL_MS - PING_BOOST_START_MS);
    }
}
