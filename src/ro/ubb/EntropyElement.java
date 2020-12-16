package ro.ubb;

public class EntropyElement {
    Integer runLength=null;
    int size;
    int amplitude;

    EntropyElement(int size, int amplitude){
        this.amplitude=amplitude;
        this.size=size;
    }

    public Integer getRunLength() {
        return runLength;
    }

    public void setRunLength(Integer runLength) {
        this.runLength = runLength;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAmplitude() {
        return amplitude;
    }

    public void setAmplitude(int amplitude) {
        this.amplitude = amplitude;
    }

    @Override
    public String toString() {
        if(runLength==null){
            return "(" + size + ")(" + amplitude + ")";
        }
        return "(" + runLength + "," + size + ")(" + amplitude + ")";
    }
}
