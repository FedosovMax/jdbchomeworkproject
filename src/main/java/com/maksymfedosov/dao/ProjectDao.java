package com.maksymfedosov.dao;

import com.maksymfedosov.Util.DBInitHelper;
import com.maksymfedosov.model.Company;
import com.maksymfedosov.model.Developer;
import com.maksymfedosov.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProjectDao {

    private static final String INSERT_SQL = "INSERT INTO projects(project_name, start_time, company_id, cost ) VALUES (?, ?, ?, ?);";
    private static final String FIND_ONE_BY_ID_SQL = "SELECT * FROM projects WHERE project_id = ?;";
    private static final String FIND_ALL_SQL = "SELECT * FROM projects;";
    private static final String UPDATE_BY_ID_SQL = "UPDATE projects SET project_name = ?, start_time = ?, company_id = ?, cost = ? WHERE id = ?;";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM projects WHERE project_id = ?;";
    private static final String GET_SALARIES_IN_PROJECT_SQL = "SELECT sum(d.salary) FROM developers d, projects p, developers_projects dp\n" +
            "WHERE dp.developer_id = d.id AND dp.project_id = p.project_id AND p.project_id = ?;";
    private static final String GET_DEVELOPERS_LIST_OF_PROJECT = "SELECT d.id, d.first_name, d.middle_name, d.last_name, d.age, d.salary FROM developers d, projects p, developers_projects dp\n" +
            "WHERE dp.developer_id = d.id AND dp.project_id = p.project_id AND p.project_id = ?;";
    private static final String GET_ALL_PROJECTS_WITH_DEVELOPERS_COUNT_SQL = "SELECT p.start_time, p.project_name, count(dp.developer_Id)\n" +
            "FROM developers d, projects p, developers_projects dp WHERE dp.project_id = p.project_id AND dp.developer_id = d.id GROUP BY p.project_name;" ;

    private Connection connection;

    public ProjectDao() {
        this.connection = DBInitHelper.getConnection();
    }

    public void save(Project project){
        Objects.requireNonNull(project);
        try {
            PreparedStatement insertStatement = prepareInsertStatement(project, connection);
            executeUpdate(insertStatement);
            int id = fetchGeneratedId(insertStatement);
            project.setCompany_id(id);
        } catch (SQLException e) {
            throw new RuntimeException("Can't save the project " + e);
        }
    }

    private PreparedStatement prepareInsertStatement(Project project, Connection connection) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillInsertStatementWithParameters(insertStatement, project);
        } catch (SQLException e) {
            throw new RuntimeException("Can't prepare statement for unserting the new project " + e);
        }
    }

    private PreparedStatement fillInsertStatementWithParameters(PreparedStatement insertStatement, Project project) throws SQLException {
        insertStatement.setString(1, project.getProject_name());
        insertStatement.setString(2, project.getStart_time());
        insertStatement.setInt(3, project.getCompany_id());
        insertStatement.setInt(4, project.getCost());
        return insertStatement;
    }


    private void executeUpdate(PreparedStatement insertStatement) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if (rowsAffected == 0){
            throw new RuntimeException("Nothing has been changed during executing update");
        }
    }

    private int fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if(generatedKeys.next()){
            return generatedKeys.getInt(1);
        }else{
            throw new RuntimeException("Can't update generated keys");
        }
    }

    public Project findOne(int id){
        try {
            PreparedStatement findOneStatement = connection.prepareStatement(FIND_ONE_BY_ID_SQL);
            findOneStatement.setInt(1, id);
            ResultSet rs = findOneStatement.executeQuery();
            if(rs.next()){
                return parseRow(rs);
            }else{
                throw new RuntimeException("Can't parse the project");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find the project by the id");
        }
    }

    private Project parseRow(ResultSet rs) {
        try {
            return createFromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Can't parse any row " + e);
        }
    }

    private Project createFromResultSet(ResultSet rs) throws SQLException {
        Project project = new Project();
        project.setProject_id(rs.getInt("project_id"));
        project.setProject_name(rs.getString("project_name"));
        project.setStart_time(rs.getString("start_time"));
        project.setCompany_id(rs.getInt("company_id"));
        project.setCost(rs.getInt("cost"));
        return project;
    }

    public List<Project> findAll(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            return collectToList(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all projects " + e);
        }
    }

    private List<Project> collectToList(ResultSet resultSet) throws SQLException {
        List<Project> projects = new ArrayList<>();
        while(resultSet.next()){
            Project project = parseRow(resultSet);
            projects.add(project);
        }
        return projects;
    }

    public void update(Project project){
        Objects.requireNonNull(project);
        try {
            PreparedStatement updateStatement = prepareUpdateStatement(project, connection);
            executeUpdate(updateStatement);
        } catch (SQLException e) {
            throw new RuntimeException("Not able to update the project " + e);
        }
    }

    private PreparedStatement prepareUpdateStatement(Project project, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BY_ID_SQL);
            preparedStatement.setString(1, project.getProject_name());
            preparedStatement.setString(2, project.getStart_time());
            preparedStatement.setInt(3, project.getCompany_id());
            preparedStatement.setInt(4, project.getProject_id());
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException("Cant prepare UpdateStatement");
        }
    }

    public void delete(Project project){
        Objects.requireNonNull(project);
        try {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_BY_ID_SQL);
            deleteStatement.setInt(1, project.getProject_id());
            executeUpdate(deleteStatement);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete project " + e);
        }
    }

    public int getSalarySumInProjectById(int projectId){
        try {
            PreparedStatement getSalariesStatement = connection.prepareStatement(GET_SALARIES_IN_PROJECT_SQL);
            getSalariesStatement.setInt(1, projectId);
            ResultSet resultSet = getSalariesStatement.executeQuery();
            if (resultSet.next()){
                return parseRowInt(resultSet);
            }else {
                throw new RuntimeException("Can't parse resultSet from the query");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Can't get developers salaries from this project");
        }
    }

    private int parseRowInt(ResultSet resultSet) throws SQLException {
        int sumOfSalaries = resultSet.getInt("sum(d.salary)");
        return sumOfSalaries;
    }

    public List<Developer> getDevelopersListOfProject(int project_id) {
        try {
            PreparedStatement getDevelopersStatement = connection.prepareStatement(GET_DEVELOPERS_LIST_OF_PROJECT);
            getDevelopersStatement.setInt(1, project_id);
            ResultSet resultSet = getDevelopersStatement.executeQuery();
            List<Developer> list = new ArrayList<>();
            while(resultSet.next()) {
                Developer developer = parseDeveloperRow(resultSet);
                list.add(developer);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Cna't get list of developers from this project " + e);
        }

    }

    private Developer parseDeveloperRow(ResultSet rs) throws SQLException {
        Developer developer = new Developer();
        developer.setId(rs.getInt("d.id"));
        developer.setFirst_name(rs.getString("d.first_name"));
        developer.setMiddle_name(rs.getString("d.middle_name"));
        developer.setLast_name(rs.getString("d.last_name"));
        developer.setAge(rs.getInt("d.age"));
        developer.setSalary(rs.getInt("d.salary"));
        return developer;
    }
    
    public List<Object[]> getAllProjectsWithDevelopersCount() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(GET_ALL_PROJECTS_WITH_DEVELOPERS_COUNT_SQL);
            return collectToListWithDevelopersCount(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all projects with Developers count " + e);
        }
    }

    private List<Object[]> collectToListWithDevelopersCount(ResultSet resultSet) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        while(resultSet.next()){
            Object[] resultObject = parseRowWithSumOfDevelopers(resultSet);
            list.add(resultObject);
        }
        return list;
    }

    private Object[] parseRowWithSumOfDevelopers(ResultSet resultSet) throws SQLException {
        Object[] object = new Object[]{
                resultSet.getString("p.start_time"),
                resultSet.getString("p.project_name"),
                resultSet.getInt("count(dp.developer_Id)")
        };
        return object;
    }


}
