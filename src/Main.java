public class Main {

    public static void main(String[] args) {
        String path = "ressources\\DOOM1.WAD";
        WADReader reader = new WADReader();
        reader.loadWAD(path);
    }
}