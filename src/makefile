ifeq ($(OS), Windows_NT)
    modules = --module-path ../lib/javafx-sdk-16-win/lib --add-modules javafx.controls --add-modules javafx.media
else
    modules = --module-path ../lib/javafx-sdk-16-mac/lib --add-modules javafx.controls --add-modules javafx.media
endif

classpath = -cp ../bin/json-simple.jar

all:
	javac $(modules) *.java

clean:
	rm *.class

run: all
	java $(modules) Game