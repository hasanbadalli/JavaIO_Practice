import enums.Department;
import enums.Position;

import java.io.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        List<Employee> modifiedEmployer = processEmployees(readEmployeeData("src/employees.txt"));
        //modifiedEmployer.forEach(System.out::println);

        writeProcessedData(modifiedEmployer, "src/processed_employees.txt");
        departmentsReport(modifiedEmployer, "src/departments_report.txt");
        duplicateEmployee(modifiedEmployer, "src/duplicate_employees.txt");
        calculateAverageSalary(modifiedEmployer, "src/average_salary.txt");
        longestServingEmployees(modifiedEmployer, "src/longest_serving_employee.txt");
        positionCount(modifiedEmployer, "src/position_count.txt");
        groupEmployeesByDepartmentAndPosition(modifiedEmployer, "src/group_employees_by_department.txt");
    }



    /////         READ EMPLOYEE DATA           ///////////////
    public static List<Employee> readEmployeeData(String filepath){
        List<Employee> employees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Employee employee = new Employee();
                employee.setId(Integer.parseInt(data[0]));
                employee.setName(data[1]);
                employee.setSurname(data[2]);
                employee.setAge(Integer.parseInt(data[3]));
                employee.setSalary(Double.parseDouble(data[4]));
                employee.setDepartment(Department.valueOf(data[5]));
                employee.setEmployer(Boolean.parseBoolean(data[6]));
                employee.setStartDate(LocalDate.parse(data[7]));
                employee.setEmail(data[8]);
                employee.setPhoneNumber(data[9]);
                employee.setPosition(Position.valueOf(data[10]));
                employee.setAddress(data[11]);

                employees.add(employee);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return employees;
    }


    ////     Filter, Sorted , Modify /////
    public static List<Employee> processEmployees(List<Employee> employees) {
        return employees.stream()
                .filter(e -> !e.getPosition().equals(Position.INTERN))
                .sorted(Comparator.comparing(Employee::getStartDate))
                .peek(e -> {
                    if (e.getDepartment() == Department.IT && ChronoUnit.YEARS.between(e.getStartDate(), LocalDate.now()) >= 10) {
                        e.setSalary(e.getSalary() * 1.1);
                    }
                })
                .toList();
    }

    //// processed data
    public static void writeProcessedData(List<Employee> employees, String filePath) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Employee employee : employees) {
                bw.write(employee.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //// DEPARTMENTS REPORT     //////////////////////////////////////////
    public static void departmentsReport(List<Employee> employees, String filePath) {
        Map<Department, Long> departmentsSummary = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.counting()));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Department, Long> entry : departmentsSummary.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void duplicateEmployee(List<Employee> employees, String filePath) {
        Set<Employee> duplicateEmployees = new HashSet<>(employees);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            duplicateEmployees.forEach(employee -> {
                try {
                    bw.write(String.valueOf(employee));
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /////   AVERAGE SALARY BY DEPARTMEMTS   /////////////////////////
    public static void calculateAverageSalary(List<Employee> employees, String filePath) {
        Map<Department, Double> departmentAverageSalary = employees.stream().collect(Collectors.groupingBy(Employee::getDepartment, Collectors.averagingDouble(Employee::getSalary)));


        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            departmentAverageSalary.forEach((department, salary) -> {
                try {
                    bw.write(department + ": " + salary);
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    ////////////   LONGEST SERVING EMPLOYEE   ///////////////////////
    public static void longestServingEmployees(List<Employee> employees, String filePath) {
        Employee employee = employees.stream().min(Comparator.comparing(Employee::getStartDate)).get();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(employee.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //// POSITION REPORT     //////////////////////////////////////////
    public static void positionCount(List<Employee> employees, String filePath) {
        Map<Position, Long> positinCount = employees.stream().collect(Collectors.groupingBy(Employee::getPosition, Collectors.counting()));

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<Position, Long> entry : positinCount.entrySet()) {
                bw.write(entry.getKey() + ": " + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    ////
    public static void groupEmployeesByDepartmentAndPosition(List<Employee> employees, String filePath) {

        Map<Department, Map<Position, List<Employee>>> groupedEmployees = employees.stream()
                .collect(Collectors.groupingBy(
                        Employee::getDepartment,
                        Collectors.groupingBy(Employee::getPosition)
                ));

        try(BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))){
            groupedEmployees.forEach((department, positions) -> {
                try {
                    bw.write("Department-" + department + ": ");
                    bw.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                positions.forEach((position, employee) -> {
                    try {

                        bw.write(position + ": ");
                        bw.newLine();
                        employee.forEach(e -> {
                            try {
                                bw.write(String.valueOf(e));
                                bw.newLine();
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        });
                        bw.newLine();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            });
        }catch (IOException e){
            e.printStackTrace();
        }

    }

}