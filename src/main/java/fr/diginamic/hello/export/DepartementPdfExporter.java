package fr.diginamic.hello.export;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfShading;
import com.itextpdf.text.pdf.PdfShadingPattern;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import fr.diginamic.hello.entities.Departement;
import fr.diginamic.hello.entities.Ville;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Génère la fiche PDF d'un département (TP 12), avec iText.
 * <p>
 * Isolé dans son propre package pour ne pas alourdir {@code DepartementControleur}, qui se contente d'appeler
 * {@link #genererPdf(Departement, List)} et de poser les en-têtes HTTP de téléchargement.
 * <p>
 * La mise en page s'appuie sur du dessin vectoriel (dégradés, formes, icônes) plutôt que sur des images
 * ou des émojis, non gérés par les polices PDF standard.
 */
@Component
public class DepartementPdfExporter {

    private static final BaseColor VERT_FONCE = new BaseColor(13, 90, 46);
    private static final BaseColor VERT = new BaseColor(22, 143, 74);
    private static final BaseColor VERT_CLAIR = new BaseColor(58, 184, 108);
    private static final BaseColor VERT_PALE = new BaseColor(234, 248, 239);
    private static final BaseColor VERT_BORDURE = new BaseColor(198, 232, 211);
    private static final BaseColor GRIS_TEXTE = new BaseColor(96, 106, 100);
    private static final BaseColor OR = new BaseColor(233, 179, 40);
    private static final BaseColor ARGENT = new BaseColor(170, 181, 191);
    private static final BaseColor BRONZE = new BaseColor(194, 128, 68);
    private static final BaseColor JAUNE_SMILEY = new BaseColor(255, 206, 60);
    private static final BaseColor BRUN_SMILEY = new BaseColor(92, 66, 16);

    /** Hauteur du bandeau d'en-tête dessiné en pleine largeur sur chaque page. */
    private static final float HAUTEUR_ENTETE = 165f;

    /**
     * Génère le PDF de présentation d'un département : bandeau dégradé avec smiley, cartes de statistiques,
     * podium des trois villes les plus peuplées et tableau détaillé avec barres de répartition.
     *
     * @param departement département à présenter
     * @param villes villes rattachées à ce département, déjà triées par population décroissante
     * @return le contenu binaire du PDF généré
     */
    public byte[] genererPdf(Departement departement, List<Ville> villes){
        String nomAffiche = (departement.getNom() != null && !departement.getNom().isBlank())
                ? departement.getNom() : "Nom non renseigné";

        ByteArrayOutputStream flux = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 42, 42, HAUTEUR_ENTETE + 28, 62);

        try{
            PdfWriter writer = PdfWriter.getInstance(document, flux);
            writer.setPageEvent(new HabillagePage(nomAffiche, departement.getCode()));
            document.open();
            PdfContentByte contenuDirect = writer.getDirectContent();

            Font policeValeurCarte = new Font(Font.FontFamily.HELVETICA, 21, Font.BOLD, VERT_FONCE);
            Font policeLabelCarte = new Font(Font.FontFamily.HELVETICA, 7, Font.BOLD, GRIS_TEXTE);
            Font policeSectionTitre = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, VERT_FONCE);
            Font policeEntete = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
            Font policeCellule = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL, new BaseColor(40, 48, 43));
            Font policeCelluleForte = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, VERT_FONCE);
            Font policeCelluleRang = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_TEXTE);
            Font policeTotal = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD, BaseColor.WHITE);
            Font policePodiumVille = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, VERT_FONCE);
            Font policePodiumPop = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, GRIS_TEXTE);
            Font policeVide = new Font(Font.FontFamily.HELVETICA, 11, Font.ITALIC, GRIS_TEXTE);

            // Espace classique en séparateur de milliers : l'espace fine du Locale.FRANCE par défaut
            // n'existe pas dans l'encodage WinAnsi utilisé par la police Helvetica et disparaît à l'affichage
            DecimalFormatSymbols symbolesFrance = new DecimalFormatSymbols(Locale.FRANCE);
            symbolesFrance.setGroupingSeparator(' ');
            NumberFormat formatNombre = new DecimalFormat("#,##0", symbolesFrance);

            int populationTotale = villes.stream().mapToInt(this::population).sum();
            int populationMax = villes.stream().mapToInt(this::population).max().orElse(0);
            int populationMoyenne = villes.isEmpty() ? 0 : populationTotale / villes.size();

            // Bloc des trois indicateurs clés
            PdfPTable cartes = new PdfPTable(3);
            cartes.setWidthPercentage(100);
            cartes.setSpacingAfter(24);
            ajouterCarteStat(cartes, creerIconeVilles(contenuDirect, 26f), String.valueOf(villes.size()),
                    villes.size() > 1 ? "VILLES RECENSÉES" : "VILLE RECENSÉE", policeValeurCarte, policeLabelCarte);
            ajouterCarteStat(cartes, creerIconeHabitant(contenuDirect, 26f), formatNombre.format(populationTotale),
                    "HABITANTS AU TOTAL", policeValeurCarte, policeLabelCarte);
            ajouterCarteStat(cartes, creerIconeSmiley(contenuDirect, 26f), formatNombre.format(populationMoyenne),
                    "HABITANTS EN MOYENNE", policeValeurCarte, policeLabelCarte);
            document.add(cartes);

            // Podium des trois villes les plus peuplées
            if(villes.size() >= 3){
                document.add(titreSection("Le podium du département", policeSectionTitre, contenuDirect));

                PdfPTable podium = new PdfPTable(3);
                podium.setWidthPercentage(100);
                podium.setSpacingAfter(26);
                BaseColor[] couleursMedailles = {OR, ARGENT, BRONZE};
                for(int i = 0; i < 3; i++){
                    Ville ville = villes.get(i);
                    ajouterCartePodium(podium, creerImageMedaille(contenuDirect, 30f, i + 1, couleursMedailles[i]),
                            ville.getNom(), formatNombre.format(population(ville)) + " habitants",
                            couleursMedailles[i], policePodiumVille, policePodiumPop);
                }
                document.add(podium);
            }

            document.add(titreSection("Toutes les villes du département", policeSectionTitre, contenuDirect));

            if(villes.isEmpty()){
                document.add(new Paragraph("Aucune ville n'est encore rattachée à ce département.", policeVide));
            } else {
                PdfPTable tableVilles = new PdfPTable(new float[]{0.8f, 2.9f, 1.7f, 2.6f});
                tableVilles.setWidthPercentage(100);
                tableVilles.setHeaderRows(1);

                ajouterEntete(tableVilles, "#", policeEntete, Element.ALIGN_CENTER);
                ajouterEntete(tableVilles, "NOM DE LA VILLE", policeEntete, Element.ALIGN_LEFT);
                ajouterEntete(tableVilles, "POPULATION", policeEntete, Element.ALIGN_RIGHT);
                ajouterEntete(tableVilles, "RÉPARTITION", policeEntete, Element.ALIGN_LEFT);

                int rang = 1;
                for(Ville ville : villes){
                    BaseColor fond = (rang % 2 == 1) ? VERT_PALE : BaseColor.WHITE;

                    if(rang <= 3){
                        BaseColor couleurMedaille = rang == 1 ? OR : rang == 2 ? ARGENT : BRONZE;
                        PdfPCell celluleRang = new PdfPCell(creerImageMedaille(contenuDirect, 17f, rang, couleurMedaille), false);
                        celluleRang.setBackgroundColor(fond);
                        celluleRang.setBorder(Rectangle.NO_BORDER);
                        celluleRang.setPadding(5);
                        celluleRang.setHorizontalAlignment(Element.ALIGN_CENTER);
                        celluleRang.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        tableVilles.addCell(celluleRang);
                    } else {
                        ajouterCellule(tableVilles, String.valueOf(rang), policeCelluleRang, fond, Element.ALIGN_CENTER);
                    }

                    ajouterCellule(tableVilles, ville.getNom(), rang <= 3 ? policeCelluleForte : policeCellule,
                            fond, Element.ALIGN_LEFT);
                    ajouterCellule(tableVilles, formatNombre.format(population(ville)), policeCellule, fond, Element.ALIGN_RIGHT);

                    float ratio = populationMax > 0 ? (float) population(ville) / populationMax : 0f;
                    PdfPCell celluleBarre = new PdfPCell();
                    celluleBarre.setBackgroundColor(fond);
                    celluleBarre.setBorder(Rectangle.NO_BORDER);
                    celluleBarre.setFixedHeight(22f);
                    celluleBarre.setCellEvent(new BarreRepartitionEvent(ratio));
                    tableVilles.addCell(celluleBarre);

                    rang++;
                }

                // Ligne de total, sur fond plein
                Phrase texteTotal = new Phrase("TOTAL DU DÉPARTEMENT  ", policeTotal);
                texteTotal.add(new Chunk(creerImageSmiley(contenuDirect, 12f), 0, -2.5f, false));
                PdfPCell celluleTotalLibelle = new PdfPCell(texteTotal);
                celluleTotalLibelle.setColspan(2);
                celluleTotalLibelle.setBackgroundColor(VERT_FONCE);
                celluleTotalLibelle.setBorder(Rectangle.NO_BORDER);
                celluleTotalLibelle.setPadding(9);
                celluleTotalLibelle.setHorizontalAlignment(Element.ALIGN_LEFT);
                tableVilles.addCell(celluleTotalLibelle);

                ajouterCellule(tableVilles, formatNombre.format(populationTotale), policeTotal, VERT_FONCE, Element.ALIGN_RIGHT);

                PdfPCell celluleTotalVide = new PdfPCell(new Phrase(""));
                celluleTotalVide.setBackgroundColor(VERT_FONCE);
                celluleTotalVide.setBorder(Rectangle.NO_BORDER);
                tableVilles.addCell(celluleTotalVide);

                document.add(tableVilles);
            }
        } catch(DocumentException | IOException e){
            throw new IllegalStateException("Erreur lors de la génération du PDF du département " + departement.getCode(), e);
        } finally {
            document.close();
        }

        return flux.toByteArray();
    }

    /**
     * Renvoie la population d'une ville, en considérant une population absente comme nulle.
     */
    private int population(Ville ville){
        return ville.getPopulation() != null ? ville.getPopulation() : 0;
    }

    /**
     * Construit un titre de section : petite étoile décorative suivie du libellé.
     */
    private Paragraph titreSection(String libelle, Font police, PdfContentByte contenuDirect) throws DocumentException{
        Paragraph titre = new Paragraph();
        titre.add(new Chunk(creerImageEtoile(contenuDirect, 11f), 0, -1f, false));
        titre.add(new Chunk("  " + libelle, police));
        titre.setSpacingAfter(11);
        return titre;
    }

    /**
     * Ajoute une carte d'indicateur (icône, valeur, libellé) sur fond arrondi au bloc de synthèse.
     */
    private void ajouterCarteStat(PdfPTable table, Image icone, String valeur, String libelle,
                                   Font policeValeur, Font policeLibelle){
        PdfPCell cellule = new PdfPCell();
        cellule.setBorder(Rectangle.NO_BORDER);
        cellule.setPadding(15);
        cellule.setCellEvent(new CarteArrondieEvent(VERT_PALE, VERT_BORDURE, null));

        Paragraph iconeP = new Paragraph();
        iconeP.add(new Chunk(icone, 0, 0));
        iconeP.setAlignment(Element.ALIGN_CENTER);
        cellule.addElement(iconeP);

        Paragraph valeurP = new Paragraph(valeur, policeValeur);
        valeurP.setAlignment(Element.ALIGN_CENTER);
        valeurP.setSpacingBefore(6);
        cellule.addElement(valeurP);

        Paragraph libelleP = new Paragraph(libelle, policeLibelle);
        libelleP.setAlignment(Element.ALIGN_CENTER);
        libelleP.setSpacingBefore(3);
        cellule.addElement(libelleP);

        table.addCell(cellule);
    }

    /**
     * Ajoute une carte du podium (médaille, nom de la ville, population) au bloc des trois villes les plus peuplées.
     */
    private void ajouterCartePodium(PdfPTable table, Image medaille, String nomVille, String population,
                                     BaseColor accent, Font policeVille, Font policePopulation){
        PdfPCell cellule = new PdfPCell();
        cellule.setBorder(Rectangle.NO_BORDER);
        cellule.setPadding(14);
        cellule.setCellEvent(new CarteArrondieEvent(BaseColor.WHITE, VERT_BORDURE, accent));

        Paragraph medailleP = new Paragraph();
        medailleP.add(new Chunk(medaille, 0, 0));
        medailleP.setAlignment(Element.ALIGN_CENTER);
        medailleP.setSpacingBefore(4);
        cellule.addElement(medailleP);

        Paragraph villeP = new Paragraph(nomVille, policeVille);
        villeP.setAlignment(Element.ALIGN_CENTER);
        villeP.setSpacingBefore(7);
        cellule.addElement(villeP);

        Paragraph populationP = new Paragraph(population, policePopulation);
        populationP.setAlignment(Element.ALIGN_CENTER);
        populationP.setSpacingBefore(2);
        cellule.addElement(populationP);

        table.addCell(cellule);
    }

    /**
     * Ajoute une cellule d'en-tête sur fond de couleur principale au tableau des villes.
     */
    private void ajouterEntete(PdfPTable table, String libelle, Font police, int alignement){
        PdfPCell cellule = new PdfPCell(new Phrase(libelle, police));
        cellule.setBackgroundColor(VERT);
        cellule.setBorder(Rectangle.NO_BORDER);
        cellule.setPadding(9);
        cellule.setHorizontalAlignment(alignement);
        table.addCell(cellule);
    }

    /**
     * Ajoute une cellule de données au tableau des villes, avec alignement et couleur de fond alternée.
     */
    private void ajouterCellule(PdfPTable table, String valeur, Font police, BaseColor fond, int alignement){
        PdfPCell cellule = new PdfPCell(new Phrase(valeur, police));
        cellule.setBackgroundColor(fond);
        cellule.setBorder(Rectangle.NO_BORDER);
        cellule.setPadding(7);
        cellule.setHorizontalAlignment(alignement);
        cellule.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cellule);
    }

    /**
     * Dessine une icône « villes » (trois immeubles blancs sur pastille verte).
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur de l'icône, en points
     * @return l'icône sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     */
    private Image creerIconeVilles(PdfContentByte contenuDirect, float taille) throws DocumentException{
        PdfTemplate gabarit = pastille(contenuDirect, taille);
        float c = taille / 2f;

        gabarit.setColorFill(BaseColor.WHITE);
        gabarit.rectangle(c - 0.30f * taille, c - 0.20f * taille, 0.16f * taille, 0.26f * taille);
        gabarit.fill();
        gabarit.rectangle(c - 0.08f * taille, c - 0.20f * taille, 0.16f * taille, 0.40f * taille);
        gabarit.fill();
        gabarit.rectangle(c + 0.14f * taille, c - 0.20f * taille, 0.16f * taille, 0.32f * taille);
        gabarit.fill();

        return Image.getInstance(gabarit);
    }

    /**
     * Dessine une icône « habitants » (silhouette blanche sur pastille verte).
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur de l'icône, en points
     * @return l'icône sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     */
    private Image creerIconeHabitant(PdfContentByte contenuDirect, float taille) throws DocumentException{
        PdfTemplate gabarit = pastille(contenuDirect, taille);
        float c = taille / 2f;

        gabarit.setColorFill(BaseColor.WHITE);
        gabarit.circle(c, c + 0.16f * taille, 0.12f * taille);
        gabarit.fill();
        gabarit.arc(c - 0.24f * taille, c - 0.26f * taille, c + 0.24f * taille, c + 0.10f * taille, 0, 180);
        gabarit.fill();

        return Image.getInstance(gabarit);
    }

    /**
     * Dessine une icône « smiley » (visage jaune souriant sur pastille verte).
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur de l'icône, en points
     * @return l'icône sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     */
    private Image creerIconeSmiley(PdfContentByte contenuDirect, float taille) throws DocumentException{
        PdfTemplate gabarit = pastille(contenuDirect, taille);
        dessinerSmiley(gabarit, taille / 2f, taille / 2f, taille * 0.32f);
        return Image.getInstance(gabarit);
    }

    /**
     * Dessine un smiley seul (sans pastille), utilisable comme accent décoratif.
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur du smiley, en points
     * @return le smiley sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     */
    private Image creerImageSmiley(PdfContentByte contenuDirect, float taille) throws DocumentException{
        PdfTemplate gabarit = contenuDirect.createTemplate(taille, taille);
        dessinerSmiley(gabarit, taille / 2f, taille / 2f, taille / 2f - 1f);
        return Image.getInstance(gabarit);
    }

    /**
     * Dessine une médaille colorée portant le rang de la ville.
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur de la médaille, en points
     * @param rang rang affiché au centre de la médaille
     * @param couleur couleur de la médaille (or, argent ou bronze)
     * @return la médaille sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     * @throws IOException si la police utilisée pour le rang ne peut pas être chargée
     */
    private Image creerImageMedaille(PdfContentByte contenuDirect, float taille, int rang, BaseColor couleur)
            throws DocumentException, IOException{
        PdfTemplate gabarit = contenuDirect.createTemplate(taille, taille);
        float centre = taille / 2f;

        gabarit.setColorFill(couleur);
        gabarit.circle(centre, centre, centre - 1f);
        gabarit.fill();

        gabarit.setColorFill(BaseColor.WHITE);
        gabarit.setLineWidth(1f);
        gabarit.setColorStroke(new BaseColor(255, 255, 255, 90));
        gabarit.circle(centre, centre, centre - 3.5f);
        gabarit.stroke();

        BaseFont police = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        gabarit.beginText();
        gabarit.setFontAndSize(police, taille * 0.46f);
        gabarit.setColorFill(BaseColor.WHITE);
        gabarit.showTextAligned(Element.ALIGN_CENTER, String.valueOf(rang), centre, centre - taille * 0.16f, 0);
        gabarit.endText();

        return Image.getInstance(gabarit);
    }

    /**
     * Dessine une petite étoile à cinq branches, utilisée comme puce des titres de section.
     *
     * @param contenuDirect canevas iText sur lequel créer le gabarit
     * @param taille largeur/hauteur de l'étoile, en points
     * @return l'étoile sous forme d'{@link Image} prête à être insérée dans le document
     * @throws DocumentException si l'image ne peut pas être créée à partir du gabarit
     */
    private Image creerImageEtoile(PdfContentByte contenuDirect, float taille) throws DocumentException{
        PdfTemplate gabarit = contenuDirect.createTemplate(taille, taille);
        dessinerEtoile(gabarit, taille / 2f, taille / 2f, taille / 2f - 0.5f, VERT);
        return Image.getInstance(gabarit);
    }

    /**
     * Crée un gabarit contenant une pastille ronde verte, servant de fond aux icônes.
     */
    private PdfTemplate pastille(PdfContentByte contenuDirect, float taille){
        PdfTemplate gabarit = contenuDirect.createTemplate(taille, taille);
        gabarit.setColorFill(VERT);
        gabarit.circle(taille / 2f, taille / 2f, taille / 2f);
        gabarit.fill();
        return gabarit;
    }

    /**
     * Trace un visage souriant (visage, yeux, sourire) sur un canevas iText quelconque.
     */
    private static void dessinerSmiley(PdfContentByte canevas, float centreX, float centreY, float rayon){
        canevas.setColorFill(JAUNE_SMILEY);
        canevas.circle(centreX, centreY, rayon);
        canevas.fill();

        canevas.setColorFill(BRUN_SMILEY);
        canevas.circle(centreX - rayon * 0.36f, centreY + rayon * 0.20f, rayon * 0.12f);
        canevas.fill();
        canevas.circle(centreX + rayon * 0.36f, centreY + rayon * 0.20f, rayon * 0.12f);
        canevas.fill();

        canevas.setColorStroke(BRUN_SMILEY);
        canevas.setLineWidth(Math.max(0.8f, rayon * 0.15f));
        canevas.arc(centreX - rayon * 0.55f, centreY - rayon * 0.58f, centreX + rayon * 0.55f, centreY + rayon * 0.08f,
                200, 140);
        canevas.stroke();
    }

    /**
     * Trace une étoile à cinq branches pleine sur un canevas iText quelconque.
     */
    private static void dessinerEtoile(PdfContentByte canevas, float centreX, float centreY, float rayonExterieur,
                                        BaseColor couleur){
        float rayonInterieur = rayonExterieur * 0.45f;
        double angle = -Math.PI / 2;
        double pas = Math.PI / 5;

        canevas.setColorFill(couleur);
        for(int i = 0; i < 10; i++){
            float rayon = (i % 2 == 0) ? rayonExterieur : rayonInterieur;
            float x = (float) (centreX + rayon * Math.cos(angle));
            float y = (float) (centreY + rayon * Math.sin(angle));
            if(i == 0){
                canevas.moveTo(x, y);
            } else {
                canevas.lineTo(x, y);
            }
            angle += pas;
        }
        canevas.closePath();
        canevas.fill();
    }

    /**
     * Dessine le fond arrondi des cartes (indicateurs et podium), avec un liseré d'accent optionnel en haut.
     */
    private static class CarteArrondieEvent implements PdfPCellEvent {

        private final BaseColor fond;
        private final BaseColor bordure;
        private final BaseColor accent;

        CarteArrondieEvent(BaseColor fond, BaseColor bordure, BaseColor accent){
            this.fond = fond;
            this.bordure = bordure;
            this.accent = accent;
        }

        @Override
        public void cellLayout(PdfPCell cellule, Rectangle position, PdfContentByte[] canevas){
            PdfContentByte fondCanevas = canevas[PdfPTable.BACKGROUNDCANVAS];
            float marge = 5f;
            float gauche = position.getLeft() + marge;
            float largeur = position.getWidth() - 2 * marge;
            float hauteurAccent = 5f;

            fondCanevas.saveState();

            // La bande d'accent est obtenue en superposant la carte sur un fond coloré légèrement plus haut,
            // ce qui garantit un liseré net en haut sans risque de chevaucher le contenu de la cellule
            if(accent != null){
                fondCanevas.setColorFill(accent);
                fondCanevas.roundRectangle(gauche, position.getBottom(), largeur, position.getHeight(), 9f);
                fondCanevas.fill();
            }

            fondCanevas.setColorFill(fond);
            fondCanevas.setColorStroke(bordure);
            fondCanevas.setLineWidth(1f);
            fondCanevas.roundRectangle(gauche, position.getBottom(), largeur,
                    accent != null ? position.getHeight() - hauteurAccent : position.getHeight(), 9f);
            fondCanevas.fillStroke();

            fondCanevas.restoreState();
        }
    }

    /**
     * Dessine, dans la dernière colonne du tableau, la barre de répartition de la population d'une ville
     * par rapport à la ville la plus peuplée du département.
     */
    private static class BarreRepartitionEvent implements PdfPCellEvent {

        private final float ratio;

        BarreRepartitionEvent(float ratio){
            this.ratio = ratio;
        }

        @Override
        public void cellLayout(PdfPCell cellule, Rectangle position, PdfContentByte[] canevas){
            PdfContentByte fondCanevas = canevas[PdfPTable.BACKGROUNDCANVAS];
            float hauteur = 7f;
            float gauche = position.getLeft() + 6f;
            float largeurPiste = position.getWidth() - 16f;
            float bas = position.getBottom() + (position.getHeight() - hauteur) / 2f;
            float largeurBarre = Math.max(14f, largeurPiste * ratio);

            fondCanevas.saveState();
            fondCanevas.setColorFill(new BaseColor(214, 236, 223));
            fondCanevas.roundRectangle(gauche, bas, largeurPiste, hauteur, hauteur / 2f);
            fondCanevas.fill();

            fondCanevas.setColorFill(ratio > 0.66f ? VERT_FONCE : ratio > 0.33f ? VERT : VERT_CLAIR);
            fondCanevas.roundRectangle(gauche, bas, largeurBarre, hauteur, hauteur / 2f);
            fondCanevas.fill();
            fondCanevas.restoreState();
        }
    }

    /**
     * Dessine, sur chaque page, le bandeau d'en-tête dégradé (nom et code du département, smiley, étoiles)
     * ainsi que le pied de page numéroté.
     */
    private static class HabillagePage extends PdfPageEventHelper {

        private final String nomDepartement;
        private final String codeDepartement;

        HabillagePage(String nomDepartement, String codeDepartement){
            this.nomDepartement = nomDepartement;
            this.codeDepartement = codeDepartement;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document){
            PdfContentByte canevas = writer.getDirectContentUnder();
            Rectangle page = document.getPageSize();
            float largeurPage = page.getWidth();
            float hautPage = page.getHeight();
            float basBandeau = hautPage - HAUTEUR_ENTETE;

            dessinerBandeau(writer, canevas, largeurPage, hautPage, basBandeau);
            dessinerContenuBandeau(canevas, largeurPage, hautPage, basBandeau, writer.getPageNumber());
            dessinerPiedDePage(canevas, largeurPage, writer.getPageNumber());
        }

        /**
         * Peint le fond dégradé du bandeau et ses bulles translucides.
         */
        private void dessinerBandeau(PdfWriter writer, PdfContentByte canevas, float largeurPage, float hautPage,
                                      float basBandeau){
            canevas.saveState();
            PdfShading degrade = PdfShading.simpleAxial(writer, 0, hautPage, largeurPage, basBandeau,
                    VERT_FONCE, VERT_CLAIR);
            canevas.setShadingFill(new PdfShadingPattern(degrade));
            canevas.rectangle(0, basBandeau, largeurPage, HAUTEUR_ENTETE);
            canevas.fill();
            canevas.restoreState();

            canevas.saveState();
            PdfGState transparence = new PdfGState();
            transparence.setFillOpacity(0.10f);
            canevas.setGState(transparence);
            canevas.setColorFill(BaseColor.WHITE);
            canevas.circle(largeurPage - 60f, basBandeau + 24f, 95f);
            canevas.fill();
            canevas.circle(48f, hautPage - 10f, 62f);
            canevas.fill();
            canevas.restoreState();
        }

        /**
         * Écrit le texte du bandeau et ajoute les éléments décoratifs (pastille du code, smiley, étoiles).
         */
        private void dessinerContenuBandeau(PdfContentByte canevas, float largeurPage, float hautPage,
                                             float basBandeau, int numeroPage){
            Font policeSurtitre = new Font(Font.FontFamily.HELVETICA, 8, Font.BOLD, new BaseColor(188, 236, 208));
            Font policeSousTitre = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, new BaseColor(214, 245, 227));
            float tailleNom = nomDepartement.length() > 24 ? 20f : nomDepartement.length() > 16 ? 25f : 30f;
            Font policeNom = new Font(Font.FontFamily.HELVETICA, tailleNom, Font.BOLD, BaseColor.WHITE);

            String surtitre = numeroPage == 1 ? "F I C H E   D É P A R T E M E N T A L E"
                    : "F I C H E   D É P A R T E M E N T A L E   ( S U I T E )";
            ColumnText.showTextAligned(canevas, Element.ALIGN_LEFT, new Phrase(surtitre, policeSurtitre),
                    46f, hautPage - 54f, 0);
            ColumnText.showTextAligned(canevas, Element.ALIGN_LEFT, new Phrase(nomDepartement, policeNom),
                    46f, hautPage - 92f, 0);
            ColumnText.showTextAligned(canevas, Element.ALIGN_LEFT,
                    new Phrase("Département n° " + codeDepartement, policeSousTitre), 46f, hautPage - 114f, 0);

            // Pastille blanche portant le code du département
            float centrePastilleX = largeurPage - 92f;
            float centrePastilleY = hautPage - 86f;
            canevas.saveState();
            canevas.setColorFill(BaseColor.WHITE);
            canevas.circle(centrePastilleX, centrePastilleY, 33f);
            canevas.fill();
            canevas.restoreState();

            Font policeCode = new Font(Font.FontFamily.HELVETICA, 23, Font.BOLD, VERT_FONCE);
            ColumnText.showTextAligned(canevas, Element.ALIGN_CENTER, new Phrase(codeDepartement, policeCode),
                    centrePastilleX, centrePastilleY - 8f, 0);

            canevas.saveState();
            dessinerSmiley(canevas, largeurPage - 45f, basBandeau + 34f, 15f);
            dessinerEtoile(canevas, largeurPage - 138f, hautPage - 44f, 7f, new BaseColor(255, 214, 92));
            dessinerEtoile(canevas, largeurPage - 118f, basBandeau + 30f, 5f, new BaseColor(255, 224, 130));
            dessinerEtoile(canevas, largeurPage - 158f, basBandeau + 52f, 4f, new BaseColor(210, 245, 224));
            canevas.restoreState();
        }

        /**
         * Trace le filet et les mentions du pied de page, avec le numéro de page.
         */
        private void dessinerPiedDePage(PdfContentByte canevas, float largeurPage, int numeroPage){
            Font policePied = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, GRIS_TEXTE);
            String dateGeneration = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE).format(LocalDate.now());

            canevas.saveState();
            canevas.setColorStroke(VERT_BORDURE);
            canevas.setLineWidth(0.9f);
            canevas.moveTo(42f, 46f);
            canevas.lineTo(largeurPage - 42f, 46f);
            canevas.stroke();
            canevas.restoreState();

            ColumnText.showTextAligned(canevas, Element.ALIGN_LEFT,
                    new Phrase("Recensement des villes de France — généré le " + dateGeneration, policePied),
                    42f, 33f, 0);
            ColumnText.showTextAligned(canevas, Element.ALIGN_RIGHT,
                    new Phrase("Page " + numeroPage, policePied), largeurPage - 42f, 33f, 0);
        }
    }
}
