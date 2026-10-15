package com.elms.config;

import com.elms.exception.DatabaseException;
import com.elms.util.PasswordUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Enterprise JDBC Connection Manager with HikariCP Pooling.
 * Supports MySQL primary database with seamless H2 fallback for zero-downtime development.
 */
public final class DBConnectionManager {
    private static final Logger LOGGER = Logger.getLogger(DBConnectionManager.class.getName());
    private static volatile DBConnectionManager instance;
    private HikariDataSource dataSource;
    private String activeDatabaseType = "unknown";

    private DBConnectionManager() {
        initDataSource();
        initSchemaAndSeedData();
    }

    public static DBConnectionManager getInstance() {
        if (instance == null) {
            synchronized (DBConnectionManager.class) {
                if (instance == null) {
                    instance = new DBConnectionManager();
                }
            }
        }
        return instance;
    }

    private void initDataSource() {
        String configuredType = AppConfig.get("db.type", "mysql").trim().toLowerCase();
        boolean autoFallback = AppConfig.getBoolean("db.auto.fallback", true);

        if ("mysql".equals(configuredType)) {
            try {
                LOGGER.info("Attempting connection to Primary MySQL Database...");
                dataSource = buildMySQLDataSource();
                // Test connection
                try (Connection conn = dataSource.getConnection()) {
                    activeDatabaseType = "MySQL (" + conn.getMetaData().getDatabaseProductVersion() + ")";
                    LOGGER.info("Successfully connected to MySQL Database. Pool initialized.");
                    return;
                }
            } catch (Exception e) {
                LOGGER.warning("Could not establish connection to MySQL: " + e.getMessage());
                if (autoFallback) {
                    LOGGER.info("Activating Resilient Local H2 Database storage (MySQL Mode)...");
                    if (dataSource != null) {
                        dataSource.close();
                    }
                    dataSource = buildH2DataSource();
                    activeDatabaseType = "H2 (Local Resilient Mode)";
                } else {
                    throw new DatabaseException("Failed to connect to MySQL and auto-fallback is disabled.", e);
                }
            }
        } else {
            dataSource = buildH2DataSource();
            activeDatabaseType = "H2 Engine";
        }
    }

    private HikariDataSource buildMySQLDataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("ELMS-MySQL-Pool");
        config.setDriverClassName(AppConfig.get("db.mysql.driver", "com.mysql.cj.jdbc.Driver"));
        config.setJdbcUrl(AppConfig.get("db.mysql.url", "jdbc:mysql://localhost:3306/leave_management_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"));
        config.setUsername(AppConfig.get("db.mysql.username", "root"));
        config.setPassword(AppConfig.get("db.mysql.password", "root"));

        config.setMaximumPoolSize(AppConfig.getInt("db.pool.maximumPoolSize", 10));
        config.setMinimumIdle(AppConfig.getInt("db.pool.minimumIdle", 2));
        config.setConnectionTimeout(AppConfig.getInt("db.pool.connectionTimeout", 4000));
        config.setIdleTimeout(AppConfig.getInt("db.pool.idleTimeout", 300000));
        config.setMaxLifetime(AppConfig.getInt("db.pool.maxLifetime", 1800000));
        config.setInitializationFailTimeout(3000);

