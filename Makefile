.PHONY: run test

test: ## Runs the test
	@echo "Running all unit tests..."
	mvn clean test

run: ## Runs the Java app
	@echo "Running the app with the default configuration..."
	mvn clean install
	mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="--config config.json --betting-amount 100"