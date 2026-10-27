# mini-erp

Learning project: Jakarta EE 10 on Payara, REST API + PostgreSQL.

## Structure

```
mini-erp/
├── pom.xml
└── src/main/java/fr/guillaume/minierp/
    ├── RestApplication.java        # activates JAX-RS under /api
    └── hello/
        ├── HelloResource.java      # first endpoints
        └── HelloMessage.java       # object returned as JSON
```

## Saturday: installation

1. **JDK 17 or 21**: Eclipse Temurin (adoptium.net). Check with `java -version`.
2. **Payara Server 6 Community** (Jakarta EE 10): download the zip from payara.fish and unzip it, for example to `C:\payara6` or `~/payara6`.
   Payara 6 requires JDK 11, 17 or 21. Check the compatible versions on the download page.
3. **IntelliJ IDEA**: File > Open > choose the `mini-erp` folder. IntelliJ detects the `pom.xml` and downloads the dependencies. Maven is bundled with IntelliJ, no separate install needed.

## Start Payara

```
payara6/bin/asadmin start-domain
```

Admin console: http://localhost:4848

To stop it: `asadmin stop-domain`

## Build and deploy

In IntelliJ: Maven panel (on the right) > Lifecycle > double-click `package`.
Or in a terminal, from the project folder: `mvn package`

This produces `target/mini-erp.war`. Deploy it:

```
asadmin deploy --force=true target/mini-erp.war
```

`--force=true` redeploys over the previous version. Alternatively, in the admin console: Applications > Deploy.

## Test

- http://localhost:8080/mini-erp/api/hello -> `Hello from Payara`
- http://localhost:8080/mini-erp/api/hello/Guillaume -> `{"date":"...","message":"Hello Guillaume"}`

URL breakdown: `8080` (Payara HTTP port) / `mini-erp` (context root, from `finalName`) / `api` (`@ApplicationPath`) / `hello` (`@Path`).

## When it goes wrong

- Server log: `payara6/glassfish/domains/domain1/logs/server.log`. This is the first place to look, deployment errors are written there.
- 404: check the URL, then that the application appears in `asadmin list-applications`.
- Port 8080 already used: stop the other program, or change the port in the admin console (Configurations > server-config > HTTP Service).

## Exercises for Saturday

1. Add `GET /api/hello/upper/{name}` that returns the name in upper case.
2. Add a query parameter: `GET /api/hello?lang=fr` returns "Bonjour". Hint: `@QueryParam("lang")`.
3. Add a field `int length` to `HelloMessage` and check it appears in the JSON.
4. Deliberately break something (remove `@Path` on the class), redeploy and read `server.log`.
