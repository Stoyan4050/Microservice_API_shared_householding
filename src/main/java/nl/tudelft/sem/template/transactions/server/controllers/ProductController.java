package nl.tudelft.sem.template.transactions.server.controllers;

import java.util.List;
import nl.tudelft.sem.template.transactions.server.entities.Product;
import nl.tudelft.sem.template.transactions.server.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/allProducts")
    public @ResponseBody
    List<Product> getAllProducts() {
        // This returns a JSON or XML with the products
        return productRepository.findAll();
    }

    /**
     * Adds a new product.
     *
     * @param product - product to be added to the database
     * @return true if product successfully added, false otherwise
     */
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

    /**
     * Edits a product.
     *
     * @param product - product to be edited in the database
     *
     * @return true if product successfully edited, false otherwise
     */
    @RequestMapping("/editProduct") // Map ONLY POST Requests
    public @ResponseBody
    boolean editHolidays(@RequestBody Product product) {
        // @ResponseBody means the returned String is the response, not a view name
        // @RequestParam means it is a parameter from the GET or POST request
        try {
            if (productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(),
                    product.getPrice(),
                    product.getTotalPortions(),
                    product.getPortionsLeft(),
                    product.getExpired(),
                    product.getProductId()) == 1) {
                return true;
            }
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }


    /**
     * Delete a product.
     *
     * @param productId - id of a product
     *
     * @return true if product successfully deleted, false otherwise
     */
    @GetMapping("/deleteProduct")
    public @ResponseBody
    boolean deleteProduct(@RequestParam int productId) {
        try {
            if (productRepository.deleteProductById(productId) != 0) {
                return true;
            } else {
                return false;
            }
        } catch (NullPointerException e) {
            return false;
        }
    }
}
