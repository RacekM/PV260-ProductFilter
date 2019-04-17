package cz.muni.fi.pv260.productfilter;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.CatchException.verifyException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AtLeastNOfFilterTest {

    @Test
    public void atLeastNOFilterThrowsIllegalArgumentException() throws Exception {
        verifyException(() -> new AtLeastNOfFilter(0, mock(Filter.class)));
        assertThat((Exception) caughtException())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void atLeastNOFilterThrowsFilterNeverSucceeds() throws Exception {
        verifyException(() -> new AtLeastNOfFilter(10, mock(Filter.class)));
        assertThat((Exception) caughtException())
                .isInstanceOf(FilterNeverSucceeds.class);
    }

    public static Filter[] prepareFilters(int numberOfFilters, int passingFilters) {
        List<Filter> filters = new ArrayList<>();

        for (int i = 0; i < numberOfFilters; i++) {
            Filter filter = mock(Filter.class);
            when(filter.passes(any())).thenReturn(passingFilters-- > 0);
            filters.add(filter);
        }
        return filters.toArray(new Filter[0]);

    }

    @Test
    public void passIfNFilterPasses() {
        String testedInput = "TEST";
        Filter[] filters = prepareFilters(4, 3);

        AtLeastNOfFilter filter = new AtLeastNOfFilter(3, filters);

        assertTrue(filter.passes(testedInput));
        for (Filter f : filters) {
            verify(f, times(1)).passes(testedInput);
        }
    }

    @Test
    public void failIfN_1FilterPasses() {
        String testedInput = "TEST";
        Filter[] filters = prepareFilters(4, 3);
        AtLeastNOfFilter filter = new AtLeastNOfFilter(4, filters);

        assertFalse(filter.passes(testedInput));
        for (Filter f : filters) {
            verify(f, times(1)).passes(testedInput);
        }
    }

}