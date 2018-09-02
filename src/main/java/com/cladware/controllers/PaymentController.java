package com.cladware.controllers;
import com.cladware.configuration.PaypalPaymentIntent;
import com.cladware.configuration.PaypalPaymentMethod;
import com.cladware.entities.CladwareOrder;
import com.cladware.entities.CladwareUser;
import com.cladware.entities.Item;
import com.cladware.repositories.CladwareUserRepository;
import com.cladware.repositories.ItemRepository;
import com.cladware.repositories.OrderRepository;
import com.cladware.services.PaypalService;
import com.cladware.util.URLUtils;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class PaymentController {

    @Autowired
    private HttpSession session;
    @Autowired
    private ItemRepository itemRepository;
	
	public static final String PAYPAL_SUCCESS_URL = "order?orderPlaced";
	public static final String PAYPAL_CANCEL_URL = "order?cancelled";
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private PaypalService paypalService;

    @Autowired
    private CladwareUserRepository cladwareUserRepository;

    @Autowired
    private OrderRepository orderRepository;

	@RequestMapping(method = RequestMethod.POST, value = "pay")
	public String pay(HttpServletRequest request, Principal principal,
                      Model model,
                      @RequestParam String country,
                      @RequestParam String postalAddress,
                      @RequestParam String phoneNumber

    ){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        model.addAttribute("cart", cladwareUser.getCart());
        model.addAttribute("name", cladwareUser.getFullname());

        this.session.setAttribute("country", country);
        this.session.setAttribute("postalAddress", postalAddress);

        // description for the order payment.
        String orderDesc = "Order by " + cladwareUser.getFullname();

        // amount to be paid converted to dollars.
        double amount = (double) cladwareUser.getCart().getTotal() / 100;

		String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
		String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
		try {
			Payment payment = paypalService.createPayment(
                    amount,
					"USD",
					PaypalPaymentMethod.paypal,
					PaypalPaymentIntent.sale,
					orderDesc,
					cancelUrl, 
					successUrl);
			for(Links links : payment.getLinks()){
				if(links.getRel().equals("approval_url")){
                    CladwareOrder cladwareOrder = new CladwareOrder();
                    cladwareOrder.setName(cladwareOrder.getName());
                    cladwareOrder.setAddress(postalAddress);
                    cladwareOrder.setCountry(country);
                    cladwareOrder.setPhoneNumber(phoneNumber);
                    cladwareOrder.setStatus("Undelivered");
                    cladwareOrder.setOrderId(principal.getName() + "_" + System.currentTimeMillis());
                    cladwareOrder.setCart(cladwareUser.getCart());
                    this.orderRepository.save(cladwareOrder);

                    // save order id in session
                    this.session.setAttribute("lastOrderId", cladwareOrder.getOrderId());

                    // remove bought items.
                    removeBoughtItems(cladwareOrder);

					return "redirect:" + links.getHref();
				}
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}
		return "redirect:/order?error";
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_CANCEL_URL)
	public String cancelPay(Model model, Principal principal){
        CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();
        model.addAttribute("cart", cladwareUser.getCart());
        model.addAttribute("name", cladwareUser.getFullname());
		return "redirect:/order?cancelled";
	}

	@RequestMapping(method = RequestMethod.GET, value = PAYPAL_SUCCESS_URL)
	public String successPay(@RequestParam("paymentId") String paymentId,
                             @RequestParam("PayerID") String payerId,
                             Principal principal,
                             Model model){
		try {
		    // provide cart to the user.
            CladwareUser cladwareUser = this.cladwareUserRepository.findById(principal.getName()).get();

            model.addAttribute("cart", cladwareUser.getCart());
            model.addAttribute("name", cladwareUser.getFullname());

			Payment payment = paypalService.executePayment(paymentId, payerId);
			if(payment.getState().equals("approved")){
                CladwareOrder cladwareOrder = new CladwareOrder();
                cladwareOrder.setName(cladwareOrder.getName());
                cladwareOrder.setAddress((String) this.session.getAttribute("postalAddress"));
                cladwareOrder.setCountry((String) this.session.getAttribute("country"));
                cladwareOrder.setPhoneNumber((String) this.session.getAttribute("phoneNumber"));
                cladwareOrder.setStatus("Undelivered");
                cladwareOrder.setOrderId(principal.getName() + "_" + System.currentTimeMillis());

                cladwareOrder.setCart(cladwareUser.getCart());

                this.orderRepository.save(cladwareOrder);

                // save order id in session
                this.session.setAttribute("lastOrderId", cladwareOrder.getOrderId());

				return "redirect:/order?orderPlaced";
			}
		} catch (PayPalRESTException e) {
			log.error(e.getMessage());
		}

		return "redirect:/order?error";
	}

	@GetMapping("/receipt")
	public String generateReceipt(){
        return "redirect:/pdf/" + this.session.getAttribute("lastOrderId") + "/receipt";
    }

    private void removeBoughtItems(CladwareOrder order){
        Collection<Item> items = order.getCart().getItems();
        Map<String, Item> modifiedItems = new HashMap<>();
        // loop over the cart and get update the quantities of the items in the database.
        items.stream().map(Item::getCode).map(code -> code.replace("Cart_", ""))
                .forEach(itemCode -> {
                    if(modifiedItems.containsKey(itemCode)){
                        Item item = modifiedItems.get(itemCode);
                        item.setQuantity(item.getQuantity() - 1);
                    }else{
                        Item item = this.itemRepository.findById(itemCode).get();
                        item.setQuantity(item.getQuantity() - 1);
                        modifiedItems.put(item.getCode(), item);
                    }
                });

        // save the items back to the DB
        this.itemRepository.saveAll(modifiedItems.values());
    }
	
}
