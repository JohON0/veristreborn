/*
 * Decompiled with CFR 0.153-SNAPSHOT (d6f6758-dirty).
 */
package im.expensive.command.api;

import im.expensive.command.interfaces.Parameters;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ParametersImpl
implements Parameters {
    private final String[] parameters;

    public ParametersImpl(String[] parameters) {
        this.parameters = parameters;
    }

    @Override
    public Optional<Integer> asInt(int index) {
        return Optional.ofNullable(this.getElementFromParametersOrNull(index, Integer::valueOf));
    }

    @Override
    public Optional<Float> asFloat(int index) {
        return Optional.ofNullable(this.getElementFromParametersOrNull(index, Float::valueOf));
    }

    @Override
    public Optional<Double> asDouble(int index) {
        return Optional.ofNullable(this.getElementFromParametersOrNull(index, Double::valueOf));
    }

    @Override
    public Optional<String> asString(int index) {
        return Optional.ofNullable(this.getElementFromParametersOrNull(index, String::valueOf));
    }

    @Override
    public String collectMessage(int startIndex) {
        return IntStream.range(startIndex, this.parameters.length).mapToObj(i -> this.asString(i).orElse("")).collect(Collectors.joining(" ")).trim();
    }

    private <T> T getElementFromParametersOrNull(int index, Function<String, T> mapper) {
        if (index >= this.parameters.length) {
            return null;
        }
        try {
            return mapper.apply(this.parameters[index]);
        } catch (Exception e) {
            return null;
        }
    }
}

