package nl.tudelft.sem.transactions.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import nl.tudelft.sem.transactions.strategy.AmountStrategy;
import nl.tudelft.sem.transactions.strategy.SortProductsStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SuppressWarnings("PMD")
class ProductControllerTest {
    private Product product;

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
        final List<Product> result = productController.getAllProducts( null);
        verify(productRepository).findAll();
        verify(productController).createStrategy(null);

        assertEquals(products.size(), result.size());
        assertEquals(products.get(0), result.get(0));
    }

    @Test
    public void testGetAllProductsAmount() {
        Product p1 =  new Product("Milk", 14.0f, 12, "kendra");
        Product p2 = new Product("Butter", 5.f, 5, "kendra");
        final List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts( "amount");
        final List<Product> expected = Arrays.asList(p2,p1);
        verify(productRepository).findAll();
        verify(productController).createStrategy("amount");
        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0), result.get(0));
    }

    @Test
    public void testGetAllProductsName() {
        Product p1 =  new Product("Milk", 14.0f, 12, "kendra");
        Product p2 = new Product("Butter", 5.f, 5, "kendra");
        final List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts( "name");
        final List<Product> expected = Arrays.asList(p2,p1);
        verify(productRepository).findAll();
        verify(productController).createStrategy("name");
        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0), result.get(0));
    }

    @Test
    public void testGetAllProductsPrice() {
        Product p1 =  new Product("Milk", 14.0f, 12, "kendra");
        Product p2 = new Product("Butter", 5.f, 5, "kendra");
        final List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts( "price");
        final List<Product> expected = Arrays.asList(p2,p1);
        verify(productRepository).findAll();
        verify(productController).createStrategy("price");
        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0), result.get(0));
    }

    @Test
    public void testGetAllProductsPriceThenAmountThenNames() {
        Product p1 =  new Product("Milk", 14.0f, 12, "kendra");
        Product p2 = new Product("Butter", 5.f, 5, "kendra");
        final List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts( "priceThenAmountThenName");
        final List<Product> expected = Arrays.asList(p2,p1);
        verify(productRepository).findAll();
        verify(productController).createStrategy("priceThenAmountThenName");
        assertEquals(expected.size(), result.size());
        assertEquals(expected.get(0), result.get(0));
    }

    @Test
    public void testGetAllProductsRandom() {
        Product p1 =  new Product("Milk", 14.0f, 12, "kendra");
        Product p2 = new Product("Butter", 5.f, 5, "kendra");
        final List<Product> products = Arrays.asList(p1, p2);
        when(productRepository.findAll()).thenReturn(products);
        final List<Product> result = productController.getAllProducts( "willBeDefault");
        verify(productRepository).findAll();
        verify(productController).createStrategy("willBeDefault");
        assertEquals(2, result.size());
    }

    @Test
    public void testGetUserProducts() {
        final List<Product> products = Arrays.asList(new Product("Butter", 5, 5, "kendra"));

        when(productRepository.findAll()).thenReturn(products);
        final ResponseEntity result = productController.getUserProducts("kendra");
        final ResponseEntity expected = new ResponseEntity<>(products, HttpStatus.OK);
        verify(productRepository).findAll();
        verify(productController).getUserProducts("kendra");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expected, result);
    }

    @Test
    public void testGetUserProducts2() {
        final List<Product> products = new ArrayList<>();

        when(productRepository.findAll()).thenReturn(products);
        final ResponseEntity result = productController.getUserProducts("kendra");
        final ResponseEntity expected = new ResponseEntity<>(products, HttpStatus.NOT_FOUND);
        verify(productRepository).findAll();
        verify(productController).getUserProducts("kendra");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    //null prod
    @Test
    public void testDeleteProduct() {
        Optional<Product> product = Optional.ofNullable(null);
        when(productRepository.findById(7L)).thenReturn(product);


        verify(productRepository, times(0)).deleteById(1L);

        ResponseEntity result = productController.deleteProduct("kendra", 7);
        ResponseEntity expected = new ResponseEntity(HttpStatus.NOT_FOUND);

        assertEquals(result.getStatusCode(), expected.getStatusCode());


    }

    @Test
    public void testDeleteProduct2() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        product.setProductId(7);
        product.setUsername("kendra");

        ResponseEntity result = productController.deleteProduct("kendra", 7);

        verify(productController).deleteProduct("kendra", 7);
        assertEquals(result.getStatusCode(), HttpStatus.OK);

    }

    @Test
    public void deleteProduct3() {
        product.setProductId(7);
        when(productRepository.findById(7L)).thenReturn(Optional.empty());
        ResponseEntity result = productController.deleteProduct("kendra", 7);
        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void deleteProduct4() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        product.setProductId(7);
        product.setUsername("kendra");
        doThrow(DataIntegrityViolationException.class).when(productRepository).delete(product);
        ResponseEntity result = productController.deleteProduct("kendra", 7);
        ResponseEntity expected = ResponseEntity.badRequest()
                .body("The product couldn't be deleted");
        assertEquals(expected.getStatusCode(), result.getStatusCode());
    }

    @Test
    public void testAddProduct() {
        final Product newProduct = new Product("Butter", 5.123f, 10, "kendra");

        ResponseEntity<?> result = productController.addNewProduct(newProduct);

        verify(productRepository).save(newProduct);
//        verify(microserviceCommunicator).sendRequestForChangingCredits("kendra", 5.12f, true );
        final ResponseEntity<?> expected = ResponseEntity.created(URI.create("/addProduct"))
            .build();

        assertEquals(expected, result);


    }

    @Test
    public void testAddProductException() {
        doThrow(DataIntegrityViolationException.class).when(productRepository)
            .save(product);

        ResponseEntity<?> result = productController.addNewProduct(product);
        final ResponseEntity<?> expected = ResponseEntity.badRequest().build();
        assertEquals(expected, result);
    }

    @Test
    public void testUpdateProductNotFound() {
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        ResponseEntity result = productController.updateProduct("kendra", product);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testUpdateProductNotYourProduct() {
        product.setProductId(7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        ResponseEntity result = productController.updateProduct("fabian", product);

        assertEquals(HttpStatus.FORBIDDEN, result.getStatusCode());
    }

    @Test
    public void testUpdateProductCouldntUpdate() {
        product.setProductId(7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        doThrow(DataIntegrityViolationException.class).when(productRepository)
                .save(product);
        ResponseEntity result = productController.updateProduct("kendra", product);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testUpdateProductAllGood() {
        product.setProductId(7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));
        ResponseEntity result = productController.updateProduct("kendra", product);
        verify(productRepository).save(product);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testDeleteExpired() {
        product.setExpired(1);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        ResponseEntity result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testDeleteExpired2() {
        product.setExpired(0);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));

        ResponseEntity result = productController.deleteExpired(7);
        verify(productController).deleteExpired(7);
        assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void deleteExpired3() {
        product.setExpired(1);
        when(productRepository.findById(7L)).thenReturn(Optional.ofNullable(product));
        doThrow(DataIntegrityViolationException.class).when(productRepository).delete(product);
        ResponseEntity result = productController.deleteExpired(7);
        ResponseEntity expected = ResponseEntity
                .badRequest().body("The product couldn't be deleted");
        verify(productController).deleteExpired(7);
        assertEquals(expected, result);
    }

    @Test
    public void deleteExpired4() {
        when(productRepository.findById(7L)).thenReturn(Optional.empty());
        ResponseEntity result = productController.deleteExpired(7);
        ResponseEntity expected = ResponseEntity.notFound().build();
        verify(productController).deleteExpired(7);
        assertEquals(expected, result);
    }

    @Test
    public void setExpired1() {
        when(productRepository.findByProductId(7L)).thenReturn(null);
        ResponseEntity expected = ResponseEntity.badRequest().build();
        ResponseEntity result = productController.setExpired("kendra", 7);

        verify(productController).setExpired("kendra", 7);
        assertEquals(expected, result);
    }

    @Test
    public void setExpired2() {
        when(productRepository.updateExistingProduct("milk",
                "kendra", 7, 5, 5, 0, 7))
                .thenThrow(DataIntegrityViolationException.class);
        product.setProductId(7);
        ResponseEntity result = productController.setExpired("kendra", 7);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
    }

    @Test
    public void getProductRepositoryTest() {
        ProductRepository rep = productController.getProductRepository();
        assertNotNull(rep);
    }



}