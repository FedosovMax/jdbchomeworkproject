package com.maksymfedosov.dao;

import com.maksymfedosov.Util.DBInitHelper;
import com.maksymfedosov.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CompanyDao {

    private static final String INSERT_SQL = "INSERT INTO companies(company_name, company_country) VALUES (?, ?);";
    private static final String FIND_ONE_BY_ID_SQL = "SELECT * FROM companies WHERE company_id = ?;";
    private static final String FIND_ALL_SQL = "SELECT * FROM companies;";
    private static final String UPDATE_BY_ID_SQL = "UPDATE companies SET company_name = ?, company_country = ? WHERE id = ?;";
    private static final String DELETE_BY_ID_SQL = "DELETE FROM companies WHERE company_id = ?;";

    private Connection connection;

    public CompanyDao() {
        this.connection = DBInitHelper.getConnection();
    }

    public void save(Company company){
        Objects.requireNonNull(company);
        try {
            PreparedStatement insertStatement = prepareInsertStatement(company, connection);
            executeUpdate(insertStatement);
            int id = fetchGeneratedId(insertStatement);
            company.setCompany_id(id);
        } catch (SQLException e) {
            throw new RuntimeException("Can't save the company " + e);
        }
    }

    private PreparedStatement prepareInsertStatement(Company company, Connection connection) {
        try {
            PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL, PreparedStatement.RETURN_GENERATED_KEYS);
            return fillInsertStatementWithParameters(insertStatement, company);
        } catch (SQLException e) {
            throw new RuntimeException("Can't prepare statement for unserting the new company " + e);
        }
    }

    private PreparedStatement fillInsertStatementWithParameters(PreparedStatement insertStatement, Company company) throws SQLException {
        insertStatement.setString(1, company.getCompany_name());
        insertStatement.setString(2, company.getCompany_country());
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

    public Company findOne(int id){
        try {
            PreparedStatement findOneStatement = connection.prepareStatement(FIND_ONE_BY_ID_SQL);
            findOneStatement.setInt(1, id);
            ResultSet rs = findOneStatement.executeQuery();
            if(rs.next()){
                return parseRow(rs);
            }else{
                throw new RuntimeException("Can't parse the company");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Can't find the company by the id");
        }
    }

    private Company parseRow(ResultSet rs) {
        try {
            return createFromResultSet(rs);
        } catch (SQLException e) {
            throw new RuntimeException("Can't parse any row " + e);
        }
    }

    private Company createFromResultSet(ResultSet rs) throws SQLException {
        Company company = new Company();
        company.setCompany_id(rs.getInt("company_id"));
        company.setCompany_name(rs.getString("company_name"));
        company.setCompany_country(rs.getString("company_country"));
        return company;
    }

    public List<Company> findAll(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(FIND_ALL_SQL);
            return collectToList(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Can't get all companies " + e);
        }
    }

    private List<Company> collectToList(ResultSet resultSet) throws SQLException {
        List<Company> companies = new ArrayList<>();
        while(resultSet.next()){
            Company company = parseRow(resultSet);
            companies.add(company);
        }
        return companies;
    }

    public void update(Company company){
        Objects.requireNonNull(company);
        try {
            PreparedStatement updateStatement = prepareUpdateStatement(company, connection);
            executeUpdate(updateStatement);
        } catch (SQLException e) {
            throw new RuntimeException("Not able to update the company" + e);
        }
    }

    private PreparedStatement prepareUpdateStatement(Company company, Connection connection) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BY_ID_SQL);
            preparedStatement.setString(1, company.getCompany_name());
            preparedStatement.setString(2, company.getCompany_country());
            preparedStatement.setInt(3, company.getCompany_id());
            return preparedStatement;
        } catch (SQLException e) {
            throw new RuntimeException("Cant prepare UpdateStatement");
        }
    }

    public void delete(Company company){
        Objects.requireNonNull(company);
        try {
            PreparedStatement deleteStatement = connection.prepareStatement(DELETE_BY_ID_SQL);
            deleteStatement.setInt(1, company.getCompany_id());
            executeUpdate(deleteStatement);
        } catch (SQLException e) {
            throw new RuntimeException("Can't delete company " + e);
        }
    }














}
