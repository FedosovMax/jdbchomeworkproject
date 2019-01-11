package com.maksymfedosov.model;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Company {
    private int company_id;
    private String company_name;
    private String company_country;

    private Developer developer;

}
