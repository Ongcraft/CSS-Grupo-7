package pt.ul.fc.css.tascaeats.controllers;

import java.util.UUID;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import pt.ul.fc.css.tascaeats.dtos.common.AddressDTO;
import pt.ul.fc.css.tascaeats.dtos.order.OrderDTO;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentDTO;
import pt.ul.fc.css.tascaeats.dtos.order.CreateOrderDTO;
import pt.ul.fc.css.tascaeats.dtos.order.PaymentInfoDTO;
import pt.ul.fc.css.tascaeats.dtos.order.AddProductDTO;
import pt.ul.fc.css.tascaeats.dtos.user.CreateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.LoginUserDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UpdateCustomerDTO;
import pt.ul.fc.css.tascaeats.dtos.user.UserDTO;
import pt.ul.fc.css.tascaeats.entities.Order;
import pt.ul.fc.css.tascaeats.entities.Customer;
import pt.ul.fc.css.tascaeats.entities.Address;
import pt.ul.fc.css.tascaeats.entities.User;
import pt.ul.fc.css.tascaeats.enums.CardFlag;
import pt.ul.fc.css.tascaeats.enums.PaymentType;
import pt.ul.fc.css.tascaeats.enums.Role;
import pt.ul.fc.css.tascaeats.services.MenuService;
import pt.ul.fc.css.tascaeats.services.OrderService;
import pt.ul.fc.css.tascaeats.services.ProductService;
import pt.ul.fc.css.tascaeats.services.RestaurantService;
import pt.ul.fc.css.tascaeats.services.UserService;


@Controller
@RequestMapping("/web")
public class WebController {
    
    private final MenuService menuService;
    private final OrderService orderService;
    private final ProductService productService;
    private final RestaurantService restaurantService;
    private final UserService userService;

