package com.somshubra.sort;

import com.sun.istack.internal.NotNull;

import java.util.Comparator;

/**
 * Created by Yue on 01-May-16.
 *
 * ColumnComparator is a Comparator to sort a two dimensional array using one column as the key rather than the value in
 * each row.
 *
 * Usage:
 * Arrays.sort(data, new ColumnComparator(columnIndex)) -
 * If column contains a Comparable data type
 *
 * Arrays.sort(data, new ColumnComparator(columnIndex, desc)) -
 * If column contains a Comparable data type, and asc/desc order
 *
 * Arrays.sort(data, new ColumnComparator(columnIndex, desc, new TypeConverter())) -
 * If column does not contain a comparable data type, use type converter to convert the non-Comparable data type to
 * a Comparable data type before using it to sort.
 *
 * TypeConverter must be user defined in such cases, or simply ensure that the user defined data type implements
 * Comparable<T>
 */
public class ColumnComparator implements Comparator<Object[]> {

    /**
     * @author Yue
     *
     * TypeConverter is an interface in order to wrap a column of data type which does not inherintly support the
     * Comparable interface. Thus, user defined class objects which are in a column of data can be made Comparable using
     * this interface.
     */
    public interface TypeConverter {
        Comparable convert(Object o);
    }

    private static final TypeConverter DEFAULT_CONVERTER = o -> (Comparable) o;

    private final int columnIndex;
    private final boolean descending;
    private final TypeConverter converter;

    public ColumnComparator(int columnSortIndex) {
        this(columnSortIndex, false, DEFAULT_CONVERTER);
    }

    public ColumnComparator(int columnSortIndex, boolean descending) {
        this(columnSortIndex, descending, DEFAULT_CONVERTER);
    }

    public ColumnComparator(int columnSortIndex, boolean descending, @NotNull TypeConverter converter) {
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
