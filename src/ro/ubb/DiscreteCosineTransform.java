package ro.ubb;

import java.util.ArrayList;
import java.util.Arrays;

public class DiscreteCosineTransform {

    public static ArrayList<Block> encode(ArrayList<Block> inputBlocks){
        ArrayList<Block> encodedBlocks = new ArrayList<>();
        for(Block block: inputBlocks){
            Block encodedBlock=forwardDCT(block);
            encodedBlocks.add(encodedBlock);
        }
        return encodedBlocks;
    }

    public static ArrayList<Block> decode(ArrayList<Block> inputBlocks){
        ArrayList<Block> decodedBlocks = new ArrayList<>();
        for(Block block: inputBlocks){
            Block encodedBlock=inverseDCT(block);
            decodedBlocks.add(encodedBlock);
        }
        return decodedBlocks;
    }

    public static Block inverseDCT(Block block){
        quantization(block,"*");

        double[][] inputMatrix=block.getMatrixBlock();
        double[][] resultMatrix= new double[8][8];
        double alphaU = 0, alphaV = 0;

        for(int x=0; x<8;x++){
            for(int y=0; y<8;y++){

                double sum=0 , partialSum=0;
                for(int u=0; u<8;u++){
                    for(int v=0;v<8;v++){
                        if(u == 0){
                            alphaU=1/Math.sqrt(2);
                        }
                        else{
                            alphaU=1;
                        }
                        if(v == 0){
                            alphaV=1/Math.sqrt(2);
                        }
                        else{
                            alphaV=1;
                        }
                        partialSum=alphaU*alphaV*inputMatrix[u][v] * Math.cos((2*x+1)*u*Math.PI/16) * Math.cos((2*y+1)*v*Math.PI/16);
                        sum+=partialSum;
                    }
                }
                resultMatrix[x][y]=sum/4;
            }
        }
        block.setMatrixBlock(resultMatrix);
        DCT128(block,"+");
        //System.out.println(Arrays.deepToString(block.getMatrixBlock()));
        return block;
    }

    public static Block forwardDCT(Block block){
        prepareBlock(block);
        //System.out.println(Arrays.deepToString(block.getMatrixBlock()));
        DCT128(block,"-");

        double[][] inputMatrix=block.getMatrixBlock();
        double[][] resultMatrix= new double[8][8];
        double alphaU = 0, alphaV = 0;
        for(int u=0; u<8;u++){
            for(int v=0; v<8;v++){
                if(u == 0){
                    alphaU=1/Math.sqrt(2);
                }
                else{
                    alphaU=1;
                }
                if(v == 0){
                    alphaV=1/Math.sqrt(2);
                }
                else{
                    alphaV=1;
                }
                double sum=0 , partialSum=0;
                for(int x=0; x<8;x++){
                    for(int y=0;y<8;y++){
                        partialSum=inputMatrix[x][y] * Math.cos((2*x+1)*u*Math.PI/16) * Math.cos((2*y+1)*v*Math.PI/16);
                        sum+=partialSum;
                    }
                }
                resultMatrix[u][v]=(alphaU*alphaV*sum)/4;
            }
        }
        block.setMatrixBlock(resultMatrix);
        quantization(block,"/");
        return block;
    }

    private static void prepareBlock(Block block){
        if (!block.getBlockType().equals("Y")) {
            double[][] fourBlock = block.getMatrixBlock();
            double[][] eightBlock = new double[8][8];

            for (int height = 0; height <= 3; height++) {
                for (int width = 0; width <= 3; width++) {
                    eightBlock[height * 2][width * 2] = fourBlock[height][width];
                    eightBlock[height * 2 + 1][width * 2] = fourBlock[height][width];
                    eightBlock[height * 2][width * 2 + 1] = fourBlock[height][width];
                    eightBlock[height * 2 + 1][width * 2 + 1] = fourBlock[height][width];
                }
            }
            block.setMatrixBlock(eightBlock);
        }
    }

    //sign == - => forwardDCT
    //sign == + => inverseDCT
    private static void DCT128(Block block, String sign){
        double[][] inputMatrix = block.getMatrixBlock();
        double[][] resultMatrix = new double[8][8];
        for(int height=0;height<8;height++){
            for(int width=0;width<8;width++){
                resultMatrix[height][width]=Helpers.chooseOperation(inputMatrix[height][width],128,sign);
            }
        }
        block.setMatrixBlock(resultMatrix);
    }

    //sign == / => quantization
    //sign == * => dequantization
    private static void quantization(Block block,String sign){
        double[][] quantizationMatrix = {
                {6,4,4,6,10,16,20,24},
                {5,5,6,8,10,23,24,22},
                {6,5,6,10,16,23,28,22},
                {6,7,9,12,20,35,32,25},
                {7,9,15,22,27,44,41,31},
                {10,14,22,26,32,42,45,37},
                {20,26,31,35,41,48,48,40},
                {29,37,38,39,45,40,41,40}
        };
        double[][] inputMatrix = block.getMatrixBlock();
        double[][] resultMatrix = new double[8][8];
        for (int height = 0; height <= 3; height++) {
            for (int width = 0; width <= 3; width++) {
                resultMatrix[height][width]=(int)Helpers.chooseOperation(inputMatrix[height][width],quantizationMatrix[height][width],sign);
            }
        }
        block.setMatrixBlock(resultMatrix);
    }

}
