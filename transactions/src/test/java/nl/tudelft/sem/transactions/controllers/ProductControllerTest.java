package nl.tudelft.sem.transactions.controllers;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;


@SuppressWarnings("PMD")
class ProductControllerTest {
     private Product product;
    // private ProductController productController;

    @Mock
    private transient ProductRepository productRepository;

    @InjectMocks
    private transient ProductController productController;

    @BeforeEach
    void setUp() {
        productController = spy(ProductController.class);
        MockitoAnnotations.initMocks(this);
        product = new Product("Milk", 14.0f, 12, "Chris");
    }

    @Test
    public void testGetAllProducts() {
        final List<Product> products = Arrays.asList(new Product("Butter",5,5,"kendra"));
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts("kendra");

        assertEquals(products.size(), result.size());
        assertEquals(products.get(0), result.get(0));
    }

    @Test
    public void testGetUserProducts(){
        final List<Product> products = Arrays.asList(new Product("Butter",5,5,"kendra"));

        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getUserProducts("kendra");

        assertEquals(products.size(), result.size());
        assertEquals(products.get(0), result.get(0));
    }

    @Test
    public void testDeleteProduct() {
        Optional<Product> product = Optional.ofNullable(null);
        when(productRepository.findById(7l)).thenReturn(product);

        productController.deleteProduct(7);

        verify(productRepository, times(0)).deleteById(1l);
    }

    @Test
    public void testDeleteProduct2(){
        when(productRepository.findById(7l)).thenReturn(Optional.ofNullable(product));
        doReturn(1).when(productRepository)
                .deleteProductById(7);

        boolean result = productController.deleteProduct(7);

        verify(productRepository)
                .deleteProductById(7);
        assertTrue(result);
    }

    @Test
    public void testAddProduct(){
        final Product newProduct = new Product("Butter",5,10,"kendra");

        boolean result = productController.addProduct(newProduct);

        verify(productRepository).save(newProduct);

        assertTrue(result);
    }

    @Test
    public void testAddProductException(){
        doThrow(DataIntegrityViolationException.class).when(productRepository)
                .save(product);
        assertFalse(productController.addProduct(product));

        verify(productRepository).save(product);
    }

    @Test
    public void testEditProduct(){
        doReturn(1).when(productRepository)
                .updateExistingProduct("Milk", "Chris", 14, 12,12,0,0L);


        boolean result = productController.editProduct(product);

        verify(productRepository)
                .updateExistingProduct("Milk", "Chris", 14, 12,12,0,0L);
        assertTrue(result);
    }

    @Test
    public void testSetExpired(){
        boolean result = productController.setExpired(product);
        verify(productController).setExpired(product);
        assertTrue(result);
    }

    @Test
    public void testIsExpiredFalse(){
        boolean result = productController.isExpired(product);
        verify(productController).isExpired(product);
        assertFalse(result);
    }

    @Test
    public void testIsExpiredTrue(){
        product.setExpired(1);
        boolean result = productController.isExpired(product);
        verify(productController).isExpired(product);
        assertTrue(result);
    }

    @Test
    public void testDeleteExpired(){
        product.setExpired(1);
        when(productRepository.findById(7l)).thenReturn(Optional.ofNullable(product));

        boolean result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertTrue(result);
    }

    @Test
    public void testDeleteExpired2(){
        product.setExpired(0);
        when(productRepository.findById(7l)).thenReturn(Optional.ofNullable(product));

        boolean result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertFalse(result);
    }

}