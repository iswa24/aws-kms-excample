package com.example.demo.util;

import java.util.Objects;

public class KmsTextEncryptorOptions {

    private static final OutputMode DEFAULT_OUTPUT_MODE = OutputMode.PLAIN;

    private final OutputMode outputMode;

    KmsTextEncryptorOptions(OutputMode outputMode) {
        this.outputMode = outputMode == null ? DEFAULT_OUTPUT_MODE : outputMode;
    }

    OutputMode getOutputMode() {
        return outputMode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KmsTextEncryptorOptions that = (KmsTextEncryptorOptions) o;
        return outputMode == that.outputMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(outputMode);
    }
}