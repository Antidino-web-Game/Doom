import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WADLoader {

    public void loadWAD(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {

            // --- 1. Lecture du Magic Number (4 octets) ---
            byte[] magic = new byte[4];
            int bytesRead = fis.read(magic);

            if (bytesRead != 4) {
                System.err.println("Erreur: Le fichier est trop court.");
                return;
            }

            String wadType = new String(magic);
            if (!wadType.equals("IWAD") && !wadType.equals("PWAD")) {
                System.err.println("Erreur: Le fichier n'est pas un WAD valide (" + wadType + ").");
                return;
            }

            System.out.println("Type de WAD détecté: " + wadType);

            // --- 2. Lecture du Nombre de Répertoires (4 octets) ---
            // Le WAD utilise le format Little-Endian, Java est Big-Endian.
            // On utilise ByteBuffer pour gérer l'inversion d'octets.
            byte[] numLumpsBytes = new byte[4];
            fis.read(numLumpsBytes);

            ByteBuffer bb1 = ByteBuffer.wrap(numLumpsBytes);
            bb1.order(ByteOrder.LITTLE_ENDIAN); // Définir l'ordre Little-Endian
            int numLumps = bb1.getInt();

            System.out.println("Nombre de Lumps (entrées dans le répertoire): " + numLumps);

            // --- 3. Lecture de l'Offset du Répertoire (4 octets) ---
            byte[] dirOffsetBytes = new byte[4];
            fis.read(dirOffsetBytes);

            ByteBuffer bb2 = ByteBuffer.wrap(dirOffsetBytes);
            bb2.order(ByteOrder.LITTLE_ENDIAN);
            int directoryOffset = bb2.getInt();

            System.out.println("Début du Répertoire à l'offset: " + directoryOffset);

            // Maintenant, on peut passer à la lecture du répertoire (Étape 2)
            readDirectory(fis, directoryOffset, numLumps);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ... La méthode readDirectory sera implémentée ensuite ...
    private void readDirectory(FileInputStream fis, int directoryOffset, int numLumps) throws IOException {
        // Logique de lecture du répertoire ici
    }

}