import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Тестирование класса {@link CustomerService}
 * @author Пыжьянов Вячеслав
 */
@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    private final CustomerDao customerDaoMock = Mockito.mock(CustomerDao.class);

    private final CustomerService customerService =
            new CustomerService(customerDaoMock);

    /**
     * Тестирование добавления покупателя
     */
    @Test
    public void testAddCustomer() {
        when(customerDaoMock.save(any(Customer.class)))
                .thenReturn(Boolean.TRUE);

        Customer customer = new Customer(0, "11-11-11");

        Assertions.assertTrue(customerService.addCustomer(customer));

        verify(customerDaoMock, times(1))
                .exists(anyString());
        verify(customerDaoMock, never())
                .delete(any(Customer.class));
    }

    /**
     * Тестирование отсутствия сохранения при добавлении покупателя с таким же телефоном
     */
    @Test
    public void testNotSaveCustomerWithSamePhone() {
        when(customerDaoMock.exists(any(String.class))).thenReturn(Boolean.TRUE);

        Customer customer = new Customer(0, "11-11-11");
        Assertions.assertFalse(customerService.addCustomer(customer));
    }

    /**
     * Использование класса Answer, для установки id
     */
    @Test
    public void testAddCustomerWithId() {

        // Using Answer to set an id to the customer which is passed in as a parameter to the mock method.
        when(customerDaoMock.save(any(Customer.class)))
                .thenAnswer((Answer<Boolean>) invocation -> {

            Object[] arguments = invocation.getArguments();

            if (arguments != null && arguments.length > 0 && arguments[0] != null){

                Customer customer = (Customer) arguments[0];
                customer.setId(1);

                return Boolean.TRUE;
            }

            return Boolean.FALSE;
        });

        Customer customer = new Customer(0, "11-11-11");

        Assertions.assertTrue(customerService.addCustomer(customer));
        Assertions.assertTrue(customer.getId() > 0);

    }

    /**
     * Кинуть исключение из mock объекта
     */
    @Test
    public void testAddCustomerThrowsException() {
        when(customerDaoMock.save(any(Customer.class)))
                .thenThrow(RuntimeException.class);

        Assertions.assertThrows(RuntimeException.class, () -> {
            Customer customer = new Customer(0, "11-11-11");
            customerService.addCustomer(customer);
        });
    }
}