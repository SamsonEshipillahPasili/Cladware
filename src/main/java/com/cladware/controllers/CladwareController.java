package com.cladware.controllers;

import com.cladware.entities.CladwareUser;
import com.cladware.repositories.CladwareUserRepository;
import com.cladware.repositories.ItemRepository;
import com.cladware.repositories.OrderRepository;
import com.cladware.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;

// controller class contains methods that map user requests to themselves.
@Controller
public class CladwareController {
    @Autowired
    private HttpSession httpSession;
    @Autowired
    private CladwareUserRepository cladwareUserRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/admin/addItem")
    public String addItem(){
        return "add_item";
    }

    @GetMapping("/")
    public String index(Principal principal){
        if(principal == null){
            return "index";
        }else{
            CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
            if(cladwareUser.getRole().equals("ADMIN")){
                return "redirect:/admin/inventory";
            }else{
                return "redirect:/dashboard";
            }
        }
    }

    @GetMapping("/admin/inventory")
    public String inventory(Model model){
        model.addAttribute("items", itemRepository.findAll());
        return "inventory";
    }

    @RequestMapping(value = "/logInPage", method = {RequestMethod.GET, RequestMethod.POST})
    public String logIn(){
        return "log_in";
    }

    @PostMapping("/logIn")
    public String postLogIn(){
        return "log_in";
    }

    @GetMapping("/admin/manageOrders")
    public String manageOrders(Principal principal, Model model){
        model.addAttribute("orders", this.orderRepository.findAll());
        return "manage_orders";
    }

    @GetMapping("/order")
    public String orderPage(Principal principal, Model model){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        model.addAttribute("cart", cladwareUser.getCart());
        model.addAttribute("name", cladwareUser.getFullname());
        return "order_page";
    }

    @GetMapping("/products")
    public String getProducts(Principal principal, Model model){
        model.addAttribute("principal", principal);
        return "products";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/settings")
    public String settings(Principal principal, Model model){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        model.addAttribute("cart", cladwareUser.getCart());
        return "settings";
    }

    @GetMapping("/logOut")
    public String logOut(){
        // invalidate session upon logout
        this.httpSession.invalidate();
        return "redirect:/logIn";
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model){
        if(principal == null){
            return "index";
        }else{
            CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
            if(cladwareUser.getRole().equals("ADMIN")){
                return "redirect:/admin/inventory";
            }else{
                model.addAttribute("rows", this.itemService.group(this.itemRepository.findAll()));
                model.addAttribute("cart", cladwareUser.getCart());
                return "dashboard";
            }
        }
    }


}