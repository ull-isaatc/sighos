package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import simkit.random.DiscreteRandomVariate;
import simkit.random.RandomVariateFactory;

public class TestRandomVariates {
    @Test
    void testBernoulli() {
        final DiscreteRandomVariate rnd = (DiscreteRandomVariate) RandomVariateFactory.getInstance("BernoulliVariate", 0.5);
        for (int i = 0; i < 100; i++) {
            int value = rnd.generateInt();
            assertTrue(value == 0 || value == 1);
        }
    }
}
