package cz.muni.fi.pv260.productfilter;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

    @Captor
    private ArgumentCaptor<List<Product>> captor;

    @Test
    public void outputContainsAllProductsFulfillingFilters() throws ObtainFailedException {
        final int filterPrice = 31;
        Input input = mock(Input.class);
        when(input.obtainProducts()).thenReturn(prepareProducts());
        Output output = mock(Output.class);
        Controller controller = new Controller(input, output, mock(Logger.class));
        controller.select(new PriceLessThanFilter(new BigDecimal(filterPrice)));

        verify(output, times(1)).postSelectedProducts(captor.capture());
        List<Product> returnedProducts = captor.getValue();
        assertThat(returnedProducts).are(new Condition<>(p -> p.getPrice().compareTo(new BigDecimal(filterPrice)) <= 0, "all filtered products has price lower or equal to " + filterPrice));
    }

    @Test
    public void loggerLogsSuccessInCorrectFormat() throws ObtainFailedException {
        final int filterPrice = 31;
        Input input = mock(Input.class);
        when(input.obtainProducts()).thenReturn(prepareProducts());
        Output output = new OutputStub();
        Logger logger = mock(Logger.class);
        Controller controller = new Controller(input, output, logger);
        controller.select(new PriceLessThanFilter(new BigDecimal(filterPrice)));

        verify(logger).setLevel("INFO");
        String expectedOutput = "Successfully selected 3 out of 4 available products.";
        verify(logger).log(Controller.TAG_CONTROLLER, expectedOutput);
    }

    @Test
    public void controllerLogsException() throws ObtainFailedException {
        Input input = mock(Input.class);
        when(input.obtainProducts()).thenThrow(new ObtainFailedException());
        Output output = new OutputStub();
        Logger logger = mock(Logger.class);
        Controller controller = new Controller(input, output, logger);
        controller.select(null);

        verify(logger).setLevel("ERROR");
        String expectedOutput = "Filter procedure failed with exception: " + ObtainFailedException.class.getName();
        verify(logger).log(Controller.TAG_CONTROLLER, expectedOutput);

    }

    @Test
    public void nothingIsReturnToOutputInCaseOfException() throws ObtainFailedException {
        Input input = mock(Input.class);
        when(input.obtainProducts()).thenThrow(new ObtainFailedException());
        Output output = mock(Output.class);
        Logger logger = mock(Logger.class);
        Controller controller = new Controller(input, output, logger);
        controller.select(null);

        verify(output, never()).postSelectedProducts(any());
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
