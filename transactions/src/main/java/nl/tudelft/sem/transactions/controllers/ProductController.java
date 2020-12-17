package nl.tudelft.sem.transactions.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import nl.tudelft.sem.transactions.MicroserviceCommunicator;
import nl.tudelft.sem.transactions.config.JwtConf;
import nl.tudelft.sem.transactions.config.Username;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @param product - the new product to be added in the fridge
     */
    @PostMapping("/addProduct")
    ResponseEntity<?> addProduct(@RequestBody Product product) {
        float credits = product.getPrice();
        credits = Math.round(credits * 100) / 100;

        try {
            productRepository.save(product);
            MicroserviceCommunicator.sendRequestForChangingCredits(product.getUsername(),
                    credits, true);

            return ResponseEntity.created(URI.create("/addProduct")).build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

 
 
    /**
     * The method returns the products added by a specified user.
     *
     * @param username - the username of the person whose products we are searching for
     * @return - a list of products that were added by the user with the indicated username
     */
    @GetMapping("/getUserProducts")
    @ResponseBody
    public List<Product> getUserProducts(@Username String username) {
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


    /**
     * Edits a product.
     *
     * @param productId - product to be got by Id
     * @return true if product successfully edited, false otherwise
     */
    @RequestMapping("/editProduct") // Map ONLY POST Requests
    public @ResponseBody
    boolean editProduct(@RequestBody Product productId) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        Product product = productRepository.findByProductId(productId.getProductId());
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
    @DeleteMapping("/deleteProduct")
    public @ResponseBody
    boolean deleteProduct(@RequestParam int productId) {
        try {
            return productRepository.deleteProductById(productId) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method allows the user to change the status of
     * an object to expired once it has gone bad.
     *
     * @param productId - the product of which the expired field must be changed
     * @return - true in case the expired field was changed, fale otherwise.
     */
    @PostMapping("/setExpired")
    public @ResponseBody
    ResponseEntity<?> setExpired(@Username String username, @RequestBody Product productId) {
        Product product = productRepository.findByProductId(productId.getProductId());
        if(product == null){
            return ResponseEntity.badRequest().build();
        }
        try {
            float price = product.getPrice();
            float pricePerPortion = price / product.getTotalPortions();
            price = pricePerPortion * product.getPortionsLeft();
            
            MicroserviceCommunicator.subtractCreditsWhenExpired(username, price);
            
            productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(), product.getPrice(),
                    product.getTotalPortions(), product.getPortionsLeft(),
                    1, product.getProductId());
            return ResponseEntity.created(URI.create("/setExpired")).build();
    
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * This method deletes all the products which are expired but are still in the database.
     *
     * @return - true if the products were successfully deleted, false otherwise
     */
    @DeleteMapping("/deleteExpired")
    public @ResponseBody
    boolean deleteExpired(@RequestParam long productId) {
        try {
            Optional<Product> p = productRepository.findById(productId);
            Product product = p.get();
            if (product.getExpired() == 0) {
                System.out.println("The product is not expired");
                return false;
            } else {
                productRepository.delete(product);
                return true;
            }
        } catch (Exception e) {
            return false;
        }
    }


}

