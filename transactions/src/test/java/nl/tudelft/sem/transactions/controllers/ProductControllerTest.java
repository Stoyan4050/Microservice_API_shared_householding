package nl.tudelft.sem.transactions.controllers;

import java.util.Arrays;
import java.util.List;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import org.junit.jupiter.api.Assertions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("PMD")
class ProductControllerTest {
     private Product product;
    // private ProductController productController;

    @Mock
    private transient ProductRepository productRepository;

    @InjectMocks
    private transient ProductController productController;

    @BeforeEach
    public void setUp() {
        initMocks(this);
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
    void deleteProduct() {
        Assertions.assertFalse(productController.deleteProduct(-128));
    }

  /*  @Test
    void editProduct() {
        productController.editProduct(product);
        Assertions.assertEquals("Milk", this.product.getProductName());
    }*/
}