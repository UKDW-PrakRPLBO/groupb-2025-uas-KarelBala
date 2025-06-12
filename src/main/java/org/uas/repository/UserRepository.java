package org.uas.repository;

import org.uas.data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final Connection connection;

    public UserRepository(Connection connection) {
        this.connection = connection;
        createTable();
    }

    public void createTable() {
        String userTableSql = "CREATE TABLE IF NOT EXISTS users ("
                + "email TEXT NOT NULL PRIMARY KEY,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL"
                + ")";
        if (connection != null) {
            try (Statement stmt = connection.createStatement()) {
                stmt.execute(userTableSql);
            } catch (SQLException e) {
                System.err.println("Gagal membuat tabel: " + e.getMessage());
            }
        }
    }
    public List<User> findAll() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT email, username, password FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Gagal mengambil data users: " + e.getMessage());
        }
        return users;
    }
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    return storedPassword.equals(password);
                }
            }
        } catch (SQLException e) {
            System.err.println("Gagal melakukan otentikasi: " + e.getMessage());
        }

        return false;
    }

    public boolean insertUser(String email, String username, String password) {
        String sql = "INSERT INTO users (email, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.executeUpdate();
            System.out.println("User berhasil ditambahkan.");
            return true;
        } catch (SQLException e) {
            System.err.println("Gagal menambahkan user: " + e.getMessage());
        }
        return false;
    }
    public boolean updateUser(String email, String password, String username) {
        String sql = "UPDATE users SET email = ?, password = ? WHERE username = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, username);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Data user berhasil diperbarui.");
                return true;
            } else {
                System.out.println("User dengan username '" + username + "' tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal memperbarui data user: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteUser(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement pstmt = this.connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User telah dihapus.");
                return true;
            } else {
                System.out.println("User dengan email '" + email + "' tidak ditemukan.");
            }
        } catch (SQLException e) {
            System.err.println("Gagal menghapus user: " + e.getMessage());
        }
        return false;
    }
}