package com.core.common.context;

import com.core.common.model.metadata.TrackingData;
import org.springframework.core.NamedInheritableThreadLocal;
import org.springframework.core.NamedThreadLocal;

public class TrackingDataContextHolder {

    private static final ThreadLocal<TrackingData> trackingDataHolder =
            new NamedThreadLocal<>("TrackingData Context");

    private static final ThreadLocal<TrackingData> inheritableGisHolder =
            new NamedInheritableThreadLocal<>("TrackingData Context");

    /**
     * Reset the TrackingData for the current thread.
     */
    public static void resetTrackingData() {
        trackingDataHolder.remove();
    }

    /**
     * Bind the given TrackingData to the current thread,
     * <i>not</i> exposing it as inheritable for child threads.
     * @param trackingData the TrackingData to expose
     * @see #setTrackingData(TrackingData, boolean)
     */
    public static void setTrackingData(TrackingData trackingData) {
        setTrackingData(trackingData, false);
    }

    /**
     * Bind the given TrackingData to the current thread.
     * @param trackingData the TrackingData to expose,
     * or {@code null} to reset the thread-bound context
     * @param inheritable whether to expose the TrackingData as inheritable
     * for child threads (using an {@link InheritableThreadLocal})
     */
    public static void setTrackingData(TrackingData trackingData, boolean inheritable) {
        if (trackingData == null) {
            resetTrackingData();
        }
        else {
            if (inheritable) {
                inheritableGisHolder.set(trackingData);
                trackingDataHolder.remove();
            }
            else {
                trackingDataHolder.set(trackingData);
                inheritableGisHolder.remove();
            }
        }
    }

    /**
     * Return the TrackingData currently bound to the thread.
     * @return the TrackingData currently bound to the thread,
     * or {@code null} if none bound
     */
    public static TrackingData getTrackingData() {
        TrackingData trackingData = trackingDataHolder.get();
        if (trackingData == null) {
            trackingData = inheritableGisHolder.get();
        }
        return trackingData;
    }
}
