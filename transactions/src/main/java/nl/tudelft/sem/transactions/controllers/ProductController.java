package nl.tudelft.sem.transactions.controllers;

import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.transactions.config.JwtConf;
import nl.tudelft.sem.transactions.config.Username;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@EnableJpaRepositories("nl.tudelft.sem.template.repositories")

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtConf jwtConf;

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(
        ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public JwtConf getJwtConf() {
        return jwtConf;
    }

    public void setJwtConf(JwtConf jwtConf) {
        this.jwtConf = jwtConf;
    }

    /**
     * Adds a new product to the table of products.
     *
     * @param product       - the product to be added
     */
    @PostMapping("/addProduct")
    @ResponseBody
    public Product addProduct(@RequestBody Product product) {
        System.out.println("Product added");
        return productRepository.save(product);
    }

 
 
    /**
     * The method returns the products added by a specified user.
     *
     * @param username - the username of the person whose products we are searching for
     * @return - a list of products that were added by the user with the indicated username
     */
    @GetMapping("getUserProducts/{userID}")
    @ResponseBody
    public List<Product> getUserProducts(@PathVariable String username) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> products = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getUsername().equals(username)) {
                products.add(p);
            }

        }
        return products;
    }
    
    /**
     * Gets all products from the database.
     *
     * @param username The username of the user making the request
     * @return All products in the database
     */
    @GetMapping("/allProducts")
    public @ResponseBody
    List<Product> getAllProducts(@Username String username) {

        System.out.println(username);
        return productRepository.findAll();
    }

    /*
    @PostMapping("/addProduct") // Map ONLY POST Requests
    public @ResponseBody
    boolean addHolidays(@RequestBody Product product) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestBody means it is a parameter from the GET or POST request
        try {
            productRepository.save(product);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
     */

    /**
     * Edits a product.
     *
     * @param product - product to be edited in the database
     * @return true if product successfully edited, false otherwise
     */
    @RequestMapping("/editProduct") // Map ONLY POST Requests
    public @ResponseBody
    boolean editProduct(@RequestBody Product product) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        try {
            return productRepository.updateExistingProduct(product.getProductName(),
                product.getUsername(),
                product.getPrice(),
                product.getTotalPortions(),
                product.getPortionsLeft(),
                product.getExpired(),
                product.getProductId()) == 1;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Delete a product.
     *
     * @param productId - id of a product
     * @return true if product successfully deleted, false otherwise
     */
    @GetMapping("/deleteProduct")
    public @ResponseBody
    boolean deleteProduct(@RequestParam int productId) {
        try {
            return productRepository.deleteProductById(productId) != 0;
        } catch (Exception e) {
            return false;
        }
    }

}
