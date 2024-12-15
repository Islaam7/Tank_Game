package Man;

import javax.media.opengl.GL;
import java.util.Scanner;
import java.util.Random;

public class Game {
    public int type; // 0 for AI game, 1 for PvP game
    public boolean pause;
    public int turn; // 0 for Player 1, 1 for Player 2 or AI
    public int[] heightmap;
    public static final int MAX_TERRAIN_WIDTH = 500;
    public static final int MAX_TERRAIN_HEIGHT = 300;
    public Player[] players;
    public int difficulty;
    public boolean gameEnded;
    public GL gl;
    public Player currentplayer;

    public Game(GL gl,int type, int difficulty) {
        this.type = type;
        this.pause = false;
        this.difficulty = difficulty;
        this.turn = 0;
        this.heightmap = HeightmapGenerator.generateHeightmap(MAX_TERRAIN_WIDTH, MAX_TERRAIN_HEIGHT);
        this.gameEnded = false;
        this.players = new Player[]{
                new Player("Player 1", false),
                new Player(type == 0 ? "AI" : "Player 2",type == 0 ? true : false)
        };
        this.gl = gl;
        this.currentplayer = players[turn];
    }

    public class HeightmapGenerator {
        private static final Random random = new Random();

        public static int[] generateHeightmap(int MaxX, int MaxHeight) {
            int[] heightmap = new int[MaxX + 1];

            // Initialize endpoints
            heightmap[0] = random.nextInt(MaxHeight / 4) + MaxHeight / 2; // Left endpoint
            heightmap[MaxX] = random.nextInt(MaxHeight / 4) + MaxHeight / 2; // Right endpoint

            // Perform midpoint displacement
            midpointDisplacement(heightmap, 0, MaxX, MaxHeight / 2);

            // Add mountains to the heightmap
            addMountains(heightmap, MaxHeight);

            // Apply smoothing filter to balance spikiness
            smoothHeightmap(heightmap, 5);

            return heightmap;
        }

        private static void midpointDisplacement(int[] heightmap, int left, int right, int range) {
            if (left + 1 == right || range <= 0) {
                return; // No more points to divide or range is invalid
            }

            int midpoint = (left + right) / 2;

            // Calculate midpoint height
            int averageHeight = (heightmap[left] + heightmap[right]) / 2;
            int displacement = random.nextInt(Math.max(1, range)) - range / 2; // Ensure range is positive
            heightmap[midpoint] = Math.max(0, averageHeight + displacement);

            // Reduce the range of displacement for finer details
            int newRange = range / 2;

            // Recursively apply to left and right segments
            midpointDisplacement(heightmap, left, midpoint, newRange);
            midpointDisplacement(heightmap, midpoint, right, newRange);
        }


        private static void addMountains(int[] heightmap, int MaxHeight) {
            int length = heightmap.length;

            // Add a few mountain peaks
            int numMountains = random.nextInt(3) + 2; // 2 to 4 mountains
            for (int i = 0; i < numMountains; i++) {
                int peakPosition = random.nextInt(length / 2) + length / 4; // Avoid peaks at edges
                int peakHeight = MaxHeight - random.nextInt(MaxHeight / 3);

                // Spread the mountain height to surrounding points
                for (int x = Math.max(0, peakPosition - 10); x <= Math.min(length - 1, peakPosition + 10); x++) {
                    int distance = Math.abs(x - peakPosition);
                    int heightContribution = (int) (peakHeight * (1.0 - (distance / 10.0)));
                    heightmap[x] = Math.min(MaxHeight, heightmap[x] + heightContribution);
                }
            }
        }

        private static void smoothHeightmap(int[] heightmap, int iterations) {
            int length = heightmap.length;

            for (int iter = 0; iter < iterations; iter++) {
                int[] smoothed = new int[length];

                for (int i = 1; i < length - 1; i++) {
                    // Average the current point with its neighbors
                    smoothed[i] = (heightmap[i - 1] + heightmap[i] + heightmap[i + 1]) / 3;
                }

                // Preserve endpoints
                smoothed[0] = heightmap[0];
                smoothed[length - 1] = heightmap[length - 1];

                // Copy smoothed values back to heightmap
                System.arraycopy(smoothed, 0, heightmap, 0, length);
            }
        }
    }


    public void CleanUp(){
        players = null;
        heightmap = null;
        pause = false;
        turn = 0;
        difficulty = 0;
    }



    public void processMove(int angle, int power) {

        if (!gameEnded) {
            System.out.println("Firing projectile at angle " + angle + " and power " + power);
            boolean hit = simulateProjectileAndDraw(this.gl, angle, power);
            if (hit) {
                currentplayer.increaseScore(10); // Increase score on successful hit
                System.out.println(currentplayer.name + "Score" + currentplayer.score);
            }

            currentplayer.decreaseRockets();
            currentplayer.decreaseMoves();
            checkWin(); // Check if the game ends after the move
            turn = 1 - turn;
            currentplayer = players[turn];

            if (currentplayer.isAi())
            {
                System.out.println("Making AI Move");
                currentplayer.makeAiMove(players[1 - turn].getPosition(), difficulty);
                processMove(currentplayer.angle,currentplayer.power);
            }
            }
        }


