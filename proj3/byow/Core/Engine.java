package byow.Core;

import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.io.File;

public class Engine implements Serializable{
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
    TETile[][] cover=new TETile[WIDTH][HEIGHT];
    TERenderer ter = new TERenderer();
    LinkedList<int[][]> rects=new LinkedList<>();
    int userX;
    int userY;

    int foodNum=10;
    int[] hintCount=new int[] {0};
    String name = "Avatar";
    TETile appearance = Tileset.AVATAR;
    TETile floor = Tileset.FLOOR;
    TETile wall = Tileset.WALL;
    TETile background = Tileset.NOTHING;
    double time;
    Long initTime=java.lang.System.nanoTime();
    Long time1;
    Long time2;
    float totalTime;


//    int[] gameData;


    ArrayList<ArrayList<Integer>> validCoor=new ArrayList<>();//valid coordinate inside of the walls

    static final File CWD = new File(".");
    static final File GAME_DIR = new File(CWD, ".game");


    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     * <p>
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     * <p>
     * In other words, both of these calls:
     * - interactWithInputString("n123sss:q")
     * - interactWithInputString("lww")
     * <p>
     * should yield the exact same world state as:
     * - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */


    private void loadGame() {
        File f = Utils.join(GAME_DIR, "prevGame");
        if (f.exists()) {
            ter.initialize(WIDTH, HEIGHT);
            Engine read = Utils.readObject(f, Engine.class);
//            finalWorldFrame=getFinalWorrldFame
            time2=java.lang.System.nanoTime();
            read.move();

        } else {
            Font font = new Font("Monaco", Font.BOLD, 15);
            StdDraw.setFont(font);
            StdDraw.text(20,5,"No Record");
            StdDraw.show();
            StdDraw.pause(1000);
        }

    }


    private void saveGame() {
        if (!GAME_DIR.exists()) {
            GAME_DIR.mkdir();
        }
        File f = Utils.join(GAME_DIR, "prevGame");
        if (f.exists()) {
            f.delete();
        }
        Utils.writeObject(f, this);
    }

    private void quit() {
        saveGame();
        interactWithKeyboard();

    }


