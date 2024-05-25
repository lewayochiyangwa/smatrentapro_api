package com.smatpro.api.Helpers;

import java.sql.*;

public class IdEmail {

    public static int getIdByEmail(String email) {
        int id=0;
        try (
                Connection conn = Dao.connection();
                PreparedStatement stmt = conn.prepareStatement("SELECT id FROM _user WHERE email = ?");
        ) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    id= rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
      return id;
    }
}
