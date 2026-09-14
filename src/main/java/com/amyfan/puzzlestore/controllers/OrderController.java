package com.amyfan.puzzlestore.controllers;

import com.amyfan.puzzlestore.entities.*;
import com.amyfan.puzzlestore.payment.gateway.PaymentProcessor;
import com.amyfan.puzzlestore.payment.model.PaymentResult;
import com.amyfan.puzzlestore.payment.model.PaymentStatus;
import com.amyfan.puzzlestore.dtos.CheckoutInfo;
import com.amyfan.puzzlestore.dtos.OrderStatus;
import com.amyfan.puzzlestore.security.CustomUserDetails;
import com.amyfan.puzzlestore.services.CartService;
import com.amyfan.puzzlestore.services.OrderNumberService;
import com.amyfan.puzzlestore.services.OrderService;
import com.amyfan.puzzlestore.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class OrderController {
    CartService cartService;
    UserService userService;
    OrderService orderService;
    OrderNumberService orderNumberService;
    PaymentProcessor paymentProcessor;

    @Autowired
    public OrderController(CartService cartService, UserService userService, OrderService orderService,
                           OrderNumberService orderNumberService, PaymentProcessor paymentProcessor) {
        this.cartService = cartService;
        this.userService = userService;
        this.orderService = orderService;
        this.orderNumberService = orderNumberService;
        this.paymentProcessor = paymentProcessor;
    }

    @GetMapping("/cart")
    public String getShoppingCart(@AuthenticationPrincipal CustomUserDetails user, Model model) {
        BigDecimal subtotal = cartService.getSubtotal(user.getEmail());
        BigDecimal tax = cartService.getTax(subtotal);
        BigDecimal shipping = cartService.getShipping(subtotal);

        model.addAttribute("cart", cartService.getCart(user.getEmail()));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", cartService.getTotal(subtotal, tax, shipping));

        model.addAttribute("cartItem", new CartItem());

        return "cart";
    }

    @PostMapping("/cart")
    public String updateShoppingCart(@Valid @ModelAttribute("cartItem") CartItem cartItem,
                                     @AuthenticationPrincipal CustomUserDetails user,
                                     Model model) {
        cartService.updateItem(user.getEmail(), cartItem.getPuzzle().getProductId(), cartItem.getQuantity());

        BigDecimal subtotal = cartService.getSubtotal(user.getEmail());
        BigDecimal tax = cartService.getTax(subtotal);
        BigDecimal shipping = cartService.getShipping(subtotal);

        model.addAttribute("cart", cartService.getCart(user.getEmail()));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", cartService.getTotal(subtotal, tax, shipping));

        model.addAttribute("cartItem", new CartItem());

        return "cart";
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@ModelAttribute("productId") String productId,
                                 @AuthenticationPrincipal CustomUserDetails user) {
        cartService.removeCartItem(productId, user.getEmail());

        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal CustomUserDetails user,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (cartService.getCart(user.getEmail()).isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Must add products to cart before checkout");
            return "redirect:/cart";
        }

        if (cartService.containsInactive(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "CHECKOUT FAILED: Remove all inactive products before checkout");
            return "redirect:/cart";
        }

        BigDecimal subtotal = cartService.getSubtotal(user.getEmail());
        BigDecimal tax = cartService.getTax(subtotal);
        BigDecimal shipping = cartService.getShipping(subtotal);

        model.addAttribute("cart", cartService.getCart(user.getEmail()));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", cartService.getTotal(subtotal, tax, shipping));

        model.addAttribute("checkoutInfo", new CheckoutInfo());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String processOrder(@Valid @ModelAttribute("checkoutInfo") CheckoutInfo checkoutInfo,
                               BindingResult result,
                               @AuthenticationPrincipal CustomUserDetails principal,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        User user = userService.findUserByEmail(principal.getEmail());

        if (cartService.containsInactive(user.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "CHECKOUT FAILED: Remove all inactive products before checkout");
            return "redirect:/cart";
        }

        BigDecimal subtotal = cartService.getSubtotal(user.getEmail());
        BigDecimal tax = cartService.getTax(subtotal);
        BigDecimal shipping = cartService.getShipping(subtotal);

        model.addAttribute("cart", cartService.getCart(user.getEmail()));
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("tax", tax);
        model.addAttribute("shipping", shipping);
        model.addAttribute("total", cartService.getTotal(subtotal, tax, shipping));

        if (result.hasErrors()) {
            return "checkout";
        }

        checkoutInfo.getPayment().setAmount(cartService.getTotal(subtotal, tax, shipping));
        PaymentResult paymentResult = paymentProcessor.processPayment(checkoutInfo.getPayment());

        if (paymentResult.getStatus().equals(PaymentStatus.DECLINED)) {
            if (paymentResult.getMessage().equals("The payment was declined by the payment processor.")) {
                model.addAttribute("errorMessage", "CHECKOUT FAILED: " + paymentResult.getMessage());
            } else if (paymentResult.getMessage().equals("Payment amount must be greater than 0")) {
                model.addAttribute("errorMessage",
                        "CHECKOUT FAILED: Cart is empty. Add products before checkout");
            } else {
                model.addAttribute("paymentError", paymentResult.getMessage());
            }

            return "checkout";
        }

        String orderNumber = orderNumberService.generateOrderNumber(new OrderNumber());

        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setDateTime(LocalDateTime.now());
        order.setStatus(OrderStatus.PAID);
        order.setTotalAmount(cartService.getTotal(subtotal, tax, shipping));
        order.setTransactionId(paymentResult.getTransactionId());
        order.setShippingInfo(checkoutInfo.getShipping());

        List<OrderItem> items = new ArrayList<>();
        for (CartItem item : user.getCart()) {
            OrderItem orderItem = new OrderItem(item.getPuzzle(), item.getQuantity(), item.getPuzzle().getPrice());
            orderItem.setOrder(order);
            items.add(orderItem);
        }
        order.setItems(items);

        orderService.addOrderToUserHistory(user, order);
        userService.emptyCart(user.getEmail());

        return "redirect:/checkout/" + orderNumber;
    }

    @GetMapping("/checkout/{orderNumber}")
    public String checkoutSuccess(@PathVariable String orderNumber,
                                  @AuthenticationPrincipal CustomUserDetails user,
                                  Model model) {
        orderService.validateUser(user, orderNumber);

        Order order = orderService.findOrderByOrderNumber(orderNumber);
        model.addAttribute("order", order);

        return "checkout-success";
    }

    @GetMapping("/account/orders/{orderNumber}")
    public String getOrderDetails(@PathVariable("orderNumber") String orderNumber,
                                  @AuthenticationPrincipal CustomUserDetails user,
                                  Model model) {
        orderService.validateUser(user, orderNumber);

        Order order = orderService.findOrderByOrderNumber(orderNumber);
        model.addAttribute("order", order);
        model.addAttribute("shippingInfo", order.getShippingInfo());
        model.addAttribute("items", order.getItems());

        return "order-details";
    }
}