    // @Source:
    // https://stackoverflow.com/questions/23981678/is-it-possible-
    // to-use-a-string-as-a-seed-for-an-instance-random
    private static long stringToSeed(String s) {
        if (s == null) {
            return 0;
        }
        long hash = 0;
        for (char c : s.toCharArray()) {
            hash = 31L * hash + c;
        }
        return hash;
    }

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        setUp();
        drawMenu();
        userInput();
    }

    public void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(20,20,s);
        StdDraw.show();
    }


    public void newGame() {
        drawFrame("Enter a random seed, end with 'S'");
        String seed = "";
//        Character[] a=new Character[] {'0','1','2','3','4','5','6','7','8','9'};
        ArrayList<Character> a=new ArrayList<>();
        for(int i=0;i<=9;i++){
            a.add((char) (i+'0'));
        }
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String nextKey = Character.toString(StdDraw.nextKeyTyped());
                seed += nextKey;
                drawFrame(seed);
                if (Character.toUpperCase(seed.charAt(seed.length()-1)) == 'S') {
                    break;

                }
                if (!(Character.toUpperCase(seed.charAt(seed.length()-1)) == 'S' || a.contains(Character.toUpperCase(seed.charAt(seed.length()-1)))) ){
                    drawFrame("Wrong input, try again!");
                    StdDraw.pause(300);
                    seed="";
                    continue;


                }
            }
        }
        StdDraw.pause(200);
        mode1(seed);

    }

    public void userInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character nextKey = StdDraw.nextKeyTyped();

                switch (Character.toUpperCase(nextKey)) {
                    case 'N':
                        newGame();
                        break;
                    case 'L':
                        loadGame();
                        break;
                    case ':': //quit and save
                        while (true) {
                            if (StdDraw.hasNextKeyTyped()) {
                                Character charQ = Character.toUpperCase(StdDraw.nextKeyTyped());
                                if (charQ.equals('Q')) {
                                    quit();
                                } else {
                                    return;
                                }
                            }
                        }
//                        saveGame();
//                        break;
                    case 'M' :
                        menu();
                        break;
                    case 'X': //quit and save
                       changeName();
                       break;
                    case 'Y' :
                        changeAppearance();
                        break;
                    case 'Z' :
                        changeTheme();
                        break;
                    default:
                        Font font = new Font("Monaco", Font.BOLD, 15);
                        StdDraw.setFont(font);
                        StdDraw.text(20,5,"No command with that name exists. Try again.");
                        StdDraw.show();
                        StdDraw.pause(1000);
                        userInput();
                        return;
                }
            }
        }
    }

    public void changeName() {
        drawFrame("Enter your name, end with ':'");
        String inputName = "";

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                String nextKey = Character.toString(StdDraw.nextKeyTyped());
                inputName += nextKey;
                drawFrame(inputName);
                if (Character.toUpperCase(inputName.charAt(inputName.length()-1)) == ':') {
                    name = inputName.substring(0,inputName.length()-1);
                    break;
                }
            }
        }
        StdDraw.pause(200);
        intermediate();
    }

    public void changeAppearance() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontBig);
        StdDraw.text(40/2, 30, "I want to be a : ");
        Font fontSmall = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmall);

        StdDraw.text(40/2, 20, "@");
        StdDraw.text(40/2, 17, "$");
        StdDraw.text(40/2, 14, "#");
        StdDraw.text(40/2, 11, "*");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character nextKey = StdDraw.nextKeyTyped();
                if (nextKey.equals('$')) {
                    appearance = Tileset.me1;
                } else if (nextKey.equals('#')) {
                    appearance = Tileset.me2;
                } else if (nextKey.equals('*')) {
                    appearance = Tileset.me3;
                } else if (nextKey.equals('@')){
                    appearance = Tileset.AVATAR;
                } else {
                    Font font = new Font("Monaco", Font.BOLD, 15);
                    StdDraw.setFont(font);
                    StdDraw.text(20,5,"No command with that name exists. Try again.");
                    StdDraw.show();
                    StdDraw.pause(1000);
                    changeAppearance();
                }
                drawFrame(Character.toString(nextKey));

                break;
            }
        }
        intermediate();
    }

    public void changeTheme() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontBig);
        StdDraw.text(40/2, 30, "Choose a theme : ");
        Font fontSmall = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmall);

        StdDraw.text(40/2, 20, "Default (1)");
        StdDraw.text(40/2, 17, "Beach (2)");
        StdDraw.text(40/2, 14, "Park (3)");
        StdDraw.show();
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character nextKey = StdDraw.nextKeyTyped();
                if (nextKey.equals('1')) {
                    break;
                } else if (nextKey.equals('2')) {
                    background = Tileset.WATER;
                    floor = Tileset.FLOOR;
                    wall = Tileset.SAND;
                } else if (nextKey.equals('3')) {
                    background = Tileset.FLOOR;
                    floor = Tileset.GRASS;
                    wall = Tileset.TREE;
                } else {
                    Font font = new Font("Monaco", Font.BOLD, 15);
                    StdDraw.setFont(font);
                    StdDraw.text(20,5,"No command with that name exists. Try again.");
                    StdDraw.show();
                    StdDraw.pause(1000);
                    changeTheme();
                }
                drawFrame(Character.toString(nextKey));

                break;
            }
        }
        intermediate();

    }

    public void intermediate() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontBig);
        StdDraw.text(40/2, 22, "Start Game (N)");
        StdDraw.text(40/2, 18, "Back to Menu (M)");
        StdDraw.show();

        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                Character nextKey = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (nextKey.equals('N')) {
                    newGame();
                    break;
                } else if (nextKey.equals('M')) {
                    menu();
                    break;
                } else {
                    Font font = new Font("Monaco", Font.BOLD, 15);
                    StdDraw.setFont(font);
                    StdDraw.text(20,5,"No command with that name exists. Try again.");
                    StdDraw.show();
                    StdDraw.pause(1000);
                    intermediate();
                }
            }
        }

    }

    public void setUp() {
        StdDraw.setCanvasSize(40 * 16, 40 * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, 40);
        StdDraw.setYscale(0, 40);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }

    public void drawMenu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontBig);
        StdDraw.text(40/2, 30, "BYoW");
        Font fontSmall = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmall);
        StdDraw.text(40/2, 20, "New Game (N)");
        StdDraw.text(40/2, 17, "Load Game (L)");
        StdDraw.text(40/2, 14, "Quit (:Q)");
        StdDraw.text(40/2, 11, "Menu (M)");
        StdDraw.show();
    }

    private void menu() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font fontBig = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(fontBig);
        StdDraw.text(40/2, 30, "Custom Settings");
        Font fontSmall = new Font("Monaco", Font.BOLD, 25);
        StdDraw.setFont(fontSmall);
        StdDraw.text(40/2, 20, "Change Name (X)");
        StdDraw.text(40/2, 17, "Change Appearance (Y)");
        StdDraw.text(40/2, 14, "Change Theme (Z)");
        StdDraw.show();
    }



    public TETile[][] interactWithInputString(String input) {
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        ter.initialize(WIDTH, HEIGHT);
//        Random rdm=new Random();
//        LinkedList<int[][]> rects=new LinkedList<>();
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                finalWorldFrame[x][y] = background;
            }
        }

        Random rdm = new Random(Long.parseLong(input.substring(0,input.length()-1)));


        for (int i = 0; i <=10; i++) {
            int[][] rect = generateRoom(rdm);
            rects.add(rect);
            for (int x = rect[0][0]; x <= rect[0][1]; x++) {
                for (int y = rect[1][0]; y <= rect[1][1]; y++) {
                    if (x >= 2 && x < WIDTH - 2 && y >= 2 && y < HEIGHT - 2) {
                        finalWorldFrame[x][y] = floor;

                    }
                }
            }

        }
        ArrayList<int[][]> sortedRects=sortedRects(rects);
        for(int i=0;i<sortedRects.size()-1;i++){
            connect(sortedRects.get(i),sortedRects.get(i+1),rdm);
        }
        for (int x = 1; x < WIDTH - 1; x += 1) {
            for (int y = 1; y < HEIGHT - 1; y += 1) {
                if (finalWorldFrame[x][y].equals(background)
                        && (finalWorldFrame[x + 1][y].equals(floor)
                        || finalWorldFrame[x][y + 1].equals(floor)
                        || finalWorldFrame[x - 1][y].equals(floor)
                        || finalWorldFrame[x][y - 1].equals(floor)
                        || finalWorldFrame[x + 1][y - 1].equals(floor)
                        || finalWorldFrame[x - 1][y - 1].equals(floor)
                        || finalWorldFrame[x - 1][y + 1].equals(floor)
                        || finalWorldFrame[x + 1][y + 1].equals(floor))) {
                    finalWorldFrame[x][y] = wall;

                }
            }
        }


