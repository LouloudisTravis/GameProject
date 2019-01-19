import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import com.sun.javafx.geom.Vec2d;

public class Main extends Application { 
	
	private Timeline gameloop;
	
	private Pane root;
	private StackPane finalgame;
	
    private List<GameObject> bullets = new ArrayList<>();
    private List<GameObject> enemies = new ArrayList<>();
    
    private LongProperty score = new SimpleLongProperty();
    
    private Text text1 = new Text();
    private Text gameover = new Text();
    private Text gametitle = new Text();
    
    public Button restart = new Button("Play Again");
     
    
    // setup the 2 background images for use later.
    Image background1 = new Image("background1.PNG");
    ImageView b1 = new ImageView(background1);
  
    private GameObject player;
    
    //getting my audio setup for use
    AudioClip mediaShot = new AudioClip(this.getClass().getResource("musicShot.wav").toExternalForm());
    AudioClip mediaDeath = new AudioClip(this.getClass().getResource("musicDeath.wav").toExternalForm());
    AudioClip Enemydeath = new AudioClip(this.getClass().getResource("enemydeath.wav").toExternalForm());
    AudioClip musicBI = new AudioClip(this.getClass().getResource("Overworld.MP3").toExternalForm());
   
    //starting the timer for global use
    AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            onUpdate();
        }
    };
    
    
    
    private Parent createContent() { 
    	restart.setTranslateX(225);
    	restart.setTranslateY(255);
    	restart.setPrefSize(150, 50);
    	restart.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 16));
        restart.setTextFill(Color.RED);
        
        gameover.textProperty().bind(Bindings.createStringBinding(() ->("Game Over \n" + "Your Final Score is \n" + score.get()), score));
        gameover.setTranslateX(225);
        gameover.setTranslateY(100);
        gameover.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 24));
        gameover.setFill(Color.YELLOW);
    	
    	text1.textProperty().bind(Bindings.createStringBinding(() ->("Score: " + score.get()), score));
    	text1.setFill(Color.YELLOW);
    	text1.setTranslateX(230);
    	text1.setTranslateY(-270);
    	text1.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 20));
    	
        root = new Pane();
        root.setPrefSize(600, 600);
        root.getChildren().addAll(b1);
        Gameloop();
        
        finalgame = new StackPane();
        finalgame.getChildren().addAll(root, text1);
        
        player = new Player();
        player.setVelocity(new Point2D(1, 0)); //setting the players velocity to always be moving forward.
        addGameObject(player, 300, 300); 
        
        return finalgame;
    }

    private void addBullet(GameObject bullet, double x, double y) {
        bullets.add(bullet);
        addGameObject(bullet, x, y);
    }

    private void addEnemy(GameObject enemy, double x, double y) {
        enemies.add(enemy);
        addGameObject(enemy, x, y);
    }
   

    private void addGameObject(GameObject object, double x, double y) {
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);
        root.getChildren().add(object.getView());
    }
       

    public void Gameloop() {
    	gameloop = new Timeline();
    	gameloop.setCycleCount(Timeline.INDEFINITE);
    	gameloop.setAutoReverse(false);
    	final KeyValue kv = new KeyValue (b1.translateXProperty(), -1800);
    	final KeyFrame kf = new KeyFrame(Duration.millis(20000), kv);
    	gameloop.getKeyFrames().add(kf);
    	gameloop.play();    			
    	//************************************
    //	 START ANIMATION OF BACKGROUND WITH B1
    	//***********************************
    	}
           
    private void onUpdate() {	
        for (GameObject bullet : bullets) {
            for (GameObject enemy : enemies) {
                if (bullet.isColliding(enemy))
                	{
                    bullet.setAlive(false);
                    enemy.setAlive(false); 
                    
                    score.set(score.get() + 10);
                    
                    Enemydeath.play();
                    root.getChildren().removeAll(bullet.getView(), enemy.getView());
                }
            }
        }
         for (GameObject enemy : enemies) {
                if (player.isColliding(enemy)) 
            	{
            	player.setAlive(false);
            	root.getChildren().remove(player.getView());
            	mediaDeath.play();
            	timer.stop();
            	gameloop.stop();
            	root.getChildren().addAll(restart, gameover);
            	//******GAMEOVER**********
            	}
            }
        

        bullets.removeIf(GameObject::isDead);
        enemies.removeIf(GameObject::isDead);

        bullets.forEach(GameObject::update);
        enemies.forEach(GameObject::update);

        player.update();
     
        if (Math.random() < 0.02)
        {          
        	double x =  Math.random() * root.getPrefWidth();
        	double y =  Math.random() * root.getPrefHeight();
        	
        	if (playercheck(player, x)) {
        		
        	addEnemy(new Enemy(), x, y);
        	}
        }
        
        if (score.get() >= 200) {
        	if (Math.random() < .02) {        		
        	
        		double x =  Math.random() * root.getPrefWidth();
            	double y =  Math.random() * root.getPrefHeight();
            	
        	if (playercheck(player, x))
        	{
        		addEnemy(new UFO(), x, y);
        	}
      	}
      }
        if (score.get() >= 600) {
        	if (Math.random() < .02) {        		
        	
        		double x =  Math.random() * root.getPrefWidth();
            	double y =  Math.random() * root.getPrefHeight();
            	
        	if (playercheck(player, x))
        	{
        		addEnemy(new UFO(), x, y);
        		addEnemy(new Enemy(), x, y);
        	}
      	}
      }
    }
    
   
    
    public boolean playercheck(GameObject object, double x) {
    	boolean flag = true;
    	if (object.getX() - 100 > x) {
    		return flag;
    		    				
    	}
    	if(object.getX()+ 100 < x) {
			return flag;
			
		}  
		return false;
    }
    static Image spaceShip = new Image("spaceShip.PNG");
    static Image bullet = new Image("bullet.PNG");
    static Image alien = new Image("alien.PNG");
    static Image ufo = new Image("ufo.PNG");
    
    
    private static class Player extends GameObject {
        Player() {
            super(new ImageView(spaceShip));
        }
    }
    private static class Enemy extends GameObject {
        Enemy() {
            super(new ImageView(alien));
        }
    }
   private static class Bullet extends GameObject {

        Bullet() {
            super(new ImageView(bullet));
        }
     }
    private static class UFO extends GameObject {
    	UFO(){
    		super(new ImageView(ufo));
    	}
    }
    
    public void cleanup() 
    {
    	musicBI.stop();
    	timer.stop();
    	score.set(0);
    	player.setAlive(true);
    	player.getView().setLayoutX(300);
    	player.getView().setLayoutY(300);
    	
    	for (GameObject bullet: bullets)
    	{
    		for(GameObject enemy:enemies)
    		{
    			root.getChildren().removeAll(bullet.getView(), enemy.getView());
    		}
    	}
    	bullets.removeAll(bullets);
        enemies.removeAll(enemies);

     }
    
    public void Restart(Stage stage) throws Exception {
    	cleanup();
    	start(stage);    	
    }
    
    @Override
    public void start(Stage primarystage) throws Exception {
    	BackgroundImage MenuBI = new BackgroundImage(new Image("gamemenubackground.jpg",600,600,false,true),
    	        BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
    	          BackgroundSize.DEFAULT);
    	
    	Scene scenegame = new Scene(createContent());
    	
    	Button exit = new Button("Exit"); 
    	exit.setTranslateX(275);
    	exit.setTranslateY(260);
    	exit.setOnAction((ActionEvent e)->{
    		System.out.print("Game Exiting");
    		System.exit(0);   
    		timer.stop();
    	});
    	
    	//************BUTTON PLAY STARTS GAME***************
    	Button play = new Button("Play");
    	play.setTranslateX(275);
    	play.setTranslateY(215);
    	
    	play.setOnAction((ActionEvent e)->{
    		musicBI.stop();
    		primarystage.setScene(scenegame);
            primarystage.show();
            timer.start();
    	});
    	//*************************END***************
    	
        //**********GAME CONTROLS****************
        scenegame.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                player.rotateLeft();
            } else if (e.getCode() == KeyCode.RIGHT) {
                player.rotateRight();
            } else if (e.getCode() == KeyCode.SPACE) {
            	mediaShot.play();
                Bullet bullet = new Bullet();
                bullet.setVelocity(player.getVelocity().normalize().multiply(5));
                addBullet(bullet, player.getView().getTranslateX(), player.getView().getTranslateY());
                
            }
        });
        //*************END GAME CONTROLS*****************
        gametitle.setText("Smuggler's \n Run");
        gametitle.setTranslateX(225);
        gametitle.setTranslateY(40);
        gametitle.setFont(Font.font(STYLESHEET_MODENA, FontWeight.BOLD, 42));
        gametitle.setFill(Color.YELLOW);
        
        VBox buttons = new VBox();
        buttons.getChildren().addAll(play, exit, gametitle);
        buttons.setPrefSize(600, 600);
        buttons.setBackground(new Background(MenuBI));
        Scene gamemenu = new Scene(buttons);
        
        restart.setOnAction((ActionEvent e)->{
        	
        	try {
				Restart(primarystage);
			} catch (Exception e1) {
				
				e1.printStackTrace();
			}
            
    	});
        
        musicBI.play();
        primarystage.setScene(gamemenu);
        primarystage.show();
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}