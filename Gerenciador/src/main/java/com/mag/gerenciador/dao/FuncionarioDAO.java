package com.mag.gerenciador.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.mag.gerenciador.model.Funcionario;


public class FuncionarioDAO {
	private String jdbcURL = "jdbc:mysql://localhost:3306/funcionariodb?useSSL=false&serverTimezone=UTC";
	private String jdbcUsername = "root";
	private String jdbcPassword = "9666";
	private String jdbcDriver = "com.mysql.cj.jdbc.Driver";
	
	private static final String INSERT_FUNCIONARIOS_SQL = "INSERT INTO funcionario" + "  (nome, email, cargo) VALUES "
			+ " (?, ?, ?);";

	private static final String SELECT_FUNCIONARIOS_BY_ID = "select id,nome,email,cargo from funcionario where id =?";
	private static final String SELECT_ALL_FUNCIONARIOS = "select * from funcionario";
	private static final String DELETE_FUNCIONARIOS_SQL = "delete from funcionario where id = ?;";
	private static final String UPDATE_FUNCIONARIOS__SQL = "update funcionario set nome = ?,email= ?, cargo =? where id = ?;";

	protected Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return connection;
	}
	public void criarFuncionario(Funcionario funcionario) throws SQLException {
		System.out.println(INSERT_FUNCIONARIOS_SQL);
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(INSERT_FUNCIONARIOS_SQL)) {
			preparedStatement.setString(1, funcionario.getNome());
			preparedStatement.setString(2, funcionario.getEmail());
			preparedStatement.setString(3, funcionario.getCargo());
			System.out.println(preparedStatement);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			printSQLException(e);
		}
	}

	public Funcionario buscarFuncionario(int id) {
		Funcionario funcionario = null;
		// Step 1: estabelecer conexão
		try (Connection connection = getConnection();
				PreparedStatement preparedStatement = connection.prepareStatement(SELECT_FUNCIONARIOS_BY_ID);) {
			preparedStatement.setInt(1, id);
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				String nome = rs.getString("nome");
				String email = rs.getString("email");
				String cargo = rs.getString("cargo");
				funcionario = new Funcionario(id, nome, email, cargo);
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return funcionario;
	}

	public List<Funcionario> buscarFuncionarios() {

		List<Funcionario> funcionarios = new ArrayList<>();
		try (Connection connection = getConnection();

			PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_FUNCIONARIOS);) {
			System.out.println(preparedStatement);
			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				String nome = rs.getString("nome");
				String email = rs.getString("email");
				String cargo = rs.getString("cargo");
				funcionarios.add(new Funcionario(id, nome, email, cargo));
			}
		} catch (SQLException e) {
			printSQLException(e);
		}
		return funcionarios;
	}

	public boolean deleteFuncionario(int id) throws SQLException {
		boolean rowDeleted;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(DELETE_FUNCIONARIOS_SQL);) {
			statement.setInt(1, id);
			rowDeleted = statement.executeUpdate() > 0;
		}
		return rowDeleted;
	}

	public boolean atualizarFuncionario(Funcionario funcionario) throws SQLException {
		boolean rowUpdated;
		try (Connection connection = getConnection();
				PreparedStatement statement = connection.prepareStatement(UPDATE_FUNCIONARIOS__SQL);) {
			System.out.println("Atualizar funcionario: "+statement);
			statement.setString(1, funcionario.getNome());
			statement.setString(2, funcionario.getEmail());
			statement.setString(3, funcionario.getCargo());
			statement.setInt(4, funcionario.getId());

			rowUpdated = statement.executeUpdate() > 0;
		}
		return rowUpdated;
	}

	private void printSQLException(SQLException ex) {
		for (Throwable e : ex) {
			if (e instanceof SQLException) {
				e.printStackTrace(System.err);
				System.err.println("SQLState: " + ((SQLException) e).getSQLState());
				System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
				System.err.println("Message: " + e.getMessage());
				Throwable t = ex.getCause();
				while (t != null) {
					System.out.println("Cause: " + t);
					t = t.getCause();
				}
			}
		}
	}

}