package ro.ubb;

import java.util.ArrayList;

public class Entropy {

    public static ArrayList<Block> decode(ArrayList<EntropyElement> entropyElements){
        ArrayList<Block> result = new ArrayList<>();
        ArrayList<Block> yBlocks= new ArrayList<>();
        ArrayList<Block> uBlocks= new ArrayList<>();
        ArrayList<Block> vBlocks= new ArrayList<>();

        ArrayList<Integer> elementsOfCurrentBlock = new ArrayList<>();
        int noOfElementsInCurrentBlock=0;
        String currentBlockType="Y";

        for(int pos=0;pos<entropyElements.size();pos++){
            EntropyElement currentElem=entropyElements.get(pos);

            //check if it is last element of current block, either by reaching 64 elements, or by having a (0,0) code
            EntropyElement nextElem;
            if(pos==entropyElements.size()-1){
                nextElem=new EntropyElement(0,0);
            }
            else{
                nextElem=entropyElements.get(pos+1);
            }
            if(noOfElementsInCurrentBlock==64 ||
                    (currentElem.getRunLength()==null && currentElem.getAmplitude()==0 && currentElem.getSize()==0 &&
                            nextElem.getRunLength()==null)){

                    //if code is (0,0), add how many 0's are necessary
                    int currentSize=elementsOfCurrentBlock.size();
                    for(int i=0; i<64-currentSize; i++){
                        elementsOfCurrentBlock.add(0);
                    }

                    //add block to result
                    double[][] matrixBlock = fromListToBlock(elementsOfCurrentBlock);
                    Block currentBlock=new Block(matrixBlock,currentBlockType,new Position(0,0));
                    result.add(currentBlock);

                    //reset current block search
                    elementsOfCurrentBlock = new ArrayList<>();
                    noOfElementsInCurrentBlock=0;
                    currentBlockType=Helpers.nextBlockType(currentBlockType);
            }

            //check how many 0's we have to add
            if(currentElem.getRunLength()!=null){
                if(currentElem.getRunLength()!=0){
                    for(int i=0;i<currentElem.getRunLength();i++){
                        elementsOfCurrentBlock.add(0);
                        noOfElementsInCurrentBlock++;
                    }
                }
            }
            elementsOfCurrentBlock.add(currentElem.getAmplitude());
            noOfElementsInCurrentBlock++;
        }
        return result;
    }

    public static ArrayList<EntropyElement> encode(ArrayList<Block> blocks){
        ArrayList<EntropyElement> result = new ArrayList<>();
        ArrayList<Block> yBlocks= new ArrayList<>();
        ArrayList<Block> uBlocks= new ArrayList<>();
        ArrayList<Block> vBlocks= new ArrayList<>();
        for(Block block : blocks){
            switch(block.blockType){
                case "Y":
                    yBlocks.add(block);
                case "U":
                    uBlocks.add(block);
                case "V":
                    vBlocks.add(block);
            }
        }
        for(int i=0;i<yBlocks.size();i++){
            result.addAll(blockToEntropyEncode(yBlocks.get(i)));
            result.addAll(blockToEntropyEncode(uBlocks.get(i)));
            result.addAll(blockToEntropyEncode(vBlocks.get(i)));
        }
        return result;
    }

    public static ArrayList<EntropyElement> blockToEntropyEncode(Block block){
        ArrayList<EntropyElement> result = new ArrayList<>();
        ArrayList<Integer> zigzagParsedBlock= zigzagParsing(block);

        //add first element
        result.add(new EntropyElement(Helpers.getSizeOfAmplitude(zigzagParsedBlock.get(0)),zigzagParsedBlock.get(0)));

        int runOfZeros=0;
        for(int num : zigzagParsedBlock.subList(1,zigzagParsedBlock.size())){
            if(num==0){
                runOfZeros++;
            }
            else{
                EntropyElement entropyElement=new EntropyElement(Helpers.getSizeOfAmplitude(num),num);
                entropyElement.setRunLength(runOfZeros);
                runOfZeros=0;
                result.add(entropyElement);
            }
        }

        //if the list ends in 0, add special byte (0,0)
        if(runOfZeros!=0){
            result.add(new EntropyElement(0,0));
        }
        return result;
    }

    public static double[][] fromListToBlock(ArrayList<Integer> elems) {
        double[][] matrixBlock = new double[8][8];
        int currentDiagonal=1;
        int i=0;
        int j=1;
        matrixBlock[0][0]= elems.get(0);
        int currentPos=1;
        int currentDiagonalTimes=1;
        while(currentDiagonal<15){
            if(currentDiagonal%2==1){
                int times=currentDiagonalTimes+1;
                while(times>0){
                    times--;
                    matrixBlock[i][j]=elems.get(currentPos);
                    i++;
                    j--;
                    currentPos++;
                }
                if (currentDiagonal < 7) {
                    j++;
                    currentDiagonalTimes++;
                }
                else{
                    currentDiagonalTimes--;
                    j=j+2;
                    i--;
                }
                currentDiagonal++;
            }
            if(currentDiagonal%2==0){
                int times=currentDiagonalTimes+1;
                while(times>0){
                    times--;
                    matrixBlock[i][j]=elems.get(currentPos);
                    currentPos++;
                    i--;
                    j++;
                }
                if (currentDiagonal < 7) {
                    i++;
                    currentDiagonalTimes++;
                }
                else{
                    i=i+2;
                    j--;
                    currentDiagonalTimes--;
                }
                currentDiagonal++;
            }
        }
        return matrixBlock;
    }

    public static ArrayList<Integer> zigzagParsing(Block block){
        ArrayList<ArrayList<Integer>> solution = new ArrayList<>();

        for(int i=0; i<8+8-1; i++){
            ArrayList<Integer> diagonalsList= new ArrayList<>();
            solution.add(diagonalsList);
        }

        double[][] inputMatrix=block.getMatrixBlock();
        int sum=0;
        for(int i=0; i<8;i++){
            for(int j=0;j<8;j++){
                sum=i+j;
                if(sum%2==0){
                    solution.get(sum).add(0,(int)inputMatrix[i][j]);
                }
                else{
                    solution.get(sum).add((int)inputMatrix[i][j]);
                }
            }
        }

        ArrayList<Integer> finalSolution= new ArrayList<>();
        for(ArrayList<Integer> diagonalsList : solution){
            finalSolution.addAll(diagonalsList);
        }
        return finalSolution;
    }
}
