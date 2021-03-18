ifeq ($(OS), Windows_NT)
    modules = --module-path ../lib/javafx-sdk-16-win/lib --add-modules javafx.controls
else
    modules = --module-path ../lib/javafx-sdk-16-mac/lib --add-modules javafx.controls
endif

all: Game.class

Game.class: Game.java Camera.class Sprite.class LayeredSprite.class Vector.class Updatable.class GameController.class PlayerController.class KeyHandler.class
	javac $(modules) Game.java

Sprite.class: Sprite.java Vector.class
	javac $(modules) Sprite.java

LayeredSprite.class: LayeredSprite.java Vector.class
	javac $(modules) LayeredSprite.java

Camera.class: Camera.java Vector.class Updatable.class Spring.class
	javac $(modules) Camera.java

GameController.class: GameController.java KeyHandler.class Updatable.class
	javac $(modules) GameController.java

PlayerController.class: PlayerController.java KeyHandler.class Sprite.class Vector.class Updatable.class
	javac $(modules) PlayerController.java

KeyHandler.class: KeyHandler.java
	javac $(modules) KeyHandler.java

Vector.class: Vector.java
	javac $(modules) Vector.java

Updatable.class: Updatable.java
	javac Updatable.java

Spring.class: Spring.java
	javac Spring.java

clean:
	rm *.class

run: all
	java $(modules) Game