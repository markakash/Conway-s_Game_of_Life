package sample;

public class Cell {

    private boolean isAlive;
    private boolean stateNextGen;

    public Cell() {
        isAlive = false;
    }

    public void toggleLife(){
        isAlive = !isAlive;
    }

    public boolean isAlive() {
        return (isAlive);
    }

    /*
     * This method will calculate internally what the next generation should be.
     */
    public void calculatenextGen(int aantalburen) {
        if (isAlive) {
            if (aantalburen == 2 || aantalburen == 3) {
                stateNextGen = true;
            }
            if (aantalburen < 2 || aantalburen > 3) {
                stateNextGen = false;
            }
        }
        else{
            stateNextGen = aantalburen == 3;
        }
    }

    /*
     * Updates the current generation
     */
    public void goNextGen(){
        isAlive = stateNextGen;
    }
}
