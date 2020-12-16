package nl.tudelft.sem.transactions.controllers;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.tudelft.sem.transactions.config.JwtConf;
import nl.tudelft.sem.transactions.entities.Product;
import nl.tudelft.sem.transactions.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.context.SecurityContextHolder;
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
@SuppressWarnings("PMD")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private JwtConf jwtConf;

    /**
     * Adds a new product to the table of products.
     *
     * @param productName   - name of the product to be added
     * @param price         - the price of the product to be added
     * @param totalPortions - the total number of portions a product has
     * @param username      - the username of the person who bought the product
     */
    @PostMapping("/addProduct/{product_name}/{price}/{total_portions}/{username}")
    @ResponseBody
    public Product addProduct(@PathVariable(value = "product_name") String productName,
                              @PathVariable(value = "price") float price,
                              @PathVariable(value = "total_portions") int totalPortions,
                              @PathVariable(value = "username") String username) {

        Product newProduct = new Product(productName, price, totalPortions, username);

        System.out.println("Product added");
        return productRepository.save(newProduct);
    }

    /**
     * The method returns the products added by a specified user.
     *
     * @param username - the username of the person whose products we are searching for
     * @return - a list of products that were added by the user with the indicated username
     */
    @GetMapping("getUserProducts/{username}")
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

    @GetMapping("/allProducts")
    public @ResponseBody
    List<Product> getAllProducts(HttpServletRequest request,
                                 HttpServletResponse response) {

        // TODO add a service that does all that
        String header = request.getHeader(jwtConf.getHeader());
        String token = header.replace(jwtConf.getPrefix(), "");
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConf.getSecret().getBytes())
                .parseClaimsJws(token)
                .getBody();

        String username = claims.getSubject();
        System.out.println(request.getHeader("Authorization"));
        System.out.println(username);
        System.out.println(response);
        System.out.println(SecurityContextHolder.getContext());
        // This returns a JSON or XML with the products
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
