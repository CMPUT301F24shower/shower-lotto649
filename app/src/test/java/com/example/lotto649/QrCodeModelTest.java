package com.example.lotto649;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.lotto649.Models.QrCodeModel;

public class QrCodeModelTest {

    /**
     * Tests the generateHash method to verify that it generates the correct SHA-256 hash
     * for a known input.
     */
    @Test
    public void testGenerateHash() {
        // Known input and its expected SHA-256 hash output
        String input = "TestInput";
        String expectedHash = "4943171c2ca244a3b1d73df02f1a3c1cfa7330833c0e0cf58d0b2c617ac885b1";

        // Generate the hash using the generateHash method
        String generatedHash = QrCodeModel.generateHash(input);

        // Assert that the generated hash matches the expected hash
        assertEquals("Generated hash should match the expected hash", expectedHash, generatedHash);
    }

    /**
     * Tests the generateHash method for edge cases with an empty string input.
     */
    @Test
    public void testGenerateHashWithEmptyInput() {
        // Expected SHA-256 hash for an empty input string
        String emptyInput = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        // Generate the hash using the generateHash method
        String generatedHash = QrCodeModel.generateHash(emptyInput);

        // Assert that the generated hash matches the expected hash
        assertEquals("Generated hash for empty input should match the expected hash", expectedHash, generatedHash);
    }
}
