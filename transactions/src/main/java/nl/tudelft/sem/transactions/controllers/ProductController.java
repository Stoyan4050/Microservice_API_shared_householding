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
import nl.tudelft.sem.transactions.strategy.AmountStrategy;
import nl.tudelft.sem.transactions.strategy.NameStrategy;
import nl.tudelft.sem.transactions.strategy.PriceStrategy;
import nl.tudelft.sem.transactions.strategy.PriceThenAmountThenNameStrategy;
import nl.tudelft.sem.transactions.strategy.RandomStrategy;
import nl.tudelft.sem.transactions.strategy.SortProductsStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@EnableJpaRepositories("nl.tudelft.sem.template.repositories")

@Controller
public class ProductController {
    @Autowired
    private MicroserviceCommunicator microserviceCommunicator;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private transient JwtConf jwtConf;

    public ProductRepository getProductRepository() {
        return productRepository;
    }

    public void setProductRepository(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Adds a new product to the table of products.
     *
     * @param product - the new product to be added in the fridge
     */
    @PostMapping("/addNewProduct")
    ResponseEntity<?> addNewProduct(@RequestBody Product product) {
        float credits = product.getPrice();
        credits = Math.round(credits * 100) / 100.f;

        try {
            productRepository.save(product);
            microserviceCommunicator.sendRequestForChangingCredits(product.getUsername(),
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
    public ResponseEntity<List<Product>> getUserProducts(@Username String username) {
        List<Product> allProducts = productRepository.findAll();
        List<Product> products = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.getUsername().equals(username)) {
                products.add(p);
            }

        }
        // return not found if user has no products or there's no user in database
        if (products.size() == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(products, HttpStatus.OK);
        }
    }

    /**
     * Gets all products from the database.
     *
     * @param strategy The strategy to sort the returned products; possible options:
     *                 null - random order
     *                 "amount" - sorted by portions left in ascending order
     *                 "name" - sorted lexicographically by product name
     *                 "price" - sorted by price in ascending order
     *                 "priceThenAmountThenName" - sorted primarily on price,
     *                 secondary on number of portions left, ternary on product name
     * @return All products in the database corresponding to specific user, sorted
     * on given strategy
     */
    @GetMapping("/allProducts")
    public @ResponseBody
    List<Product> getAllProducts(@RequestParam String strategy) {
        List<Product> products = productRepository.findAll();

        createStrategy(strategy).sortProducts(products);
        return products;
    }

    /**
     * Creates a strategy for retrieving all products based on the name of the strategy.
     *
     * @param strategy name of the strategy to be created
     * @return Strategy created from given string
     */
    public SortProductsStrategy createStrategy(String strategy) {
        if (strategy == null) {
            return new RandomStrategy();
        } else {
            switch (strategy) {
                case "amount":
                    return new AmountStrategy();
                case "name":
                    return new NameStrategy();
                case "price":
                    return new PriceStrategy();
                case "priceThenAmountThenName":
                    return new PriceThenAmountThenNameStrategy();
                default:
                    return new RandomStrategy();
            }
        }
    }


    /**
     * Edits a product.
     *
     * @param productWithNewInfo - product to be got by Id
     * @return true if product successfully edited, false otherwise
     */
    @PutMapping("/updateProduct") // Map ONLY POST Requests
    public @ResponseBody
    ResponseEntity<String> updateProduct(@Username String username,
                                         @RequestBody Product productWithNewInfo) {
        Optional<Product> product = productRepository.findById(productWithNewInfo.getProductId());

        if (product.isPresent()) {
            try {
                if (!product.get().getUsername().equals(username)) {
                    return new ResponseEntity<>("It's not your product!",
                        HttpStatus.FORBIDDEN);
                }
                productRepository.save(productWithNewInfo);
            } catch (DataIntegrityViolationException e) {
                return new ResponseEntity<>("Product couldn't be updated!",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>("Product updated successfully!", HttpStatus.OK);
        }

        return new ResponseEntity<>("Product not found!", HttpStatus.NOT_FOUND);

    }

    /**
     * Delete a product.
     *
     * @param productId - id of a product
     * @return true if product successfully deleted, false otherwise
     */
    @DeleteMapping("/deleteProduct/{productId}")
    public @ResponseBody
    ResponseEntity<String> deleteProduct(@Username String username, @PathVariable long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            try {
                if (!product.getUsername().equals(username)) {
                    return new ResponseEntity<>("It's not your product!",
                        HttpStatus.FORBIDDEN);
                }
                productRepository.delete(product);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("The product couldn't be deleted");
            }
            return ResponseEntity.ok().body("Product successfully deleted.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * This method allows the user to change the status of
     * an object to expired once it has gone bad.
     *
     * @param productId - the product of which the expired field must be changed
     * @return - true in case the expired field was changed, fale otherwise.
     */
    @PostMapping("/setExpired/{productId}")
    public @ResponseBody
    ResponseEntity<?> setExpired(@Username String username, @PathVariable long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            try {
                float price = product.getPrice();
                float pricePerPortion = price / product.getTotalPortions();
                price = pricePerPortion * product.getPortionsLeft();

                microserviceCommunicator.subtractCreditsWhenExpired(username, price);

                productRepository.updateExistingProduct(product.getProductName(),
                    product.getUsername(), product.getPrice(),
                    product.getTotalPortions(), product.getPortionsLeft(),
                    1, product.getProductId());
                return ResponseEntity.created(URI.create("/setExpired")).build();

            } catch (Exception e) {
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * This method deletes all the products which are expired but are still in the database.
     *
     * @return - true if the products were successfully deleted, false otherwise
     */
    @DeleteMapping("/deleteExpired/{productId}")
    public @ResponseBody
    ResponseEntity<String> deleteExpired(@PathVariable long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {

            Product product = optionalProduct.get();
            try {
                if (product.getExpired() == 0) {
                    return ResponseEntity.badRequest().body("The product is not expired");

                } else {
                    productRepository.delete(product);
                    return ResponseEntity.ok().body("Product successfully deleted");
                }
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("The product couldn't be deleted");
            }
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get products of a house fridge.
     *
     * @param houseNr house number
     * @return list of products
     */
    @GetMapping("/getProductsByHouse/{houseNr}")
    @ResponseBody
    public List<Product> getProductsByHouse(@PathVariable int houseNr) {
        //List<Product> allProducts = productRepository.findAll();
        List<String> usernames = microserviceCommunicator.sendRequestForUsersOfHouse(houseNr);

        if (usernames == null) {
            return null;
        }
        List<Product> products = new ArrayList<>();

        for (String username : usernames) {
            //if (usernames.contains(p.getUsername())) {
            //  products.add(p);
            //}
            products.addAll(productRepository.findByUsername(username));
        }
        return products;
    }
}

