package org.project.munera.utils;

import lombok.Data;
import lombok.NonNull;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Data
public final class Filter
{
    public static final String QUERY_KEY_VALUE_DIVIDER = ":";
    public static final String QUERY_MULTI_VALUES_DIVIDER = ";";

    private String field;
    private String value;
    private List<String> values;

    private Filter(final String field, final String value)
    {
        this.field = field;
        this.value = value;
    }

    private Filter(final String field, final List<String> values)
    {
        this.field = field;
        this.values = values;
    }

    public String getValue()
    {
        if (Objects.nonNull(this.value)) return value;
        if (Objects.isNull(this.values) || this.values.isEmpty()) return "";
        return this.values.get(0);
    }

    public List<String> getValues()
    {
        return Objects.nonNull(values) ? values : List.of(Objects.requireNonNullElse(value, ""));
    }

    public boolean hasOneValue()
    {
        return this.getValues().size() == 1;
    }

    @Override
    public String toString()
    {
        final var bits = String.join(Filter.QUERY_MULTI_VALUES_DIVIDER, this.getValues());
        return this.getField() + QUERY_KEY_VALUE_DIVIDER + bits;
    }

    public static Filter of(final @NonNull String filter)
    {
        Assert.hasText(filter, "The filter must not be null or empty");
        final var splitterIndex = filter.indexOf(QUERY_KEY_VALUE_DIVIDER);
        if (splitterIndex == -1) throw new RuntimeException(); //TODO: create expection
        final var key = filter.substring(0, splitterIndex);
        final var value = filter.substring(splitterIndex + 1);
        Assert.hasText(key, "The filter does not contain a proper filter key. [" + key + "]");
        Assert.notNull(value, "The filter does not contain a proper filter value.");
        if (!value.contains(QUERY_MULTI_VALUES_DIVIDER)) return new Filter(key, value);
        return new Filter(key, (List.of(value.split(QUERY_MULTI_VALUES_DIVIDER))));
    }

    public static Filter of(final @NonNull String field, final @NonNull Object... value)
    {
        Assert.hasText(field, "the field must not be empty");
        final var values = Arrays.stream(value).map(v -> Objects.requireNonNullElse(v.toString(), "")).toList();
        return new Filter(field, values);
    }
}
