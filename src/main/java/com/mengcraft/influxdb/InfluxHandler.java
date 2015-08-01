package com.mengcraft.influxdb;

import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.impl.InfluxDBImpl;

public class InfluxHandler {

    private final InfluxDB db;
    private final String database;

    private boolean shutdown;
    private boolean asynchronous;

    public InfluxHandler(String url, String user, String pass, String database) {
        this.db = new InfluxDBImpl(url, user, pass);
        this.database = database;
    }

    public boolean isAsynchronous() {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        return asynchronous;
    }

    public void createDatabase() {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        db.createDatabase(database);
    }

    public void deleteDatabase() {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        db.deleteDatabase(database);
    }

    public void setAsynchronous(boolean b) {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        if (b) {
            db.enableBatch(1024, 100, TimeUnit.MILLISECONDS);
        } else {
            db.disableBatch();
        }
        asynchronous = b;
    }

    public Writer write(String table) {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        return new Writer(this, table);
    }

    public static class Writer {

        private final Point.Builder builder;
        private final InfluxHandler handler;

        private Writer(InfluxHandler handler, String table) {
            this.handler = handler;
            this.builder = new Point.Builder(table);
        }

        public Writer value(String name, Object value) {
            builder.field(name, value);
            return this;
        }

        public Writer where(String name, String value) {
            builder.tag(name, value);
            return this;
        }

        public void flush() {
            builder.time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
            handler.write(builder.build());
        }

    }

    public Pong ping() {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        return db.ping();
    }

    private void write(Point point) {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        db.write(database, "default", point);
    }

    public void shutdown() {
        if (shutdown) {
            throw new RuntimeException("Already shutdown.");
        }
        if (asynchronous) {
            setAsynchronous(false);
        }
        shutdown = true;
    }

}
