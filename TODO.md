# TODO List
 - Implement death
 - Add platforms with json support
 - Add some basic UI



{
    Sprite mushroom1 = new Sprite(new Image("images/mushrooms/type1-single-1.png"));
    mushroom1.setPos(100, 130);
    sprites.add(mushroom1);

    Sprite mushroom2 = new Sprite(new Image("images/mushrooms/type1-double-1.png"));
    mushroom2.setPos(300, 130);
    sprites.add(mushroom2);

    Sprite mushroom3 = new Sprite(new Image("images/mushrooms/type1-single-2.png"));
    mushroom3.setPos(400, 130);
    sprites.add(mushroom3);

    Sprite mushroom4 = new Sprite(new Image("images/mushrooms/type1-triple-1.png"));
    mushroom4.setPos(700, 130);
    sprites.add(mushroom4);

    Sprite mushroom5 = new Sprite(new Image("images/mushrooms/type1-single-3.png"));
    mushroom5.setPos(900, 130);
    sprites.add(mushroom5);
}

Sprite platformTest = new Sprite(new Image("images/platform.png"));
platformTest.setPos(200, 50);
sprites.add(platformTest);

physicsWorld.add(new PhysicsRectangle(adventurer.getPos().getX(), adventurer.getPos().getY() - 100, 30, 30));
physicsWorld.add(new PhysicsRectangle(adventurer.getPos().getX() + 300, adventurer.getPos().getY() + 114 - 20, 200, 60).setFixed());
physicsWorld.add(new PhysicsRectangle(adventurer.getPos().getX() - 100, adventurer.getPos().getY() + 114, 50, 50).setFixed());