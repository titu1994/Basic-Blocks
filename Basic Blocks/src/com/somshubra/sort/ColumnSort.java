package com.somshubra.sort;

import java.util.Comparator;

/**
 * Created by Yue on 01-May-16.
 */
public class ColumnSort implements Comparator<Object[]> {

    public interface TypeConverter {
        Comparable convert(Object o);
    }

    private static final TypeConverter DEFAULT_CONVERTER = o -> (Comparable) o;

    private final int columnIndex;
    private final boolean descending;
    private final TypeConverter converter;

    public ColumnSort(int columnSortIndex) {
        this(columnSortIndex, false, DEFAULT_CONVERTER);
    }

    public ColumnSort(int columnSortIndex, boolean descending) {
        this(columnSortIndex, descending, DEFAULT_CONVERTER);
    }

    public ColumnSort(int columnSortIndex, boolean descending, TypeConverter converter) {
        this.columnIndex = columnSortIndex;
        this.descending = descending;
        this.converter = converter;
    }

    @Override
    public int compare(Object[] o1, Object[] o2) {
        Comparable c1 = converter.convert(o1[columnIndex]);
        Comparable c2 = converter.convert(o2[columnIndex]);
        int cmp = c1.compareTo(c2);
        return descending ? -cmp : cmp;
    }

}
