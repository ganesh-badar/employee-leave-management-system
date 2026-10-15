# Custom Domain Setup Guide: leave.internal.corp

This guide details how to map and run the Employee Leave Management System (ELMS) under a custom enterprise domain (`leave.internal.corp` or `hrms.local`).

---

## 1. Local Domain Name Resolution (DNS / Hosts)

To route `leave.internal.corp` to your server instance locally:

### On Windows
1. Open PowerShell or Command Prompt as **Administrator**.
2. Edit `C:\Windows\System32\drivers\etc\hosts`:
   ```powershell
   Add-Content -Path "$env:windir\System32\drivers\etc\hosts" -Value "`n127.0.0.1  leave.internal.corp`n127.0.0.1  hrms.local"
   ```
3. Verify resolution:
   ```cmd
   ping leave.internal.corp
   ```

### On Linux / macOS
```bash
echo "127.0.0.1  leave.internal.corp" | sudo tee -a /etc/hosts
```

---

## 2. Running Directly with Embedded Tomcat

The application's `ServerMain` automatically binds `leave.internal.corp` as an active alias host.

Simply start the server:
```bash
mvn compile exec:java
```

Now navigate to:
```
http://leave.internal.corp:8080/elms
```

---

## 3. Production Deployment with Standalone Apache Tomcat

1. Copy `target/elms.war` to `$CATALINA_HOME/webapps/elms.war`.
2. In `$CATALINA_HOME/conf/server.xml`, add the `<Host>` entry from `domain/server.xml.snippet`.
3. Restart Apache Tomcat.
4. The system is live at `http://leave.internal.corp:8080/`.

---

## 4. Reverse Proxy Setup with Nginx (Port 80/443 Standard HTTP/HTTPS)

1. Copy `domain/nginx.conf` to `/etc/nginx/sites-available/leave.internal.corp`.
2. Enable site:
   ```bash
   ln -s /etc/nginx/sites-available/leave.internal.corp /etc/nginx/sites-enabled/
   nginx -t
   systemctl reload nginx
   ```
3. Access directly without specifying port `:8080`:
   ```
   http://leave.internal.corp/
   ```
