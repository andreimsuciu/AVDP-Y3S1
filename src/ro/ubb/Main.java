package ro.ubb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static int width;
    private static int height;
    private static int maxValueOfByteComponent;
    private static double[][] yMatrix;
    private static double[][] uMatrix;
    private static double[][] vMatrix;
    private static double[][] decodedYMatrix;
    private static double[][] decodedUMatrix;
    private static double[][] decodedVMatrix;
    private static ArrayList<Block> blocks = new ArrayList<>();
    public static void main(String[] args) throws IOException {

        //encoder
        readImage();
        divideYMatrixIntoBlocks();
        divideUVMatricesIntoBlocks();
        printBlocks();

        //decoder
        reconstruct();
        decode();

        //irrelevant test functions
        printYUVMatrices();
    }

    private static void decode() throws IOException {
        FileWriter resultWriter = new FileWriter("resultP3.ppm");
        resultWriter.write("P3\n" + width + " " + height + "\n" + maxValueOfByteComponent + "\n");

        int R=0;
        int G=0;
        int B=0;
        boolean done = false;
        for(int heightMatrix=0; heightMatrix<height; heightMatrix++){
            for(int widthMatrix=0; widthMatrix<width; widthMatrix++){
                double Y=decodedYMatrix[heightMatrix][widthMatrix];
                double U=decodedUMatrix[heightMatrix][widthMatrix];
                double V=decodedVMatrix[heightMatrix][widthMatrix];
                R = (int) (Y + 1.140 * V);
                G = (int) (Y - 0.395*U - 0.581*V);
                B = (int) (Y + 2.0232*U);
                resultWriter.write(R+"\n"+G+"\n"+B+"\n");
            }
        }
        resultWriter.close();
    }
    private static void reconstruct() {
        decodedYMatrix = new double[height][width];
        decodedUMatrix = new double[height][width];
        decodedVMatrix = new double[height][width];
        for(Block block: blocks) {
            double[][] blockMatrix = block.getMatrixBlock();
            int blockHeight = block.getPosition().getHeightTopLeft();
            int blockWidth = block.getPosition().getWidthTopLeft();
            if (block.getBlockType().equals("Y")) {
                for (int heightMatrix = 0; heightMatrix < 8; heightMatrix++) {
                    for (int widthMatrix = 0; widthMatrix < 8; widthMatrix++) {
                        decodedYMatrix[blockHeight + heightMatrix][blockWidth + widthMatrix] = blockMatrix[heightMatrix][widthMatrix];
                    }
                }
            } else if (block.getBlockType().equals("U")) {

                    for (int heightMatrix = 0; heightMatrix < 4; heightMatrix++) {
                        for (int widthMatrix = 0; widthMatrix < 4; widthMatrix++) {
                            decodedUMatrix[blockHeight + heightMatrix * 2][blockWidth + widthMatrix * 2] = blockMatrix[heightMatrix][widthMatrix];
                            decodedUMatrix[blockHeight + heightMatrix * 2 + 1][blockWidth + widthMatrix * 2] = blockMatrix[heightMatrix][widthMatrix];
                            decodedUMatrix[blockHeight + heightMatrix * 2][blockWidth + widthMatrix * 2 + 1] = blockMatrix[heightMatrix][widthMatrix];
                            decodedUMatrix[blockHeight + heightMatrix * 2 + 1][blockWidth + widthMatrix * 2 + 1] = blockMatrix[heightMatrix][widthMatrix];
                        }
                    }
                } else {
                    for (int heightMatrix = 0; heightMatrix < 4; heightMatrix++) {
                        for (int widthMatrix = 0; widthMatrix < 4; widthMatrix++) {
                            decodedVMatrix[blockHeight + heightMatrix * 2][blockWidth + widthMatrix * 2] = blockMatrix[heightMatrix][widthMatrix];
                            decodedVMatrix[blockHeight + heightMatrix * 2 + 1][blockWidth + widthMatrix * 2] = blockMatrix[heightMatrix][widthMatrix];
                            decodedVMatrix[blockHeight + heightMatrix * 2][blockWidth + widthMatrix * 2 + 1] = blockMatrix[heightMatrix][widthMatrix];
                            decodedVMatrix[blockHeight + heightMatrix * 2 + 1][blockWidth + widthMatrix * 2 + 1] = blockMatrix[heightMatrix][widthMatrix];
                        }
                    }
                }

        }
    }

    public static void printBlocks() throws IOException {
        FileWriter resultWriter = new FileWriter("result.out");
        StringBuilder resSB= new StringBuilder();

        for (Block block:blocks) {
            resSB.append("[");
            double[][] matrixBlock = block.getMatrixBlock();
            if(block.getBlockType().equals("Y")){
                for(int i=0; i<8; i++){
                    for(int j=0; j<8; j++){
                        resSB.append(matrixBlock[i][j]);
                        resSB.append(",");
                    }
                    resSB.append("\n");
                }
            }
            else{
                for(int i=0; i<4; i++){
                    for(int j=0; j<4; j++){
                        resSB.append(matrixBlock[i][j]);
                        resSB.append(",");
                    }
                    resSB.append("\n");
                }
            }
            resSB.append("]\n");
        }
        resultWriter.write(resSB.toString());
        resultWriter.close();
    }

    public static void divideUVMatricesIntoBlocks(){
        boolean done = false;

        int widthCount=0;
        int heightCount=0;
        int yWidth=0;
        int yHeight=0;

        double[][] matrixBlockU= new double[4][4];
        double[][] matrixBlockV= new double[4][4];
        while(!done){
            double averageU=(uMatrix[heightCount+yHeight][widthCount+yWidth] +
                    uMatrix[heightCount+yHeight][widthCount+yWidth+1] +
                    uMatrix[heightCount+yHeight+1][widthCount+yWidth] +
                    uMatrix[heightCount+yHeight+1][widthCount+yWidth+1]) / 4;

            double averageV=(vMatrix[heightCount+yHeight][widthCount+yWidth] +
                    vMatrix[heightCount+yHeight][widthCount+yWidth+1] +
                    vMatrix[heightCount+yHeight+1][widthCount+yWidth] +
                    vMatrix[heightCount+yHeight+1][widthCount+yWidth+1]) / 4;

            matrixBlockU[widthCount/2][heightCount/2]=averageU;
            matrixBlockV[widthCount/2][heightCount/2]=averageV;

            widthCount+=2;
            if(widthCount==8){
                widthCount=0;
                heightCount+=2;
            }
            if(heightCount==8){
                heightCount=0;
                Position position = new Position(yHeight,yWidth);
                blocks.add(new Block(matrixBlockU,"U",position));
                blocks.add(new Block(matrixBlockV,"V",position));
                matrixBlockU=new double[4][4];
                matrixBlockV=new double[4][4];
                yWidth+=8;
                if(yWidth == width){
                    yWidth=0;
                    yHeight+=8;
                }
                if(yHeight==height){
                    done=true;
                }
            }
        }
    }

    public static void divideYMatrixIntoBlocks(){
        String type="Y";
        boolean done = false;
        int currentWidth=0;
        int currentHeight=0;
        int yWidth=0;
        int yHeight=0;
        double[][] matrixBlock= new double[8][8];
        while(!done){
            if(currentHeight==0 && currentWidth ==0){
                matrixBlock= new double[8][8];
            }
            matrixBlock[currentHeight][currentWidth]=yMatrix[yHeight+currentHeight][yWidth+currentWidth];

            currentWidth++;
            if(currentWidth == 8){
                currentWidth=0;
                currentHeight++;
            }
            if(currentHeight ==8){
                currentHeight=0;
                Position position = new Position(yHeight,yWidth);
                blocks.add(new Block(matrixBlock,type,position));
                yWidth+=8;
                if(yWidth==width){
                    yWidth=0;
                    yHeight+=8;
                }
                if(yHeight==height){
                    done=true;
                }
            }
        }
    }

    public static void readImage() throws IOException {
        File ppmImage = new File("nt-P3.ppm");
        java.util.Scanner ppmReader = new java.util.Scanner(ppmImage);

        String data = ppmReader.nextLine();
        data = ppmReader.nextLine();
        data = ppmReader.nextLine();

        String[] resolutionStr = data.split(" ");
        width= Integer.parseInt(resolutionStr[0]);
        height= Integer.parseInt(resolutionStr[1]);

        data = ppmReader.nextLine();
        maxValueOfByteComponent = Integer.parseInt(data);

        yMatrix = new double[height][width];
        uMatrix = new double[height][width];
        vMatrix = new double[height][width];

        int currentWidth=0;
        int currentHeight=0;
        int currentComponent=0;
        int R=0;
        int G=0;
        int B=0;

        while (ppmReader.hasNextLine()){
            if(currentWidth==width*3){
                currentWidth=0;
                currentHeight++;
            }
            if(currentHeight == height){
                break;
            }
            if(currentComponent == 3){
                currentComponent =0;
                yMatrix[currentHeight][currentWidth/3]= 0.299*R + 0.587*G + 0.114*B;
                uMatrix[currentHeight][currentWidth/3]= -0.147*R - 0.289*G + 0.436*B;
                vMatrix[currentHeight][currentWidth/3]= 0.615*R - 0.515*G - 0.100*B;
                ;
            }

            data= ppmReader.nextLine();

            if(currentComponent==0){
                R=Integer.parseInt(data);
            }
            if(currentComponent==1){
                G=Integer.parseInt(data);
            }
            if(currentComponent==2){
                B=Integer.parseInt(data);
            }
            currentWidth++;
            currentComponent++;
        }
    }
    public static void printYUVMatrices() throws IOException {
        FileWriter yResultWriter = new FileWriter("y.out");
        FileWriter uResultWriter = new FileWriter("u.out");
        FileWriter vResultWriter = new FileWriter("v.out");
        StringBuilder ySB= new StringBuilder();
        StringBuilder uSB= new StringBuilder();
        StringBuilder vSB= new StringBuilder();
        int count=0;
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                ySB.append(yMatrix[i][j]);
                ySB.append(" ");
                uSB.append(uMatrix[i][j]);
                uSB.append(" ");
                vSB.append(vMatrix[i][j]);
                vSB.append(" ");
                count++;
            }
            ySB.append("\n");
            uSB.append("\n");
            vSB.append("\n");
            if(i == height/2){
                yResultWriter.write(ySB.toString());
                uResultWriter.write(uSB.toString());
                vResultWriter.write(vSB.toString());
                ySB=new StringBuilder();
                uSB=new StringBuilder();
                vSB=new StringBuilder();
            }
        }
        yResultWriter.write(ySB.toString());
        uResultWriter.write(uSB.toString());
        vResultWriter.write(vSB.toString());

        yResultWriter.close();
        uResultWriter.close();
        vResultWriter.close();
    }
}
