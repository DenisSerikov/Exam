package com.dataPg.rest.model;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String tin;
    private Timestamp hired;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Person> accounts = new ArrayList<>();

    public Employee() {
    }

    public Employee(String name, String tin, Timestamp hired, List<Person> accounts) {
        this.name = name;
        this.tin = tin;
        this.hired = hired;
        this.accounts = accounts;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public Timestamp getHired() {
        return hired;
    }

    public void setHired(Timestamp hired) {
        this.hired = hired;
    }

    public List<Person> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Person> accounts) {
        this.accounts = accounts;
    }

    public void addAccount(Person person) {
        accounts.add(person);
    }

    public void removeAccount(Person person) {
        accounts.remove(person);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return id == employee.id &&
                Objects.equals(name, employee.name) &&
                Objects.equals(tin, employee.tin) &&
                Objects.equals(hired, employee.hired) &&
                Objects.equals(accounts, employee.accounts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, tin, hired, accounts);
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", tin='" + tin + '\'' +
                ", hired=" + hired +
                ", accounts=" + accounts +
                '}';
    }
}
