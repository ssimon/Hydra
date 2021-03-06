package com.findwise.hydra.mongodb;

import java.io.IOException;

import org.junit.rules.ExternalResource;

import com.findwise.hydra.DatabaseConfiguration;
import com.mongodb.Mongo;

public class MongoConnectorResource extends ExternalResource {

	public static final String DB_PREFIX = "junit-";

	private final String dbName;

	private MongoConnector mdc;
	private Mongo mongo;
	
	public MongoConnectorResource(Class<?> testClass) {
		this.dbName = DB_PREFIX + testClass.getSimpleName();
	}
	
	@Override
	protected void before() throws Throwable {
		connect();
	}
	
	@Override
	protected void after() {
		disconnect();
	}
	
	public MongoConnector getConnector() {
		return mdc;
	}
	
	public void reset() throws IOException {
		disconnect();
		connect();
	}

	private void connect() throws IOException {
		mongo = new Mongo();
		DatabaseConfiguration conf = DatabaseConfigurationFactory.getDatabaseConfiguration(dbName);
		mdc = new MongoConnector(conf);
		mdc.waitForWrites(true);
		mdc.connect(mongo, false);
	}
	
	private void disconnect() {
		mdc = null;
		mongo.dropDatabase(dbName);
		mongo.close();
	}
}