    public boolean simulateProjectile(int angle, int power) {
        double radians = Math.toRadians(angle);
        double velocityX = power * Math.cos(radians);
        double velocityY = power * Math.sin(radians);
        double gravity = 9.8;

        double x = players[turn].getPosition()[0];
        double y = heightmap[(int) x];

        while (x >= 0 && x < MAX_TERRAIN_WIDTH && y >= 0) {
            x += velocityX * 0.1;
            y += velocityY * 0.1;
            velocityY -= gravity * 0.1;

            if (Math.abs(x - players[1 - turn].getPosition()[0]) < 10) { // Adjust the hitbox size as needed
                System.out.println("Projectile hit player!");
                return true;
            }

            // Check for collision with terrain
            if (x >= 0 && x < MAX_TERRAIN_WIDTH && y <= heightmap[(int) x]) {
                System.out.println("Projectile hit terrain at (" + (int) x + ", " + (int) y + ")");
                destroyTerrain((int) x, 22);
                return false; // Indicate that the projectile did not hit the other player
            }
        }

        return false; // Projectile did not hit the other player or the terrain
    }

    private void DrawProjectile(GL gl, double x, double y) {
        // Save the current transformation matrix
        gl.glPushMatrix();

        // Set the color for the projectile (e.g., red)
        gl.glColor3f(1.0f, 0.0f, 0.0f);

        // Translate to the projectile's position
        gl.glTranslated(x, y, 0);

        // Draw the projectile as a small circle
        int segments = 20; // Smoothness of the circle
        double radius = 2.0; // Radius of the projectile
        gl.glBegin(GL.GL_POLYGON);
        for (int i = 0; i < segments; i++) {
            double angle = 2 * Math.PI * i / segments;
            double dx = radius * Math.cos(angle);
            double dy = radius * Math.sin(angle);
            gl.glVertex2d(dx, dy);
            System.out.println(dx + " " + dy);
        }
        gl.glEnd();

        // Restore the previous transformation matrix
        gl.glPopMatrix();
    }


    public boolean simulateProjectileAndDraw(GL gl, int angle, int power) {
        double radians = Math.toRadians(angle);
        double velocityX = power * Math.cos(radians);
        double velocityY = power * Math.sin(radians);
        double gravity = 9.8;

        double x = players[turn].getPosition()[0];
        double y = heightmap[(int) x];

        while (x >= 0 && x < MAX_TERRAIN_WIDTH && y >= 0) {
            x += velocityX * 0.1;
            y += velocityY * 0.1;
            velocityY -= gravity * 0.1;

            // Draw the projectile at the current position
            DrawProjectile(gl, x, y);

            // Check for collision with the other player
            if (Math.abs(x - players[1 - turn].getPosition()[0]) < 10) { // Adjust the hitbox size as needed
                System.out.println("Projectile hit player!");
                return true;
            }

            // Check for collision with terrain
            if (x >= 0 && x < MAX_TERRAIN_WIDTH && y <= heightmap[(int) x]) {
                System.out.println("Projectile hit terrain at (" + (int) x + ", " + (int) y + ")");
                destroyTerrain((int) x, 22);
                return false; // Indicate that the projectile did not hit the other player
            }
        }

        return false; // Projectile did not hit the other player or the terrain
    }



    private void destroyTerrain(int cx, int radius) {
        for (int x = Math.max(0, cx - radius); x <= Math.min(MAX_TERRAIN_WIDTH - 1, cx + radius); x++) {
            int distance = Math.abs(x - cx);
            int destructionHeight = (int) Math.sqrt(Math.pow(radius, 2) - Math.pow(distance, 2));
            heightmap[x] = Math.max(0, heightmap[x] - destructionHeight);
        }
    }

    public void pauseGame() {
        System.out.println("Game is paused");
        pause = true;
    }

    public void unPauseGame() {
        System.out.println("Game Unpaused");
        pause = false;
    }



    public void checkWin() {
        boolean allOutOfRockets = players[0].getRocketsLeft() <= 0 && players[1].getRocketsLeft() <= 0;

        if (allOutOfRockets) {
            System.out.println("Both players are out of rockets! Ending game...");
            gameEnded = true;
            if (players[0].getScore() > players[1].getScore()) {
                System.out.println(players[0].getName() + " wins with " + players[0].getScore() + " points!");
            } else if (players[0].getScore() < players[1].getScore()) {
                System.out.println(players[1].getName() + " wins with " + players[1].getScore() + " points!");
            } else {
                System.out.println("It's a tie!");
            }
        }
    }

    public class Player {
        public String name;
        public boolean ai;
        public int posX;
        public int posY;
       public int power;
        public int angle;
        public int score;
        public int rocketsLeft;
        public int movesLeft;

        public Player(String name, boolean ai) {
            this.name = name;
            this.ai = ai;

            this.posX = new Random().nextInt(MAX_TERRAIN_WIDTH);
            this.posY = 0;
            this.score = 0;
            this.rocketsLeft = 5;
            this.movesLeft = 5;
        }

