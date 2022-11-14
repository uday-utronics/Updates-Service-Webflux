package com.springReactive.updatesService.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {

    private int employeeId;

    private String employeeName;

    private String employeeCity;

    private String employeePhone;

    private double javaExperience;

    private double springExperience;

    @Override
    public String toString() {
        return "EmployeeRequest{" +
                "employeeId=" + employeeId + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", employeeCity='" + employeeCity + '\'' +
                ", employeePhone='" + employeePhone + '\'' +
                ", javaExperience=" + javaExperience + '\'' +
                ", springExperience=" + springExperience +'\'' +
                '}';
    }

}
