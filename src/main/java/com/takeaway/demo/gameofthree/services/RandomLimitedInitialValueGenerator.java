package com.takeaway.demo.gameofthree.services;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * RandomLimitedInitialValueGenerator.
 *
 * @author Andrey Arefyev
 */
@Service
public class RandomLimitedInitialValueGenerator implements InitialValueGenerator {
    private final int topGameNumber;

    public RandomLimitedInitialValueGenerator(@Value("${game.topNumber:1000}") Integer topGameNumber) {this.topGameNumber = topGameNumber;}

    @Override
    public int generate() {
        return RandomUtils.nextInt(topGameNumber / 2, topGameNumber);
    }
}