        return new HikariDataSource(config);
    }

    private HikariDataSource buildH2DataSource() {
        HikariConfig config = new HikariConfig();
        config.setPoolName("ELMS-H2-Pool");
        config.setDriverClassName(AppConfig.get("db.h2.driver", "org.h2.Driver"));
        config.setJdbcUrl(AppConfig.get("db.h2.url", "jdbc:h2:file:./data/leave_management_db;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE"));
        config.setUsername(AppConfig.get("db.h2.username", "sa"));
        config.setPassword(AppConfig.get("db.h2.password", ""));

        config.setMaximumPoolSize(AppConfig.getInt("db.pool.maximumPoolSize", 10));
        config.setMinimumIdle(AppConfig.getInt("db.pool.minimumIdle", 2));
        config.setConnectionTimeout(AppConfig.getInt("db.pool.connectionTimeout", 5000));

        return new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("Database connection pool is closed or uninitialized.");
        }
        return dataSource.getConnection();
    }

    public String getActiveDatabaseType() {
        return activeDatabaseType;
    }

    /**
     * Initializes schema definitions and populates default records if database is empty.
     */
    private void initSchemaAndSeedData() {
        try (Connection conn = getConnection()) {
            executeSchemaFile(conn, "schema.sql");
            seedInitialData(conn);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed during database schema initialization", e);
        }
    }

    private void executeSchemaFile(Connection conn, String fileName) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                LOGGER.warning("Schema resource " + fileName + " not found.");
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                 Statement stmt = conn.createStatement()) {
                StringBuilder sql = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("--") || line.startsWith("//")) {
                        continue;
                    }
                    sql.append(line).append(" ");
                    if (line.endsWith(";")) {
                        String query = sql.toString().replace(";", "").trim();
                        if (!query.isEmpty()) {
                            try {
                                stmt.execute(query);
                            } catch (SQLException ex) {
                                LOGGER.warning("Schema execution notice: " + ex.getMessage() + " | Query: " + query);
                            }
                        }
                        sql.setLength(0);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Notice during schema script execution", e);
        }
    }

    private void seedInitialData(Connection conn) {
        try {
            // Check if departments exist
            boolean hasDepts = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM departments")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasDepts = true;
                }
            }

            if (!hasDepts) {
                LOGGER.info("Seeding initial corporate departments...");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO departments (department_code, department_name, description) VALUES (?, ?, ?)")) {
                    insertDept(ps, "ENG", "Engineering", "Software development, QA, and cloud infrastructure operations");
                    insertDept(ps, "HR", "Human Resources", "Talent acquisition, employee welfare, and personnel policy compliance");
                    insertDept(ps, "FIN", "Finance & Accounting", "Payroll, fiscal reporting, and statutory tax compliance");
                    insertDept(ps, "OPS", "Operations & Logistics", "Enterprise project coordination and facility management");
                    insertDept(ps, "MKT", "Marketing & Communications", "Corporate communications, digital branding, and client engagement");
                }
            }

            // Check if leave types exist
            boolean hasTypes = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM leave_types")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasTypes = true;
                }
            }

            if (!hasTypes) {
                LOGGER.info("Seeding leave types...");
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO leave_types (type_code, type_name, description, default_days_per_year, is_paid) VALUES (?, ?, ?, ?, ?)")) {
                    insertType(ps, "CL", "Casual Leave", "Short-duration leave for personal matters", 12, true);
                    insertType(ps, "SL", "Sick Leave", "Medical absence and health-related recovery", 10, true);
                    insertType(ps, "PL", "Paid / Privilege Leave", "Tenure-accrued planned annual vacation", 15, true);
                    insertType(ps, "ML", "Maternity / Paternity Leave", "Parental leave under statutory requirements", 90, true);
                    insertType(ps, "BL", "Bereavement Leave", "Compassionate leave for immediate family demise", 5, true);
                    insertType(ps, "LOP", "Loss of Pay (Unpaid Leave)", "Approved absence beyond accrued quotas", 30, false);
                }
            }

            // Check if employees exist
            boolean hasEmployees = false;
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM employees")) {
                if (rs.next() && rs.getInt(1) > 0) {
                    hasEmployees = true;
                }
            }

            if (!hasEmployees) {
                // Clear any partial users from failed previous seed
                try (Statement st = conn.createStatement()) {
                    st.execute("DELETE FROM users");
                } catch (Exception ignored) {}
                LOGGER.info("Seeding default Administrator (admin / Admin@123)...");
                String adminHash = PasswordUtil.hashPassword("Admin@123");
                int adminUserId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "admin");
                    ps.setString(2, "admin@internal.corp");
                    ps.setString(3, adminHash);
                    ps.setString(4, "ROLE_ADMIN");
                    ps.setBoolean(5, true);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        adminUserId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, department_id, designation, joining_date, salary, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, adminUserId);
                    ps.setString(2, "EMP-1001");
                    ps.setString(3, "System");
                    ps.setString(4, "Administrator");
                    ps.setString(5, "admin@internal.corp");
                    ps.setString(6, "+1-555-0100");
                    ps.setInt(7, 2); // HR
                    ps.setString(8, "HR Operations Director");
                    ps.setDate(9, java.sql.Date.valueOf("2022-01-15"));
                    ps.setBigDecimal(10, new java.math.BigDecimal("95000.00"));
                    ps.setString(11, "ACTIVE");
                    ps.executeUpdate();
                }

                LOGGER.info("Seeding sample Employee (john.doe / Employee@123)...");
                String empHash = PasswordUtil.hashPassword("Employee@123");
                int empUserId;
                int empRecordId;
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO users (username, email, password_hash, role, is_active) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "john.doe");
                    ps.setString(2, "john.doe@internal.corp");
                    ps.setString(3, empHash);
                    ps.setString(4, "ROLE_EMPLOYEE");
                    ps.setBoolean(5, true);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        empUserId = rs.getInt(1);
                    }
                }

                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO employees (user_id, employee_code, first_name, last_name, email, phone, department_id, designation, joining_date, salary, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, empUserId);
                    ps.setString(2, "EMP-1002");
                    ps.setString(3, "John");
                    ps.setString(4, "Doe");
                    ps.setString(5, "john.doe@internal.corp");
                    ps.setString(6, "+1-555-0102");
                    ps.setInt(7, 1); // Engineering
                    ps.setString(8, "Senior Software Engineer");
                    ps.setDate(9, java.sql.Date.valueOf("2023-03-01"));
                    ps.setBigDecimal(10, new java.math.BigDecimal("82000.00"));
                    ps.setString(11, "ACTIVE");
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        empRecordId = rs.getInt(1);
                    }
                }

                // Initialize leave balances for John Doe (year 2026)
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO leave_balances (employee_id, leave_type_id, cal_year, total_allocated, used_days, pending_days) VALUES (?, ?, ?, ?, ?, ?)")) {
                    insertBalance(ps, empRecordId, 1, 2026, 12.0, 2.0, 0.0);
                    insertBalance(ps, empRecordId, 2, 2026, 10.0, 1.0, 0.0);
                    insertBalance(ps, empRecordId, 3, 2026, 15.0, 0.0, 0.0);
                    insertBalance(ps, empRecordId, 4, 2026, 90.0, 0.0, 0.0);
                    insertBalance(ps, empRecordId, 5, 2026, 5.0, 0.0, 0.0);
                    insertBalance(ps, empRecordId, 6, 2026, 30.0, 0.0, 0.0);
                }

                // Add sample approved leave request for history
                try (PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO leave_requests (employee_id, leave_type_id, start_date, end_date, total_days, reason, emergency_contact, status, admin_remarks, reviewed_by, reviewed_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    ps.setInt(1, empRecordId);
                    ps.setInt(2, 1); // CL
                    ps.setDate(3, java.sql.Date.valueOf("2026-02-10"));
                    ps.setDate(4, java.sql.Date.valueOf("2026-02-11"));
                    ps.setDouble(5, 2.0);
                    ps.setString(6, "Residential relocation and utilities setup");
                    ps.setString(7, "+1-555-0199");
                    ps.setString(8, "APPROVED");
                    ps.setString(9, "Approved by HR Department");
                    ps.setInt(10, adminUserId);
                    ps.setTimestamp(11, java.sql.Timestamp.valueOf("2026-02-08 10:15:00"));
                    ps.executeUpdate();
                }

                LOGGER.info("Default seed data initialized successfully.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Notice during initial seed data verification", e);
        }
    }

    private void insertDept(PreparedStatement ps, String code, String name, String desc) throws SQLException {
        ps.setString(1, code);
        ps.setString(2, name);
        ps.setString(3, desc);
        ps.executeUpdate();
    }

    private void insertType(PreparedStatement ps, String code, String name, String desc, int days, boolean paid) throws SQLException {
        ps.setString(1, code);
        ps.setString(2, name);
        ps.setString(3, desc);
        ps.setInt(4, days);
        ps.setBoolean(5, paid);
        ps.executeUpdate();
    }

    private void insertBalance(PreparedStatement ps, int empId, int typeId, int year, double allocated, double used, double pending) throws SQLException {
        ps.setInt(1, empId);
        ps.setInt(2, typeId);
        ps.setInt(3, year);
        ps.setDouble(4, allocated);
        ps.setDouble(5, used);
        ps.setDouble(6, pending);
        ps.executeUpdate();
    }

    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try {
                    c.close();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            LOGGER.info("Database pool closed.");
        }
    }
}
