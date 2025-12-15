import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class WADReader {

    // Structure de données interne pour stocker les informations d'un Lump
    private class LumpInfo {
        int fileOffset; // Adresse de début de la donnée dans le fichier
        int size; // Taille en octets
        String name; // Nom du Lump (ex: E1M1, S_START, etc.)

        public LumpInfo(int offset, int size, String name) {
            this.fileOffset = offset;
            this.size = size;
            this.name = name.trim(); // Supprimer les espaces de remplissage
        }

        @Override
        public String toString() {
            return String.format("Lump: %-10s Offset: %8d Size: %8d", name, fileOffset, size);
        }
    }

    private List<LumpInfo> directory = new ArrayList<>();


        // 1. Positionner le curseur de lecture au début du répertoire
        // Note : Cela réinitialise le flux, donc soyez sûr d'appeler cette méthode dans
        // le try-with-resources de loadWAD.
        fis.getChannel().position(directoryOffset);

        System.out.println("--- Début de la lecture du Répertoire ---");

        byte[] entryBytes = new byte[32]; // Chaque entrée fait 32 octets

        for (int i = 0; i < numLumps; i++) {

            // Lire les 32 octets de l'entrée (Lump)
            if (fis.read(entryBytes) != 32) {
                System.err.println("Erreur de lecture du Lump #" + i);
                break;
            }

            // Utiliser ByteBuffer pour gérer l'ordre Little-Endian
            ByteBuffer bb = ByteBuffer.wrap(entryBytes);
            bb.order(ByteOrder.LITTLE_ENDIAN);

            // 1. Offset du Lump (4 octets)
            int offset = bb.getInt();

            // 2. Taille du Lump (4 octets)
            int size = bb.getInt();

            // 3. Nom du Lump (8 octets)
            byte[] nameBytes = new byte[8];
            bb.get(nameBytes); // Lire les 8 octets suivants

            String name = new String(nameBytes).trim(); // Le nom est en ASCII

            LumpInfo lump = new LumpInfo(offset, size, name);
            directory.add(lump);

            // Afficher le premier Lump de chaque carte (E1M1, E2M1, etc.) pour validation
            if (name.matches("E[0-9]M[0-9]|MAP[0-9][0-9]")) {
                System.out.println("CARTE TROUVÉE: " + lump);
            }
        }

        System.out.println("Lecture du répertoire terminée. Total de lumps stockés: " + directory.size());

        // Maintenant, vous pouvez utiliser la liste 'directory' pour trouver n'importe
        // quelle ressource !
    }
}