        public String getName() {
            return name;
        }
        public int getAngle(){
            return angle;
        }
        public void setAngle(int angle){
            this.angle = angle;
        }

        public boolean isAi() {
            return ai;
        }

        public int[] getPosition() {
            return new int[]{posX, posY};
        }

        public int getScore() {
            return score;
        }

        public void increaseScore(int points) {
            score += points;
        }

        public int getRocketsLeft() {
            return rocketsLeft;
        }

        public void decreaseRockets() {
            rocketsLeft = Math.max(0, rocketsLeft - 1);
        }

        public int getMovesLeft() {
            return movesLeft;
        }

        public void decreaseMoves() {
            movesLeft = Math.max(0, movesLeft - 1);
        }
        public void makeAiMove(int[] targetPosition, int difficulty) {
            Random random = new Random();
            int playerX = targetPosition[0];

            // Calculate the distance to the target
            int distanceToTarget = Math.abs(playerX - this.posX);

            // Calculate the base angle (aiming towards the target)
            int baseAngle = (playerX > this.posX) ? 45 : 135;

            // **Prediction Attempt:**
            int predictedAngle = baseAngle;
            int predictedPower = 50 + (distanceToTarget / 10); // Simple power estimation

            // Simulate the projectile (without drawing) to see if it hits
            boolean predictedHit = simulateProjectile(predictedAngle, predictedPower);

            // Adjust angle and power based on prediction and difficulty
            int angleRange;
            int powerRange;
            switch (difficulty) {
                case 1: // Easy
                    angleRange = 40;
                    powerRange = 20;
                    break;
                case 2: // Medium
                    angleRange = 20;
                    powerRange = 15;
                    break;
                case 3: // Hard
                    angleRange = 10;
                    powerRange = 10;
                    break;
                default:
                    angleRange = 40;
                    powerRange = 20;
                    break;
            }

            if (predictedHit) {
                // If the prediction was successful, refine the aim slightly
                angle = predictedAngle + random.nextInt(angleRange / 2) - (angleRange / 4);
                power = predictedPower + random.nextInt(powerRange) - (powerRange / 2);
            } else {
                // If the prediction failed, adjust the aim more randomly
                int angleOffset = random.nextInt(angleRange) - (angleRange / 2);
                angle = baseAngle + angleOffset;
                power = random.nextInt(powerRange) + 50;
            }

            this.angle = angle;
            this.power = power;
        }

        public void drawTank(GL gl, int[] textures, int x, float scale, int[] heightMap, float canonAngle) {
            // Ensure x is within bounds of the heightMap array
            if (x < 1 || x >= heightMap.length - 1) {
                System.err.println("Error: x out of bounds for heightMap array");
                return;
            }

            // Get the y value (height) of the terrain at x
            float y = heightMap[x];

            // Calculate the slope at x using adjacent points
            float slope = (heightMap[x + 1] - heightMap[x - 1]) / 2.0f; // Approximate slope
            float groundAngle = (float) Math.toDegrees(Math.atan(slope)); // Convert slope to angle in degrees

            // Enable textures
            gl.glEnable(GL.GL_BLEND);
            gl.glBindTexture(GL.GL_TEXTURE_2D, textures[1]);

            // Save the current transformation state
            gl.glPushMatrix();

            // Position the tank
            gl.glTranslatef(x, y, 0);
            gl.glRotatef(-groundAngle, 0, 0, 1); // Align with terrain slope
            gl.glScalef(scale, scale, 1);

            // Draw tank body
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(-0.5f, -0.25f); // Bottom-left
            gl.glTexCoord2f(1, 0); gl.glVertex2f(0.5f, -0.25f);  // Bottom-right
            gl.glTexCoord2f(1, 1); gl.glVertex2f(0.5f, 0.25f);   // Top-right
            gl.glTexCoord2f(0, 1); gl.glVertex2f(-0.5f, 0.25f);  // Top-left
            gl.glEnd();

            // Draw cannon
            gl.glPushMatrix(); // Save transformation for cannon
            gl.glTranslatef(0, 0.25f, 0); // Position cannon on top of the tank body
            gl.glRotatef(canonAngle, 0, 0, 1); // Rotate for cannon angle
            gl.glScalef(0.5f, 0.1f, 1); // Scale to make the cannon thin and long

            gl.glBindTexture(GL.GL_TEXTURE_2D, textures[2]); // Bind cannon texture
            gl.glBegin(GL.GL_QUADS);
            gl.glTexCoord2f(0, 0); gl.glVertex2f(0, -0.05f);   // Bottom-left
            gl.glTexCoord2f(1, 0); gl.glVertex2f(1.0f, -0.05f); // Bottom-right
            gl.glTexCoord2f(1, 1); gl.glVertex2f(1.0f, 0.05f);  // Top-right
            gl.glTexCoord2f(0, 1); gl.glVertex2f(0, 0.05f);     // Top-left
            gl.glEnd();

            gl.glPopMatrix(); // Restore transformation for cannon

            // Restore transformation state for tank
            gl.glPopMatrix();

            // Disable textures
            gl.glDisable(GL.GL_BLEND);
        }

    }
}
