import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import product.Product;
import product.ProductDao;
import shopping.BuyException;
import shopping.Cart;
import shopping.ShoppingService;
import shopping.ShoppingServiceImpl;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ShoppingServiceTest {
    private final ProductDao productDAO = Mockito.mock(ProductDao.class);
    private final ShoppingService shoppingService = new ShoppingServiceImpl(productDAO);
    private final Cart cart = Mockito.mock(Cart.class);

    /**
     * Тестирование получения всех продуктов
     */
    @Test
    public void testGetAllProducts() {
        shoppingService.getAllProducts();
        verify(productDAO).getAll();
    }

    /**
     * Тестирование получения продукта по имени
     */
    @Test
    public void testGetProductByName() {
        shoppingService.getProductByName("Яблоко");
        verify(productDAO).getByName("Яблоко");
    }

    /**
     * Тестирование невозможности покупки, по причине пустой корзины
     */
    @Test
    public void testNotBuyWithEmptyCart() throws BuyException {
        when(cart.getProducts()).thenReturn(new HashMap<>());

        Assertions.assertFalse(shoppingService.buy(cart));
        verify(productDAO, never()).save(any());
    }

    /**
     * Тестирование невозможности покупки, по причине недостаточного количества продукта на складе
     */
    @Test
    public void testInvalidateBuy() {
        Product testProduct = new Product();
        testProduct.addCount(1);
        Mockito.when(cart.getProducts()).thenReturn(Map.of(testProduct, 2));

        Assertions.assertThrows(BuyException.class, () -> shoppingService.buy(cart));
    }

    /**
     * Тестирование логики покупки
     * кол-во товара на складе должно уменьшиться
     */
    @Test
    public void testValidateBuy() {
        Product testProduct = new Product();
        testProduct.addCount(5);
        Mockito.when(cart.getProducts()).thenReturn(Map.of(testProduct, 5));
        try {
            shoppingService.buy(cart);
        } catch (BuyException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(0, testProduct.getCount());
    }

    /**
     * Тестирование покупки последнего товара
     * Итог - покупка не совершена, хотя должна быть совершена.
     */
    @Test
    public void testBuyLastProduct() throws BuyException {
        Product testProduct = new Product();
        testProduct.addCount(1);
        Mockito.when(cart.getProducts()).thenReturn(Map.of(testProduct, 1));
        Assertions.assertEquals(0, testProduct.getCount());
    }

    /**
     * Тестирование покупки, если в корзине неверное количество товара
     * Итог - покупка совершена, хотя не должна.
     */
    @Test
    public void testIncorrectlyEnteredCountProduct() throws BuyException {
        Product testProduct = new Product();
        testProduct.addCount(5);
        Mockito.when(cart.getProducts()).thenReturn(Map.of(testProduct, -5));
        Assertions.assertFalse(shoppingService.buy(cart));
    }

}
