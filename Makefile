.PHONY: jar	run play

jar: ## Creates an executable JAR
	@echo "Creating JAR..."
	mvn clean install
	javac -d bin src/main/java/org/example/Main.java
	jar cfm Game.jar MANIFEST.MF -C bin .
	@echo "JAR created!"

run: ## Runs the JAR
	@echo "Executing JAR..."
	mvn clean install

play: ## Runs the Java app
	@echo "Running the app with the default configuration..."
	mvn clean install
	mvn exec:java -Dexec.mainClass="org.example.Main" -Dexec.args="--config config.json --betting-amount 100"