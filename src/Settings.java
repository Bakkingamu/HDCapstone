/**
 * Created by Justin on 10/1/2017.
 */
public final class Settings {
    public boolean verbose;
    // I hope this works, this class should be unchangeable after construction, safe global settings
    public Settings(boolean verbose){
        this.verbose = verbose;
    }
}
