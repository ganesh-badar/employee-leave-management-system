package com.elms;

import com.elms.config.AppConfig;
import com.elms.config.DBConnectionManager;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.DirResourceSet;
import org.apache.catalina.webresources.StandardRoot;

import java.io.File;
import java.util.logging.Logger;

/**
 * Enterprise Embedded Tomcat Server Runner.
 * Provides instant CLI launch, custom domain host binding, and zero-configuration execution.
 */
public class ServerMain {
    private static final Logger LOGGER = Logger.getLogger(ServerMain.class.getName());

    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getProperty("server.port", String.valueOf(AppConfig.getInt("server.port", 8080))));
        String contextPath = System.getProperty("server.contextPath", AppConfig.get("server.contextPath", "/elms"));

        // Initialize and test database connection pool eagerly
        String dbInfo = DBConnectionManager.getInstance().getActiveDatabaseType();

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector(); // Initialize default HTTP connector

        // Configure custom domain alias if provided via environment or properties
        String customDomain = AppConfig.get("server.custom.domain", "leave.internal.corp");
        Host defaultHost = tomcat.getHost();
        defaultHost.addAlias(customDomain);
        defaultHost.addAlias("localhost");
        defaultHost.addAlias("127.0.0.1");

        // Base directory for Tomcat temporary files
        String baseDir = new File("target/tomcat").getAbsolutePath();
        tomcat.setBaseDir(baseDir);

        // Resolve web application docBase
        File webappDir = new File("src/main/webapp");
        if (!webappDir.exists()) {
            webappDir = new File("employee-leave-management-system/src/main/webapp");
        }

        Context ctx = tomcat.addWebapp(contextPath, webappDir.getAbsolutePath());
        ctx.setParentClassLoader(ServerMain.class.getClassLoader());
        ctx.setReloadable(true);

        // Map compiled classes into WebResourceRoot so Servlets and JSTL taglibs resolve cleanly
        File additionWebInfClasses = new File("target/classes");
        if (!additionWebInfClasses.exists()) {
            additionWebInfClasses = new File("employee-leave-management-system/target/classes");
        }

        WebResourceRoot resources = new StandardRoot(ctx);
        if (additionWebInfClasses.exists()) {
            resources.addPreResources(new DirResourceSet(
                    resources,
                    "/WEB-INF/classes",
                    additionWebInfClasses.getAbsolutePath(),
                    "/"
            ));
        }
        ctx.setResources(resources);

        tomcat.start();

        System.out.println("=====================================================================");
        System.out.println("   EMPLOYEE LEAVE MANAGEMENT SYSTEM (ELMS) - ENTERPRISE HR PORTAL    ");
        System.out.println("=====================================================================");
        System.out.println("  Port             : " + port);
        System.out.println("  Context Path     : " + contextPath);
        System.out.println("  Local URL        : http://localhost:" + port + contextPath);
        System.out.println("  Custom Domain    : http://" + customDomain + ":" + port + contextPath);
        System.out.println("  Database Engine  : " + dbInfo);
        System.out.println("---------------------------------------------------------------------");
        System.out.println("  Default Administrator : admin / Admin@123");
        System.out.println("  Default Employee      : john.doe / Employee@123");
        System.out.println("=====================================================================");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("Gracefully shutting down ELMS server...");
                tomcat.stop();
                tomcat.destroy();
                DBConnectionManager.getInstance().closePool();
            } catch (Exception e) {
                LOGGER.warning("Error during server shutdown: " + e.getMessage());
            }
        }));

        tomcat.getServer().await();
    }
}
