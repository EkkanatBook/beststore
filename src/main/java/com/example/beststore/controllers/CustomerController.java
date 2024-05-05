package com.example.beststore.controllers;

import com.example.beststore.dto.CustomerDto;
import com.example.beststore.dto.MenuDto;
import com.example.beststore.models.Customer;
import com.example.beststore.models.Menu;
import com.example.beststore.services.CustomerRepository;
import com.example.beststore.services.MenuRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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

    @GetMapping("/orderList")
    public String showOrderList(Model model){
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "customers/orderList";
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
            Model model,
            @RequestParam int id,
            @ModelAttribute MenuDto menuDto,
            @ModelAttribute CustomerDto customerDto
    ){
        Date createdAt = new Date();
        Customer customer = new Customer();
        Menu menu = menuRepository.findById(id).get();
        model.addAttribute("menu", menu);

        customer.setMenuId(menu.getId());
        customer.setMenuName(menu.getName());
        customer.setMenuPrice(menu.getPrice());
        customer.setName(customerDto.getName());
        customer.setPhoneNumber(customerDto.getPhoneNumber());
        customer.setCreatedAt(createdAt);


        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/updateStatus")
    public String updateStatus(
            @RequestParam int id
    ) {
        Customer customer = customerRepository.findById(id).get();

        if (customer.getStatus().equals("รอดำเนินงาน")){
            customer.setStatus("กำลังดำเนิดงาน");
        } else if (customer.getStatus().equals("กำลังดำเนิดงาน")) {
            customer.setStatus("อาหารเสร็จแล้ว");
        } else if (customer.getStatus().equals("อาหารเสร็จแล้ว")) {
            customer.setStatus("รับอาหารเรียบร้อย");
        }

        customerRepository.save(customer);
        return "redirect:/customers/orderList";
    }

    @GetMapping("/delete")
    public String deleteMenu(
            @RequestParam int id
    ){
        try {
            Customer customer = customerRepository.findById(id).get();

            customerRepository.delete(customer);
        } catch (Exception ex){
            System.out.println("Exception: "+ ex.getMessage());
        }
        return "redirect:/customers/orderList";
    }
}
