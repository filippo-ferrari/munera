package org.project.munera.utils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Query
{
    public static final String GLOBAL_FILTER = "global";
    private static final String QUERY_FILTERS_DIVIDER = ",";

    private Map<String, Filter> filtersMap = new HashMap<>();

    private Sort sort;

    protected Query(final @NonNull Query query)
    {
        this.filtersMap = new HashMap<>(query.filtersMap);
        if (Objects.isNull(query.sort)) return;
        this.sort = Sort.of(query.sort.getField(), query.sort.getDirection());
    }

    public boolean isEmpty()
    {
        return (Objects.isNull(this.filtersMap) || this.filtersMap.isEmpty()) && this.hasNoSorting();
    }

    public boolean hasNoFilters(final @NonNull String key)
    {
        return !this.filtersMap.containsKey(key);
    }

    public boolean hasNoSorting()
    {
        return Objects.isNull(this.sort) || this.sort.isEmpty();
    }

    public Collection<Filter> getFilters()
    {
        return this.filtersMap.values();
    }

    public Optional<Filter> getFilter(final @NonNull String key)
    {
        return Optional.ofNullable(this.filtersMap.get(key));
    }

    public void removeFilter(final @NonNull String key)
    {
        this.filtersMap.remove(key);
    }

    public String getGlobalQuery()
    {
        final var optional = this.getFilter(GLOBAL_FILTER);
        return optional.isPresent() ? optional.get().getValue() : "";
    }

    public void addFilter(final @NonNull String field, final @NonNull Object... value)
    {
        this.filtersMap.put(field, Filter.of(field, value));
    }

    public void addFilter(final @NonNull Filter filter)
    {
        this.filtersMap.put(filter.getField(), filter);
    }

    @Override
    public String toString()
    {
        final var builder = new StringBuilder();
        final var queries = this.filtersMap.values().stream().map(Filter::toString).toArray(String[]::new);
        builder.append(String.join(QUERY_FILTERS_DIVIDER, queries));
        return builder.toString();
    }

    public static Query empty()
    {
        return new Query();
    }

    public static Query parse(final String query)
    {
        if (!StringUtils.hasText(query)) return Query.empty();
        final var elements = query.split(QUERY_FILTERS_DIVIDER);
        final var filters = Arrays.stream(elements)
                .filter(f -> f.contains(Filter.QUERY_KEY_VALUE_DIVIDER))
                .map(Filter::of)
                .collect(Collectors.toList());
        if (elements.length == 1 && !elements[0].contains(Filter.QUERY_KEY_VALUE_DIVIDER))
            filters.add(Filter.of("global:" + elements[0]));
        final var instance = Query.empty();
        filters.forEach(instance::addFilter);
        return instance;
    }

    public static Query withFilters(final @NonNull Filter... filter)
    {
        final var instance = Query.empty();
        Arrays.stream(filter).forEach(instance::addFilter);
        return instance;
    }

    public static Query withSort(final String query, final String sortQuery)
    {
        final var lazyQuery = Query.parse(query);
        lazyQuery.setSort(Sort.of(sortQuery));
        return lazyQuery;
    }

    public static Query clone(final @NonNull Query query)
    {
        return new Query(query);
    }
}
