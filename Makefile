.PHONY: jar	run

jar: ## Creates an executable JAR
	@echo "Creating JAR..."
	mvn clean
	java -d sr

run:


# javac src/main/java/org/example/Main.java