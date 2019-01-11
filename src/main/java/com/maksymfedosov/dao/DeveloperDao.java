package com.maksymfedosov.dao;

import com.maksymfedosov.Util.DBInitHelper;
import com.maksymfedosov.model.Company;
import com.maksymfedosov.model.Developer;
import com.maksymfedosov.model.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DeveloperDao{

    private static final String INSERT_SQL = "INSERT INTO developers (first_name, middle_name, last_name, age, salary) VALUES (?, ?, ?, ?, ?);";
    private static final String SELECT_ALL_SQL = "SELECT * FROM developers;";
    private static final String SELECT_BY_ID_SQL = "SELECT * FROM developers WHERE id = ?;";
    private static final String UPDATE_BY_ID_SQL = "UPDATE developers SET first_name = ?, middle_name = ?, last_name = ?, age = ?, salary = ? WHERE id = ?;";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM developers WHERE id = ?;";
    private static final String ADD_COMPANY_SQL = "INSERT INTO developers_projects (developer_id, project_id) VALUES (?, ?);";
    private static final String GET_DEVELOPERS_LIST_WITH_JAVA = "SELECT d.id, d.first_name, d.middle_name, d.last_name, d.age, d.salary\n" +
            "FROM developers d, skills s, developers_skills ds, languages l WHERE ds.developer_id=d.id AND ds.skills_id = s.id AND l.id = ?;";
    private static final String GET_DEVELOPERS_LIST_WITH_MIDDLE_LEVEL_SQL = "SELECT d.id, d.first_name, d.middle_name, d.last_name, d.age, d.salary\n" +
            "FROM developers d, skills s, developers_skills ds, skills_level sl WHERE ds.developer_id = d.id AND ds.skills_id = s.id AND sl.id = ?;";


    private Connection connection;

    public DeveloperDao() {this.connection = DBInitHelper.getConnection();
    }

    public void save(Developer developer) {
        try {
            saveDeveloper(developer, connection);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Error during saving a developer: %s", developer), e);
        }
    }

    private void saveDeveloper(Developer developer, Connection connection) throws SQLException {
        PreparedStatement insertStatement = prepareInsertStatement(developer, connection);
        executeUpdate(insertStatement);
        int id = fetchGeneratedId(insertStatement);
        developer.setId(id);
    }

    private PreparedStatement prepareInsertStatement(Developer developer, Connection connection) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL,
                    PreparedStatement.RETURN_GENERATED_KEYS);
            return fillInsertStatementWithParameters(insertStatement, developer);
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Can't prepare statement for developer %s" + developer), e);
        }
    }

    private PreparedStatement fillInsertStatementWithParameters(PreparedStatement insertStatement, Developer developer) throws SQLException {
        insertStatement.setString(1, developer.getFirst_name());
        insertStatement.setString(2, developer.getMiddle_name());
        insertStatement.setString(3, developer.getLast_name());
        insertStatement.setInt(4, developer.getAge());
        insertStatement.setInt(5, developer.getSalary());
        return insertStatement;
    }

    private void executeUpdate(PreparedStatement insertStatement) throws SQLException {
        int rowsAffected = insertStatement.executeUpdate();
        if(rowsAffected == 0){
            throw new RuntimeException("Nothing has been changed");
        }
    }

    private int fetchGeneratedId(PreparedStatement insertStatement) throws SQLException {
        ResultSet generatedKeys = insertStatement.getGeneratedKeys();
        if (generatedKeys.next()){
            return generatedKeys.getInt(1);
        }else {
            throw new RuntimeException("Can't retrieve programmer id");
        }
    }

    public List<Developer> findAll() {
            try {
                Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(SELECT_ALL_SQL);
                return collectToList(rs);
            }catch (Exception e){
                throw new RuntimeException("can't find all Developers in the base");
            }
    }

    private List<Developer> collectToList(ResultSet rs) throws SQLException {
        List <Developer> developers = new ArrayList<>();
        while(rs.next()){
            Developer developer = parseRow(rs);
            developers.add(developer);
        }
        return developers;
    }

    private Developer parseRow(ResultSet rs) throws SQLException {
        Developer developer = new Developer();
        developer.setId(rs.getInt("id"));
        developer.setFirst_name(rs.getString("first_name"));
        developer.setMiddle_name(rs.getString("middle_name"));
        developer.setLast_name(rs.getString("last_name"));
        developer.setAge(rs.getInt("age"));
        developer.setSalary(rs.getInt("salary"));
        return developer;
    }

    public Developer findOne(int id){
        Objects.requireNonNull(id);
        try {
            PreparedStatement selectByIdStatement = connection.prepareStatement(SELECT_BY_ID_SQL);
            selectByIdStatement.setLong(1, id);

            ResultSet rs = selectByIdStatement.executeQuery();
            if(rs.next()){
                return parseRow(rs);
            }else{
                throw new RuntimeException("Can't parse rows from the DB");
            }

        } catch (SQLException e) {
            throw new RuntimeException("There is no such Developer in the DB");
        }
    }

    public void update(Developer developer){
        Objects.requireNonNull(developer);
        try {
            updateDeveloper(developer, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Can't update developer " + e);
        }
    }

    private void updateDeveloper(Developer developer, Connection connection) throws SQLException {
        PreparedStatement updateStatement = prepareUpdateStatement(developer, connection);
        executeUpdate(updateStatement);
    }

    private PreparedStatement prepareUpdateStatement(Developer developer, Connection connection) {
        try {
            PreparedStatement updateStatement = connection.prepareStatement(UPDATE_BY_ID_SQL);
            updateStatement.setString(1, developer.getFirst_name());
            updateStatement.setString(2, developer.getMiddle_name());
            updateStatement.setString(3, developer.getLast_name());
            updateStatement.setInt(4, developer.getAge());
            updateStatement.setInt(5, developer.getSalary());
            updateStatement.setInt(6, developer.getId());
            return updateStatement;
        } catch (SQLException e) {
            throw new RuntimeException("can't update prepared statement " + e);
        }
    }

    public void delete (Developer developer){
        Objects.requireNonNull(developer);
        try {
            PreparedStatement removeStatement = connection.prepareStatement(DELETE_BY_ID_SQL);
            removeStatement.setInt(1, developer.getId());
            executeUpdate(removeStatement);
        } catch (SQLException e) {
            throw  new RuntimeException("Can't delete the developer " + e);
        }
    }

//    public void addProject(Developer developer, Project project){
//        try {
//            PreparedStatement addProjectStatement = connection.prepareStatement(ADD_COMPANY_SQL);
//            addProjectStatement.setInt(1, developer.getId());
//            addProjectStatement.setInt(2, project.getProject_id());
//            executeUpdate(addProjectStatement);
//            developer.setProject(project);
//        } catch (SQLException e) {
//            throw new RuntimeException("Can't add a project to the developer " + e);
//        }
//    }

    public List<Developer> getAllDevelopersWhichLanguageIsJava(int language_id){
        try {
            PreparedStatement getDevelopersStatement = connection.prepareStatement(GET_DEVELOPERS_LIST_WITH_JAVA);
            getDevelopersStatement.setInt(1, language_id);
            ResultSet resultSet = getDevelopersStatement.executeQuery();
            List<Developer> list = new ArrayList<>();
            while(resultSet.next()) {
                Developer developer = parseRow(resultSet);
                list.add(developer);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of developers which use Java language " + e);
        }
    }

    public List<Developer> getAllDevelopersWithParticularLevel(int level_id){
        try {
            PreparedStatement getMiddlesStatement = connection.prepareStatement(GET_DEVELOPERS_LIST_WITH_MIDDLE_LEVEL_SQL);
            getMiddlesStatement.setInt(1, level_id);
            ResultSet resultSet = getMiddlesStatement.executeQuery();
            List<Developer> middlesList = new ArrayList<>();
            while(resultSet.next()) {
                Developer developer = parseRow(resultSet);
                middlesList.add(developer);
            }
            return middlesList;
        } catch (SQLException e) {
            throw new RuntimeException("Can't get list of developers which have middle level " + e);
        }
    }

}
