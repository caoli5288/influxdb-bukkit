# InfluxDB-Bukkit
Bukkit plugin libraries for InfluxDB. See [influxdb-java](https://github.com/influxdb/influxdb-java).

## Developer
```java
String url = "http://localhost:8086";
// If server need auth.
String user = "root";
String pass = "pass";
// Your database name.
String database = "db";
    
InfluxHandler handler = new InfluxHandler(url, user, pass, database);
// Set asynchronous will let main thread not be blocked.
handler.setAsynchronous(true);
// The write(String) set measurement, where(String) set tag. etc.
handler.write("player_value")
       .where("host", "play.sample.com")
       .where("serv", "server_000")
       .value("value", 0)
       .flush();
// Shutdown client at last.
handler.shutdown();
```
