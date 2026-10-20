
package com.freightfox.dispatch.service.optimizer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HaversineDistanceCalculatorTest {

    @Test
    void sameCoordinatesShouldReturnZero() {
        double distance =
                HaversineDistanceCalculator.calculateDistanceKm(
                        17.3850, 78.4867,
                        17.3850, 78.4867);

        assertEquals(0.0, distance, 0.000001);
    }

    @Test
    void distanceShouldBeSymmetric() {
        double forward =
                HaversineDistanceCalculator.calculateDistanceKm(
                        17.3850, 78.4867,
                        16.5062, 80.6480);

        double reverse =
                HaversineDistanceCalculator.calculateDistanceKm(
                        16.5062, 80.6480,
                        17.3850, 78.4867);

        assertEquals(forward, reverse, 0.000001);
    }

    @Test
    void invalidLatitudeShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HaversineDistanceCalculator.calculateDistanceKm(
                        91.0, 78.4867,
                        17.3850, 78.4867));
    }

    @Test
    void invalidLongitudeShouldBeRejected() {
        assertThrows(
                IllegalArgumentException.class,
                () -> HaversineDistanceCalculator.calculateDistanceKm(
                        17.3850, 181.0,
                        17.3850, 78.4867));
    }
}
