package com.maksymfedosov.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class Project {
    private int project_id;
    private String project_name;
    private String start_time;
    private int company_id;
    private int cost;
}
