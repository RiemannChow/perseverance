package com.riemann;

import com.riemann.entity.Employee;
import com.riemann.repository.EmployeeRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.Random;

@SpringBootApplication
public class MongoDBRepositoryApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MongoDBRepositoryApplication.class, args);
        EmployeeRepository employeeRepository = context.getBean(EmployeeRepository.class);
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Employee resume = new Employee();
            resume.setName("test" + (i + 1));
            resume.setBirthday(new Date());
            resume.setExpectSalary((i + 1) * random.nextDouble());
            resume.setCity("shenzhen");
            employeeRepository.insert(resume);
            System.out.println("insert success");
        }
        System.out.println("query start");
        employeeRepository.findAll().forEach(employee -> System.out.println(employee));
    }
}
