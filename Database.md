# PostgreSQL on Google Cloud SQL

## Instance parameters

* Location: ??? For development, I'm starting with the default parameters.
* Machine type: ??? For development, I'm starting with a minimal instance based on the default parameters.
* Automatic backups: Enabled unless/until we find a need for a different backup solution.
* Availability: Will probably use high availability for production. Single zone for non-production because high availability costs twice as much.
* Authorized networks: None (for now)
* Database flags: None (for now)
* Maintenance schedule: Must select something. Maintenance will start during the window but is not guaranteed to finish within the window.

After creation, change SSL setting to require SSL connections
 
> Instance details > "SSL" tab > "Allow only SSL connections" button

## Connecting

https://cloud.google.com/sql/docs/postgres/external-connection-methods

### App Engine

An App Engine application deployed in the same project as the Cloud SQL instance needs no special setup to allow connections to the database when using the recommended Google Cloud SQL JDBC socket factory (see below), and the connection is secure/encrypted. However, the local dev server does not have the same privilege so additional steps need to be taken. There are a few options:

* Google Cloud SQL JDBC socket factory <--- ***selected***
* Pre-authorized IP addresses
* Google Cloud SQL Proxy (not investigated for App Engine)

#### Google Cloud SQL JDBC socket factory

https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory

Effectively the same as using the Cloud SQL Proxy (https://cloud.google.com/sql/docs/postgres/sql-proxy) without the need for a separate process.

Must enable Cloud SQL Admin API for the project.

Uses Application Default Credentials (ADC) (https://cloud.google.com/docs/authentication/production) which come from either:

* an environment variable with the location of a JSON service account key file
* the default service account for the deployment environment (Compute Engine, Kubernetes Engine, App Engine, Cloud Functions) or the Cloud SDK application-default credentials (`glcoud auth application-default login`)

Pros:

* Can connect from any IP address, not just the Broad network
* Connection is automatically encrypted without additional setup
* Can connect to SSL-only database without manually setting up a client certificate
* Same config as recommended connection for App Engine application

Cons:

* Access is dependent solely on secret keys which could accidentally leak (but are centrally authorized via IAM & admin in cloud console)

#### ~~Pre-authorized IP Addresses~~

Whitelist IP addresses in cloud console. Client needs only DB credentials; no ADC credentials required.

Pros:

* No need to set up ADC credentials on client
* Centralized control over allowed trusted networks

Cons:

* Must be on a known network
* Must set up client SSL certificates for encrypted connections
* Config is different than recommended for App Engine; must use different strategies for different environments or use more complicated config for App Engine deploys (effectively treating it as an external application (https://cloud.google.com/sql/docs/postgres/connect-external-app))

### Interactive

#### `psql`

Just like for App Engine dev server connections, one option is pre-authorized IP addresses.

`psql "host=[INSTANCE_IP] sslmode=disable dbname=avram user=avram"`

Alternately, there's the Google Cloud SQL Proxy (https://cloud.google.com/sql/docs/postgres/sql-proxy) which is an external process that establishes a secure connection between the client machine and the Cloud SQL instance. This takes some extra setup but is quicker for establishing the connection.

```shell
# leave this running in one terminal
$ ./cloud_sql_proxy -instances=broad-avram-dev:us-central1:avram=tcp:5432 -credential_file=[PATH_TO_JSON]

# run psql in a separate terminal
$ psql "host=127.0.0.1 sslmode=disable dbname=avram user=avram"
```

We should be able to simplify this process by bundling the proxy and a script in a docker image.

#### ~~`gcloud` (via Cloud Shell or local)~~

From the cloud console, you can click a button to open an interactive session in Google Cloud Console which simply uses `gcloud sql connect`. This can also be done from a local workstation, i.e.:

`gcloud --project broad-avram-dev sql connect avram --user=avram`

This temporarily whitelists the cloud shell or workstation IP to allow connection (this does appear in the Authorization tab in cloud console). Whitelisting takes some time (> 30 seconds) which is inconvenient. This connection also requires that non-SSL connections be allowed which is not desirable.

## SSL

We want to use SSL for all connections, even to dev databases. As Albano said, "no excuse not to use it anymore nowadays." Google Cloud SQL Proxy takes care of this for us and is the recommended (and easiest) way to set up a database connection, both from a deployed App Engine application and local development instances.

Note: There are _many_ examples of JDBC connection strings out there, including those from Google (https://github.com/GoogleCloudPlatform/java-docs-samples/blob/master/appengine-java8/cloudsql-postgres/src/main/webapp/WEB-INF/appengine-web.xml) (https://github.com/GoogleCloudPlatform/cloud-sql-jdbc-socket-factory/blob/master/examples/postgres/appengine-standard-java8/src/main/webapp/WEB-INF/appengine-web.xml) that use the _wrong_ parameter name of `useSSL` for enabling SSL connections. They get away with it because they're setting it to `false` anyway. The `useSSL` parameter is for the MySQL JDBC driver. The PostgreSQL JDBC driver uses `ssl`. Also, there doesn't seem to be an analogous `requireSSL` parameter as there is for MySQL. `ssl=true` in PostgreSQL is equivalent to `useSSL=true&requireSSL=true` in MySQL.