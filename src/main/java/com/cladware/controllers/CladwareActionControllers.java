package com.cladware.controllers;

import com.cladware.entities.CladwareOrder;
import com.cladware.entities.CladwareUser;
import com.cladware.entities.Item;
import com.cladware.repositories.CladwareUserRepository;
import com.cladware.repositories.ItemRepository;
import com.cladware.repositories.OrderRepository;
import com.cladware.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;

@Controller
public class CladwareActionControllers {
    @Autowired
    private CladwareUserRepository cladwareUserRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;
    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/registerNew")
    public String register(@RequestParam String fullname, @RequestParam String email,
                           @RequestParam  String password, @RequestParam  String confirmPassword,
                           @RequestParam String gender){
        if(!password.trim().equals(confirmPassword.trim())){
            return "redirect:/register?passwordError";
        }else if(this.cladwareUserRepository.existsById(email.trim())){
            return "redirect:/register?duplicate";
        }else{
            if(!gender.trim().equals("Male") && !gender.trim().equals("Female") && !gender.trim().equals("Other")){
                return "redirect:/register?invalidGender";
            }else if(!fullname.trim().isEmpty()){
                // save the user to db
                CladwareUser cladwareUser = new CladwareUser();
                cladwareUser.setEmail(email);
                cladwareUser.setFullname(fullname);
                cladwareUser.setGender(gender);
                cladwareUser.setRole("USER");
                cladwareUser.setPassword(this.bCryptPasswordEncoder.encode(password));

                this.cladwareUserRepository.save(cladwareUser);
                return "redirect:/register?success";
            }else{
                return "redirect:/register?invalidName";
            }
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(Principal principal, String oldPassword, String newPassword, String confirmPassword){
        if(confirmPassword.trim().equals(newPassword)){
             if(null != principal){
                 CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
                 if(this.bCryptPasswordEncoder.matches(oldPassword, cladwareUser.getPassword())){
                     // the passwords match, update the user and store them to db
                     cladwareUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                     this.cladwareUserRepository.save(cladwareUser);
                     return "redirect:/settings?success";
                 }else{
                     return "redirect:/settings?invalidPassword";
                 }
             }else{
                 return "redirect:/settings?logIn";
             }
        }else{
            return "redirect:/settings?empty";
        }
    }

    @PostMapping("/deleteFromCart")
    public String deleteFromCart(Principal principal, @RequestParam String code){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        cladwareUser.getCart().deleteItem(code);
        this.cladwareUserRepository.save(cladwareUser);
        return "redirect:/order";
    }

    @PostMapping("/clearCart")
    public String clearCart(Principal principal){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        cladwareUser.getCart().clearCart();
        this.cladwareUserRepository.save(cladwareUser);
        return "redirect:/order";
    }

    @PostMapping("/deleteItem")
    public String deleteItem(@RequestParam String code){
        this.itemRepository.deleteById(code);
        return "redirect:/admin/inventory?deleted";
    }


    @PostMapping("/addItem")
    public String addItem(@RequestParam String code, @RequestParam String name, @RequestParam int unitPrice,
                          @RequestParam int quantity, @RequestParam String description, @RequestParam MultipartFile itemPicture) throws IOException {
        if(!code.trim().isEmpty() && !name.trim().isEmpty() && !description.trim().isEmpty()){

            String returnURL = "redirect:/admin/addItem?added";
            if(this.itemRepository.existsById(code.trim())){
                returnURL = "redirect:/admin/inventory";
            }

            if(itemPicture != null && !itemPicture.isEmpty()){
                this.itemService.addItem(code, name, unitPrice, quantity, description, itemPicture.getBytes());
                return returnURL;
            }else{
                this.itemService.addItem(code, name, unitPrice, quantity, description, null);
                return returnURL;
            }
        }else{
            return "redirect:/admin/addItem?emptyFields";
        }
    }

    @PostMapping("/addToCart")
    public String addToCart(Principal principal, @RequestParam String code){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        Item item = this.itemRepository.findById(code).get();
        cladwareUser.getCart().addToCart(item.getCode(), item);
        this.cladwareUserRepository.save(cladwareUser);
        return "redirect:/dashboard";
    }

    @PostMapping("/placeOrder")
    public String placeOrder(Principal principal, @RequestParam  String name, @RequestParam String phoneNumber,
                             @RequestParam String country, @RequestParam  String postalAddress, @RequestParam String paymentMethod,
                             @RequestParam String cardNumber){
        if(!name.trim().isEmpty() && !phoneNumber.trim().isEmpty() && !country.trim().isEmpty()
                && !postalAddress.trim().isEmpty() && !paymentMethod.trim().isEmpty() && !cardNumber.trim().isEmpty()){
            try{
                CladwareOrder cladwareOrder = new CladwareOrder();
                cladwareOrder.setCardNumber(Integer.parseInt(cardNumber));
                cladwareOrder.setName(name);
                cladwareOrder.setAddress(postalAddress);
                cladwareOrder.setCountry(country);
                cladwareOrder.setPhoneNumber(phoneNumber);
                cladwareOrder.setPaymentMethod(paymentMethod);
                cladwareOrder.setStatus("Undelivered");

                CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
                cladwareOrder.setCart(cladwareUser.getCart());

                this.orderRepository.save(cladwareOrder);

                return "redirect:/order?orderPlaced";
            }catch(Exception e){
                e.printStackTrace();
                return "redirect:/order?error";
            }
        }else{
            return "redirect:/order?error";
        }
    }

}
