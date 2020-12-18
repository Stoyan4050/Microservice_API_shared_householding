package nl.tudelft.sem.transactions.controllers;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
        product = new Product("Milk", 14.0f, 12, "kendra");
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
        final ResponseEntity result = productController.getUserProducts("kendra");
        final ResponseEntity expected = new ResponseEntity<>(products, HttpStatus.OK);
        verify(productRepository).findAll();
        verify(productController).getUserProducts("kendra");

        assertEquals(HttpStatus.OK,result.getStatusCode());
        assertEquals(expected, result);
    }

    //null prod
    @Test
    public void testDeleteProduct() {
        Optional<Product> product = Optional.ofNullable(null);
        when(productRepository.findById(7L)).thenReturn(product);


        verify(productRepository, times(0)).deleteById(1L);

        ResponseEntity result = productController.deleteProduct("kendra",7);
        ResponseEntity expected =new ResponseEntity(HttpStatus.NOT_FOUND);

        assertEquals(result.getStatusCode(),expected.getStatusCode());


    }

    @Test
    public void testDeleteProduct2() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        product.setProductId(7);
        product.setUsername("kendra");

        ResponseEntity result = productController.deleteProduct("kendra",7);

    verify(productController).deleteProduct("kendra",7);
        assertEquals(result.getStatusCode(),HttpStatus.OK);

    }

    @Test
    public void deleteProduct3(){
        product.setProductId(7);
        when(productRepository.findById(7L)).thenReturn(Optional.empty());
        ResponseEntity result = productController.deleteProduct("kendra",7);
        //verify(productController).deleteProduct("kendra",7);
        assertEquals(result.getStatusCode(),HttpStatus.NOT_FOUND);
    }

    @Test
    public void testAddProduct() {
        final Product newProduct = new Product("Butter", 5, 10, "kendra");

        ResponseEntity<?> result = productController.addNewProduct(newProduct);

        verify(productRepository).save(newProduct);

        final ResponseEntity<?> expected = ResponseEntity.created(URI.create("/addProduct"))
                .build();

        assertEquals(expected, result);


    }

    @Test
    public void testAddProductException() {
        doThrow(DataIntegrityViolationException.class).when(productRepository)
                .save(product);

        ResponseEntity<?> result = productController.addNewProduct(product);

        verify(productRepository).save(product);

        final ResponseEntity<?> expected = new ResponseEntity("Product not added!",
                HttpStatus.OK);
        verify(productRepository).save(product);
    }

    @Test
    public void testUpdateProduct() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        ResponseEntity result = productController.updateProduct("kendra",product);

        verify(productController)
               .updateProduct("kendra",product);
        assertEquals(result.getStatusCode(),HttpStatus.NOT_FOUND);
    }

    @Test
    public void testDeleteExpired() {
        product.setExpired(1);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        ResponseEntity result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertEquals(result.getStatusCode(),HttpStatus.OK);
    }

    @Test
    public void testDeleteExpired2() {
        product.setExpired(0);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));

        ResponseEntity result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertEquals(result.getStatusCode(),HttpStatus.BAD_REQUEST);
    }

    @Test
    public void setExpired1(){
        when(productRepository.findByProductId(7L)).thenReturn(null);
        ResponseEntity expected =ResponseEntity.badRequest().build();
        ResponseEntity result = productController.setExpired("kendra", 7);

        verify(productController).setExpired("kendra", 7);

        assertEquals(expected, result);
    }

    @Test
    public void setExpired2(){
        when(productRepository.updateExistingProduct("milk",
                "kendra",7,5,5,0,7))
                .thenThrow(DataIntegrityViolationException.class);
        product.setProductId(7);
        ResponseEntity result = productController.setExpired("kendra",7);
        assertEquals(HttpStatus.BAD_REQUEST,result.getStatusCode());
    }


}