//        for(int i=)
//        ter.renderFrame(finalWorldFrame);
        this.validCoordinates();
        return finalWorldFrame;
    }


    private void connect(int[][] rect1, int[][] rect2, Random rdm) {
//        if(rect1[1][0]<rect2[1][0]){
            int beginX=rect1[0][0];
            int beginY=rect1[1][0];
            int endX=rect2[0][0];
            int endY=rect2[1][0];
            int xLength=endX-beginX;
            int yLength=endY-beginY;
            LinkedList<Integer> turn=new LinkedList<>();
            if(xLength>0){
                for(int i=0;i<xLength;i++){
                    turn.addLast(0);
                }

            }else{
                for(int i=xLength;i<0;i++){
                    turn.addLast(2);
                }

            }

            if(yLength>0) {
                for (int i = 0; i < yLength; i++) {
                    turn.addLast(1);
                }
            }else{
                for (int i = yLength; i< 0; i++) {
                    turn.addLast(3);
                }

            }
            while(turn.size()!=0){
                if(turn.get(0)==0){
                    if(beginY<HEIGHT&&beginY>=0&&beginX>=0&&beginX+1<WIDTH) {
                        finalWorldFrame[beginX][beginY] = floor;
                        beginX += 1;
                        finalWorldFrame[beginX][beginY] = floor;
//                        ter.renderFrame(finalWorldFrame);
                    }
                }else if(turn.get(0)==1){
                    if(beginY+1<HEIGHT&&beginY>=0&&beginX>=0&&beginX<WIDTH) {
                        finalWorldFrame[beginX][beginY] = floor;
                        beginY += 1;
                        finalWorldFrame[beginX][beginY] = floor;
//                        ter.renderFrame(finalWorldFrame);
                    }
                } else if (turn.get(0)==2) {
                    if(beginY<HEIGHT&&beginY>=0&&beginX-1>=0&&beginX<WIDTH) {
                        finalWorldFrame[beginX][beginY] = floor;
                        beginX -= 1;
                        finalWorldFrame[beginX][beginY] = floor;
//                        ter.renderFrame(finalWorldFrame);
                    }

                }else if(turn.get(0)==3) {
                    if(beginY<HEIGHT&&beginY-1>=0&&beginX>=0&&beginX<WIDTH) {
                        finalWorldFrame[beginX][beginY] = floor;
                        beginY -= 1;
                        finalWorldFrame[beginX][beginY] = floor;
//                        ter.renderFrame(finalWorldFrame);
                    }
                }
                turn.removeFirst();
            }

//        }
//        return finalWorldFrame;
    }

    public int[][] generateRoom(Random rdm) {

        int x = rdm.nextInt(5, WIDTH - 5);
        int y = rdm.nextInt(5, HEIGHT - 5);
        int width = rdm.nextInt(3, 5);
//        int width=(int)RandomUtils.poisson(rdm,3);
        int height = rdm.nextInt(3, 5);
//        int height=(int)RandomUtils.poisson(rdm,3);
//        int y1=rdm.nextInt(10);
//        int y2=rdm.nextInt(10);
        int[][] res = new int[2][2];
        res[0][0] = x;
        res[0][1] = x + width;
        res[1][0] = y;
        res[1][1] = y + height;


        return res;
    }

    public int[][] generateHallways(Random rdm) {

        int x = rdm.nextInt(5, WIDTH - 5);
        int y = rdm.nextInt(5, HEIGHT - 5);
        int width = rdm.nextInt(3, 5);
//        int width=(int)RandomUtils.poisson(rdm,3);
        int height = rdm.nextInt(3, 5);
//        int height=(int)RandomUtils.poisson(rdm,3);
//        int y1=rdm.nextInt(10);
//        int y2=rdm.nextInt(10);
        int[][] res = new int[2][2];
        res[0][0] = x;
        res[0][1] = x + width;
        res[1][0] = y;
        res[1][1] = y + height;


        return res;
    }

    public ArrayList<int[][]> sortedRects(LinkedList<int[][]> rects){
        ArrayList<int[][]> newRects=new ArrayList<>();
        while(rects.size()!=0){
            int [][] rect=rects.get(0);
            for(int[][] i:rects){
                if(i[0][0]<rect[0][0]){
                    rect=i;
                }
            }
            newRects.add(rect);
            rects.remove(rect);

        }
        return newRects;
    }

    //return valid coordinates inside walls.
    public void validCoordinates(){
//        int[][] res=new int[WIDTH][HEIGHT];
        ArrayList<Integer> x=new ArrayList<>();
        ArrayList<Integer> y=new ArrayList<>();
//        ArrayList<ArrayList<Integer>> res=new ArrayList<>();
//        List<Integer> list= new
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                if(finalWorldFrame[i][j].equals(floor)){
                    x.add(i);
                    y.add(j);
                }

            }
        }
        validCoor.add(x);
        validCoor.add(y);
    }

    //verify if a coordinate is inside the walls.
    public boolean verifyValidity(int x,int y){
        ArrayList<Integer> X=validCoor.get(0);
        ArrayList<Integer> Y=validCoor.get(1);
        for(int i=0;i<X.size();i++){
            if(X.get(i).equals(x)&&Y.get(i).equals(y)){
                return true;
            }
        }

        return false;
    }

    public void generateWorld(String seed){
        interactWithInputString(seed);


        generateFood(seed,foodNum);
        generateUser(seed);
        setCover();


    }

    public void mode1(String seed){
        generateWorld(seed);
        time1=java.lang.System.nanoTime();
        time2=java.lang.System.nanoTime();
        time=0;
        totalTime=0;
        move();

    }



    public void move(){
        ter.renderFrame(finalWorldFrame);
        StdDraw.pause(500);
        ter.renderFrame(cover);
        while(true){
            time1=java.lang.System.nanoTime();
            time+=(time1-time2)/1000000000000.0;
            totalTime+=(time1-time2)/1000000000000.0;
            mouse();
            if(StdDraw.hasNextKeyTyped()) {
                Character step=StdDraw.nextKeyTyped();
                String upperStep=step.toString().toUpperCase();
                    switch (upperStep) {
                        case "W" -> {
                            if(verifyValidity(userX,userY+1)) {
                                finalWorldFrame[userX][userY] = floor;
                                userY += 1;
                                eatFood();
                                finalWorldFrame[userX][userY] = appearance;
                            }
                        }
                        case "S" -> {
                            if(verifyValidity(userX,userY-1)) {
                                finalWorldFrame[userX][userY] = floor;
                                userY -= 1;
                                eatFood();
                                finalWorldFrame[userX][userY] = appearance;
                            }
                        }
                        case "A" -> {
                            if(verifyValidity(userX-1,userY)) {

                                finalWorldFrame[userX][userY] = floor;
                                userX -= 1;
                                eatFood();
                                finalWorldFrame[userX][userY] = appearance;
                            }
                        }
                        case "J" ->{
                            hintCount[0]+=1;
                            ter.renderFrame(finalWorldFrame);
                            StdDraw.pause(500);
                        }
                        case ":" ->{
                            while (true) {
                                if (StdDraw.hasNextKeyTyped()) {
                                    Character charQ = StdDraw.nextKeyTyped();
                                    Character.toUpperCase(charQ);
                                    if (charQ.equals('Q')) {
                                       quit();

                                    }
                                    break;

                                }
                            }


                        }
                        case "D"  -> {
                            if(verifyValidity(userX+1,userY)) {
                                finalWorldFrame[userX][userY] = floor;
                                userX += 1;
                                eatFood();
                                finalWorldFrame[userX][userY] = appearance;
                            }
                        }
                        default -> {
                            continue;
                        }
                    }
                    setCover();
//                    ter.renderFrame(cover);
//
//                    StdDraw.setPenColor(Color.WHITE);
//
//                    StdDraw.text(WIDTH, HEIGHT - 1, String.valueOf(time/1000));
//                    StdDraw.show();



            }
            if(time>=60){
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
//                        Font fontBig = new Font("Monaco", Font.BOLD, 40);
//                        StdDraw.setFont(fontBig);
                StdDraw.text(WIDTH/2, HEIGHT/2, name + "  You lose..");
                StdDraw.show();
                StdDraw.pause(5000);
                Engine newEngine=new Engine();
                newEngine.interactWithKeyboard();
            }
            if(win()){
//                        long time2=java.lang.System.nanoTime();
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
//                        Font fontBig = new Font("Monaco", Font.BOLD, 40);
//                        StdDraw.setFont(fontBig);
                StdDraw.text(WIDTH/2, HEIGHT/2, name + "  You win!!");
                        StdDraw.text(WIDTH/2, HEIGHT/2-4, "Time used: "+String.valueOf((totalTime))+" seconds.");
                if(hintCount[0]==0){
                    StdDraw.text(WIDTH/2, HEIGHT/2-8, "You didn't use the hint! Good job!");
                }else {
                    if(hintCount[0]==1){
                        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "You used the hint " + hintCount[0] + " time.");

                    }else{
                        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "You used the hint " + hintCount[0] + " times.");

                    }
                }

                StdDraw.show();
                StdDraw.pause(5000);
                Engine newEngine=new Engine();
                newEngine.interactWithKeyboard();

            }
            ter.renderFrame(cover);

            StdDraw.setPenColor(Color.WHITE);

            StdDraw.textRight(WIDTH, HEIGHT - 1, String.valueOf((int) (60-time))+" seconds");
            StdDraw.show();




        }
    }


    private void generateUser(String seed) {
        Random rdm=new Random(Long.parseLong((seed.substring(0,seed.length()-1))));
        int a=rdm.nextInt(validCoor.get(0).size());
        userX=validCoor.get(0).get(a);
        userY=validCoor.get(1).get(a);
        finalWorldFrame[userX][userY]=appearance;
//        setCover();
//        ter.renderFrame(finalWorldFrame);

    }

    private void generateFood(String seed, int foodNum) {
        Random rdm=new Random(1+Long.parseLong((seed.substring(0,seed.length()-1))));
//        int[] rdmNumbers=new int[10];
        ArrayList<Integer> rdmNumbers=new ArrayList<>();
        int rdmNumber=rdm.nextInt(validCoor.get(0).size());
        rdmNumbers.add(rdmNumber);
        finalWorldFrame[validCoor.get(0).get(rdmNumber)][validCoor.get(1).get(rdmNumber)]=Tileset.FLOWER;
        int i=1;
        while(true) {
            rdmNumber=rdm.nextInt(validCoor.get(0).size());
            if(i==foodNum){
                break;
            }
            if(!rdmNumbers.contains(rdmNumber)){
                rdmNumbers.add(rdmNumber);
                i+=1;
                finalWorldFrame[validCoor.get(0).get(rdmNumber)][validCoor.get(1).get(rdmNumber)]=Tileset.FLOWER;
            }

        }
//        ter.renderFrame(finalWorldFrame);
    }

    public void eatFood(){
        if(finalWorldFrame[userX][userY].equals(Tileset.FLOWER)){
            foodNum-=1;
            time-=5;
//            if(time<=0){
//                time=0;
//            }
        }
    }

    public boolean win(){
        if(foodNum==0){
            return true;
        }
        return false;
    }
    public void setCover(){
//        TETile[][] coverCoor=new TETile[WIDTH][HEIGHT];
        for(int i=0;i<WIDTH;i++){
            for(int j=0;j<HEIGHT;j++){
                cover[i][j]=background;
            }
        }
        for(int i=0;i<=5;i++){
            for(int j=-i;j<=i;j++){
                if(userX+j<WIDTH&&userX+j>=0&&userY+5-i<HEIGHT&&userY+5-i>=0) {
                    cover[userX + j][userY + 5 - i] = finalWorldFrame[userX + j][userY + 5 - i];
                }
                if(userX+j<WIDTH&&userX+j>=0&&userY-5+i<HEIGHT&&userY-5+i>=0) {
                    cover[userX + j][userY - 5 + i] = finalWorldFrame[userX + j][userY - 5 + i];
                }
            }
        }
    }

    public void mouse() {
        int x = (int) StdDraw.mouseX();
        int y = (int) StdDraw.mouseY();
        if(x>=0&&x<WIDTH&&y>=0&&y<HEIGHT) {

            if (cover[x][y].equals(wall)) {
                ter.renderFrame(cover);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, "Wall: You Cannot Climb the Wall");
            } else if (cover[x][y].equals(appearance)) {
                ter.renderFrame(cover);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, name + ": Hi!");
            } else if (cover[x][y].equals(floor)) {
                ter.renderFrame(cover);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, "Floor: Proceed!");
            } else if (cover[x][y].equals(Tileset.FLOWER)) {
                ter.renderFrame(cover);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.textLeft(1, HEIGHT - 1, "Food: Eat It!");
            } else if (cover[x][y].equals(background)) {
//            ter.renderFrame(finalWorldFrame);
//            StdDraw.setPenColor(Color.BLACK);
//            StdDraw.textRight(x, y, "NOTHING");
                return;
            }

            StdDraw.show();
        }


    }
}
