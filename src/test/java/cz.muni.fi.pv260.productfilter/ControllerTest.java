package cz.muni.fi.pv260.productfilter;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ControllerTest {

    @Test
    public void outputContainsAllProductsFulfillingFilters() throws ObtainFailedException {
        int filterPrice = 31;
        Input input = mock(Input.class);
        when(input.obtainProducts()).thenReturn(prepareProducts());
        Output output = mock(Output.class);
        Controller controller = new Controller(input, output, new LoggerStub());

        controller.select(new PriceLessThanFilter(new BigDecimal(filterPrice)));
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(output, times(1)).postSelectedProducts(captor.capture());
        List<Product> returnedProducts = captor.getValue();
        assertThat(returnedProducts).are(new Condition<>(p -> p.getPrice().compareTo(new BigDecimal(filterPrice)) <= 0, "all filtered products has price lower or equal to " + filterPrice));

    }


    private Collection<Product> prepareProducts() {
        List<Product> products = new ArrayList<>();
        products.add(new Product(0, "Product 0", Color.BLACK, new BigDecimal(10)));
        products.add(new Product(1, "Product 1", Color.BLUE, new BigDecimal(20)));
        products.add(new Product(2, "Product 2", Color.GREEN, new BigDecimal(30)));
        products.add(new Product(3, "Product 0", Color.RED, new BigDecimal(40)));
        return products;
    }
}
