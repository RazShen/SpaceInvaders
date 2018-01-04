# 311130777
# shenkmr

compile: bin
	javac -d bin -cp biuoop-1.4.jar src/*.java src/*/*.java
jar: compile
	jar cfm space-invaders.jar Manifest.mf -C bin . -C resources .
run: jar
	java -jar space-invaders.jar
bin:
	mkdir bin

