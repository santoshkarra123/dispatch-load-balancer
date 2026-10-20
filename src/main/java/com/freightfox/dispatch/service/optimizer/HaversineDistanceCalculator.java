
package com.freightfox.dispatch.service.optimizer;

public final class HaversineDistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private HaversineDistanceCalculator() {
        // Prevent unnecessary object creation.
    }

    public static double calculateDistanceKm(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        validateCoordinates(latitude1, longitude1);
        validateCoordinates(latitude2, longitude2);

        double lat1 = Math.toRadians(latitude1);
        double lat2 = Math.toRadians(latitude2);

        double deltaLat = Math.toRadians(latitude2 - latitude1);
        double deltaLon = Math.toRadians(longitude2 - longitude1);

        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

        // Protect against floating-point rounding errors.
        a = Math.max(0.0, Math.min(1.0, a));

        double c = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1.0 - a));

        return EARTH_RADIUS_KM * c;
    }

    private static void validateCoordinates(
            double latitude, double longitude) {

        if (!Double.isFinite(latitude)
                || !Double.isFinite(longitude)) {
            throw new IllegalArgumentException(
                    "Coordinates must be finite numbers.");
        }

        if (latitude < -90.0 || latitude > 90.0) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90.");
        }

        if (longitude < -180.0 || longitude > 180.0) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180.");
        }
    }
}
