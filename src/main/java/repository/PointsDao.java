package repository;
import java.sql.*;

public class JdbcRepository {
    private static final String URL = "jdbc:postgresql://localhost:5432/lab2Web";
    private static final String USER = "postgres";
    private static final String PASS = "56793";

    public boolean insertPoint(double x, double y, int r, boolean hit, String time, long exec) {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO points (x, y, r, hit, current_time_back, execution_time) VALUES (?, ?, ?, ?, ?, ?)")) {
            ps.setDouble(1, x);
            ps.setDouble(2, y);
            ps.setInt(3, r);
            ps.setBoolean(4, hit);
            ps.setTimestamp(5, Timestamp.valueOf(time));
            ps.setLong(6, exec);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LoggerCustom.log("insertPoint: " + e.getMessage());
            return  false;
        }
    }

    public boolean insertFailedAttempt(String x, String y, String r) {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO failed_attemps_of_points (x_param, y_param, r_param) VALUES (?, ?, ?)")) {
            // в таблице стоит ограничение длины 15 символов
            ps.setString(1, cut(x));
            ps.setString(2, cut(y));
            ps.setString(3, cut(r));
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            LoggerCustom.log("insertFailedAttempt: " + e.getMessage());
            return  false;
        }
    }

    private String cut(String s) {
        return s != null && s.length() > 15 ? s.substring(0, 15) : s;
    }

    public boolean clearPoints() {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement()) {
            s.executeUpdate("TRUNCATE TABLE points");
            return true;
        } catch (SQLException e) {
            LoggerCustom.log("clearPoints: " + e.getMessage());
            return false;
        }
    }


    public static String getPointsPage(int pageNumber, int pageSize) {
        StringBuilder sb = new StringBuilder("[");
        int offset = (pageNumber - 1) * pageSize;

        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement ps = c.prepareStatement(
                     "SELECT x, y, r, hit, current_time_back, execution_time " +
                             "FROM points ORDER BY id DESC LIMIT ? OFFSET ?")) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                boolean first = true;

                while (rs.next()) {
                    if (!first) {
                        sb.append(",");
                    }
                    first = false;

                    String t = rs.getTimestamp("current_time_back")
                            .toString()
                            .substring(0, 19);

                    sb.append(String.format(
                            java.util.Locale.US,
                            "{\"x\":%.2f,\"y\":%.2f,\"r\":%d," +
                                    "\"popadanie\":%b,\"currentTime\":\"%s\"," +
                                    "\"executionTime\":%d}",
                            rs.getDouble(1),
                            rs.getDouble(2),
                            rs.getInt(3),
                            rs.getBoolean(4),
                            t,
                            rs.getLong(6)
                    ));
                }
            }
        } catch (SQLException e) {
            LoggerCustom.log("getPointsPage: " + e.getMessage());
            return "[]";
        }

        sb.append("]");
        return sb.toString();
    }


    public static int getTotalPages(int pageSize) {
        try (Connection c = DriverManager.getConnection(URL, USER, PASS);
             Statement s = c.createStatement();
             ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM points")) {
            if (rs.next()) {
                int total = rs.getInt(1);
                return Math.max(1, (total + pageSize - 1) / pageSize);
            }
        } catch (SQLException e) {
            LoggerCustom.log("getTotalPages: " + e.getMessage());
        }
        return 1;
    }
}