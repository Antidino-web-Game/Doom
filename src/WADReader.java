import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WADReader {

    // Structure de données interne pour stocker les informations d'un Lump
    private class LumpInfo {
        int fileOffset;
        int size;
        String name;

        public LumpInfo(int offset, int size, String name) {
            this.fileOffset = offset;
            this.size = size;
            this.name = name.trim();
        }

        @Override
        public String toString() {
            return String.format("Lump: %-10s Offset: %8d Size: %8d", name, fileOffset, size);
        }
    }

    private final List<LumpInfo> directory = new ArrayList<>();

    // Méthode de la Phase 1 : Lit l'en-tête et appelle la lecture du répertoire
    public void loadWAD(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {

            // Lecture de l'En-tête (Magic, numLumps, directoryOffset)
            byte[] headerBytes = new byte[12];
            if (fis.read(headerBytes) != 12) {
                throw new IOException("Fichier WAD incomplet ou corrompu.");
            }

            ByteBuffer header = ByteBuffer.wrap(headerBytes).order(ByteOrder.LITTLE_ENDIAN);

            // 1. Magic Number
            byte[] magic = new byte[4];
            header.get(magic);
            String wadType = new String(magic);
            System.out.println("Type de WAD détecté: " + wadType);

            // 2. Nombre de Répertoires
            int numLumps = header.getInt();
            System.out.println("Nombre de Lumps: " + numLumps);

            // 3. Offset du Répertoire
            int directoryOffset = header.getInt();
            System.out.println("Début du Répertoire à l'offset: " + directoryOffset);

            // Appel de la lecture du répertoire
            readDirectory(fis, directoryOffset, numLumps);

        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture du WAD: " + e.getMessage());
        }
    }

    // Méthode de la Phase 2 : Lit le Répertoire
    private void readDirectory(FileInputStream fis, int directoryOffset, int numLumps) throws IOException {

        // 1. Positionner le curseur de lecture
        fis.getChannel().position(directoryOffset);

        System.out.println("--- Début de la lecture du Répertoire ---");

        byte[] entryBytes = new byte[32];

        for (int i = 0; i < numLumps; i++) {

            // Lire les 32 octets de l'entrée (Lump)
            if (fis.read(entryBytes) != 32) {
                System.err.println("Erreur de lecture du Lump #" + i + ". Lecture interrompue.");
                break;
            }

            // Utiliser ByteBuffer pour gérer l'ordre Little-Endian
            ByteBuffer bb = ByteBuffer.wrap(entryBytes).order(ByteOrder.LITTLE_ENDIAN);

            // 1. Offset du Lump (4 octets)
            int offset = bb.getInt();

            // 2. Taille du Lump (4 octets)
            int size = bb.getInt();

            // 3. Nom du Lump (8 octets)
            byte[] nameBytes = new byte[8];
            bb.get(nameBytes);
            String name = new String(nameBytes).trim();

            LumpInfo lump = new LumpInfo(offset, size, name);
            directory.add(lump);

            // Afficher le premier Lump de chaque carte
            if (name.matches("E[0-9]M[0-9]|MAP[0-9][0-9]")) {
                System.out.println("CARTE TROUVÉE: " + lump);
            }
        }

        System.out.println("Lecture du répertoire terminée. Total de lumps stockés: " + directory.size());
    }

    // Point d'entrée pour le test

}