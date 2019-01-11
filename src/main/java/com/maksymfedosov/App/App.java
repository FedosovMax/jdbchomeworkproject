package com.maksymfedosov.App;

import com.maksymfedosov.dao.CompanyDao;
import com.maksymfedosov.dao.DeveloperDao;
import com.maksymfedosov.dao.ProjectDao;
import com.maksymfedosov.model.Developer;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class App {
    CompanyDao comDao;
    DeveloperDao devDao;
    ProjectDao projDao;

    public App() {

        comDao = new CompanyDao();
        devDao = new DeveloperDao();
        projDao = new ProjectDao();

        Scanner scanner = new Scanner(System.in);
        String currentCommand = null;
        do {
            printMenu();
            currentCommand = scanner.nextLine();

            if (currentCommand.startsWith("salaries_sum")) {
                String[] commandParts = currentCommand.split(" ");
                int projectId = Integer.parseInt(commandParts[1]);
                showSalariesInProject(projectId);
            } else if (currentCommand.startsWith("list_devs_at_project")) {
                String[] commandParts = currentCommand.split(" ");
                int projectId = Integer.parseInt(commandParts[1]);
                showDevelopersFromProjectById(projectId);
            } else if (currentCommand.startsWith("list_devs_language")) {
                showJavaDevelopers();
            } else if (currentCommand.startsWith("list_devs_middle")){
                showMiddleLevelDevelopers();
            } else if (currentCommand.startsWith("list_projects")){
                showAllProjects();
            }
        } while (!currentCommand.equals("exit"));
        scanner.close();
    }

    public static void main(String[] args) throws SQLException {
        new App();
    }

    private void printMenu() {
            System.out.println("Insert the command and press Enter:\n" +
                    "1. The sum of developer's salaries in the project (salaries_sum <project_id>)\n" +
                    "2. List of developers in the particular project (list_devs_at_project <project_id>)\n" +
                    "3. List of all java developers (list_devs_language)\n" +
                    "4. List of all middle developers (list_devs_middle)\n" +
                    "5. List of all projects (list_projects)\n" +
                    "6. Выход (exit)\n");
        }

        private void showSalariesInProject(int projectId){
            int sumOfProject = projDao.getSalarySumInProjectById(projectId);
            System.out.println("The name of company: " + projDao.findOne(projectId).getProject_name());
            System.out.println("The sum of salaries in this project is: " + sumOfProject);
            System.out.println("\n");
        }

        private void showDevelopersFromProjectById(int projectId){
            List<Developer> developers = projDao.getDevelopersListOfProject(projectId);
            System.out.println("Developers from project " + projDao.findOne(projectId).getProject_name() + " are: ");
            System.out.println("ID | NAME | MIDDLE_NAME | LAST_NAME | AGE | SALARY");
            for (Developer developer:developers) {
                System.out.println(developer.getId() + " | " + developer.getFirst_name() + " | " + developer.getMiddle_name() + " | " + developer.getLast_name() + " | "
                + developer.getAge() + " | " + developer.getSalary());
            }
            System.out.println("\n");
        }

        private void showJavaDevelopers(){
            List<Developer> developers = devDao.getAllDevelopersWhichLanguageIsJava(201); //201 is Java language id in the database
            System.out.println("The list of developers which use Java programming language");
            System.out.println("ID | NAME | MIDDLE_NAME | LAST_NAME | AGE | SALARY");
            for (Developer developer:developers) {
                System.out.println(developer.getId() + " | " + developer.getFirst_name() + " | " + developer.getMiddle_name() + " | " + developer.getLast_name() + " | "
                        + developer.getAge() + " | " + developer.getSalary());
            }
            System.out.println("\n");
        }

        private void showMiddleLevelDevelopers(){
            List<Developer> developers = devDao.getAllDevelopersWithParticularLevel(102); //102 is Middle level id in the database
            System.out.println("Developers with middle level are: ");
            System.out.println("ID | NAME | MIDDLE_NAME | LAST_NAME | AGE | SALARY");
            for (Developer developer:developers) {
                System.out.println(developer.getId() + " | " + developer.getFirst_name() + " | " + developer.getMiddle_name() + " | " + developer.getLast_name() + " | "
                        + developer.getAge() + " | " + developer.getSalary());
            }
            System.out.println("\n");
        }

        private void showAllProjects(){
            List <Object[]> projects = projDao.getAllProjectsWithDevelopersCount();
            System.out.println("List of projects with developers count");
            System.out.println("CREATION_DATE | PROJECT_NAME | DEVELOPERS_COUNT" );
            for (Object[] object:projects) {
                System.out.println(object[0] + " | " + object[1] + " | " + object[2]);
            }
            System.out.println("\n");
        }



}