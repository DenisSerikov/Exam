package com.dataPg.rest.repository;

import com.dataPg.rest.model.Person;
import java.util.ArrayList;
import java.util.List;

public class PersonRepositoryImpl implements PersonRepository {

    private List<Person> persons = new ArrayList<>();
    private int nextId = 1;

    @Override
    public List<Person> findAll() {
        return persons;
    }

    @Override
    public Person findById(int id) {
        return persons.stream()
                .filter(person -> person.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public Person save(Person person) {
        if (person.getId() == 0) {
            person.setId(nextId++);
            persons.add(person);
        } else {
            persons.removeIf(p -> p.getId() == person.getId());
            persons.add(person);
        }
        return person;
    }

    @Override
    public void deleteById(int id) {
        persons.removeIf(person -> person.getId() == id);
    }

    @Override
    public Person findByLogin(String login) {
        return persons.stream()
                .filter(person -> person.getLogin().equals(login))
                .findFirst()
                .orElse(null);
    }
}
