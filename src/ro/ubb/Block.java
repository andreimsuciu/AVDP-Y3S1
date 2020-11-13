package ro.ubb;

public class Block {
    double[][] matrixBlock;
    String blockType;
    Position position;

    public Block(double[][] matrixBlock, String blockType, Position position) {
        this.matrixBlock = matrixBlock;
        this.blockType = blockType;
        this.position = position;
    }

    public double[][] getMatrixBlock() {
        return matrixBlock;
    }

    public String getBlockType() {
        return blockType;
    }

    public Position getPosition() {
        return position;
    }
}
