package com.example.beststore.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CustomerDto {

    @NotEmpty(message = "กรุณาใส่ขื่อ")
    private String name;

    @NotEmpty(message = "กรุณาใส่เบอร์โทรศัพท์")
    private String phoneNumber;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
