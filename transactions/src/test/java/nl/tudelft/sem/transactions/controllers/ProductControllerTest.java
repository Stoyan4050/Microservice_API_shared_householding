package nl.tudelft.sem.transactions.controllers;

import java.net.URI;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        final List<Product> products = Arrays.asList(new Product("Butter", 5, 5, "kendra"));
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts("kendra");

        verify(productRepository).findAll();
        verify(productController).getAllProducts("kendra");

        assertEquals(products.size(), result.size());
        assertEquals(products.get(0), result.get(0));
    }

    @Test
    public void testGetUserProducts() {
        final List<Product> products = Arrays.asList(new Product("Butter", 5, 5, "kendra"));

        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getUserProducts("kendra");

        verify(productRepository).findAll();
        verify(productController).getUserProducts("kendra");

        assertEquals(products.size(), result.size());
        assertEquals(products.get(0), result.get(0));
    }

    @Test
    public void testDeleteProduct() {
        Optional<Product> product = Optional.ofNullable(null);
        when(productRepository.findById(7L)).thenReturn(product);

        productController.deleteProduct(7);

        verify(productRepository, times(0)).deleteById(1L);
    }

    @Test
    public void testDeleteProduct2() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        doReturn(1).when(productRepository)
                .deleteProductById(7);

        boolean result = productController.deleteProduct(7);

        verify(productRepository)
                .deleteProductById(7);
        assertTrue(result);
    }

    @Test
    public void testAddProduct() {
        final Product newProduct = new Product("Butter", 5, 10, "kendra");

        ResponseEntity<?> result = productController.addProduct(newProduct);

        verify(productRepository).save(newProduct);

        final ResponseEntity<?> expected = ResponseEntity.created(URI.create("/addProduct"))
                .build();

        assertEquals(expected, result);


    }

    @Test
    public void testAddProductException() {
        doThrow(DataIntegrityViolationException.class).when(productRepository)
                .save(product);

        ResponseEntity<?> result = productController.addProduct(product);

        verify(productRepository).save(product);

        final ResponseEntity<?> expected = new ResponseEntity("Product not added!",
                HttpStatus.OK);
        verify(productRepository).save(product);
    }

    @Test
    public void testEditProduct() {
        doReturn(1).when(productRepository)
                .updateExistingProduct("Milk", "Chris", 14, 12, 12, 0, 0L);


        boolean result = productController.editProduct(product);

        verify(productRepository)
                .updateExistingProduct("Milk",  "Chris", 14, 12, 12, 0, 0L);
        assertTrue(result);
    }

    @Test
    public void testDeleteExpired() {
        product.setExpired(1);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));

        boolean result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertTrue(result);
    }

    @Test
    public void testDeleteExpired2() {
        product.setExpired(0);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));

        boolean result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertFalse(result);
    }

    @Test
    public void setExpired1(){
        when(productRepository.findByProductId(7L)).thenReturn(null);
        ResponseEntity expected =ResponseEntity.badRequest().build();
        ResponseEntity result = productController.setExpired("kendra", product);

        verify(productController).setExpired("kendra", product);

        assertEquals(expected, result);
    }


}