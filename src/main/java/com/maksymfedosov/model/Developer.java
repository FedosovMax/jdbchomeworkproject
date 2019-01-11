package com.maksymfedosov.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Developer {

    private int id;
    private String first_name;
    private String middle_name;
    private String last_name;
    private int age;
    private int salary;

    public Developer(String first_name, String middle_name, String last_name, int age, int salary) {
        this.first_name = first_name;
        this.middle_name = middle_name;
        this.last_name = last_name;
        this.age = age;
        this.salary = salary;
    }
}
