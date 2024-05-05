package com.example.beststore.controllers;

import com.example.beststore.dto.CustomerDto;
import com.example.beststore.models.Customer;
import com.example.beststore.models.Menu;
import com.example.beststore.services.CustomerRepository;
import com.example.beststore.services.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping({"","/"})
    public String showMenuList(Model model){
        List<Menu> menus = menuRepository.findAll();
        model.addAttribute("menus", menus);
        return "customers/index";
    }

    @GetMapping("/confirmOrder")
    public String showConfirmOrder(
            Model model,
            @RequestParam int id
    ){

        try {

            Menu menu = menuRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Menu not found with id: " + id));
            model.addAttribute("menu", menu);

            CustomerDto customerDto = new CustomerDto();
            model.addAttribute("customerDto", customerDto);


        } catch (Exception e){
            System.out.println("Exception: "+ e.getMessage());
            return "redirect:/customers";
        }

        return "customers/Confirm";
    }

    @PostMapping("/confirmOrder")
    public String confirmOrder(
            @ModelAttribute("customerDto") CustomerDto customerDto
    ){

        // สร้างอ็อบเจกต์ Customer และกำหนดค่า
        Customer customer = new Customer();
        customer.setName(customerDto.getName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());

        customerRepository.save(customer);

        return "redirect:/customers";
    }



}
