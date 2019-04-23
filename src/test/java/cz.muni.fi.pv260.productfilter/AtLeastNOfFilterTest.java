package cz.muni.fi.pv260.productfilter;


import com.googlecode.zohhak.api.TestWith;
import com.googlecode.zohhak.api.runners.ZohhakRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(ZohhakRunner.class)
public class AtLeastNOfFilterTest {

    @Test
    public void atLeastNOFilterThrowsIllegalArgumentExceptionZeroN() throws Exception {
        verifyException(() -> new AtLeastNOfFilter<Object>(0, mock(Filter.class)));
        assertThat((Exception) caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void atLeastNOFilterThrowsIllegalArgumentExceptionNegativeN() throws Exception {
        verifyException(() -> new AtLeastNOfFilter<Object>(-1, mock(Filter.class)));
        assertThat((Exception) caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void atLeastNOFilterThrowsFilterNeverSucceedsByOne() throws Exception {
        verifyException(() -> new AtLeastNOfFilter<Object>(2, mock(Filter.class)));
        assertThat((Exception) caughtException())
                .isInstanceOf(FilterNeverSucceeds.class);
    }

    @TestWith({
            "3, 3",
            "1, 4"
    })
    public void passIfAtLeastNFiltersPasses(final int atLeastPassingFilter, final int numberOfPassingFilters) {
        String testedInput = "TEST";
        Filter<Object>[] filters = prepareFilters(4, numberOfPassingFilters);

        AtLeastNOfFilter<Object> filter = new AtLeastNOfFilter<>(atLeastPassingFilter, filters);

        assertTrue(filter.passes(testedInput));

        for (Filter<Object> f : filters) {
            verify(f, times(1)).passes(testedInput);
        }

        int numberOfPassedFilters = 0;
        for (Filter<Object> f : filters) {
            if (f.passes(testedInput)) {
                numberOfPassedFilters++;
            }
        }
        assertEquals(numberOfPassingFilters, numberOfPassedFilters);
    }


    @TestWith({
            "4,3",
            "3,1"
    })
    public void failIfN_1FilterPasses(final int atLeastPassingFilter, final int numberOfPassingFilters) {
        String testedInput = "TEST";
        Filter<Object>[] filters = prepareFilters(5, numberOfPassingFilters);
        AtLeastNOfFilter<Object> filter = new AtLeastNOfFilter<Object>(atLeastPassingFilter, filters);

        assertFalse(filter.passes(testedInput));
        for (Filter<Object> f : filters) {
            verify(f, times(1)).passes(testedInput);
        }

        int numberOfPassedFilters = 0;
        for (Filter<Object> f : filters) {
            if (f.passes(testedInput)) {
                numberOfPassedFilters++;
            }
        }
        assertEquals(numberOfPassingFilters, numberOfPassedFilters);

    }

    private static Filter<Object>[] prepareFilters(int numberOfFilters, int passingFilters) {
        List<Filter<Object>> filters = new ArrayList<>();

        for (int i = 0; i < numberOfFilters; i++) {
            Filter<Object> filter = mock(Filter.class);
            when(filter.passes(any())).thenReturn(passingFilters-- > 0);
            filters.add(filter);
        }
        return filters.toArray(new Filter[0]);

    }


}