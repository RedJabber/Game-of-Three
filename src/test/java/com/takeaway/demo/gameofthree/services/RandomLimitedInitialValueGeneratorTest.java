package com.takeaway.demo.gameofthree.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

class RandomLimitedInitialValueGeneratorTest {

    @Test
    void generatesRandomly() {
        InitialValueGenerator valueGenerator = new RandomLimitedInitialValueGenerator(100);
        assertThat(valueGenerator.generate())
                .isNotEqualTo(valueGenerator.generate());
    }
}