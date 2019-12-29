package com.mashibing.entity;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import java.util.List;

//@ConfigurationProperties(prefix = "person")
@Component
//@Validated
public class Person {

//    @Value("${person.name}")
//    @Email
    private String lastName;
    @Value("#{1+2}")
    private Integer age;
    private String sex;
    private List<String> likes;

    public Person() {
    }

    public Person(String lastName, Integer age, String sex, List<String> likes) {
        this.lastName = lastName;
        this.age = age;
        this.sex = sex;
        this.likes = likes;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public List<String> getLikes() {
        return likes;
    }

    public void setLikes(List<String> likes) {
        this.likes = likes;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Person{" +
                "lastName='" + lastName + '\'' +
                ", age=" + age +
                ", sex='" + sex + '\'' +
                ", likes=" + likes +
                '}';
    }
}
