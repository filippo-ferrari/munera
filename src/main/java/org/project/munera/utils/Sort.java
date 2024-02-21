package org.project.munera.utils;

import lombok.*;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class Sort
{
    private String field;

    private Sort.Direction direction;

    public org.springframework.data.domain.Sort sort()
    {
        if (Objects.isNull(field)) return org.springframework.data.domain.Sort.unsorted();
        return org.springframework.data.domain.Sort.by(this.direction, this.field);
    }

    public boolean isEmpty()
    {
        return Objects.isNull(this.field);
    }

    public boolean isAsc()
    {
        return org.springframework.data.domain.Sort.Direction.ASC.equals(this.direction);
    }

    public static Sort of(final String queryString)
    {
        if (!StringUtils.hasText(queryString)) return new Sort();
        var parts = queryString.split(",");
        if (parts.length == 1) parts = new String[] { parts[0], "asc" };
        return new Sort(parts[0], org.springframework.data.domain.Sort.Direction.fromString(parts[1]));
    }

    public static Sort of(final @NonNull String field, final @NonNull org.springframework.data.domain.Sort.Direction direction)
    {
        return new Sort(field, direction);
    }
}