    public WebController(MenuService menuService,
                         OrderService orderService,
                         ProductService productService,
                         RestaurantService restaurantService,
                         UserService userService) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.productService = productService;
        this.restaurantService = restaurantService;
        this.userService = userService;
    }

    @GetMapping
    public String start() {
        return "login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }


    @PostMapping("/login")
    public String login(
        @RequestParam("username") String username, 
        @RequestParam("password") String password, 
        HttpSession session,
        RedirectAttributes ra) {

        try {
            UserDTO found = userService.login(new LoginUserDTO(username, password));

            if (found.role() != Role.CUSTOMER) {
                ra.addFlashAttribute("error", "Only customers can login.");
                return "redirect:/web/login";
            }
            session.setAttribute("loggedUser", found);
            return "redirect:/web/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "User not found.");
            return "redirect:/web/login";
        }
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
        @RequestParam("name") String name,
        @RequestParam("username") String username, 
        @RequestParam("password") String password, 
        @RequestParam("city") String city, 
        @RequestParam("street") String street, 
        @RequestParam("postalCode") String postalCode, 
        HttpSession session,
        RedirectAttributes ra) {

        try {
            User created = userService.registerCustomer(new CreateCustomerDTO(name, username, password, new AddressDTO(new Address(city, postalCode, street))));
            UserDTO res = UserDTO.from(created);
            session.setAttribute("loggedUser", res);
            return "redirect:/web/dashboard";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "User not created.");
            return "redirect:/web/login";
        }
    }

    @GetMapping("/dashboard")
    public String dashboardPage(
        HttpSession session, 
        Model model) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");        
        if (user == null) {
            return "redirect:/web/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/orders")
    public String orderList(HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");

        if (user == null) return "redirect:/web/login";

        model.addAttribute("orders", orderService.getOrdersByCustomerId(user.id()));
        return "order-list";
    }

    @GetMapping("/order")
    public String orderDetails(
            @RequestParam("id") UUID orderId,
            HttpSession session,
            Model model) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        OrderDTO order = new OrderDTO(orderService.getOrderById(orderId));

        model.addAttribute("order", order);
        return "order";
    }

    @PostMapping("/order/cancel")
    public String cancelOrder(
            @RequestParam("orderId") UUID orderId,
            HttpSession session) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        orderService.cancelOrder(orderId);

        return "redirect:/web/order?id=" + orderId;
    }

    @PostMapping("/order/rate")
    public String rateRestaurant(
            @RequestParam("orderId") UUID orderId,
            @RequestParam("restaurantId") UUID restaurantId,
            @RequestParam("rating") double rating,
            HttpSession session) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            restaurantService.rateRestaurant(user.id(), restaurantId, rating);
        } catch (Exception e) {
            return "redirect:/web/order?id=" + orderId + "&error=" + e.getMessage();
        }

        return "redirect:/web/order?id=" + orderId;
    }

    @GetMapping("/profile")
    public String profilePage(
            HttpSession session,
            Model model) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            Customer customer = (Customer) userService.getUserById(user.id());
            model.addAttribute("customer", customer);
            model.addAttribute("addresses", customer.getAddresses());
            return "profile";
        } catch (Exception e) {
            model.addAttribute("error", "Erro ao carregar perfil: " + e.getMessage());
            return "profile";
        }
    }

    @GetMapping("/profile/add-address")
    public String createAddressFormPage(
            HttpSession session,
            Model model) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        return "address-form";
    }

    @PostMapping("/profile/add-address")
    public String addAddress(
            @RequestParam("city") String city,
            @RequestParam("street") String street,
            @RequestParam("postalCode") String postalCode,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            Address address = new Address(city, postalCode, street);
            userService.addAddress(user.id(), address);
            
            ra.addFlashAttribute("success", "Morada adicionada com sucesso!");
            return "redirect:/web/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao adicionar morada: " + e.getMessage());
            return "redirect:/web/profile";
        }
    }

    @GetMapping("/profile/edit")
    public String editProfileFormPage(
            HttpSession session,
            Model model) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";
            
        Customer customer = (Customer) userService.getUserById(user.id());
        model.addAttribute("customer", customer);

        return "edit-profile-form";
    }

    @PostMapping("/profile/edit")
    public String editProfile(
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam (required = false) String password,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            UpdateCustomerDTO dto = new UpdateCustomerDTO(name, username, password, null);
            Customer updated = userService.updateCustomer(user.id(), dto);
            
            session.setAttribute("loggedUser", UserDTO.from(updated));
            
            return "redirect:/web/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao editar perfil: " + e.getMessage());
            return "redirect:/web/profile";
        }
    }

    @PostMapping("/profile/delete-address")
    public String removeAddress(
            @RequestParam("addressIndex") int index,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            userService.removeAddress(user.id(), index);
            
            ra.addFlashAttribute("success", "Morada removida com sucesso!");
            return "redirect:/web/profile";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao remover morada: " + e.getMessage());
            return "redirect:/web/profile";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loggedUser");
        return "redirect:/web/login";
    }

    @GetMapping("/payment")
    public String paymentPage(
            @RequestParam("id") UUID orderId,
            HttpSession session,
            Model model) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        OrderDTO order = new OrderDTO(orderService.getOrderById(orderId));

        model.addAttribute("order", order);
        return "payment";
    }

    @PostMapping("/order/pay")
    public String payOrder(
            @RequestParam("orderId") UUID orderId,
            @RequestParam("paymentType") String paymentType,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "cardFlag", required = false) String cardFlag,
            @RequestParam("amount") double amount,
            HttpSession session,
            RedirectAttributes ra) {

        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        PaymentType type = PaymentType.valueOf(paymentType);

        CardFlag flag = null;
        if (cardFlag != null) {
            flag = CardFlag.valueOf(cardFlag);
        }   

        try {

            PaymentDTO dto = new PaymentDTO(
                type,
                amount,
                flag,
                phoneNumber
            );

            orderService.payOrder(orderId, dto);

            return "redirect:/web/order?id=" + orderId;
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/order?id=" + orderId;
        }
    }

    @PostMapping("/order/prepare")
    public String prepareOrder(@RequestParam("orderId") UUID orderId, HttpSession session, RedirectAttributes ra) {
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            orderService.prepareOrder(orderId);
            ra.addFlashAttribute("success", "Pedido em preparação.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/order?id=" + orderId;
    }

    @PostMapping("/order/ready")
    public String markOrderReady(@RequestParam("orderId") UUID orderId, HttpSession session, RedirectAttributes ra) {
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            orderService.markOrderReady(orderId); // aqui envia ORDER_READY para Kafka
            ra.addFlashAttribute("success", "Pedido pronto para entrega.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/order?id=" + orderId;
    }

    @GetMapping("/restaurants")
    public String restaurantsList(HttpSession session, Model model) {
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        model.addAttribute("restaurants", restaurantService.getAllRestaurants());
        return "restaurants";
    }

    @GetMapping("/orders/new")
    public String createOrderFormPage(
            HttpSession session,
            Model model) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        Customer customer = (Customer) userService.getUserById(user.id());
        model.addAttribute("addresses", customer.getAddresses());
        
        model.addAttribute("paymentMethods", Arrays.asList(
            PaymentType.CASH.name(),
            PaymentType.MBWAY.name(),
            PaymentType.MULTIBANCO.name()
        ));
        
        return "order-form";
    }

    @PostMapping("/orders/new")
    public String createOrder(
            @RequestParam int addressIndex,
            @RequestParam String paymentType,
            @RequestParam(value = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(value = "cardFlag", required = false) String cardFlag,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            Customer customer = (Customer) userService.getUserById(user.id());
            List<Address> addresses = new ArrayList<>(customer.getAddresses());
            
            if (addressIndex < 0 || addressIndex >= addresses.size()) {
                throw new IllegalArgumentException("Morada não encontrada");
            }
            
            Address selectedAddress = addresses.get(addressIndex);
            AddressDTO addressDTO = new AddressDTO(selectedAddress);
            
            PaymentType paymentTypeEnum = PaymentType.valueOf(paymentType);
            CardFlag flag = null;
            if (cardFlag != null && !cardFlag.isEmpty()) {
                flag = CardFlag.valueOf(cardFlag);
            }
            
            PaymentInfoDTO paymentInfoDTO = new PaymentInfoDTO(
                paymentTypeEnum,
                flag,
                phoneNumber
            );
            
            CreateOrderDTO createOrderDTO = new CreateOrderDTO(
                user.id(),
                addressDTO,
                paymentInfoDTO
            );
            
            Order createdOrder = orderService.createOrder(createOrderDTO);
            
            UUID restaurantIdFromSession = (UUID) session.getAttribute("restaurantId");
            @SuppressWarnings("unchecked")
            List<UUID> cart = (List<UUID>) session.getAttribute("cart");
            
            if (cart != null && !cart.isEmpty() && restaurantIdFromSession != null) {
                for (UUID productId : cart) {
                    try {
                        AddProductDTO addProductDTO = new AddProductDTO();
                        addProductDTO.productId = productId;
                        addProductDTO.quantity = 1;
                        orderService.addProduct(createdOrder.getId(), restaurantIdFromSession, addProductDTO);
                    } catch (Exception ignored) {
                    }
                }
            }
            
            session.removeAttribute("cart");
            session.removeAttribute("restaurantId");
            
            ra.addFlashAttribute("success", "Order criada com sucesso!");
            return "redirect:/web/order?id=" + createdOrder.getId();
            
        } catch (IllegalArgumentException e) {
            ra.addFlashAttribute("error", "Dados inválidos: " + e.getMessage());
            return "redirect:/web/orders/new";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao criar order: " + e.getMessage());
            return "redirect:/web/orders/new";
        }
    }
    
    @GetMapping("/restaurant")
    public String restaurantDetail(
            @RequestParam("id") UUID id,
            HttpSession session,
            Model model,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            var restaurant = restaurantService.getById(id);
            var menu = restaurantService.getById(id).getMenu();
            
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("menu", menu.getProducts());
            
            return "restaurant";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Restaurante não encontrado");
            return "redirect:/web/restaurants";
        }
    }


    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam("productId") UUID productId,
            @RequestParam("restaurantId") UUID restaurantId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            @SuppressWarnings("unchecked")
            List<UUID> cart = (List<UUID>) session.getAttribute("cart");
            if (cart == null) {
                cart = new ArrayList<>();
            }
            
            for (int i = 0; i < quantity; i++) {
                cart.add(productId);
            }
            
            session.setAttribute("cart", cart);
            session.setAttribute("restaurantId", restaurantId);
            
            ra.addFlashAttribute("success", "Produto adicionado ao carrinho");
            return "redirect:/web/restaurant?id=" + restaurantId;
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao adicionar produto: " + e.getMessage());
            return "redirect:/web/restaurant?id=" + restaurantId;
        }
    }

    @GetMapping("/cart")
    public String viewCart(
            HttpSession session,
            Model model) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        @SuppressWarnings("unchecked")
        List<UUID> cart = (List<UUID>) session.getAttribute("cart");
        
        if (cart == null || cart.isEmpty()) {
            model.addAttribute("cartProducts", new ArrayList<>());
            model.addAttribute("totalPrice", 0.0);
            return "cart";
        }

        try {
            Map<UUID, Long> productCount = new java.util.HashMap<>();
            for (UUID productId : cart) {
                productCount.put(productId, productCount.getOrDefault(productId, 0L) + 1);
            }

            List<Map<String, Object>> cartProducts = new ArrayList<>();
            double totalPrice = 0.0;

            for (Map.Entry<UUID, Long> entry : productCount.entrySet()) {
                try {
                    var product = productService.getProductById(entry.getKey());
                    Map<String, Object> cartItem = new HashMap<>();
                    cartItem.put("productId", product.getId());
                    cartItem.put("productName", product.getName());
                    cartItem.put("productPrice", product.getPrice());
                    cartItem.put("quantity", entry.getValue());
                    
                    cartProducts.add(cartItem);
                    totalPrice += product.getPrice() * entry.getValue();
                } catch (Exception ignored) {
                }
            }

            model.addAttribute("cartProducts", cartProducts);
            model.addAttribute("totalPrice", totalPrice);
            
            return "cart";
        } catch (Exception e) {
            model.addAttribute("cartProducts", new ArrayList<>());
            model.addAttribute("totalPrice", 0.0);
            return "cart";
        }
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(
            @RequestParam("productId") UUID productId,
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            @SuppressWarnings("unchecked")
            List<UUID> cart = (List<UUID>) session.getAttribute("cart");
            if (cart != null) {
                cart.remove(productId);
                session.setAttribute("cart", cart);
            }
            
            ra.addFlashAttribute("success", "Produto removido do carrinho");
            return "redirect:/web/cart";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao remover produto: " + e.getMessage());
            return "redirect:/web/cart";
        }
    }

    @PostMapping("/cart/clear")
    public String clearCart(
            HttpSession session,
            RedirectAttributes ra) {
        
        UserDTO user = (UserDTO) session.getAttribute("loggedUser");
        if (user == null) return "redirect:/web/login";

        try {
            session.removeAttribute("cart");
            session.removeAttribute("restaurantId");
            
            ra.addFlashAttribute("success", "Carrinho limpo com sucesso");
            return "redirect:/web/cart";
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Erro ao limpar carrinho: " + e.getMessage());
            return "redirect:/web/cart";
        }
    }
}


